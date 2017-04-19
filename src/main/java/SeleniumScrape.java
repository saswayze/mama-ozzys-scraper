import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;

/*** Created by samuel.swayze on 12/5/2016. ***/
public class SeleniumScrape {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\samuel.swayze\\IdeaProjects\\mama-ozzys-scraper\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        //WebDriver driver = new HtmlUnitDriver();
        System.out.println("Power On");
        driver.get("http://mamaozzystable.blogspot.com/");

        List<WebElement> toggles = driver.findElements(By.cssSelector("li.archivedate.collapsed a.toggle"));

        int j = 0;
        int h = 0;

        // TRY JavascriptExecutor TO "click" HIDDEN LINKS


        for (int i = 0; i < toggles.size(); i++) {
            if (toggles.get(i).isDisplayed()) {
                toggles.get(i).click();
                j++;
                System.out.println(j);
                Thread.sleep(1000);
            } else {
                h++;
            }

        }

        String pageSrc = driver.getPageSource();
        driver.quit();
        //return pageSrc;
    }
}
