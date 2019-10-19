package de.scraping.scrapers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.scraping.config.CONSTANTS_ANALYTICA;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnalyticaScraper implements Scraper {

    private Config config;
    private WebDriver driver;
    private String url;
    private String pagingTextSubmits;
    private String inputField;
    private String attributeName;
    private String attributeValue;
    private String srField;
    private String exhibitorArea;
    private String addressField;
    private String nextButton;

    public AnalyticaScraper() {
        loadConfig();
        initDriver();
        initElements();
    }

    private void loadConfig() {
        config = ConfigFactory.load();
    }


    private void initDriver() {
        Objects.requireNonNull(config, () -> "Configuration is not available");
        System.setProperty("webdriver.chrome.driver", config.getString("driver.chrome"));
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    private void initElements() {
        Objects.requireNonNull(config, () -> "Configuration is not available");
        this.url = config.getString("analytica.URL_EXHIBITORS");
        this.pagingTextSubmits = config.getString("analytica.PAGING_NUMBER");
        this.inputField = config.getString("html.input");
        this.attributeName = config.getString("html.name");
        this.attributeValue = config.getString("html.value");
        this.srField = config.getString("analytica.SR_FIELD");
        this.exhibitorArea = config.getString("analytica.EXHIBITOR_AREA");
        this.addressField = config.getString("analytica.ADDRESS_FIELD");
        this.nextButton = config.getString("analytica.NEXT_BUTTON");
    }


    @Override
    public List<String> getCSV() {

        int pagesCount = getPagesCount();

        List<String> allExhibitors = new ArrayList<>();
        try {
            allExhibitors = getAllExhibitors(pagesCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return allExhibitors;
    }

    private List<String> getAllExhibitors(int pagesCount) throws InterruptedException {
        List<String> allExhibitors = new ArrayList<>();

        driver.get(url);

        int counter = pagesCount;
        do {
            List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

            for (int i = 0; i < elements.size(); i++) {

                List<WebElement> webElements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

                WebElement exhibitor = webElements.get(i);
                String exhibitorName = makeStringCSVConform(exhibitor.getText());

                WebElement href = exhibitor.findElement(By.cssSelector("a"));
                String exhibitorUrl = href.getAttribute("href");

                String exhibitorInfo = getInfoAboutExhibitor(exhibitorName, exhibitorUrl);

                allExhibitors.add(exhibitorInfo);
                System.out.println(exhibitorInfo);

                driver.navigate().back();
                //driver.navigate().refresh();
                Thread.sleep(500);
            }

            List<WebElement> nextButtonElements = driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON));
            if (nextButtonElements.size() > 0) {
                nextButtonElements.get(0).click();
            } else {
                Thread.sleep(5000);
                nextButtonElements = driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON));
                nextButtonElements.get(0).click();
            }
            //driver.navigate().refresh();
            //System.out.println("NEXT PAGE ");
            counter--;
        } while (counter > 0);

        return allExhibitors;

    }

    private String makeStringCSVConform(String input) {
        return input.replaceAll(",", "");
    }

    private String getInfoAboutExhibitor(String exhibitorName, String exhibitorUrl) {
        //Entering exhibitor page
        driver.get(exhibitorUrl);

        //System.out.println("entering = " + exhibitorUrl);

        String exhibitorCoreUrl = getExhibitorCoreUrl();

        String linkedInPageUrl = getExhibitorLinkedInProfilePage();

        return String.format("%s,%s,%s", exhibitorName, exhibitorCoreUrl, linkedInPageUrl);
    }

    private String getExhibitorLinkedInProfilePage() {
        String linkedInPageUrl = "";
        String socialMediaElement = "exd_socialMedia";
        List<WebElement> socialMediaWebElement = driver.findElements(By.className(socialMediaElement));

        if (socialMediaWebElement.size() > 0) {
            List<WebElement> socialMediaElements = socialMediaWebElement.get(0)
                    .findElements(By.cssSelector("a"));

            List<String> linkedInUrl = socialMediaElements.stream()
                    .filter(webElement -> webElement
                            .findElement(By.cssSelector("img"))
                            .getAttribute("title")
                            .equalsIgnoreCase("linkedin"))
                    .map(webElement -> webElement.getAttribute("href"))
                    .collect(Collectors.toList());
            linkedInPageUrl = linkedInUrl.size() > 0 ? linkedInUrl.get(0) : "";
        }
        return linkedInPageUrl;
    }

    private String getExhibitorCoreUrl() {
        String contactUrlField = "view_kontakt_url";
        List<WebElement> contactElements = driver.findElements(By.className(contactUrlField));

        //some exhibitors do not provide their url
        return contactElements.size() > 0 ? contactElements.get(0).getText() : "";
    }

    @Deprecated
    private List<String> init_getAllExhibitors(int pagesCount) {
        List<String> allExhibitors = new ArrayList<>();

        driver.get(url);

        int counter = pagesCount;
        do {
            List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

            List<WebElement> exhibitorAddresses = driver.findElements(By.className(CONSTANTS_ANALYTICA.ADDRESS_FIELD));

            for (int i = 0; i < elements.size(); i++) {

                String exhibitorInfo = String.format("%s,%s", elements.get(i).getText(), exhibitorAddresses.get(i).getText());
                allExhibitors.add(exhibitorInfo);

                System.out.println(exhibitorInfo);
            }

            driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON)).get(0).click();
            counter--;
        } while (counter > 0);

        return allExhibitors;
    }


    @Deprecated
    private List<String> old_getAllExhibitors(int pagesCount) {
        List<String> allExhibitors = new ArrayList<>();
        driver.get(url);

        int counter = pagesCount;
        do {
            List<WebElement> elements = driver.findElements(By.className(exhibitorArea));

            List<WebElement> exhibitorAddresses = driver.findElements(By.className(addressField));

            for (int i = 0; i < elements.size(); i++) {
                String exhibitorName = elements.get(i).getText();
                String exhibitorAddr = exhibitorAddresses.get(i).getText();
                String exhibitorInfo = String.format("%s,%s", exhibitorName, exhibitorAddr);
                allExhibitors.add(exhibitorInfo);
            }

            driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON)).get(0).click();
            counter--;
        } while (counter > 0);

        return allExhibitors;
    }

    private int getPagesCount() {

        int result = 0;

        driver.get(url);

        WebElement pagingElement = driver.findElements(By.className(pagingTextSubmits)).get(0);
        List<WebElement> inputElements = pagingElement.findElements(By.cssSelector(inputField));
        List<String> pagingNumbers = inputElements.stream()
                .filter(webElement -> webElement
                        .getAttribute(attributeName)
                        .equalsIgnoreCase(srField))
                .map(webElement -> webElement.getAttribute(attributeValue))
                .limit(1)
                .collect(Collectors.toList());


        if (pagingNumbers.size() > 0) {
            String pagingNum = pagingNumbers.get(0);
            System.out.println("paging num = " + pagingNum);
            result = Integer.parseInt(pagingNum);
        }

        return result;
    }

    @Override
    public List<String> getExtendedCSV() {
        return Collections.emptyList();
    }

    @Override
    public void close() {
        Objects.requireNonNull(driver, () -> "driver instance is null");
        driver.close();
    }
}
