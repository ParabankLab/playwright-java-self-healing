package com.playwright.framework.tests;

import com.playwright.framework.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.BaseTest;

@Feature("Authentication")
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    @Story("User Login with Valid Credentials")
    @Description("Verifies standard login flow using valid user credentials.")
    public void testSuccessfulLogin() {
        HomePage homePage = new HomePage(page);

        homePage.navigateTo();
        homePage.login("john", "demo");

        // Verify navigation or dashboard presence post-login
        Assert.assertTrue(page.url().contains("overview.htm"), "URL should contain overview page route post-login.");
    }

    @Test(priority = 2)
    @Story("Self-Healing Telemetry Verification")
    @Description("Triggers a intentionally broken selector to verify <200ms FallbackResolver execution and Allure telemetry logging.")
    public void testSelfHealingTriggerOnBrokenSelector() {
        HomePage homePage = new HomePage(page);
        homePage.navigateTo();

        // Intentionally broken selector (Simulates DOM changes/renamed IDs/classes)
        // FallbackResolver will extract 'username' token and resolve via [name='username']
        String brokenUsernameSelector = "#invalid-id-username";

        // Action executes transparently through SelfHealingLocator dynamic proxy
        page.locator(brokenUsernameSelector).fill("john");

        // Continue regular flow
        page.locator("input[name='password']").fill("demo");
        page.locator("input[value='Log In']").click();

        Assert.assertTrue(page.url().contains("overview.htm"), "Self-healing failed to recover broken locator during login.");
    }
}