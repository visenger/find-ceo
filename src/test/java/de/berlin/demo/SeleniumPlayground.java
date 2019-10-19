package de.berlin.demo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.scraping.config.CONSTANTS_ANALYTICA;
import de.scraping.util.SimpleScraper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SeleniumPlayground {

    private App app;
    private WebDriver driver;


    // TODO: 18.10.19 Extract these strings into either a config or contants class;
    private final String url = "https://exhibitors.analytica.de/onlinecatalog/2020/Exhibitors/#searchFormAnker";
    // private final String exhibitorArea = "jl_lexname";
//    private final String nextButton = "paging_RightArrows_cell";
    private final String address_field = "jl_lexadr";
    private final Config config = ConfigFactory.load();
    //    private final String nextButton = "vam paging_textArrows SRFieldSubmiter jl_p_act jl_p_actnext showToolTip";

    @BeforeEach
    void setUp() {
        ///Users/visenger/Downloads
        app = new App();

        System.setProperty("webdriver.chrome.driver", config.getString("driver.chrome"));
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }


    @Test
    void testExhibitorsOnOnePage() {

        driver.get(url);

        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        int actualNumberOfExhibitors = elements.size();

        assertEquals(15, actualNumberOfExhibitors);
    }

    @Test
    void testTheContentOfExhibitors() {
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        WebElement firstExhibitor = elements.get(0);

        String actualName = firstExhibitor.getText();

        String expectedName = "2mag AG";
        assertEquals(expectedName, actualName);

    }

    @Test
    void testTheAddressOfExhibitors() {
        driver.get(url);

        List<WebElement> elements = driver.findElements(By.className(address_field));
        WebElement firstExhibitor = elements.get(0);

        String address = firstExhibitor.getText();

        System.out.println("addr of first = " + address);
    }

    @Test
    void testNextPageExhibitor() {
        driver.get(url);
        driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON)).get(0).click();
        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        WebElement firstExhibitor = elements.get(0);

        String actualName = firstExhibitor.getText();
        String expectedName = "Advanced Chemistry Development Germany GmbH";

        assertEquals(expectedName, actualName);
    }

    @Test
    void inspectBackButton() {
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        int sizeOfExibitors = elements.size();
        Assumptions.assumingThat(sizeOfExibitors > 0, () -> {
            for (int i = 0; i < sizeOfExibitors; i++) {
                List<WebElement> webElements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

                WebElement exhibitor = webElements.get(i);
                WebElement href = exhibitor.findElement(By.cssSelector("a"));
                String exhibitorUrl = href.getAttribute("href");

                assertNotNull(exhibitorUrl);
                assertTrue(exhibitorUrl.startsWith("https://exhibitors.analytica.de/onlinecatalog/2020/exhibitorsdetails"));

                driver.get(exhibitorUrl);

                System.out.println("entering = " + exhibitorUrl);

                driver.navigate().back();
                driver.navigate().refresh();

                Thread.sleep(5000);


            }
        });


    }

    @Test
    public void inspectSingleExhibitor() {
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        WebElement firstExhibitor = elements.get(0);
        WebElement href = firstExhibitor.findElement(By.cssSelector("a"));
        String exhibitorUrl = href.getAttribute("href");

        assertNotNull(exhibitorUrl);
        assertTrue(exhibitorUrl.startsWith("https://exhibitors.analytica.de/onlinecatalog/2020/exhibitorsdetails"));
    }

    @Test
    public void deepInspectOfSingleExhibitor() {
        driver.get(url);

        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        WebElement firstExhibitor = elements.get(0);
        WebElement href = firstExhibitor.findElement(By.cssSelector("a"));
        String exhibitorUrl = href.getAttribute("href");

        assertNotNull(exhibitorUrl);
        driver.get(exhibitorUrl);

        String contactUrlField = "view_kontakt_url";
        WebElement contactElement = driver.findElement(By.className(contactUrlField));

        String exhibitorWebpage = contactElement.getText();
        assertEquals("www.2mag.de", exhibitorWebpage);
    }

    @Test
    public void inspectSocialMediaProfilesOfSingleExhibitor() {
        String singleExhibitorUrl = "https://exhibitors.analytica.de/onlinecatalog/2020/exhibitorsdetails/cab_produkttechnik_gmbh_and_co_kg/?elb=271.1100.3871.1.111";

        driver.get(singleExhibitorUrl);

        String socialMediaElement = "exd_socialMedia";
        WebElement socialMediaWebElement = driver.findElement(By.className(socialMediaElement));

        List<WebElement> webElements = socialMediaWebElement.findElements(By.cssSelector("a"));
        List<String> linkedInUrl = webElements.stream()
                .filter(webElement -> webElement
                        .findElement(By.cssSelector("img"))
                        .getAttribute("title")
                        .equalsIgnoreCase("linkedin"))
                .map(webElement -> webElement.getAttribute("href"))
                .collect(Collectors.toList());

        Assumptions.assumingThat(linkedInUrl.size() > 0, () -> {
            assertTrue(linkedInUrl.get(0).startsWith("https://www.linkedin.com/company/"));
        });
    }

    @Test
    void visitSocialMediaSite() {
        String singleExhibitorUrl = "https://exhibitors.analytica.de/onlinecatalog/2020/exhibitorsdetails/cab_produkttechnik_gmbh_and_co_kg/?elb=271.1100.3871.1.111";

        driver.get(singleExhibitorUrl);

        String socialMediaElement = "exd_socialMedia";
        WebElement socialMediaWebElement = driver.findElement(By.className(socialMediaElement));

        List<WebElement> webElements = socialMediaWebElement.findElements(By.cssSelector("a"));
        List<String> linkedInUrl = webElements.stream()
                .filter(webElement -> webElement
                        .findElement(By.cssSelector("img"))
                        .getAttribute("title")
                        .equalsIgnoreCase("linkedin"))
                .map(webElement -> webElement.getAttribute("href"))
                .collect(Collectors.toList());

        Assumptions.assumingThat(linkedInUrl.size() > 0, () -> {
            String linkedInPageUrl = linkedInUrl.get(0);
            assertTrue(linkedInPageUrl.startsWith("https://www.linkedin.com/company/"));

            // TODO: 19.10.19 going to the linkedin page requires sign in;
            driver.get(linkedInPageUrl);
            Thread.sleep(5000);
        });
    }

    @Test
    public void inspectExhibitorWithNoSocialMedia() {
        driver.get(url);

        List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

        WebElement firstExhibitor = elements.get(0);
        WebElement href = firstExhibitor.findElement(By.cssSelector("a"));
        String exhibitorUrl = href.getAttribute("href");

        assertNotNull(exhibitorUrl);
        driver.get(exhibitorUrl);
        String socialMediaElement = "exd_socialMedia";
        List<WebElement> socialMediaWebElements = driver.findElements(By.className(socialMediaElement));

        assertTrue(socialMediaWebElements.size() == 0);
    }

    @Test
    void testGetPagingNumber() throws IOException {
        int countPages = SimpleScraper.of(url).usePagingField(CONSTANTS_ANALYTICA.PAGING_FIELD).getPagingCount();
        assertEquals(54, countPages);
    }

    @Test
    void testGetPagingNumber_2() {

        driver.get(url);
        WebElement pagingElement = driver.findElements(By.className("paging_TextSubmits_cell")).get(0);
        List<WebElement> inputElements = pagingElement.findElements(By.cssSelector("input"));
        List<String> pagingNumbers = inputElements.stream()
                .filter(webElement -> webElement.getAttribute("name")
                        .equalsIgnoreCase("SRField"))
                .map(webElement -> webElement.getAttribute("value"))
                .limit(1)
                .collect(Collectors.toList());
        if (pagingNumbers.size() > 0) {
            assertEquals(54, Integer.parseInt(pagingNumbers.get(0)));
        }

    }

    @Test
    void testEndToEndRun() throws IOException {
        int countPages = SimpleScraper.of(CONSTANTS_ANALYTICA.URL_EXHIBITORS)
                .usePagingField(CONSTANTS_ANALYTICA.PAGING_FIELD)
                .getPagingCount();

        assertEquals(54, countPages);

        List<String> allExhibitors = new ArrayList<>();

        driver.get(url);

        int counter = countPages;
        do {
            List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

            List<WebElement> exhibitorAddresses = driver.findElements(By.className(CONSTANTS_ANALYTICA.ADDRESS_FIELD));

            assertTrue(elements.size() == exhibitorAddresses.size());

            for (int i = 0; i < elements.size(); i++) {
                String exhibitorInfo = String.format("%s,%s", elements.get(i).getText(), exhibitorAddresses.get(i).getText());
                allExhibitors.add(exhibitorInfo);
            }

            driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON)).get(0).click();
            counter--;
        } while (counter > 0);

        System.out.println("total number of exhibitors = " + allExhibitors.size());

    }

    @Test
    void testEndToEndRunWithClickOnExhibitor() throws IOException, InterruptedException {
        int countPages = SimpleScraper.of(CONSTANTS_ANALYTICA.URL_EXHIBITORS)
                .usePagingField(CONSTANTS_ANALYTICA.PAGING_FIELD)
                .getPagingCount();

        assertEquals(54, countPages);

        List<String> allExhibitors = new ArrayList<>();

        driver.get(url);

        int counter = 3; //todo change to countPages;
        do {
            List<WebElement> elements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

            assertTrue(elements.size() > 0);

            for (int i = 0; i < elements.size(); i++) {

                List<WebElement> webElements = driver.findElements(By.className(CONSTANTS_ANALYTICA.EXHIBITOR_AREA));

                WebElement exhibitor = webElements.get(i);
                String exhibitorName = exhibitor.getText();

                WebElement href = exhibitor.findElement(By.cssSelector("a"));
                String exhibitorUrl = href.getAttribute("href");

                assertNotNull(exhibitorUrl);
                assertTrue(exhibitorUrl.startsWith("https://exhibitors.analytica.de/onlinecatalog/2020/exhibitorsdetails"));


                //Entering exhibitor page
                driver.get(exhibitorUrl);

                System.out.println("entering = " + exhibitorUrl);

                String contactUrlField = "view_kontakt_url";
                List<WebElement> contactElements = driver.findElements(By.className(contactUrlField));

                //some exhibitors do not provide their url
                String exhibitorCoreUrl = contactElements.size() > 0 ? contactElements.get(0).getText() : "";

                String socialMediaElement = "exd_socialMedia";
                String linkedInPageUrl = "";
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

                String exhibitorInfo = String.format("%s,%s,%s", exhibitorName, exhibitorCoreUrl, linkedInPageUrl);
                allExhibitors.add(exhibitorInfo);
                System.out.println(exhibitorInfo);

                driver.navigate().back();
                //driver.navigate().refresh();

                Thread.sleep(500);
            }

            driver.findElements(By.className(CONSTANTS_ANALYTICA.NEXT_BUTTON)).get(0).click();
            //driver.navigate().refresh();
            System.out.println("NEXT PAGE ");
            counter--;
        } while (counter > 0);

        int size = allExhibitors.size();
        System.out.println("total number of exhibitors = " + size);
        assertTrue(size > 0);

    }

    @Test
    public void productCount() throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");

        driver.get("https://www.flipkart.com/");

        driver.manage().window().maximize();

        //Escape Pop up
        driver.findElement(By.cssSelector("button[class='_2AkmmA _29YdH8']")).click();

        //Search Query
        String searchString = "iphone 7";

        //Enter Search Query in Search Textbox
        driver.findElement(By.className("LM6RPg")).sendKeys(searchString);

        //Click on Search Button
        driver.findElement(By.className("LM6RPg")).sendKeys(Keys.ENTER);

        //start product count from 0
        int productCount = 0;

        //list to store page numbers
        List<WebElement> elements;

        //Count no of pagination link
        new WebDriverWait(
                driver, 20).until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//nav[@class='_1ypTlJ']/a")));

        elements = driver.findElements(By.xpath("//nav[@class='_1ypTlJ']/a"));

        //Iterate through list
        for (int i = 0; i < elements.size() - 1; i++) {

            elements.get(i).sendKeys(Keys.ENTER);
            Thread.sleep(3000);

            //Store product element
            List<WebElement> productElement = driver.findElements(By.xpath("//div[@class='_3wU53n']"));

            //Add List size to existing pages product count
            productCount = productCount + productElement.size();

        }

        String actualResult = driver.findElement(By.cssSelector("span[class='_2yAnYN']")).getText();
        int currentCount = 24 * (elements.size() - 2) + 1;
        String expectedResult = "Showing " + currentCount + " – " + productCount + " of " + productCount + " results for \"" + searchString + "\"";

        assertTrue(actualResult.equals(expectedResult));

    }


//    @Test
//    void testChromeDriver() throws InterruptedException {
//        // Optional. If not specified, WebDriver searches the PATH for chromedriver.
////        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
////
////        WebDriver driver = new ChromeDriver();
//        driver.get("http://www.google.com/");
//        Thread.sleep(5000);  // Let the user actually see something!
//        WebElement searchBox = driver.findElement(By.name("q"));
//        searchBox.sendKeys("ChromeDriver");
//        searchBox.submit();
//        Thread.sleep(5000);  // Let the user actually see something!
//        driver.quit();
//    }


    @Test
    void getGreetings() {
        assertEquals("Hello World!", app.getGreetings());
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}