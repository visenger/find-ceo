package de.scraping.factory;

import de.scraping.scrapers.AnalyticaScraper;
import de.scraping.scrapers.DummyScraper;
import de.scraping.scrapers.Scraper;
import de.scraping.scrapers.Type;

public abstract class ScraperFactory {

    public static Scraper getScraperFor(Type typeOfScraper) {

        Scraper scraper = null;

        switch (typeOfScraper) {
            case ANALYTICA:
                scraper = new AnalyticaScraper();
                break;
            default:
                scraper = new DummyScraper();

        }
        return scraper;
    }

}
