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

  static <T> void verifyMatches(DogTag.Factory<T> factory, T t1, T t2) {
    DogTag<T> dt1 = factory.tag(t1);
    DogTag<T> dt2 = factory.tag(t2);

    verifyMatches(dt1, dt2);
  }
  
  static <T> void verifyMatches(DogTag<T> dt1, DogTag<T> dt2) {
    T t1 = dt1.getInstance();
    T t2 = dt2.getInstance();

    String message = dt1.getClass().toString();
    assertEquals(message, dt1, t2); // equality test
    assertEquals(message, dt1.hashCode(), dt2.hashCode()); // hash code consistency test

    testSymmetryReflexiveAndNull(dt1, dt2, t1, t2);
  }

  static <T> void verifyNoMatch(DogTag.Factory<T> factory, T t1, T t2) {
    DogTag<T> dt1 = factory.tag(t1);
    DogTag<T> dt2 = factory.tag(t2);

    verifyNoMatch(dt1, dt2);
  }
  
  static <T> void verifyNoMatch(DogTag<T> dt1, DogTag<T> dt2) {
    T t1 = dt1.getInstance();
    T t2 = dt2.getInstance();

    String message = dt1.getClass().toString();
    assertNotEquals(message, dt1, t2); // equality test
    assertNotEquals(message, dt1.hashCode(), dt2.hashCode()); // not strictly required, but it's a good test
    
    testSymmetryReflexiveAndNull(dt1, dt2, t1, t2);
  }

  private static <T> void testSymmetryReflexiveAndNull(DogTag<T> dt1, DogTag<T> dt2, final T t1, T t2) {
    String message = dt1.getClass().toString();
    assertEquals(message, dt1.equals(t2), dt2.equals(t1)); // symmetry test
    assertEquals(message, dt1, t1);  // reflexive test
    assertEquals(message, dt2, t2);  // reflexive test
    assertNotEquals(message, dt1, null);
    assertNotEquals(message, dt2, null);
    assertNotEquals(message, dt1, "dt1");
    assertNotEquals(message, dt2, "dt2");
  }
}
