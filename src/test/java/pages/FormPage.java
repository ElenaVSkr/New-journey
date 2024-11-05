package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FormPage {
    public FormPage() {

    }

    private static final SelenideElement cardNumber = $("[placeholder='0000 0000 0000 0000']");

    private static final SelenideElement month = $$(".input__control")
            .findBy(Condition.attribute("placeholder", "08"));

    private static final SelenideElement year = $$(".input__control")
            .findBy(Condition.attribute("placeholder", "22"));

    private static final SelenideElement name = $$("[class='input__control']").
            findBy(Condition.attribute("placeholder", ""));

    private static final SelenideElement cvc = $$(".input__control")
            .findBy(Condition.attribute("placeholder", "999"));

    private static final SelenideElement orderButton = $$("[class='button__content']")
            .findBy(Condition.text("Продолжить"));

    private static final ElementsCollection notifications = $$("[class='input__sub']");

    private static final ElementsCollection cardHolder = $$("[class='input__control']");

    private static final SelenideElement successBuy = $$("[class='notification__content']")
            .findBy(Condition.exactText("Операция одобрена Банком."));

    private static final SelenideElement rejected = $$("[class='notification__content']")
            .findBy(Condition.exactText("Ошибка! Банк отказал в проведении операции."));


    public static void InputDataEntry(String card, String months, String years, String cardHolders, String CVC) {
        cardNumber.setValue(card);
        month.setValue(months);
        year.setValue(years);
        cardHolder.get(3).setValue(cardHolders);
        cvc.setValue(CVC);
    }

    public void clickOrderButton() {
        orderButton.click();
    }


    public void successBuy() {
        successBuy.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void rejected() {
        rejected.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void checkEmptyFieldErrorForCheckingOneField() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверный формат"));
    }

    public void checkEmptyFieldMothError() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверный формат"));
    }

    public void checkEmptyFieldYearError() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверный формат"));
    }

    public void checkEmptyFieldHolderError() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверный формат"));
    }

    public void checkEmptyFieldCvcError() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверный формат"));
    }

    public void checkFieldMothErrorWithInvalidValue() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Неверно указан срок действия карты"));
    }

    public void checkFieldYearErrorWithInvalidValue() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Истёк срок действия карты"));
    }

    public void checkEmptyFieldErrorNotification() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    public void errorInTheEmptyOwnerField() {
        notifications.get(0).shouldBe(Condition.visible).shouldHave(Condition.text("Поле обязательно для заполнения"));
    }
}
