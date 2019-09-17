import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static java.lang.System.*;

public class LoginTest {
    private String URL = "http://ecsd003004cf.epam.com:32879";

    private Map<String, String> getCookies(){
        return given().auth().preemptive().basic("admin", "admin")
                .when().get("http://ecsd003004cf.epam.com:32879/login.jsp").getCookies();
    }

    @RepeatedTest(5)
    public void authorizeWithUi() {
        long startTime = currentTimeMillis();
        Configuration.baseUrl =  URL;
        Configuration.startMaximized = true;
        open("/");
        $(By.id("login-form-username")).setValue("admin");
        $(By.id("login-form-password")).setValue("admin");
        $("#login").click();
        $("#qaspace-board-webitem-link").click();
        $("#plan-link").click();
        $("div.tm-plan-view-title").shouldHave(text("Test Library"));
        long endTime = currentTimeMillis();
        out.println("That took " + (endTime - startTime) + " milliseconds");
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().quit();
    }

    @RepeatedTest(5)
    public void authorizeWithWdCookie() {
        long startTime = currentTimeMillis();
        Configuration.startMaximized = true;
        Configuration.baseUrl = URL;
        Map<String, String> cookies = getCookies();

        List<Cookie> cookiesList = new ArrayList<>();
        cookies.keySet().forEach(key -> cookiesList.add(new Cookie(key, cookies.get(key))));
        open("/favicon.png");
        WebDriver driver = getWebDriver();
        cookiesList.forEach(c -> driver.manage().addCookie(c));
        open("/secure/TestManagementAction.jspa?viewTab=Plan&testRunId=64&folderId=64");
        $("div.tm-plan-view-title").shouldHave(text("Test Library"));
        long endTime = currentTimeMillis();
        out.println("That took " + (endTime - startTime) + " milliseconds");
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().quit();
    }

    @RepeatedTest(5)
    public void authorizeWithJsCookie() {
        long startTime = currentTimeMillis();
        Configuration.startMaximized = true;
        Configuration.baseUrl = URL;
        open("/favicon.png");
//        open("/robots.txt");
        WebDriver driver = getWebDriver();
        driver.manage().deleteAllCookies();
        new Authorization().authorizeWithCookie();
        open("/secure/TestManagementAction.jspa?viewTab=Plan&testRunId=64&folderId=64");
        $("div.tm-plan-view-title").shouldHave(text("Test Library"));
        long endTime = currentTimeMillis();
        out.println("That took " + (endTime - startTime) + " milliseconds");
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().quit();
    }

    @Test
    public void authorizeWithLocalStorageCookie() {
        long startTime = currentTimeMillis();
        Configuration.startMaximized = true;
        Configuration.baseUrl = URL;
        open("/favicon.png");
//        open("/robots.txt");
        WebDriver driver = getWebDriver();
        driver.manage().deleteAllCookies();
        new Authorization().authorizeWithCookieLocalStorage();
        open("/secure/TestManagementAction.jspa?viewTab=Plan&testRunId=64&folderId=64");
        $("div.tm-plan-view-title").shouldHave(text("Test Library"));
        long endTime = currentTimeMillis();
        out.println("That took " + (endTime - startTime) + " milliseconds");
    }

    public class Authorization {
        public void authorizeWithCookie() {
            Map<String, String> cookies = getCookies();

            String JSESSIONID = cookies.get("JSESSIONID");
            String token = cookies.get("atlassian.xsrf.token");

            setItemInCookie("JSESSIONID", JSESSIONID);
            setItemInCookie("atlassian.xsrf.token", token);
        }

        private void setItemInCookie(final String item, final String value) {
            final String jsCode = String.format("document.cookie='%s=%s';", item, value);
            ((JavascriptExecutor) getWebDriver()).executeScript(jsCode);
        }


//        "#####################################################"


        public void authorizeWithCookieLocalStorage() {
            Map<String, String> cookies = getCookies();

            String JSESSIONID = cookies.get("JSESSIONID");
            String token = cookies.get("atlassian.xsrf.token");

            setItemInLocalStorage("JSESSIONID", JSESSIONID);
            setItemInLocalStorage("atlassian.xsrf.token", token);
        }

        private void setItemInLocalStorage(final String item, final String value) {
            final String jsCode = String.format("window.localStorage.setItem('%s','%s');", item, value);
            ((JavascriptExecutor) getWebDriver()).executeScript(jsCode);

        }
    }
}

