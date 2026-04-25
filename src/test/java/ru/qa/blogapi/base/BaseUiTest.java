package ru.qa.blogapi.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.qa.blogapi.config.TestConfig;

public abstract class BaseUiTest {

    @BeforeAll
    static void configureSelenide() {
        Configuration.baseUrl = TestConfig.baseUrl();
        Configuration.browser = System.getProperty("selenide.browser", "chrome");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "true"));
        Configuration.timeout = 8000;
        Configuration.pageLoadTimeout = 30000;
        Configuration.browserSize = "1366x900";
        Configuration.fastSetValue = true;
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }
}
