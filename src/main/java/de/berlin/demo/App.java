package de.berlin.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.function.Predicate;

// https://stackoverflow.com/questions/48671687/pagination-in-web-scraping-using-jsoup-in-java-swing
public class App {
    public static void main(String... args) {
        System.out.println(new App().getGreetings());

        try {
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect("https://exhibitors.analytica.de/onlinecatalog/2020/Exhibitors/#searchFormAnker").get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.printf("Title: %s\n", doc.title());

            Elements exhibitors = doc.getElementsByClass("jl_lexname");

            for (Element exhibitor : exhibitors) {
                exhibitor
                        .getAllElements()
                        .stream()
                        .filter(isHTMLElementA())
                        .forEach(el -> System.out.println("element = " + el.nodeName() + "| elem value: " + el.text()));
            }

            Elements pagingElements = doc.getElementsByClass("paging_textSubmit SRFieldSubmiter");

            Element pagingNumberElement = pagingElements.first()
                    .getAllElements()
                    .first();

            System.out.printf("Paging Element is %s, and the value of pages counts = %d",
                    pagingNumberElement.nodeName(), getPagesCount(pagingNumberElement));



            // TODO: 18.10.19 LinkedIn Profiles Search
//            String linkedInSearchStr = "https://www.linkedin.com/company/allymatch/people/?keywords=ceo%2C%20head%20of%20development";
//            Document linkedInSearchPage = Jsoup
//                    .connect(linkedInSearchStr)
//                    .userAgent("Mozilla")
//                    .get();
//            System.out.printf(" LinkedIn Search for the company %s \n", linkedInSearchPage.title());
//            Elements people = doc.getElementsByClass("org-people-profile-card__profile-title t-black lt-line-clamp lt-line-clamp--single-line ember-view");
//            for (Element person : people) {
//                System.out.printf("person: %s", person.getClass());
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPagesCount(Element pagingNumberElement) {
        return Integer.parseInt(pagingNumberElement.val());
    }

    public static Predicate<Element> isHTMLElementA() {
        return element -> element.nodeName().equalsIgnoreCase("a");
    }

    public String getGreetings() {
        return "Hello World!";
    }
}
