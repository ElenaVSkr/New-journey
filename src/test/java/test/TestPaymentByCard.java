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

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPaymentByCard {
    private final DataHelper.CardInfo validDataWithCardApproved = DataHelper.getValidDataCard(DataHelper.getCardNumberSuccessfully());
    private final DataHelper.CardInfo validDataWithCardDeclined = DataHelper.getValidDataCard(DataHelper.getCardNumberDeclined());
    private final FormPage formPage = new FormPage();

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

    @Name("Проверка успешной покупки по карте c наличием записи в БД")
    @Test
    public void successfulPurchaseWithRecordInTheDatabase() {
        Database.deleteAllStringsForPaymentEntity();
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.successBuy();
        assertEquals("APPROVED", Database.checkStatus());

    }

    @Name("Проверка покупки со статусом Отклонено с наличием записи в БД")

    @Test
    public void purchaseWithTheRejectedStatusWithAnEntryInTheDatabase() {
        Database.deleteAllStringsForPaymentEntity();
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardDeclined.cardNumber,
                validDataWithCardDeclined.month,
                validDataWithCardDeclined.year,
                validDataWithCardDeclined.cardHolder,
                validDataWithCardDeclined.cvc
        );
        formPage.clickOrderButton();
        formPage.rejected();
        assertEquals("DECLINED", Database.checkStatus());

    }

    @Name("Ввод в поле Номер карты значения с одной цифрой с результатом Неверный формат")
    @Test
    void enteringSingleDigitInTheCardNumberField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                "1",
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Ввод в поле Номер карты шести цифр с результатом Неверный формат")
    @Test
    void enteringSixDigitsInTheCardNumberField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                "444444",
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Пустое поле Номер карты c результатом Неверный формат")
    @Test
    void emptyCardNumberField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                "",
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();

    }

    @Name("Ввод в поле Месяц несуществующего номера месяца с результатом Неверно указан срок действия карты")
    @Test
    void enteringNonExistentNumberInTheMonthField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                "31",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.errorTheValidityPeriodOfTheCardIsIncorrectlySpecified();
    }

    @Name("Проверка поля Месяц с вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheMonthField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                "0",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Пустое поле МЕСЯЦ c результатом Неверный формат")
    @Test
    void emptyMonthField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                "",
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Год c вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheYearField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                "0",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Год c вводом одной цифры и результатом Неверный формат")
    @Test
    void enteringTheNumberOneInTheYearField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                "1",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Год c истекшим годом и результатом Истек срок действия карты")
    @Test
    void enteringThePastYear() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                "15",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.errorTheCardExpired();
    }

    @Name("Пустое поле Год c результатом Неверный формат")
    @Test
    void emptyYearField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                "",
                validDataWithCardApproved.cardHolder,
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Владелец c вводом только фамилии и результатом Неверный формат")
    @Test
    void shouldNotificationAboutAnEmptyHolderFieldWillAppearBuyByCard() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                "Ivanov",
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Владелец c вводом кириллицы и результатом Неверный формат")
    @Test
    void enteringTheCyrillicAlphabetInTheOwnerField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                "Иванов Иван",
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Владелец c вводом символов и результатом Неверный формат")
    @Test
    void enteringCharactersInTheOwnerField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                DataHelper.getInvalidSymbols(),
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля Владелец c вводом цифр и результатом Неверный формат")
    @Test
    void enteringNumbersInTheOwnerField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                "12345",
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка пустого поля Владелец и результатом Поле обязательно для заполнения")
    @Test
    void checkingTheEmptyOwnerField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                "",
                validDataWithCardApproved.cvc
        );
        formPage.clickOrderButton();
        formPage.errorTheFieldIsRequired();
    }

    @Name("Проверка поля CVC c вводом ноля и результатом Неверный формат")
    @Test
    void enteringZeroInTheCvcField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                "0"
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка поля CVC c одной цифрой и результатом Неверный формат")
    @Test
    void enteringSingleDigitInTheCvcField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                "1"
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }

    @Name("Проверка пустого поля CVC и результатом Неверный формат")
    @Test
    void checkingAnEmptyCvcField() {
        formPage.purchaseByCard();
        formPage.inputDataEntry(
                validDataWithCardApproved.cardNumber,
                validDataWithCardApproved.month,
                validDataWithCardApproved.year,
                validDataWithCardApproved.cardHolder,
                ""
        );
        formPage.clickOrderButton();
        formPage.invalidFormat();
    }
}