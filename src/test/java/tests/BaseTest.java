package tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserType;
import com.playwright.framework.core.SelfHealingPage;
import com.playwright.framework.pages.HomePage;
import com.playwright.framework.pages.OverviewPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        // Create raw page
        Page rawPage = browser.newPage();

        // Wrap page with the SelfHealingPage dynamic proxy
        this.page = SelfHealingPage.create(rawPage);
    }

    @AfterMethod
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    public Page getPage() {
        return page;
    }

    protected HomePage homePage() {
        return new HomePage(getPage());
    }

    protected OverviewPage overviewPage() {
        return new OverviewPage(getPage());
    }
}