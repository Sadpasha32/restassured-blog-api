package ru.qa.blogapi.tests;

import org.apache.commons.lang3.RandomStringUtils;
import ru.qa.blogapi.models.UserRegistrationRequest;

final class TestUserBuilder {

    private TestUserBuilder() {
    }

    static UserRegistrationRequest valid() {
        String suffix = RandomStringUtils.secure().nextAlphanumeric(8).toLowerCase();
        return new UserRegistrationRequest(
                "student_" + suffix + "@example.com",
                "SecurePass123!",
                "Student" + suffix,
                "Api",
                "student_" + suffix,
                "1990-01-02",
                "+7987" + RandomStringUtils.secure().nextNumeric(7)
        );
    }
}
