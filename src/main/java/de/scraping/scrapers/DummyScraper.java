package de.scraping.scrapers;

import java.util.Collections;
import java.util.List;

/**
 * A default scraper object that returns empty results.
 */
public class DummyScraper implements Scraper {
    @Override
    public List<String> getCSV() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getExtendedCSV() {
        return Collections.emptyList();
    }

    @Override
    public void close() {

    }
}
