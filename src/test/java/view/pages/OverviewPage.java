package view.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import controller.util.ParaBankUtils;

public class OverviewPage {
    private Page page;
    private Locator accountsTable;
    private Locator accountLinks;

    public OverviewPage(Page page) {
        this.page = page;
        this.accountsTable = page.locator("#accountTable");
        this.accountLinks = page.getByRole( AriaRole.LINK, new Page.GetByRoleOptions().setName("Accounts Overview") );
    }

    public void navigate() {
        page.navigate(ParaBankUtils.OVERVIEW_URL);
    }

    public boolean pageIsVisible(){
        return this.page.url().equals(ParaBankUtils.OVERVIEW_URL);
    }
}
