package com.chimbori.crux.articles.configuration;

import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public interface Configuration {
    Configuration standardConfiguration = new StandardConfiguration();
    Configuration standardConfigurationWithImages = new StandardConfigurationWithImages();

    int minLengthForParagraphs();

    Set<String> removeTagsButRetainContent();
    Set<String> retainTags();
    Set<String> tagsExemptFromMinLengthCheck();
    Set<String> attributesToRetainInHtml();
    Set<String> retainTagsTopLevel();
    Set<String> tagsExemptFromEmptyTextCheck();

    Pattern unlikelyCssStyles();
    Pattern importantNodes();
    Pattern unlikelyCssClassesAndIds();
    Pattern positiveCssClassesAndIds();
    Pattern negativeCssClassesAndIds();
    Pattern negativeCssStyles();
}
