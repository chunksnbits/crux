package com.chimbori.crux;

import com.chimbori.crux.articles.Article;
import com.chimbori.crux.articles.ArticleExtractor;
import com.chimbori.crux.articles.configuration.Configuration;
import com.chimbori.crux.common.CharsetConverter;
import com.chimbori.crux.common.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.chimbori.crux.articles.configuration.StandardConfiguration.standardConfiguration;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.fail;

public class TestHelper {
  private TestHelper() {
  }

  public static Article extractFromTestFile(String baseUri, String testFile) {
    return TestHelper.extractFromTestFile(baseUri, testFile, standardConfiguration);
  }

  public static Article extractFromTestFile(String baseUri, String testFile, Configuration configuration) {
    try {
      Article article = ArticleExtractor.with(baseUri,
              requireNonNull(CharsetConverter.readStream(new FileInputStream(new File("test_data/" + testFile)))).content)
            .configure(configuration)
          .extractMetadata()
          .extractContent()
          .estimateReadingTime()
          .article();

      Log.i("%s", article.document.childNodes().toString());
      return article;
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      return null;
    }
  }
}
