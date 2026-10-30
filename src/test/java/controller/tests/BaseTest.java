package controller.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import view.pages.HomePage;
import view.pages.OverviewPage;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(isHeadless)
        );

        context = browser.newContext();
        page = context.newPage();
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

    @AfterMethod
    public void tearDown() {
        if (page != null) {
            try { page.close(); } catch (Exception ignored) {}
        }
        if (context != null) {
            try { context.close(); } catch (Exception ignored) {}
        }
        if (browser != null) {
            try { browser.close(); } catch (Exception ignored) {}
        }
        if (playwright != null) {
            try { playwright.close(); } catch (Exception ignored) {}
        }
    }
}