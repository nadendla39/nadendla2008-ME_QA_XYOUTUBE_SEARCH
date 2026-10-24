package demo.wrappers;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wrappers {
    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void clickSidebarOption(String optionName) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // Expand guide sidebar if collapsed
            List<WebElement> guideButtons = driver.findElements(By.xpath("//button[@id='button' and @aria-label='Guide']"));
            if (!guideButtons.isEmpty() && guideButtons.get(0).isDisplayed()) {
                guideButtons.get(0).click();
                Thread.sleep(500);
            }

            By optionLocator = By.xpath("//ytd-guide-section-renderer//a[contains(., '" + optionName + "') or contains(@title, '" + optionName + "')] | " +
                                        "//a[@id='endpoint' and contains(., '" + optionName + "')]");
            WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", elem);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", elem);
        } catch (Exception e) {
            // Fallback direct URL navigation
            if (optionName.equalsIgnoreCase("Movies") || optionName.equalsIgnoreCase("Films")) {
                driver.get("https://www.youtube.com/feed/storefront");
            } else if (optionName.equalsIgnoreCase("Music")) {
                driver.get("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
            } else if (optionName.equalsIgnoreCase("News")) {
                driver.get("https://www.youtube.com/channel/UCYfdidRxbB8Qhf0Nx7ioOYw");
            }
        }
    }

    public void searchItem(String query) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@id='search' or @name='search_query']")));
        searchBox.sendKeys(Keys.CONTROL + "a");
        searchBox.sendKeys(Keys.BACK_SPACE);
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);
    }

    public void scrollHorizontallyRight(WebElement container) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 10; i++) {
            List<WebElement> nextBtns = container.findElements(By.xpath(
                ".//button[@aria-label='Next' or @aria-label='Right'] | .//div[@id='right-arrow']//button"));
            if (!nextBtns.isEmpty() && nextBtns.get(0).isDisplayed()) {
                try {
                    nextBtns.get(0).click();
                    Thread.sleep(600);
                } catch (Exception e) {
                    break;
                }
            } else {
                js.executeScript("arguments[0].scrollLeft += 600;", container);
                break;
            }
        }
    }

    public long convertViewsToNumeric(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            String clean = text.toLowerCase().replace("views", "").replace("view", "").trim();
            if (clean.contains("cr") || clean.contains("crore")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (long) (val * 10000000);
            } else if (clean.contains("l") || clean.contains("lakh")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (long) (val * 100000);
            } else if (clean.contains("m")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (long) (val * 1000000);
            } else if (clean.contains("k")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (long) (val * 1000);
            } else {
                String numeric = clean.replaceAll("[^0-9]", "");
                return numeric.isEmpty() ? 0 : Long.parseLong(numeric);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public int convertLikesToNumeric(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try {
            String clean = text.toLowerCase().trim();
            if (clean.contains("k")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (int) (val * 1000);
            } else if (clean.contains("m")) {
                double val = Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                return (int) (val * 1000000);
            } else {
                String digits = clean.replaceAll("[^0-9]", "");
                return digits.isEmpty() ? 0 : Integer.parseInt(digits);
            }
        } catch (Exception e) {
            return 0;
        }
    }
}