package com.chimbori.crux.articles.configuration;

import com.chimbori.crux.articles.Article;
import com.chimbori.crux.articles.ArticleExtractor;
import org.junit.Test;

import static com.chimbori.crux.articles.configuration.StandardConfigurationWithImages.standardConfigurationWithImages;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ConfigurationTest {
    private static final String EXAMPLE_URL = "http://example.com/";

    @Test
    public void testStripsImagesWithStandardConfiguration() {
        Article article = ArticleExtractor.with(EXAMPLE_URL, "<p>\n" +
                "Visible Text that’s still longer than our minimum text size limits\n" +
                "<img src=\"https://img.scr.io/img.jpg\" alt=\"important\">" +
                "Default Text but longer that’s still longer than our minimum text size limits/p>")
            .extractContent()
            .article();

        assertTrue(article.document.getElementsByTag("img").isEmpty());
    }

    @Test
    public void testRetainsImagesWithImageConfiguration() {
        Article article = ArticleExtractor.with(EXAMPLE_URL, "<p>\n" +
                "Visible Text that’s still longer than our minimum text size limits\n" +
                "<img src=\"https://img.scr.io/img.jpg\" alt=\"important\">" +
                "Default Text but longer that’s still longer than our minimum text size limits/p>")
            .configure(standardConfigurationWithImages)
            .extractContent()
            .article();

        assertFalse(article.document.getElementsByTag("img").isEmpty());
    }
}
