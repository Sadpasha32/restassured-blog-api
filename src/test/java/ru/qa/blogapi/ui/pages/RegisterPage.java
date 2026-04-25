package ru.qa.blogapi.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class RegisterPage {

    private final SelenideElement heading = $x("//h1[contains(.,'Регистрация')]");
    private final SelenideElement emailField = $("input[type=email]");
    private final SelenideElement passwordField = $("input[type=password]");
    private final SelenideElement submitButton =
            $x("//button[@type='submit'][.//span[contains(normalize-space(.),'Зарегистрироваться')]]");

    public RegisterPage openPage() {
        open("/register");
        heading.shouldBe(visible);
        emailField.shouldBe(visible);
        return this;
    }

    public RegisterPage fillRequired(String email, String password) {
        emailField.setValue(email);
        passwordField.setValue(password);
        return this;
    }

    public void submit() {
        submitButton.click();
    }
}
