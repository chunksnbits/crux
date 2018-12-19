package com.chimbori.crux.articles;

import com.chimbori.crux.articles.configuration.Configuration;
import com.chimbori.crux.common.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

class ExtractionHelpers {

  static final String GRAVITY_SCORE_ATTRIBUTE = "gravityScore";
  static final String GRAVITY_SCORE_SELECTOR = String.format("*[%s]", GRAVITY_SCORE_ATTRIBUTE);

  private final Configuration configuration;

  private ExtractionHelpers(Configuration configuration) {
    this.configuration = configuration;
  }

  static ExtractionHelpers configure(Configuration configuration) {
    return new ExtractionHelpers(configuration);
  }

  /**
   * Weights current element. By matching it with positive candidates and
   * weighting child nodes. Since it's impossible to predict which exactly
   * names, ids or class names will be used in HTML, major role is played by
   * child nodes
   *
   * @param e Element to weight, along with child nodes
   */
  int getWeight(Element e) {
    int weight = calcWeight(e);
    weight += (int) Math.round(e.ownText().length() / 100.0 * 10);
    weight += weightChildNodes(e);
    return weight;
  }

  /**
   * Weights a child nodes of given Element. During tests some difficulties
   * were met. For instanance, not every single document has nested paragraph
   * tags inside of the major article tag. Sometimes people are adding one
   * more nesting level. So, we're adding 4 points for every 100 symbols
   * contained in tag nested inside of the current weighted element, but only
   * 3 points for every element that's nested 2 levels deep. This way we give
   * more chances to extract the element that has less nested levels,
   * increasing probability of the correct extraction.
   *
   * @param rootEl Element, who's child nodes will be weighted
   */
  private int weightChildNodes(Element rootEl) {
    int weight = 0;
    Element caption = null;
    List<Element> pEls = new ArrayList<>(5);
    for (Element child : rootEl.children()) {
      String ownText = child.ownText();

      // if you are on a paragraph, grab all the text including that surrounded by additional formatting.
      if (child.tagName().equals("p"))
        ownText = child.text();

      int ownTextLength = ownText.length();
      if (ownTextLength < 20)
        continue;

      if (ownTextLength > 200)
        weight += Math.max(50, ownTextLength / 10);

      if (child.tagName().equals("h1") || child.tagName().equals("h2")) {
        weight += 30;
      } else if (child.tagName().equals("div") || child.tagName().equals("p")) {
        weight += calcWeightForChild(child, ownText);
        if (child.tagName().equals("p") && ownTextLength > 50)
          pEls.add(child);

        if (child.className().toLowerCase().equals("caption"))
          caption = child;
      }
    }

    // use caption and image
    if (caption != null)
      weight += 30;

    if (pEls.size() >= 2) {
      for (Element subEl : rootEl.children()) {
        if ("h1;h2;h3;h4;h5;h6".contains(subEl.tagName())) {
          weight += 20;
          // headerEls.add(subEl);
        } else if ("table;li;td;th".contains(subEl.tagName())) {
          addScore(subEl, -30);
        }

        if ("p".contains(subEl.tagName())) {
          addScore(subEl, 30);
        }
      }
    }
    return weight;
  }

  private void addScore(Element el, int score) {
    setScore(el, getScore(el) + score);
  }

  private int getScore(Element el) {
    try {
      return Integer.parseInt(el.attr(GRAVITY_SCORE_ATTRIBUTE));
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  private void setScore(Element el, int score) {
    el.attr(GRAVITY_SCORE_ATTRIBUTE, Integer.toString(score));
  }

  private int calcWeightForChild(Element child, String ownText) {
    int c = StringUtils.countMatches(ownText, "&quot;");
    c += StringUtils.countMatches(ownText, "&lt;");
    c += StringUtils.countMatches(ownText, "&gt;");
    c += StringUtils.countMatches(ownText, "px");
    int val;
    if (c > 5) {
      val = -30;
    } else {
      val = (int) Math.round(ownText.length() / 25.0);
    }

    addScore(child, val);
    return val;
  }

  private int calcWeight(Element element) {
    String className = element.className();
    String id = element.id();
    String style = element.attr("style");

    int weight = 0;
    if (configuration.positiveCssClassesAndIds().matcher(className).find()) {
      weight += 35;
    }
    if (configuration.positiveCssClassesAndIds().matcher(id).find()) {
      weight += 40;
    }
    if (configuration.unlikelyCssClassesAndIds().matcher(className).find()) {
      weight -= 20;
    }
    if (configuration.unlikelyCssClassesAndIds().matcher(id).find()) {
      weight -= 20;
    }
    if (configuration.negativeCssClassesAndIds().matcher(className).find()) {
      weight -= 50;
    }
    if (configuration.negativeCssClassesAndIds().matcher(id).find()) {
      weight -= 50;
    }
    if (style != null && !style.isEmpty() && configuration.negativeCssStyles().matcher(style).find()) {
      weight -= 50;
    }
    return weight;
  }

  /**
   * @return a set of all important nodes
   */
  Collection<Element> getNodes(Document doc) {
    Map<Element, Object> nodes = new LinkedHashMap<>(64);
    int score = 100;
    for (Element el : doc.select("body").select("*")) {
      if (configuration.importantNodes().matcher(el.tagName()).matches()) {
        nodes.put(el, null);
        setScore(el, score);
        score = score / 2;
      }
    }
    return nodes.keySet();
  }
}
