package com.chimbori.crux.articles.configuration;

import java.util.Set;
import java.util.regex.Pattern;

public interface Configuration {
    int getMinLengthForParagraphs();
    Pattern getUnlikelyCssStyles();
    Set<String> getRemoveTagsButRetainContent();
    Set<String> getRetainTags();
    Set<String> getTagsExemptFromMinLengthCheck();
    Set<String> getAttributesToRetainInHtml();
    Set<String> getRetainTagsTopLevel();
    Set<String> getTagsExemptFromEmptyTextCheck();
}
