package com.equals;

import java.util.function.BiFunction;
import org.apache.commons.lang3.builder.EqualsBuilder;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/20/19
 * <p>Time: 7:40 PM
 *
 * @author Miguel Mu\u00f1oz
 * @noinspection HardcodedLineSeparator, MagicNumber
 */
@SuppressWarnings("HardCodedStringLiteral")
enum TestUtility {
  ;

  static <T> void verifyMatch__(DogTag<T> dogTag, T t1, T t2) {
    assertTrue(dogTag.doEqualsTest(t1, t2));  // equality test
    assertTrue(dogTag.doEqualsTest(t2, t1));  // symmetry test
    assertTrue(dogTag.doEqualsTest(t1, t1));  // reflexive test
    assertTrue(dogTag.doEqualsTest(t2, t2));  // reflexive test
    assertFalse(dogTag.doEqualsTest(t1, null));
    assertFalse(dogTag.doEqualsTest(t2, null));
    assertEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2)); // hash code consistency test
  }

  static <T> void verifyNoMatch(DogTag<T> dogTag, T t1, T t2) {
    assertFalse(dogTag.doEqualsTest(t1, t2)); // equality test
    assertFalse(dogTag.doEqualsTest(t2, t1)); // symmetry test
    assertTrue(dogTag.doEqualsTest(t1, t1));  // reflexive test
    assertTrue(dogTag.doEqualsTest(t2, t2));  // reflexive test
    assertNotEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2)); // not strictly required, but it's a good test
    assertFalse(dogTag.doEqualsTest(t1, null));
    assertFalse(dogTag.doEqualsTest(t2, null));
  }
}
