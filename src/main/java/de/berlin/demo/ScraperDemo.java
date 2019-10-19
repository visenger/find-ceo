package de.berlin.demo;

import de.scraping.factory.ScraperFactory;
import de.scraping.scrapers.Scraper;
import de.scraping.scrapers.Type;
import de.scraping.util.Writer;

import java.util.List;

public class ScraperDemo {
    public static void main(String... args) {
        Scraper scraper = ScraperFactory.getScraperFor(Type.ANALYTICA);
        List<String> csvData = scraper.getCSV();
        scraper.close();

        Writer.writerCSV(csvData, "???"); //todo: initial api
    }
}
