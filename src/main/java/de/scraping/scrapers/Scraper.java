package de.scraping.scrapers;

import java.util.List;

public interface Scraper {

    /**
     * Produces Web page scraping result.
     *
     * @return scraping result, formatted as comma separated values;
     */
    public List<String> getCSV();

    /**
     * Produces Web page scraping result with extended columns;
     *
     * @return scraping result, formatted as comma separated values;
     */
    public List<String> getExtendedCSV();


    /**
     * Closing scraper;
     */
    public void close();

}
