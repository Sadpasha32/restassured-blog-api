package ru.qa.blogapi.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class CreatePostPage {

    // Vuetify рендерит label рядом с input — берём input через xpath по тексту лейбла
    private final SelenideElement titleField =
            $x("//label[normalize-space()='Заголовок *']/ancestor::*[contains(@class,'v-input')][1]//input");
    private final SelenideElement descriptionField =
            $x("//label[normalize-space()='Краткое описание *']/ancestor::*[contains(@class,'v-input')][1]//input");
    private final SelenideElement bodyField =
            $x("//label[normalize-space()='Текст поста *']/ancestor::*[contains(@class,'v-input')][1]//textarea");
    // у Vuetify v-select клик надо делать по контролу .v-field, а не по скрытому combobox-input
    private final SelenideElement categoryControl =
            $x("//label[normalize-space()='Категория *']/ancestor::*[contains(@class,'v-input')][1]//div[contains(@class,'v-field')]");

    private final SelenideElement heading = $x("//h1[contains(.,'Создать новый пост')]");
    private final SelenideElement publishButton =
            $x("//button[@type='submit'][.//span[contains(normalize-space(.),'Опубликовать пост')]]");

    public CreatePostPage openPage() {
        open("/create-post");
        heading.shouldBe(visible);
        return this;
    }

    public CreatePostPage fillCommonFields(String title, String description, String body) {
        titleField.setValue(title);
        descriptionField.setValue(description);
        bodyField.setValue(body);
        return this;
    }

    public CreatePostPage selectCategory(String visibleCategory) {
        categoryControl.click();
        $x("//div[contains(@class,'v-list-item')]//*[normalize-space()='" + visibleCategory + "']").click();
        return this;
    }

    public void publish() {
        publishButton.click();
    }
}
