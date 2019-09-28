package com.equals.performance;

import java.util.function.BiFunction;
import com.equals.DogTag;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/20/19
 * <p>Time: 7:40 PM
 *
 * @author Miguel Mu\u00f1oz
 * @noinspection HardcodedLineSeparator, MagicNumber
 */
@SuppressWarnings("HardCodedStringLiteral")
enum TimingUtility {
  ;

  /**
   * Time the class, comparing DogTag, EqualsBuilder, and hand-coding. This tests a single item with an array of that 
   * same kind of item. The array consists of a clone of t1, t1 itself, and a number of objects that differ from t1 by
   * each possible field. The clone should be first, then t13 down to t1, the objects that differ, then t0, for the
   * identity test last. t13 is the element where the difference will be in the field tested last, and t1, it will be
   * tested first. 
   * @param dogTag The DogTag instance
   * @param t0 The instance of T which all the others will be compared against
   * @param instances The array of other instances of T. This should start with a clone of t0, Then go for farthest to
   *                  find a mismatch down to nearest field to find a mismatch, followed by t0 itself.
   * @param directEqual The hand-coded equals method.
   * @param excluded Excluded field names. Should match those specified by the DogTag
   * @param <T> The type being tested
   */
  static <T> void runTestCycles(DogTag<T> dogTag, final T t0, final T[] instances, final BiFunction<T, T, Boolean> directEqual, String[] excluded) {
    for (int i = 0; i < 4; ++i) {
      //noinspection StringConcatenation
      System.out.println("Test " + i);
      int index = 0;
      for (T t : instances) {
        TimingUtility.runTimedTest(dogTag, directEqual, index++, t0, t, 1000000, instances.length - 2, excluded);
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
    System.out.printf("%16s\t%5d\t%5d\t%5d\t%8.3f%n", label, dtTime, ebTime, drTime, ((double)ebTime)/dtTime); //NON-NLS
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
}
