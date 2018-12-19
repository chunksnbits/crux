package com.chimbori.crux.articles.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;

@SuppressWarnings("unused")
public class StandardConfiguration implements Configuration {
    /**
     * If a string is shorter than this limit, it is not considered a paragraph.
     */
    public int minLengthForParagraphs() {
        return 50;
    }

    /**
     * Styles that result in the element not contributing to visible content.
     */
    public Pattern unlikelyCssStyles() {
        return Pattern.compile("display:none|visibility:hidden");
    }

    /**
     * Tags that should not be output, but still may contain interesting content.
     */
    public Set<String> removeTagsButRetainContent() {
        return new HashSet<>(Arrays.asList(
            "font", "table", "tbody", "tr", "td", "div", "ol", "ul", "li", "span"
        ));
    }

    /**
     * Tags that should be retained in the output. This list should be fairly minimal, and equivalent
     * to the list of tags that callers can be expected to be able to handle.
     */
    public Set<String> retainTags() {
        return new HashSet<>(Arrays.asList(
            "p", "b", "i", "u", "strong", "em", "a", "pre", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote"
        ));
    }

    /**
     * Tags that can contain really short content, because they are not paragraph-level tags. Content
     * within these tags is not subject to the {@code getMinLengthForParagraphs} requirement.
     */
    public Set<String> tagsExemptFromMinLengthCheck() {
        return new HashSet<>(Arrays.asList(
            "b", "i", "u", "strong", "em", "a", "pre", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote"
        ));
    }

    /**
     * The whitelist of attributes that should be retained in the output. No other attributes
     * will be retained.
     */
    public Set<String> attributesToRetainInHtml() {
        return new HashSet<>(Arrays.asList(
            "href", "src", "alt"
        ));
    }

    /**
     * After a final set of top-level nodes has been extracted, all tags except these are removed.
     * This ensures that while inline tags containing shorter text, e.g. <a href="…">one word</a>
     * are kept as part of a larger paragraph, those same short tags are not allowed to be
     * top-level children.
     */
    public Set<String> retainTagsTopLevel() {
        return new HashSet<>(Arrays.asList(
            "p", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "li"
        ));
    }

    public Set<String> tagsExemptFromEmptyTextCheck() {
        return emptySet();
    }

    public Pattern importantNodes() {
        return Pattern.compile("p|div|td|h1|h2|article|section");
    }

    public Pattern unlikelyCssClassesAndIds() {
        return Pattern.compile("com(bx|ment|munity)|dis(qus|cuss)|e(xtra|[-]?mail)|foot|" +
            "header|menu|re(mark|ply)|rss|sh(are|outbox)|sponsor" +
            "a(d|ll|gegate|rchive|ttachment)|(pag(er|ination))|popup|print|" +
            "login|si(debar|gn|ngle)|facebook|twitter|email");
    }

    public Pattern positiveCssClassesAndIds(){
        return Pattern.compile("(^(body|content|h?entry|main|page|post|text|blog|story|haupt))"+
        "|arti(cle|kel)|instapaper_body");
    }

    public Pattern negativeCssClassesAndIds(){
        return Pattern.compile("nav($|igation)|user|com(ment|bx)|(^com-)|contact|"+
        "foot|masthead|(me(dia|ta))|outbrain|promo|related|scroll|(sho(utbox|pping))|"+
        "sidebar|sponsor|tags|tool|widget|player|disclaimer|toc|infobox|vcard|post-ratings");
    }

    public Pattern negativeCssStyles() {
        return Pattern.compile("hidden|display: ?none|font-size: ?small");
    }
}
