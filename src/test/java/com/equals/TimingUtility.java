package com.equals;

import java.util.function.BiFunction;
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
public enum TimingUtility {
  ;

  /**
   * Time the class, comparing DogTag, EqualsBuilder, and hand-coding. This tests a single item with an array of that 
   * same kind of item. The array consists of a clone of t1, t1 itself, and a number of objects that differ from t1 by
   * each possible field. The clone should be first, then t13 down to t1, the objects that differ, then t0, for the
   * identity test last. t13 is the element where the difference will be in the field tested last, and t1, it will be
   * tested first. 
   * @param dogTagFactory The DogTag instance
   * @param t0 The instance of T which all the others will be compared against
   * @param instances The array of other instances of T. This should start with a clone of t0, Then go for farthest to
   *                  find a mismatch down to nearest field to find a mismatch, followed by t0 itself.
   * @param directEqual The hand-coded equals method.
   * @param excluded Excluded field names. Should match those specified by the DogTag
   * @param <T> The type being tested
   */
  @SuppressWarnings("StringConcatenation")
  public static <T> void runTestCycles(DogTag.Factory<T> dogTagFactory, final T t0, final T[] instances, final BiFunction<T, T, Boolean> directEqual, String[] excluded) {
    System.out.printf("Java version %s%n", System.getProperty("java.version"));
    @SuppressWarnings({"unchecked", "SuspiciousArrayCast"})
    T[] fullInstances = (T[]) new Object[instances.length+1];
    int ii=0;
    for (T t: instances) {
      fullInstances[ii] = instances[ii];
      ii++;
    }
    fullInstances[ii] = t0;
//    Thread.dumpStack();
    for (int i = 0; i < 4; ++i) {
      System.out.println("Test " + i);
      //noinspection HardcodedFileSeparator
      System.out.printf("%15s \t%5s\t%5s\t%5s\t%5s\t%8s%n", " ", "DgTg", "R.Eq", "HC", "Eq.B", "R.Eq/DgTg");
      int index = 0;
      for (T t : fullInstances) {
        TimingUtility.runTimedTest(dogTagFactory, directEqual, index++, t0, t, 1_000_000, instances.length - 1, excluded);
      }
      System.out.println("\nKey: DgTg: DogTags\n" +
          "     R.Eq: EqualsBuilder.referenceEqual()\n" +
          "       HC: Hand Coded\n" +
          "     Eq.B: new EqualsBuilder()\n");
    }
  }
  
  // TODO: Move this to main package and make doEqualsTest non-public.

  /** @noinspection SameParameterValue*/
  static <T> void runTimedTest(DogTag.Factory<T> dogTag, BiFunction<T, T, Boolean> direct, int i, T t1, T t2, int iterations, int count, String[] excluded) {
    BiFunction<T, T, Boolean> dogTagTest = dogTag::doEqualsTest;
    Runnable dogTagRunner = makeRunner(t1, t2, dogTagTest);
    BiFunction<T, T, Boolean> refEq = (a, b) -> EqualsBuilder.reflectionEquals(a, b, excluded);
    Runnable eBRunner = makeRunner(t1, t2, refEq);
    Runnable directRunner = makeRunner(t1, t2, direct);
    Runnable eB2Runner = makeRunner(t1, t2, Object::equals);
    boolean eq = dogTagTest.apply(t1, t2);

    boolean eqRef = refEq.apply(t1, t2);
    if (eq != eqRef) {
      eq = dogTagTest.apply(t1, t2); // This line is for a breakpoint
      throw new IllegalStateException(String.format("Mismatch with refEq: %b != %b", eq, eqRef));
    }

    boolean eqDirect = direct.apply(t1, t2);
    if (eq != eqDirect) {
      eqDirect = direct.apply(t1, t2); // This line is for a breakpoint
      throw new IllegalStateException(String.format("Mismatch with direct: %b != %b", eq, eqDirect));
    }

    boolean objectEq = t1.equals(t2);
    if (eq != objectEq) {
      objectEq = t1.equals(t2); // This line is for a breakpoint
      throw new IllegalStateException(String.format("Mismatch with EqualBuilder: %b != %b", eq, objectEq));
    }

    long dtTime = time(dogTagRunner, iterations);
    long ebTime = time(eBRunner, iterations);
    long drTime = time(directRunner, iterations);
    long d2Time = time(eB2Runner, iterations);

    String label;
    if (i == 0) {
      label = "All are Equal:";
    } else if (i > count) {
      label = "Identity:";
    } else {
      label = String.format("Fields Tried %2d:", (count + 1) - i);
    }
    System.out.printf("%16s\t%5d\t%5d\t%5d\t%5d\t%8.3f%n", label, dtTime, ebTime, drTime, d2Time, ((double)ebTime)/dtTime); //NON-NLS
  }

  private static <T> Runnable makeRunner(T a, T b, BiFunction<T, T, Boolean> equal) {
    return () -> equal.apply(a, b);
  }

  private static long time(Runnable runner, int count) {
    runner.run(); // load all classes ahead of time.
    runner.run(); // Avoid spurious early results
    long start = System.currentTimeMillis();
    for (int ii = 0; ii < count; ++ii) {
      runner.run();
    }
    long end = System.currentTimeMillis();
    return end - start;
  }

  public static <T> void reverse(T[] a) {
    int tail = a.length;
    int half = tail/2;
    for (int i=0; i<half; ++i) {
      T swap = a[i];
      a[i] = a[--tail];
      a[tail] = swap;
    }
  }
}