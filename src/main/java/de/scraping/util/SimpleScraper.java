package de.scraping.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SimpleScraper {


    private Document doc = null;
    private String url = "";
    private String pagingField = "";

    private SimpleScraper(String url) throws IOException {
        this.doc = Jsoup.connect(url).get();
        this.url = url;
    }

    public static SimpleScraper of(String url) throws IOException {
        return new SimpleScraper(url);
    }

    public SimpleScraper usePagingField(String pagingField) {
        this.pagingField = pagingField;
        return this;
    }

    public int getPagingCount() {
        int result = 0;

        Elements pagingElements = doc.getElementsByClass(pagingField);

        Element pagingNumberElement = pagingElements.first()
                .getAllElements()
                .first();

        result = getPagesCount(pagingNumberElement);


        return result;
    }

    public int getPagesCount(Element pagingNumberElement) {
        return Integer.parseInt(pagingNumberElement.val());
    }
}
