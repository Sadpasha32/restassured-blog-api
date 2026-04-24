package ru.qa.blogapi.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qa.blogapi.base.BaseAuthorizedApiTest;

class BlogApiHomeworkTest extends BaseAuthorizedApiTest {

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/auth/register -> should register user with valid required fields")
    void shouldRegisterUserWithValidRequiredFields() {
        // Что проверяем:
        // Успешную регистрацию нового пользователя с валидными обязательными полями.
        //
        // Что подготовить:
        // - уникальный email
        // - валидный password
        // - при желании firstName / lastName / nickname / birthDate / phone
        //
        // Что сделать:
        // 1. Отправить POST /api/auth/register
        // 2. Передать JSON body с валидными данными
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.message = "User registered successfully"
        // - body.user.id не null
        // - body.user.email совпадает с отправленным email
        //
        // На что обратить внимание:
        // - email должен быть уникальным
        // - phone и nickname тоже могут быть уникальными, если backend так валидирует
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/auth/register -> should return validation error for invalid email")
    void shouldReturnValidationErrorForInvalidEmailOnRegistration() {
        // Что проверяем:
        // Что регистрация не проходит с невалидным email.
        //
        // Что подготовить:
        // - email в неверном формате, например "invalid-email"
        // - валидный password
        //
        // Что сделать:
        // 1. Отправить POST /api/auth/register с невалидным email
        //
        // Что проверить:
        // - status code = 400
        // - body.error.code = 400
        // - body.error.message содержит информацию о невалидном email
        //
        // Дополнительно:
        // - если backend возвращает details, можно проверить body.error.details.email
        //
        // На что обратить внимание:
        // - не надо ожидать 401, потому что это не ошибка авторизации, а ошибка валидации данных
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/login -> should login with valid credentials")
    void shouldLoginWithValidCredentials() {
        // Что проверяем:
        // Что можно залогиниться под существующим пользователем.
        //
        // Что подготовить:
        // 1. Сначала зарегистрировать нового пользователя
        // 2. Затем использовать его email и password для логина
        //
        // Что сделать:
        // 1. POST /api/login
        // 2. Передать:
        //    - username = email
        //    - password = пароль зарегистрированного пользователя
        //
        // Что проверить:
        // - status code = 200
        // - body.token не null
        // - body.refresh_token не null
        //
        // На что обратить внимание:
        // - в login используется поле username, а не email
        // - лучше регистрировать нового пользователя прямо в тесте, чтобы тест был независимым
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/login -> should return unauthorized for wrong password")
    void shouldReturnUnauthorizedForWrongPassword() {
        // Что проверяем:
        // Что логин не проходит при неверном пароле.
        //
        // Что подготовить:
        // 1. Зарегистрировать пользователя
        // 2. В login передать корректный email, но неверный password
        //
        // Что сделать:
        // 1. POST /api/login
        //
        // Что проверить:
        // - status code = 401
        // - body.error.code = 401
        // - body.error.message сообщает об ошибке авторизации
        //
        // На что обратить внимание:
        // - если backend возвращает другой текст ошибки, лучше проверять не весь message полностью,
        //   а более устойчивую часть или просто наличие error
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/token/refresh -> should refresh access token by refresh token")
    void shouldRefreshAccessToken() {
        // Что проверяем:
        // Что по refresh_token можно получить новый access token.
        //
        // Что подготовить:
        // 1. Зарегистрировать пользователя
        // 2. Залогиниться и получить refresh_token
        //
        // Что сделать:
        // 1. POST /api/token/refresh
        // 2. Передать refresh_token в body
        //
        // Что проверить:
        // - status code = 200
        // - body.token не null
        // - body.refresh_token не null
        //
        // Дополнительно:
        // - можно проверить, что новый token не пустой
        // - можно сравнить старый refresh token и новый, если backend действительно его ротирует
        //
        // На что обратить внимание:
        // - не путать access token и refresh token
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/profile -> should return current user profile for authorized user")
    void shouldReturnCurrentUserProfile() {
        // Что проверяем:
        // Что авторизованный пользователь может получить свой текущий профиль.
        //
        // Что подготовить:
        // Ничего отдельно не нужно:
        // BaseAuthorizedApiTest уже создает авторизованную сессию и authorizedRequestSpec
        //
        // Что сделать:
        // 1. GET /api/profile
        // 2. Использовать authorizedRequestSpec
        //
        // Что проверить:
        // - status code = 200
        // - body.user не null
        //
        // Важно:
        // - сначала лучше посмотреть реальный контракт /api/profile
        // - не надо автоматически ожидать те же поля, что в /api/profile/{id}
        // - если email там реально возвращается, можно проверить его
        // - если не возвращается, ограничиться user/id/наличием объекта
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("PUT /api/profile -> should update current user profile")
    void shouldUpdateCurrentUserProfile() {
        // Что проверяем:
        // Что авторизованный пользователь может обновить свой профиль.
        //
        // Что подготовить:
        // - новый firstName
        // - новый lastName
        // - новый nickname
        // - при необходимости новый phone
        //
        // Что сделать:
        // 1. PUT /api/profile
        // 2. Передать JSON body с полями для обновления
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.user.firstName совпадает с новым значением
        // - body.user.lastName совпадает с новым значением
        //
        // Дополнительно:
        // - после PUT можно сделать GET /api/profile/{id}
        //   и отдельно проверить, что изменения действительно сохранились
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts -> should return paginated list of posts")
    void shouldReturnPaginatedPostsList() {
        // Что проверяем:
        // Что список постов возвращается в пагинированном виде.
        //
        // Что сделать:
        // 1. GET /api/posts?page=1&limit=10
        //
        // Что проверить:
        // - status code = 200
        // - body.items не null
        // - body.totalItems не null
        // - body.itemsPerPage = 10
        // - body.page = 1
        // - body.pages не null
        //
        // Дополнительно:
        // - можно проверить, что размер items <= limit
        //
        // На что обратить внимание:
        // - список может быть пустым, если в системе нет постов
        // - для стабильности можно сначала создать 1-2 поста
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts -> should filter posts by category")
    void shouldFilterPostsByCategory() {
        // Что проверяем:
        // Что posts можно отфильтровать по category.
        //
        // Что подготовить:
        // 1. Создать хотя бы один пост с category = technology
        // 2. Желательно создать еще один пост с другой категорией
        //
        // Что сделать:
        // 1. GET /api/posts?category=technology
        //
        // Что проверить:
        // - status code = 200
        // - body.items не null
        // - у каждого возвращенного элемента category = technology
        //
        // На что обратить внимание:
        // - если данных нет, фильтр нечего будет проверять
        // - лучше создать данные внутри теста или в его arrange части
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/posts -> should create published post")
    void shouldCreatePublishedPost() {
        // Что проверяем:
        // Создание обычного опубликованного поста.
        //
        // Что подготовить:
        // - title
        // - body
        // - description
        // - category
        // - isDraft = false
        //
        // Что сделать:
        // 1. POST /api/posts под авторизованным пользователем
        //
        // Что проверить:
        // - status code = 201
        // - body.status = "success"
        // - body.post.id не null
        // - body.post.title совпадает с отправленным
        // - body.post.isDraft = false
        // - body.post.status = "published" если backend реально это возвращает
        // - body.post.author.email совпадает с authSession.getEmail()
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/posts -> should create draft post")
    void shouldCreateDraftPost() {
        // Что проверяем:
        // Создание черновика.
        //
        // Что подготовить:
        // - валидный PostCreate body
        // - isDraft = true
        //
        // Что сделать:
        // 1. POST /api/posts
        //
        // Что проверить:
        // - status code = 201
        // - body.post.id не null
        // - body.post.isDraft = true
        // - body.post.status = "draft" если это поле реально приходит
        //
        // Дополнительно:
        // - потом можно проверить, что этот пост появляется в /api/posts/my?drafts=true
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts/my -> should return only current user posts")
    void shouldReturnOnlyCurrentUserPosts() {
        // Что проверяем:
        // Что /api/posts/my возвращает только посты текущего пользователя.
        //
        // Что подготовить:
        // 1. Под текущим authSession создать 1-2 поста
        //
        // Что сделать:
        // 1. GET /api/posts/my
        //
        // Что проверить:
        // - status code = 200
        // - body.items не null
        // - у каждого поста author.email = authSession.getEmail()
        //
        // На что обратить внимание:
        // - если в системе до этого уже есть другие посты, они здесь не должны вернуться
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts/feed -> should return posts from other users")
    void shouldReturnFeedPosts() {
        // Что проверяем:
        // Что feed возвращает посты других пользователей, а не текущего.
        //
        // Что подготовить:
        // 1. Создать пост от другого пользователя
        // 2. При необходимости иметь пост от текущего пользователя для сравнения
        //
        // Что сделать:
        // 1. GET /api/posts/feed
        //
        // Что проверить:
        // - status code = 200
        // - body.items не null
        // - у возвращенных постов author.email != authSession.getEmail()
        //
        // На что обратить внимание:
        // - для этого теста нужен второй пользователь
        // - это хороший кейс на setup нескольких учеток
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts/{id} -> should return single post by id")
    void shouldReturnSinglePostById() {
        // Что проверяем:
        // Что можно получить конкретный пост по id.
        //
        // Что подготовить:
        // 1. Создать пост
        // 2. Сохранить его id
        //
        // Что сделать:
        // 1. GET /api/posts/{id}
        //
        // Что проверить:
        // - status code = 200
        // - body.post.id совпадает с id созданного поста
        // - body.post.title совпадает с title созданного поста
        // - body.statistics присутствует, если backend реально его возвращает
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("PUT /api/posts/{id} -> should update existing post")
    void shouldUpdateExistingPost() {
        // Что проверяем:
        // Что автор поста может обновить свой существующий пост.
        //
        // Что подготовить:
        // 1. Создать пост
        // 2. Сохранить его id
        // 3. Подготовить новые поля, например новый title и description
        //
        // Что сделать:
        // 1. PUT /api/posts/{id}
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.post.id = id исходного поста
        // - body.post.title = новый title
        // - body.post.description = новое description
        //
        // Дополнительно:
        // - затем сделать GET /api/posts/{id} и перепроверить обновленные значения
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("DELETE /api/posts/{id} -> should delete post")
    void shouldDeletePost() {
        // Что проверяем:
        // Что пользователь может удалить свой пост.
        //
        // Что подготовить:
        // 1. Создать пост
        // 2. Сохранить его id
        //
        // Что сделать:
        // 1. DELETE /api/posts/{id}
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.message сообщает об успешном удалении
        //
        // Дополнительно:
        // - после удаления сделать GET /api/posts/{id}
        // - ожидать 404
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/posts/{id}/favorite -> should add post to favorites")
    void shouldAddPostToFavorites() {
        // Что проверяем:
        // Что пользователь может добавить пост в избранное.
        //
        // Что подготовить:
        // 1. Создать пост
        // 2. Взять его id
        //
        // Что сделать:
        // 1. POST /api/posts/{id}/favorite
        // 2. Передать body: { "isFavorite": true }
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.isFavorite = true
        //
        // Дополнительно:
        // - затем вызвать GET /api/posts/favorites
        // - убедиться, что id этого поста там есть
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/posts/favorites -> should return favorite posts")
    void shouldReturnFavoritePosts() {
        // Что проверяем:
        // Что список избранных постов возвращается корректно.
        //
        // Что подготовить:
        // 1. Создать пост
        // 2. Добавить его в favorites
        //
        // Что сделать:
        // 1. GET /api/posts/favorites
        //
        // Что проверить:
        // - status code = 200
        // - body.items не null
        // - в списке есть нужный post.id
        // - при наличии поля isFavorite можно проверить, что оно true
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/files/upload -> should upload image file for post")
    void shouldUploadImageFileForPost() {
        // Что проверяем:
        // Что можно загрузить файл изображения.
        //
        // Что подготовить:
        // - тестовый jpeg/png файл в resources
        //
        // Что сделать:
        // 1. POST /api/files/upload
        // 2. multipart/form-data
        // 3. Передать:
        //    - file
        //    - type = "post-image" или "avatar"
        //
        // Что проверить:
        // - status code = 200
        // - body.id не null
        // - body.url не null
        // - body.mimeType соответствует ожидаемому типу
        // - body.filename не null
        //
        // На что обратить внимание:
        // - это не JSON body, а multipart
        // - тут используется multiPart(...) в REST-Assured
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("GET /api/files/{id} -> should return uploaded file metadata")
    void shouldReturnUploadedFileMetadata() {
        // Что проверяем:
        // Что по id загруженного файла можно получить его метаданные.
        //
        // Что подготовить:
        // 1. Сначала загрузить файл через /api/files/upload
        // 2. Сохранить fileId
        //
        // Что сделать:
        // 1. GET /api/files/{id}
        //
        // Что проверить:
        // - status code = 200
        // - body.id = fileId
        // - body.url не null
        // - body.filename не null
        // - body.size не null
        // - body.mimeType не null
    }

    @Test
    @Disabled("Практика: реализовать самостоятельно")
    @DisplayName("POST /api/profile/report/{id} -> should create report for user")
    void shouldCreateUserReport() {
        // Что проверяем:
        // Что можно отправить жалобу на пользователя.
        //
        // Что подготовить:
        // 1. Создать второго пользователя
        // 2. Получить его userId
        //
        // Что сделать:
        // 1. POST /api/profile/report/{id}
        // 2. Передать body с descriptionReport
        //
        // Что проверить:
        // - status code = 200
        // - body.status = "success"
        // - body.message сообщает, что пользователь успешно зарепорчен
        //
        // Дополнительно:
        // - можно потом вызвать GET /api/profile/report/{id}
        //   и проверить, что count увеличился
    }
}