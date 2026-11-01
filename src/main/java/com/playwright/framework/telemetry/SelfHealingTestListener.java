package com.playwright.framework.telemetry;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class SelfHealingTestListener implements IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod() && AllureReportInterceptor.wasHealed()) {
            // Force TestNG result to FAILURE so Allure renders it Red
            testResult.setStatus(ITestResult.FAILURE);

            String failureMessage = String.format(
                    "🚨 [Self-Healing Governance Failure] Test passed functionally via fallback, but failed governance rules.%n" +
                            "  • Failed Locator (Broken): %s%n" +
                            "  • Correct Locator (Healed): %s",
                    AllureReportInterceptor.getOriginalSelector(),
                    AllureReportInterceptor.getHealedSelector()
            );

            testResult.setThrowable(new AssertionError(failureMessage));
        }

        if (method.isTestMethod()) {
            AllureReportInterceptor.reset();
        }
    }
}