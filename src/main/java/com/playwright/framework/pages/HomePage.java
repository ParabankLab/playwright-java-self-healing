package com.playwright.framework.pages;

import com.microsoft.playwright.Page;
import com.playwright.framework.util.ParaBankUtils;

public class HomePage {
    private final Page page;

    // Locators
    private final String usernameInput = "input[name='username']";
    private final String passwordInput = "input[name='password']";
    private final String loginButton = "input[value='Log In']";

    public HomePage(Page page) {
        this.page = page;
    }

    public void navigateTo() {
        page.navigate(ParaBankUtils.INDEX_URL);
    }

    public void login(String username, String password) {
        page.locator(usernameInput).fill(username);
        page.locator(passwordInput).fill(password);
        page.locator(loginButton).click();
    }

    public Page getPage(){
        return page;
    }


}