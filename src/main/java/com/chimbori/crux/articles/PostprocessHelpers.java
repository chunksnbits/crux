package com.chimbori.crux.articles;

import com.chimbori.crux.articles.configuration.Configuration;
import com.chimbori.crux.common.Log;
import com.chimbori.crux.common.StringUtils;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

import static com.chimbori.crux.articles.ExtractionHelpers.GRAVITY_SCORE_ATTRIBUTE;
import static com.chimbori.crux.articles.ExtractionHelpers.GRAVITY_SCORE_SELECTOR;

/**
 * Cleans up the best-match Element after one has been picked, in order to provide a sanitized
 * output tree to the caller.
 */
class PostprocessHelpers {

  private final Configuration configuration;

  private PostprocessHelpers(Configuration configuration) {
    this.configuration = configuration;
  }

  static PostprocessHelpers configure(Configuration config) {
    return new PostprocessHelpers(config);
  }

  Document postprocess(Element topNode, List<Article.Image> images) {
    Log.i("postprocess");
    Document doc = new Document("");
    if (topNode == null) {
      return doc;
    }

    removeNodesWithNegativeScores(topNode, extractImageWithPositiveScore(images));
    unwrapFigures(topNode);
    replaceLineBreaksWithSpaces(topNode);
    removeUnlikelyChildNodes(topNode);
    removeTagsButRetainContent(topNode);
    removeTagsNotLikelyToBeParagraphs(topNode);
    removeTopLevelTagsNotLikelyToBeParagraphs(topNode);
    removeShortParagraphs(topNode);
    removeDisallowedAttributes(topNode);

    for (Node node : topNode.childNodes()) {
      doc.appendChild(node.clone());  // TODO: Don’t copy each item separately.
    }
    return doc;
  }

  private void unwrapFigures(Element topNode) {
    for (Element element : topNode.select("figure")) {
      Element childNode = element.getElementsByTag("img").first();
      if (childNode != null) {
        element.replaceWith(childNode);
      }
    }
  }

  private void replaceLineBreaksWithSpaces(Element topNode) {
    for (Element brNextToBrElement : topNode.select("br + br")) {
      brNextToBrElement.remove();
    }
    for (Element brElement : topNode.select("br")) {
      if (brElement.previousSibling() != null) {
        brElement.previousSibling().after(" • ");
      } else {
        brElement.parent().append(" • ");
      }
      brElement.unwrap();
    }
  }

  private void removeTopLevelTagsNotLikelyToBeParagraphs(Element element) {
    for (Element childElement : element.children()) {
      if (!configuration.retainTagsTopLevel().contains(childElement.tagName())) {
        Log.printAndRemove(childElement, "removeTopLevelTagsNotLikelyToBeParagraphs");
      }
    }
  }

  private void removeTagsNotLikelyToBeParagraphs(Element element) {
    for (Element childElement : element.children()) {
      if (!configuration.retainTags().contains(childElement.tagName())) {
        Log.printAndRemove(childElement, "removeTagsNotLikelyToBeParagraphs");
      } else if (childElement.children().size() > 0) {
        removeTagsNotLikelyToBeParagraphs(childElement);
      }
    }
  }

  private void removeTagsButRetainContent(Element element) {
    for (Element childElement : element.children()) {
      removeTagsButRetainContent(childElement);
      if (configuration.removeTagsButRetainContent().contains(childElement.tagName())) {
        Log.i("removeTagsButRetainContent: [%s] %s", childElement.tagName(), childElement.outerHtml());
        childElement.tagName("p");  // Set the wrapper tag to <p> instead of unwrapping them.
      }
    }
  }

  private void removeShortParagraphs(Element topNode) {
    for (int i = topNode.childNodeSize() - 1; i >= 0; i--) {
      Node childNode = topNode.childNode(i);

      String text = null;
      boolean isExemptFromMinTextLengthCheck = false;
      boolean isExemptFromTextRequirement = true;

      if (childNode instanceof TextNode) {
        text = ((TextNode) childNode).text().trim();

      } else if (childNode instanceof Element) {
        Element childElement = (Element) childNode;
        text = childElement.text().trim();
        isExemptFromTextRequirement = !configuration.tagsExemptFromEmptyTextCheck().contains(childElement.tagName());
        isExemptFromMinTextLengthCheck = configuration.tagsExemptFromMinLengthCheck().contains(childElement.tagName());
      }

      Log.i("removeShortParagraphs: [%s] isExemptFromMinTextLengthCheck : %b", childNode, isExemptFromMinTextLengthCheck);

      if (text == null ||
              (!isExemptFromTextRequirement && text.isEmpty()) ||
              (!isExemptFromMinTextLengthCheck && text.length() < configuration.minLengthForParagraphs()) ||
              text.length() > StringUtils.countLetters(text) * 2) {
        Log.printAndRemove(childNode, "removeShortParagraphs:");
      }
    }
  }

  private void removeUnlikelyChildNodes(Element element) {
    for (Element childElement : element.children()) {
      if (isUnlikely(childElement)) {
        Log.printAndRemove(childElement, "removeUnlikelyChildNodes");
      } else if (childElement.children().size() > 0) {
        removeUnlikelyChildNodes(childElement);
      }
    }
  }

  private void removeNodesWithNegativeScores(Element topNode, Elements imageElements) {
    Elements elementsWithGravityScore = topNode.select(GRAVITY_SCORE_SELECTOR);

    for (Element element : elementsWithGravityScore) {
      // Retain images that have previously been identified by a high score.
      if (imageElements.contains(element)) {
        continue;
      }

      int score = Integer.parseInt(element.attr(GRAVITY_SCORE_ATTRIBUTE));
      if (score < 0 || element.text().length() < configuration.minLengthForParagraphs()) {
        Log.printAndRemove(element, "removeNodesWithNegativeScores");
      }
    }
  }

  static private Elements extractImageWithPositiveScore(List<Article.Image> images) {
    Elements imageElements = new Elements();

    for (Article.Image image : images) {
      if (image.weight > 0) {
        imageElements.add(image.element);
      }
    }

    return imageElements;
  }

  private boolean isUnlikely(Element element) {
    String styleAttribute = element.attr("style");
    String classAttribute = element.attr("class");
    return classAttribute != null && classAttribute.toLowerCase().contains("caption")
            || configuration.unlikelyCssStyles().matcher(styleAttribute).find()
            || classAttribute != null && configuration.unlikelyCssStyles().matcher(classAttribute).find();
  }

  private void removeDisallowedAttributes(Element node) {
    for (Element childElement : node.children()) {
      removeDisallowedAttributes(childElement);
    }
    List<String> keysToRemove = new LinkedList<>();
    for (Attribute attribute : node.attributes()) {
      if (!configuration.attributesToRetainInHtml().contains(attribute.getKey())) {
        keysToRemove.add(attribute.getKey());
      }
    }
    for (String key : keysToRemove) {
      node.removeAttr(key);
    }
  }
}
