package com.chimbori.crux.articles.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@SuppressWarnings("unused")
public class StandardConfigurationWithImages extends StandardConfiguration {

    /**
     * Tags that should be retained in the output. This list should be fairly minimal, and equivalent
     * to the list of tags that callers can be expected to be able to handle.
     */
    public Set<String> retainTags() {
        return extend(super.retainTags(), "img");
    }

    /**
     * Tags that can contain really short content, because they are not paragraph-level tags. Content
     * within these tags is not subject to the {@code getMinLengthForParagraphs} requirement.
     */
    public Set<String> tagsExemptFromMinLengthCheck() {
        return extend(super.tagsExemptFromMinLengthCheck(), "img");
    }

    /**
     * The whitelist of attributes that should be retained in the output. No other attributes
     * will be retained.
     */
    public Set<String> attributesToRetainInHtml() {
        return extend(super.attributesToRetainInHtml(), "src", "alt");
    }

    /**
     * After a final set of top-level nodes has been extracted, all tags except these are removed.
     * This ensures that while inline tags containing shorter text, e.g. <a href="…">one word</a>
     * are kept as part of a larger paragraph, those same short tags are not allowed to be
     * top-level children.
     */
    public Set<String> retainTagsTopLevel() {
        return extend(super.retainTagsTopLevel(), "img");
    }

    public Pattern importantNodes() {
        return compile(super.importantNodes() + "|img");
    }

    private static Set<String> extend(Set<String> source, String... extensions) {
        Set<String> copy = new HashSet<>(source);
        copy.addAll(Arrays.asList(extensions));
        return copy;
    }
}
