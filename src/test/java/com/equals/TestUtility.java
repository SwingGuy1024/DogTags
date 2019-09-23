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

  static <T> void runTestCycles(DogTag<T> dogTag, final T t0, final T[] instances, final BiFunction<T, T, Boolean> directEqual, String[] excluded) {
    for (int i = 0; i < 4; ++i) {
      //noinspection StringConcatenation
      System.out.println("Test " + i);
      int index = 0;
      for (T t : instances) {
        TestUtility.runTimedTest(dogTag, directEqual, index++, t0, t, 1000000, instances.length - 2, excluded);
      }
      System.out.println("\n\n");
    }
  }

  /** @noinspection SameParameterValue*/
  static <T> void runTimedTest(DogTag<T> dogTag, BiFunction<T, T, Boolean> direct, int i, T t1, T t2, int iterations, int count, String[] excluded) {
    BiFunction<T, T, Boolean> dogTagTest = dogTag::doEqualsTest;
    Runnable dogTagRunner = makeRunner(t1, t2, dogTagTest);
    Runnable eBRunner = makeRunner(t1, t2, (a, b) -> EqualsBuilder.reflectionEquals(a, b, excluded));
    Runnable directRunner = makeRunner(t1, t2, direct);
    long dtTime = time(dogTagRunner, iterations);
    long ebTime = time(eBRunner, iterations);
    long drTime = time(directRunner, iterations);

    String label;
    if (i == 0) {
      label = "All are Equal:";
    } else if (i > count) {
      label = "Identity:";
    } else {
      label = String.format("Fields Tried %2d:", (count + 1) - i);
    }
    System.out.printf("%16s\t%5d\t%5d\t%5d%n", label, dtTime, ebTime, drTime); //NON-NLS
  }
  
  private static <T> Runnable makeRunner(T a, T b, BiFunction<T, T, Boolean> equal) {
    return () -> equal.apply(a, b);
  }

  private static long time(Runnable runner, int count) {
    runner.run(); // load all classes ahead of time.
    long start = System.currentTimeMillis();
    for (int ii = 0; ii < count; ++ii) {
      runner.run();
    }
    long end = System.currentTimeMillis();
    return end - start;
  }

  static <T> void reverse(T[] a) {
    int tail = a.length;
    int half = tail/2;
    for (int i=0; i<half; ++i) {
      T swap = a[i];
      a[i] = a[--tail];
      a[tail] = swap;
    }
  }

  static <T> void verifyMatch(DogTag<T> dogTag, T t1, T t2) {
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
