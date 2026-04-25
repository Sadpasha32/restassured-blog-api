package ru.qa.blogapi.tests;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.qa.blogapi.auth.AuthApiClient;
import ru.qa.blogapi.auth.AuthSession;
import ru.qa.blogapi.base.BaseAuthorizedApiTest;
import ru.qa.blogapi.helpers.PostsApiClient;
import ru.qa.blogapi.models.PostCreateRequest;
import ru.qa.blogapi.models.UserRegistrationRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

class BlogApiHomeworkTest extends BaseAuthorizedApiTest {

    // ---------- AUTH ----------

    @Test
    @Tag("smoke")
    @DisplayName("POST /api/auth/register -> регистрирует пользователя с валидными полями")
    void shouldRegisterUserWithValidRequiredFields() {
        UserRegistrationRequest body = TestUserBuilder.valid();

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("User registered successfully"))
                .body("user.id", notNullValue())
                .body("user.email", equalTo(body.getEmail()))
                .body("user.firstName", equalTo(body.getFirstName()))
                .body("user.lastName", equalTo(body.getLastName()))
                .body("user.nickname", equalTo(body.getNickname()));
    }

    @Test
    @Tag("regression")
    @DisplayName("POST /api/auth/register -> 400 при невалидном email")
    void shouldReturnValidationErrorForInvalidEmailOnRegistration() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "not-an-email");
        body.put("password", "SecurePass123!");

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400)
                .body("error.code", equalTo(400))
                .body("error.message", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("POST /api/login -> успешный логин выдаёт access и refresh токены")
    void shouldLoginWithValidCredentials() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        registerUser(user);

        given()
                .spec(requestSpec)
                .body(loginBody(user.getEmail(), user.getPassword()))
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("refresh_token", notNullValue());
    }

    @Test
    @Tag("regression")
    @DisplayName("POST /api/login -> 401 при неверном пароле")
    void shouldReturnUnauthorizedForWrongPassword() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        registerUser(user);

        // /api/login возвращает плоское тело {code,message} (не обёрнутое в error)
        given()
                .spec(requestSpec)
                .body(loginBody(user.getEmail(), "WrongPass987!"))
                .when()
                .post("/api/login")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("POST /api/token/refresh -> возвращает новую пару токенов")
    void shouldRefreshAccessToken() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        registerUser(user);

        Response loginResponse = given()
                .spec(requestSpec)
                .body(loginBody(user.getEmail(), user.getPassword()))
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String refreshToken = loginResponse.jsonPath().getString("refresh_token");

        Map<String, Object> refreshBody = new HashMap<>();
        refreshBody.put("refresh_token", refreshToken);

        given()
                .spec(requestSpec)
                .body(refreshBody)
                .when()
                .post("/api/token/refresh")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("refresh_token", notNullValue());
    }

    // ---------- PROFILE ----------

    @Test
    @Tag("smoke")
    @DisplayName("GET /api/profile -> возвращает профиль текущего пользователя")
    void shouldReturnCurrentUserProfile() {
        // /api/profile реально возвращает поле username (а не email),
        // схема в Swagger расходится с реальностью — проверяем именно username
        given()
                .spec(authorizedRequestSpec)
                .when()
                .get("/api/profile")
                .then()
                .statusCode(200)
                .body("user", notNullValue())
                .body("user.id", equalTo(authSession.getUserId()))
                .body("user.username", equalTo(authSession.getEmail()));
    }

    @Test
    @Tag("regression")
    @DisplayName("PUT /api/profile -> обновляет имя/фамилию/никнейм текущего пользователя")
    void shouldUpdateCurrentUserProfile() {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Updated" + suffix);
        body.put("lastName", "User" + suffix);
        body.put("nickname", "upd_" + suffix);

        given()
                .spec(authorizedRequestSpec)
                .body(body)
                .when()
                .put("/api/profile")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("user.firstName", equalTo(body.get("firstName")))
                .body("user.lastName", equalTo(body.get("lastName")))
                .body("user.nickname", equalTo(body.get("nickname")));

        // verify side effect — данные действительно сохранились
        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", authSession.getUserId())
                .when()
                .get("/api/profile/{id}")
                .then()
                .statusCode(200)
                .body("user.firstName", equalTo(body.get("firstName")))
                .body("user.lastName", equalTo(body.get("lastName")))
                .body("user.nickname", equalTo(body.get("nickname")));
    }

    // ---------- POSTS: LISTING ----------

    @Test
    @Tag("regression")
    @DisplayName("GET /api/posts -> возвращает страницу постов с метаданными пагинации")
    void shouldReturnPaginatedPostsList() {
        // создаём пару постов, чтобы лента точно не была пустой
        PostsApiClient posts = new PostsApiClient(authorizedRequestSpec);
        posts.createPublishedPost("technology");
        posts.createPublishedPost("technology");

        given()
                .spec(authorizedRequestSpec)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get("/api/posts")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("totalItems", greaterThanOrEqualTo(1))
                .body("itemsPerPage", equalTo(10))
                .body("page", equalTo(1))
                .body("pages", greaterThanOrEqualTo(1))
                .body("items.size()", lessThanOrEqualTo(10));
    }

    @Test
    @Tag("regression")
    @DisplayName("GET /api/posts?category=technology -> возвращает только посты выбранной категории")
    void shouldFilterPostsByCategory() {
        PostsApiClient posts = new PostsApiClient(authorizedRequestSpec);
        posts.createPublishedPost("technology");
        posts.createPublishedPost("travel");

        given()
                .spec(authorizedRequestSpec)
                .queryParam("category", "technology")
                .queryParam("limit", 50)
                .when()
                .get("/api/posts")
                .then()
                .statusCode(200)
                .body("items.category", everyItem(equalTo("technology")));
    }

    // ---------- POSTS: CRUD ----------

    @Test
    @Tag("smoke")
    @DisplayName("POST /api/posts -> создаёт опубликованный пост от автора")
    void shouldCreatePublishedPost() {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        PostCreateRequest body = new PostCreateRequest(
                "Published " + suffix,
                "Published body " + suffix,
                "Published desc " + suffix,
                "technology",
                false
        );

        given()
                .spec(authorizedRequestSpec)
                .body(body)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(201)
                .body("status", equalTo("success"))
                .body("post.id", notNullValue())
                .body("post.title", equalTo(body.getTitle()))
                .body("post.isDraft", equalTo(false))
                .body("post.status", equalTo("published"))
                .body("post.author.email", equalTo(authSession.getEmail()));
    }

    @Test
    @Tag("regression")
    @DisplayName("POST /api/posts -> создаёт черновик и он находится в /api/posts/my?drafts=true")
    void shouldCreateDraftPost() {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        PostCreateRequest body = new PostCreateRequest(
                "Draft " + suffix,
                "Draft body " + suffix,
                "Draft desc " + suffix,
                "personal_finance",
                true
        );

        Integer postId = given()
                .spec(authorizedRequestSpec)
                .body(body)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(201)
                .body("post.isDraft", equalTo(true))
                .body("post.status", equalTo("draft"))
                .extract()
                .jsonPath()
                .getInt("post.id");

        given()
                .spec(authorizedRequestSpec)
                .queryParam("drafts", true)
                .when()
                .get("/api/posts/my")
                .then()
                .statusCode(200)
                .body("items.id", hasItem(postId));
    }

    @Test
    @Tag("regression")
    @DisplayName("GET /api/posts/my -> возвращает только посты текущего пользователя")
    void shouldReturnOnlyCurrentUserPosts() {
        PostsApiClient posts = new PostsApiClient(authorizedRequestSpec);
        posts.createPublishedPost("technology");
        posts.createPublishedPost("travel");

        given()
                .spec(authorizedRequestSpec)
                .queryParam("limit", 50)
                .when()
                .get("/api/posts/my")
                .then()
                .statusCode(200)
                .body("items.author.email", everyItem(equalTo(authSession.getEmail())));
    }

    @Test
    @Tag("e2e")
    @DisplayName("GET /api/posts/feed -> возвращает посты других пользователей, не текущего")
    void shouldReturnFeedPosts() {
        // создаём отдельного второго пользователя и его пост
        AuthApiClient anotherClient = new AuthApiClient(requestSpec);
        AuthSession otherSession = anotherClient.createAuthorizedSession();
        new PostsApiClient(buildAuthSpecFor(otherSession.getAccessToken()))
                .createPublishedPost("technology");

        given()
                .spec(authorizedRequestSpec)
                .queryParam("limit", 50)
                .when()
                .get("/api/posts/feed")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("items.author.email", everyItem(not(equalTo(authSession.getEmail()))))
                .body("items.author.email", hasItem(otherSession.getEmail()));
    }

    @Test
    @Tag("regression")
    @DisplayName("GET /api/posts/{id} -> возвращает пост и блок statistics")
    void shouldReturnSinglePostById() {
        Integer postId = new PostsApiClient(authorizedRequestSpec).createPublishedPost("technology");

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .when()
                .get("/api/posts/{id}")
                .then()
                .statusCode(200)
                .body("post.id", equalTo(postId))
                .body("post.title", notNullValue())
                .body("statistics.totalViews", notNullValue())
                .body("statistics.todayViews", notNullValue());
    }

    @Test
    @Tag("regression")
    @DisplayName("PUT /api/posts/{id} -> автор может обновить свой пост")
    void shouldUpdateExistingPost() {
        Integer postId = new PostsApiClient(authorizedRequestSpec).createPublishedPost("technology");

        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        Map<String, Object> update = new HashMap<>();
        update.put("title", "Updated " + suffix);
        update.put("description", "New description " + suffix);

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .body(update)
                .when()
                .put("/api/posts/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("post.id", equalTo(postId))
                .body("post.title", equalTo(update.get("title")))
                .body("post.description", equalTo(update.get("description")));

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .when()
                .get("/api/posts/{id}")
                .then()
                .statusCode(200)
                .body("post.title", equalTo(update.get("title")))
                .body("post.description", equalTo(update.get("description")));
    }

    @Test
    @Tag("e2e")
    @DisplayName("DELETE /api/posts/{id} -> удаляет пост, GET по тому же id возвращает 404")
    void shouldDeletePost() {
        Integer postId = new PostsApiClient(authorizedRequestSpec).createPublishedPost("technology");

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .when()
                .delete("/api/posts/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"));

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .when()
                .get("/api/posts/{id}")
                .then()
                .statusCode(404);
    }

    // ---------- FAVORITES ----------

    @Test
    @Tag("e2e")
    @DisplayName("POST /api/posts/{id}/favorite -> добавляет в избранное и пост виден в /favorites")
    void shouldAddPostToFavoritesAndSeeItInFavoritesList() {
        // нужен пост от другого пользователя — нельзя добавить в избранное свой
        AuthApiClient otherClient = new AuthApiClient(requestSpec);
        AuthSession otherSession = otherClient.createAuthorizedSession();
        Integer postId = new PostsApiClient(buildAuthSpecFor(otherSession.getAccessToken()))
                .createPublishedPost("technology");

        Map<String, Object> body = new HashMap<>();
        body.put("isFavorite", true);

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", postId)
                .body(body)
                .when()
                .post("/api/posts/{id}/favorite")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("isFavorite", equalTo(true));

        given()
                .spec(authorizedRequestSpec)
                .queryParam("limit", 50)
                .when()
                .get("/api/posts/favorites")
                .then()
                .statusCode(200)
                .body("items.id", hasItem(postId));
    }

    // ---------- FILES ----------

    @Test
    @Tag("regression")
    @DisplayName("POST /api/files/upload -> загружает картинку и возвращает метаданные файла")
    void shouldUploadImageFileForPost() {
        byte[] png = TestImage.tinyPng();

        given()
                .spec(authorizedRequestSpec)
                .contentType("multipart/form-data")
                .multiPart("file", "tiny.png", png, "image/png")
                .multiPart("type", "post-image")
                .when()
                .post("/api/files/upload")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("url", notNullValue())
                .body("filename", notNullValue())
                .body("size", greaterThan(0))
                .body("mimeType", equalTo("image/png"));
    }

    @Test
    @Tag("regression")
    @DisplayName("GET /api/files/{id} -> возвращает метаданные ранее загруженного файла")
    void shouldReturnUploadedFileMetadata() {
        byte[] png = TestImage.tinyPng();

        Integer fileId = given()
                .spec(authorizedRequestSpec)
                .contentType("multipart/form-data")
                .multiPart("file", "tiny.png", png, "image/png")
                .multiPart("type", "post-image")
                .when()
                .post("/api/files/upload")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("id");

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", fileId)
                .when()
                .get("/api/files/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(fileId))
                .body("filename", notNullValue())
                .body("size", greaterThan(0))
                .body("mimeType", notNullValue())
                .body("url", notNullValue());
    }

    // ---------- REPORT ----------

    @Test
    @Tag("e2e")
    @DisplayName("POST /api/profile/report/{id} -> жалоба на другого пользователя успешно отправляется")
    void shouldCreateUserReport() {
        AuthSession victim = new AuthApiClient(requestSpec).createAuthorizedSession();

        Map<String, Object> reportBody = new HashMap<>();
        reportBody.put("descriptionReport", "Spamming and abusive behaviour");

        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", victim.getUserId())
                .body(reportBody)
                .when()
                .post("/api/profile/report/{id}")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", notNullValue());

        // GET /api/profile/report/{id} возвращает count, но на этом стенде он отдаёт фиксированный example
        // из спеки — поэтому проверяем только, что эндпоинт жив и отдаёт целое число.
        given()
                .spec(authorizedRequestSpec)
                .pathParam("id", victim.getUserId())
                .when()
                .get("/api/profile/report/{id}")
                .then()
                .statusCode(200)
                .body("count", greaterThanOrEqualTo(0));
    }

    // ---------- helpers ----------

    private Response registerUser(UserRegistrationRequest user) {
        return given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private Map<String, Object> loginBody(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", email);
        body.put("password", password);
        return body;
    }

    private io.restassured.specification.RequestSpecification buildAuthSpecFor(String accessToken) {
        return new io.restassured.builder.RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
    }
}
