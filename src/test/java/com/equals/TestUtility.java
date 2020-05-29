package com.equals;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/20/19
 * <p>Time: 7:40 PM
 *
 * @author Miguel Mu\u00f1oz
 */
enum TestUtility {
  ;

  static <T> void verifyMatch__(DogTag.Factory<T> factory, T t1, T t2) {
    DogTag<T> dt1 = factory.tag(t1);
    DogTag<T> dt2 = factory.tag(t2);

    assertEquals(dt1, t2); // equality test
    assertEquals(dt1.hashCode(), dt2.hashCode()); // hash code consistency test

    testSymmetryReflexiveAndNull(dt1, dt2, t1, t2);
  }

  static <T> void verifyNoMatch(DogTag.Factory<T> factory, T t1, T t2) {
    DogTag<T> dt1 = factory.tag(t1);
    DogTag<T> dt2 = factory.tag(t2);

    assertNotEquals(dt1, t2); // equality test
    assertNotEquals(dt1.hashCode(), dt2.hashCode()); // not strictly required, but it's a good test
    
    testSymmetryReflexiveAndNull(dt1, dt2, t1, t2);
  }

  private static <T> void testSymmetryReflexiveAndNull(DogTag<T> dt1, DogTag<T> dt2, final T t1, T t2) {
    assertEquals(dt1.equals(t2), dt2.equals(t1)); // symmetry test
    assertEquals(dt1, t1);  // reflexive test
    assertEquals(dt2, t2);  // reflexive test
    assertNotEquals(dt1, null);
    assertNotEquals(dt2, null);
  }
}
