package com.playwright.framework.core;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SelfHealingPage implements InvocationHandler {

    private final Page delegate;

    private SelfHealingPage(Page page) {
        this.delegate = page;
    }

    public static Page create(Page page) {
        return (Page) Proxy.newProxyInstance(
                Page.class.getClassLoader(),
                new Class<?>[]{Page.class},
                new SelfHealingPage(page)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(delegate, args);

        if ("locator".equals(method.getName()) && result instanceof Locator) {
            String originalSelector = (String) args[0];
            return SelfHealingLocator.create(delegate, (Locator) result, originalSelector);
        }

        return result;
    }
}