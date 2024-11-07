package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PurchaseFormPage {
    public PurchaseFormPage() {
    }

    private static final SelenideElement heading = $("[class='heading heading_size_l heading_theme_alfa-on-white']")
            .shouldHave(Condition.text("Путешествие дня"));

    public void theJourneyOfTheDay() {
        heading.shouldBe(Condition.visible);
    }
}