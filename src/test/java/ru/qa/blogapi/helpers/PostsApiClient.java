package ru.qa.blogapi.helpers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import ru.qa.blogapi.models.PostCreateRequest;

import static io.restassured.RestAssured.given;

public class PostsApiClient {

    private final RequestSpecification authorizedRequestSpec;

    public PostsApiClient(RequestSpecification authorizedRequestSpec) {
        this.authorizedRequestSpec = authorizedRequestSpec;
    }

    public Response create(PostCreateRequest request) {
        return given()
                .spec(authorizedRequestSpec)
                .body(request)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    public Integer createPublishedPost(String category) {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        PostCreateRequest body = new PostCreateRequest(
                "Post " + suffix,
                "Body of post " + suffix + " for REST-Assured tests",
                "Description " + suffix,
                category,
                false
        );
        return create(body).jsonPath().getInt("post.id");
    }

    public Integer createDraftPost(String category) {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(6);
        PostCreateRequest body = new PostCreateRequest(
                "Draft " + suffix,
                "Draft body " + suffix,
                "Draft description " + suffix,
                category,
                true
        );
        return create(body).jsonPath().getInt("post.id");
    }
}
