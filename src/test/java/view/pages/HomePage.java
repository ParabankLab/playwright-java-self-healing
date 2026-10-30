package view.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import controller.util.ParaBankUtils;

public class HomePage {
    private Page page;
    private Locator userNameInput;
    private Locator passwordInput;
    private Locator loginButton;
    private Locator logoutLink;

    public HomePage(Page page) {
        this.page = page;
        this.userNameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.locator("input[type='submit'][value='Log In']");
        this.logoutLink = page.getByRole( AriaRole.LINK, new Page.GetByRoleOptions().setName("Log Out") );
    }

    public void navigate(){
        page.navigate(ParaBankUtils.INDEX_URL);
    }

    public void login(String userName, String passWord){
        userNameInput.fill(userName);
        passwordInput.fill(passWord);
        loginButton.click();
    }

    public void clickOnRegisterLink() {
        page.getByRole( AriaRole.LINK, new Page.GetByRoleOptions().setName("Register") ).click();
    }

    public void clickOnForgotLoginInfoLink() {
        page.getByRole( AriaRole.LINK, new Page.GetByRoleOptions().setName("Forgot Login Info") ).click();
    }

    public Page getPage(){
        return this.page;
    }

    public void clickOnLogoutLink() {
        this.logoutLink.click();
    }

    public boolean pageIsVisible(){
       return this.page.url().equals(ParaBankUtils.INDEX_URL);
    }

}
