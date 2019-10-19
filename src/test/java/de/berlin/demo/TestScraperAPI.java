package de.berlin.demo;

import de.scraping.factory.ScraperFactory;
import de.scraping.scrapers.Scraper;
import de.scraping.scrapers.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestScraperAPI {

    private static Scraper analyticaScraper = null;

    @BeforeEach
    void setUp() {
        analyticaScraper = ScraperFactory.getScraperFor(Type.ANALYTICA);
    }

    @Test
    void simpleTest() {
        assertTrue(true);
        assertFalse(false);
    }

    @Test
    void testInitFactory() {
        assertNotNull(analyticaScraper);
    }

    @Test
    void testDummyScraper() {
        Scraper dummyScraper = ScraperFactory.getScraperFor(Type.UNKNOWN);
        List<String> emptyResult = dummyScraper.getCSV();

        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void testScraperOutputNotNull() {

        List<String> csv = analyticaScraper.getCSV();
        assertNotNull(csv, () -> "the csv output of the scraper should be not null");

        List<String> extendedCSV = analyticaScraper.getExtendedCSV();
        assertNotNull(extendedCSV, () -> "the extended csv output of the scraper should be not null");
    }

    @Test
    void testScraperProducesResults() {

        List<String> csv = analyticaScraper.getCSV();

        boolean resultIsNonEmpty = !csv.isEmpty();
        assertTrue(resultIsNonEmpty, () -> "the csv output of the scraper should not be empty");

    }

    @AfterEach
    void tearDown() {
        analyticaScraper.close();
        analyticaScraper = null;
    }
}
