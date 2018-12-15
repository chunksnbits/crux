package com.chimbori.crux.articles.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;

@SuppressWarnings("WeakerAccess")
public class StandardConfiguration implements Configuration {

    protected static final int MIN_LENGTH_FOR_PARAGRAPHS = 50;

    protected static final Pattern UNLIKELY_CSS_STYLES = Pattern.compile("display\\:none|visibility\\:hidden");

    protected static final Set<String> REMOVE_TAGS_BUT_RETAIN_CONTENT = new HashSet<>(Arrays.asList(
        "font", "table", "tbody", "tr", "td", "div", "ol", "ul", "li", "span"
    ));

    protected static final Set<String> RETAIN_TAGS = new HashSet<>(Arrays.asList(
        "p", "b", "i", "u", "strong", "em", "a", "pre", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote"
    ));

    protected static final Set<String> TAGS_EXEMPT_FROM_MIN_LENGTH_CHECK = new HashSet<>(Arrays.asList(
        "b", "i", "u", "strong", "em", "a", "pre", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote"
    ));

    protected static final Set<String> ATTRIBUTES_TO_RETAIN_IN_HTML = new HashSet<>(Arrays.asList(
        "href", "src", "alt"
    ));

    protected static final Set<String> RETAIN_TAGS_TOP_LEVEL = new HashSet<>(Arrays.asList(
        "p", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "li"
    ));

    protected static final Set<String> TAGS_EXEMPT_FROM_EMPTY_TEXT_CHECK = emptySet();

    /**
     * If a string is shorter than this limit, it is not considered a paragraph.
     */
    public int getMinLengthForParagraphs() {
        return MIN_LENGTH_FOR_PARAGRAPHS;
    }

    /**
     * Styles that result in the element not contributing to visible content.
     */
    public Pattern getUnlikelyCssStyles() {
        return UNLIKELY_CSS_STYLES;
    }

    /**
     * Tags that should not be output, but still may contain interesting content.
     */
    public Set<String> getRemoveTagsButRetainContent() {
        return REMOVE_TAGS_BUT_RETAIN_CONTENT;
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

    public Set<String> getTagsExemptFromEmptyTextCheck() {
        return TAGS_EXEMPT_FROM_EMPTY_TEXT_CHECK;
    }

    public static Configuration withStandardConfiguration() {
        return new StandardConfiguration();
    }
}
