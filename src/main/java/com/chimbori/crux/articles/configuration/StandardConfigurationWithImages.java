package com.chimbori.crux.articles.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class StandardConfigurationWithImages extends StandardConfiguration {

    protected static final Set<String> RETAIN_TAGS =
        extend(StandardConfiguration.RETAIN_TAGS, "img", "figure");

    protected static final Set<String> TAGS_EXEMPT_FROM_MIN_LENGTH_CHECK =
        extend(StandardConfiguration.TAGS_EXEMPT_FROM_MIN_LENGTH_CHECK, "img", "figure");

    protected static final Set<String> ATTRIBUTES_TO_RETAIN_IN_HTML =
        extend(StandardConfiguration.ATTRIBUTES_TO_RETAIN_IN_HTML, "src", "alt");

    protected static final Set<String> RETAIN_TAGS_TOP_LEVEL =
        extend(StandardConfiguration.RETAIN_TAGS_TOP_LEVEL, "img", "figure");

    @SuppressWarnings("WeakerAccess")
    public static Configuration withStandardConfigurationIncludingImages() {
        return new StandardConfigurationWithImages();
    }

    /**
     * Tags that should be retained in the output. This list should be fairly minimal, and equivalent
     * to the list of tags that callers can be expected to be able to handle.
     */
    public Set<String> getRetainTags() {
        return RETAIN_TAGS;
    }

    /**
     * Tags that can contain really short content, because they are not paragraph-level tags. Content
     * within these tags is not subject to the {@code getMinLengthForParagraphs} requirement.
     */
    public Set<String> getTagsExemptFromMinLengthCheck() {
        return TAGS_EXEMPT_FROM_MIN_LENGTH_CHECK;
    }

    /**
     * The whitelist of attributes that should be retained in the output. No other attributes
     * will be retained.
     */
    public Set<String> getAttributesToRetainInHtml() {
        return ATTRIBUTES_TO_RETAIN_IN_HTML;
    }

    /**
     * After a final set of top-level nodes has been extracted, all tags except these are removed.
     * This ensures that while inline tags containing shorter text, e.g. <a href="…">one word</a>
     * are kept as part of a larger paragraph, those same short tags are not allowed to be
     * top-level children.
     */
    public Set<String> getRetainTagsTopLevel() {
        return RETAIN_TAGS_TOP_LEVEL;

    }

    private static Set<String> extend(Set<String> source, String... extensions) {
        Set<String> copy = new HashSet<>(source);

        copy.addAll(Arrays.asList(extensions));

        return copy;
    }
}
