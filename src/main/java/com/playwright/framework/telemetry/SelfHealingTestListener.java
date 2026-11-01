package com.playwright.framework.telemetry;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class SelfHealingTestListener implements IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod() && AllureReportInterceptor.wasHealed()) {
            // Force Allure status update as the absolute final step
            Allure.getLifecycle().updateTestCase(result -> {
                result.setStatus(Status.BROKEN);
                result.setStatusDetails(new StatusDetails()
                        .setMessage("⚠️ Test passed via Self-Healing: Faulty locator replaced")
                        .setTrace("SelfHealingTelemetry: Automatically recovered by FallbackResolver (<200ms)")
                );
            });
        }
        if (method.isTestMethod()) {
            AllureReportInterceptor.reset();
        }
    }
}