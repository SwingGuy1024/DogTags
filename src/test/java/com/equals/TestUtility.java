package com.equals;

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

  static <T> void verifyMatch__(DogTag.Factory<T> factory, T t1, T t2) {

    assertTrue(factory.doEqualsTest(t1, t2));  // equality test
    assertTrue(factory.doEqualsTest(t2, t1));  // symmetry test
    assertEquals(factory.doHashCodeInternal(t1), factory.doHashCodeInternal(t2)); // hash code consistency test

    testReflexAndNull(factory, t1, t2);
  }

  static <T> void verifyNoMatch(DogTag.Factory<T> factory, T t1, T t2) {
    assertFalse(factory.doEqualsTest(t1, t2)); // equality test
    assertFalse(factory.doEqualsTest(t2, t1)); // symmetry test
    assertNotEquals(factory.doHashCodeInternal(t1), factory.doHashCodeInternal(t2)); // not strictly required, but it's a good test

    testReflexAndNull(factory, t1, t2);
  }

  private static <T> void testReflexAndNull(DogTag.Factory<T> factory, final T t1, T t2) {
    assertTrue(factory.doEqualsTest(t1, t1));  // reflexive test
    assertTrue(factory.doEqualsTest(t2, t2));  // reflexive test
    assertFalse(factory.doEqualsTest(t1, null));
    assertFalse(factory.doEqualsTest(t2, null));
  }
}
