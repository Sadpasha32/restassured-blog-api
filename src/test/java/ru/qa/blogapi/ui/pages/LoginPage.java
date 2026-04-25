package ru.qa.blogapi.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement heading = $x("//h1[contains(.,'Вход в аккаунт')]");
    private final SelenideElement emailField = $("input[type=email]");
    private final SelenideElement passwordField = $("input[type=password]");
    // именно submit-button с подписью "Войти" — в шапке висит ещё <a>Войти, его игнорируем
    private final SelenideElement submitButton =
            $x("//button[@type='submit'][.//span[contains(normalize-space(.),'Войти')]]");

    public LoginPage openPage() {
        open("/login");
        heading.shouldBe(visible);
        emailField.shouldBe(visible);
        return this;
    }

    public LoginPage fillCredentials(String email, String password) {
        emailField.setValue(email);
        passwordField.setValue(password);
        return this;
    }

    public void submit() {
        submitButton.click();
    }
}
