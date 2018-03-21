package org.jetbrains.dekaf.assertions;

import org.assertj.core.api.AbstractAssert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PatternAssert extends AbstractAssert<PatternAssert,Pattern> {


  public PatternAssert(final Pattern actual) {
    super(actual, PatternAssert.class);
  }


  public static PatternAssert assertThat(Pattern pattern) {
    return new PatternAssert(pattern);
  }


  /**
   * Asserts that the given text is matched by this regular expression.
   * @param text text to fit (to be matched).
   * @return     itself.
   */
  public PatternAssert fits(final String text) {
    isNotNull();

    Matcher m = actual.matcher(text);
    if (!m.matches()) {
      failWithMessage("Expected that the regular expression \"%s\" fits the text \"%s\" but it doesn't", actual, text);
    }

    return this;
  }


  /**
   * Asserts that the given text is not matched by this regular expression.
   * @param text text to doesn't fit (to be not matched).
   * @return     itself.
   */
  public PatternAssert doesntFit(final String text) {
    isNotNull();

    Matcher m = actual.matcher(text);
    if (m.matches()) {
      failWithMessage("Expected that the regular expression \"%s\" doesn't fit the text \"%s\" but it does", actual, text);
    }

    return this;
  }


}
