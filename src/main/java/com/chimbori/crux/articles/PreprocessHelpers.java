package com.chimbori.crux.articles;

import com.chimbori.crux.articles.configuration.Configuration;
import com.chimbori.crux.common.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * Performs basic sanitization before starting the extraction process.
 */
class PreprocessHelpers {
  private final Configuration configuration;

  private PreprocessHelpers(Configuration configuration) {
    this.configuration = configuration;
  }

  static PreprocessHelpers configure(Configuration configuration) {
    return new PreprocessHelpers(configuration);
  }

  void preprocess(Document doc) {
    Log.i("preprocess");
    stripUnlikelyCandidates(doc);
    removeScriptsStyles(doc);
    removeComments(doc.body());
  }

  /**
   * Removes unlikely candidates from HTML. It often ends up removing more than just the unlikely
   * candidates, so exercise caution when enabling this.
   */
  private void stripUnlikelyCandidates(Document doc) {
    if (true) {
      return;  // Temporarily disabled; see comment above.
    }

    for (Element child : doc.select("body").select("*")) {
      String className = child.className().toLowerCase();
      String id = child.id().toLowerCase();
      if (configuration.negativeCssClassesAndIds().matcher(className).find() || configuration.negativeCssClassesAndIds().matcher(id).find()) {
        Log.printAndRemove(child, "stripUnlikelyCandidates");
      }
    }
  }

  private void removeScriptsStyles(Document doc) {
    Elements scripts = doc.getElementsByTag("script");
    for (Element item : scripts) {
      Log.printAndRemove(item, "removeScriptsStyles('script')");
    }

    Elements noscripts = doc.getElementsByTag("noscript");
    for (Element item : noscripts) {
      Log.printAndRemove(item, "removeScriptsStyles('noscript')");
    }

    Elements styles = doc.getElementsByTag("style");
    for (Element item : styles) {
      Log.printAndRemove(item, "removeScriptsStyles('style')");
    }
  }

  private void removeComments(Node node) {
    for (int i = 0; i < node.childNodes().size();) {
      Node child = node.childNode(i);
      if (child.nodeName().equals("#comment"))
        Log.printAndRemove(child, "removeComments");
      else {
        removeComments(child);
        i++;
      }
    }
  }
}
