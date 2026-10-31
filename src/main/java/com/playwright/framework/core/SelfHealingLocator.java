package com.playwright.framework.core;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.playwright.framework.telemetry.AllureReportInterceptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SelfHealingLocator implements InvocationHandler {

    private final Page page;
    private Locator delegate;
    private String currentSelector;

    private SelfHealingLocator(Page page, Locator delegate, String originalSelector) {
        this.page = page;
        this.delegate = delegate;
        this.currentSelector = originalSelector;
    }

    public static Locator create(Page page, Locator delegate, String originalSelector) {
        return (Locator) Proxy.newProxyInstance(
                Locator.class.getClassLoader(),
                new Class<?>[]{Locator.class},
                new SelfHealingLocator(page, delegate, originalSelector)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean isAction = isActionMethod(method.getName());

        // Fast check: if primary locator does not exist on DOM, trigger self-healing immediately
        if (isAction && delegate.count() == 0) {
            return attemptSelfHealing(method, args);
        }

        try {
            return method.invoke(delegate, args);
        } catch (Throwable targetException) {
            if (!isAction) {
                throw targetException.getCause() != null ? targetException.getCause() : targetException;
            }
            return attemptSelfHealing(method, args);
        }
    }

    private Object attemptSelfHealing(Method method, Object[] args) throws Throwable {
        System.out.printf("[Self-Healing] Primary locator failed: '%s'. Starting healing engine...%n", currentSelector);

        // Execute deterministic FallbackResolver (<200ms)
        String healedSelector = FallbackResolver.resolve(page, currentSelector);

        if (healedSelector != null) {
            AllureReportInterceptor.logHealedEvent(currentSelector, healedSelector);

            // Re-bind to healed selector
            this.currentSelector = healedSelector;
            this.delegate = page.locator(healedSelector);

            // Re-run the intercepted action with healed locator
            return method.invoke(delegate, args);
        }

        throw new RuntimeException("Self-healing engine failed to resolve broken selector: " + currentSelector);
    }

    private boolean isActionMethod(String methodName) {
        return methodName.equals("click") ||
                methodName.equals("fill") ||
                methodName.equals("check") ||
                methodName.equals("uncheck") ||
                methodName.equals("dblclick") ||
                methodName.equals("press");
    }
}