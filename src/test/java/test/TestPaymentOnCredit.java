package test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import jdk.jfr.Name;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.FormPage;
import SQL.Database;
import pages.PurchaseFormPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pages.FormPage.InputDataEntry;

public class TestPaymentOnCredit {
    private final DataHelper.CardInfo validDataWithCardApproved = DataHelper.getValidDataCard(DataHelper.getCardNumberSuccessfully());
    private final DataHelper.CardInfo validDataWithCardDeclined = DataHelper.getValidDataCard(DataHelper.getCardNumberDeclined());
    private final FormPage FormPage = new FormPage();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    public void setUp() {
        Configuration.headless = true;
        open("http://localhost:8080/");
    }

    @Name("Проверка успешной покупки в кредит c наличием записи в БД")
    @Test
    public void verificationOfSuccessfulPurchaseOnCredit() {
        Database.deleteAllStringsForCreditRequestEntity();
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.successBuy();
        assertEquals("APPROVED", Database.checkStatusCredit());
    }

    @Name("Проверка отказа покупки в кредит c наличием записи в БД")
    @Test
    public void checkingTheRefusalOfPurchaseOnCredit() {
        Database.deleteAllStringsForCreditRequestEntity();
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardDeclined.cardNumber,
                validDataWithCardDeclined.moth,
                validDataWithCardDeclined.year,
                validDataWithCardDeclined.cardHolder,
                validDataWithCardDeclined.cvc
        );
        FormPage.clickOrderButton();
        FormPage.rejected();
        assertEquals("DECLINED", Database.checkStatusCredit());
    }

    @Name("Ввод в поле Номер карты значения с одной цифрой с результатом Неверный формат")
    @Test
    void enteringSingleDigitInTheCardNumberField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                "1",
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldErrorForCheckingOneField();
    }

    @Name("Ввод в поле Номер карты шести цифр с результатом Неверный формат")
    @Test
    void enteringSixDigitsInTheCardNumberField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                "444444",
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldErrorForCheckingOneField();
    }

    @Name("Пустое поле Номер карты c результатом Неверный формат")
    @Test
    void EmptyCardNumberField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                "",
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldErrorForCheckingOneField();

    }

    @Name("Ввод в поле Месяц несуществующего номера месяца с результатом Неверно указан срок действия карты")
    @Test
    void enteringNonExistentNumberInTheMonthField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                "31",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkFieldMothErrorWithInvalidValue();
    }

    @Name("Проверка поля Месяц с вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheMonthField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                "0",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldMothError();
    }

    @Name("Пустое поле МЕСЯЦ c результатом Неверный формат")
    @Test
    void emptyMonthField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                "",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldMothError();
    }

    @Name("Проверка поля Год c вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheYearField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                "0",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldYearError();
    }

    @Name("Проверка поля Год c вводом одной цифры и результатом Неверный формат")
    @Test
    void enteringTheNumberOneInTheYearField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                "1",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldYearError();
    }

    @Name("Проверка поля Год c истекшим годом и результатом Истек срок действия карты")
    @Test
    void enteringThePastYear() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                "15",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkFieldYearErrorWithInvalidValue();
    }

    @Name("Пустое поле Год c результатом Неверный формат")
    @Test
    void emptyYearField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                "",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldYearError();
    }

    @Name("Проверка поля Владелец c вводом только фамилии и результатом Неверный формат")
    @Test
    void shouldNotificationAboutAnEmptyHolderFieldWillAppearBuyByCard() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                "Ivanov",
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldHolderError();
    }

    @Name("Проверка поля Владелец c вводом кириллицы и результатом Неверный формат")
    @Test
    void enteringTheCyrillicAlphabetInTheOwnerField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                "Иванов Иван",
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldHolderError();
    }

    @Name("Проверка поля Владелец c вводом символов и результатом Неверный формат")
    @Test
    void enteringCharactersInTheOwnerField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                DataHelper.getInvalidSymbols(),
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldHolderError();
    }

    @Name("Проверка поля Владелец c вводом цифр и результатом Неверный формат")
    @Test
    void enteringNumbersInTheOwnerField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                "12345",
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldHolderError();
    }

    @Name("Проверка пустого поля Владелец и результатом Поле обязателно для заполнения")
    @Test
    void checkingTheEmptyOwnerField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                "",
                validDataWithCardApproved.cvc
        );
        FormPage.clickOrderButton();
        FormPage.errorInTheEmptyOwnerField();
    }

    @Name("Проверка поля CVC c вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheCvcField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                "0"
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldCvcError();
    }

    @Name("Проверка поля CVC c одной цифрой и результатом Неверный формат")
    @Test
    void enteringSingleDigitInTheCvcField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                "1"
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldCvcError();
    }

    @Name("Проверка пустого поля CVC и результатом Неверный формат")
    @Test
    void checkingAnEmptyCvcField() {
        PurchaseFormPage.purchaseByCredit();
        InputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.moth,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                ""
        );
        FormPage.clickOrderButton();
        FormPage.checkEmptyFieldCvcError();
    }
}
