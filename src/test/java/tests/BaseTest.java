package tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserType;
import com.playwright.framework.core.SelfHealingPage;
import com.playwright.framework.pages.HomePage;
import com.playwright.framework.pages.OverviewPage;
import com.playwright.framework.telemetry.AllureReportInterceptor;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class})
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
    public void tearDown(ITestResult result) {
        if (AllureReportInterceptor.wasHealed()) {
            // Detect CI environments (GitHub Actions, GitLab CI, Jenkins, Azure DevOps all set 'CI=true')
            boolean isCI = Boolean.parseBoolean(System.getenv("CI"))
                    || Boolean.parseBoolean(System.getProperty("CI", "false"));

            if (isCI) {
                // Hard fail the test in CI/CD to prevent merging broken selectors
                throw new AssertionError("🚨 [Strict Governance Failure] Test passed via Self-Healing fallback. " +
                        "Production locators must never rely on runtime resolutions in CI/CD. " +
                        "Please update the faulty locator immediately.");
            } else {
                // Local developer experience: record broken status in Allure, but don't break local flow
                Allure.getLifecycle().updateTestCase(testResult -> {
                    testResult.setStatus(Status.BROKEN);
                    testResult.setStatusDetails(new io.qameta.allure.model.StatusDetails()
                            .setMessage("⚠️ Local Self-Healing Warning: Faulty locator replaced")
                    );
                });
            }
        }

        // Reset thread-local state
        AllureReportInterceptor.reset();

        // Clean up Playwright resources
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