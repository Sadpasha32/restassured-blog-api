package ru.qa.blogapi.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.qa.blogapi.auth.AuthSession;
import ru.qa.blogapi.base.BaseUiTest;
import ru.qa.blogapi.config.TestConfig;
import ru.qa.blogapi.models.UserRegistrationRequest;
import ru.qa.blogapi.ui.pages.CreatePostPage;
import ru.qa.blogapi.ui.pages.LoginPage;
import ru.qa.blogapi.ui.pages.RegisterPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.WebDriverConditions.urlStartingWith;
import static com.codeborne.selenide.Selenide.webdriver;

class BlogUiTest extends BaseUiTest {

    @Test
    @Tag("smoke")
    @DisplayName("UI: пользователь, заранее созданный через API, успешно логинится через форму /login")
    void shouldLoginExistingUserThroughUi() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        registerViaApi(user);

        new LoginPage()
                .openPage()
                .fillCredentials(user.getEmail(), user.getPassword())
                .submit();

        // после успешного логина redirect на главную "/"
        webdriver().shouldHave(urlStartingWith(TestConfig.baseUrl()), Duration.ofSeconds(10));
        webdriver().shouldNotHave(urlContaining("/login"));
    }

    @Test
    @Tag("regression")
    @DisplayName("UI: попытка логина с неверным паролем показывает ошибку и не редиректит")
    void shouldShowErrorWhenLoginWithWrongPassword() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        registerViaApi(user);

        new LoginPage()
                .openPage()
                .fillCredentials(user.getEmail(), "WrongPass987!")
                .submit();

        $(".v-snackbar").shouldBe(visible, Duration.ofSeconds(5));
        webdriver().shouldHave(urlContaining("/login"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("UI: регистрация нового пользователя через форму /register")
    void shouldRegisterNewUserThroughUi() {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(8).toLowerCase();
        String email = "ui_" + suffix + "@example.com";
        String password = "SecurePass123!";

        new RegisterPage()
                .openPage()
                .fillRequired(email, password)
                .submit();

        // после успешной регистрации backend выдаёт токен и фронт редиректит на "/"
        webdriver().shouldNotHave(urlContaining("/register"), Duration.ofSeconds(10));
    }

    @Test
    @Tag("e2e")
    @DisplayName("UI E2E: регистрация -> логин -> создание поста через UI -> пост виден в /api/posts/my")
    void shouldCreatePostThroughUiAfterLogin() {
        UserRegistrationRequest user = TestUserBuilder.valid();
        AuthSession session = registerAndLoginViaApi(user);

        // логинимся через UI чтобы фронт положил токен в cookie auth_token
        new LoginPage()
                .openPage()
                .fillCredentials(user.getEmail(), user.getPassword())
                .submit();
        webdriver().shouldNotHave(urlContaining("/login"), Duration.ofSeconds(10));

        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        String title = "UI Post " + suffix;
        new CreatePostPage()
                .openPage()
                .fillCommonFields(title, "UI desc " + suffix, "UI post body " + suffix)
                .selectCategory("technology")
                .publish();

        // после публикации фронт редиректит на /my-posts и пост рендерится в списке
        webdriver().shouldHave(urlContaining("/my-posts"), Duration.ofSeconds(10));
        $x("//*[contains(text(),'" + title + "')]").shouldBe(visible, Duration.ofSeconds(10));

        // дополнительная проверка через API что пост действительно создался под этим юзером
        io.restassured.RestAssured
                .given()
                .spec(authorizedApiSpec(session))
                .when()
                .get("/api/posts/my")
                .then()
                .statusCode(200)
                .body("items.title", org.hamcrest.Matchers.hasItem(title));
    }

    // ---------- helpers ----------

    private void registerViaApi(UserRegistrationRequest user) {
        io.restassured.RestAssured
                .given()
                .baseUri(TestConfig.baseUrl())
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200);
    }

    private AuthSession registerAndLoginViaApi(UserRegistrationRequest user) {
        registerViaApi(user);
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUrl())
                .setContentType(ContentType.JSON)
                .build();
        java.util.Map<String, Object> loginBody = new java.util.HashMap<>();
        loginBody.put("username", user.getEmail());
        loginBody.put("password", user.getPassword());
        var loginResp = io.restassured.RestAssured
                .given()
                .spec(spec)
                .body(loginBody)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().response();
        AuthSession s = new AuthSession();
        s.setEmail(user.getEmail());
        s.setPassword(user.getPassword());
        s.setAccessToken(loginResp.jsonPath().getString("token"));
        s.setRefreshToken(loginResp.jsonPath().getString("refresh_token"));
        return s;
    }

    private RequestSpecification authorizedApiSpec(AuthSession session) {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + session.getAccessToken())
                .build();
    }
}
