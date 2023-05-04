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
  @SafeVarargs
  @SuppressWarnings("StringConcatenation")
  public static <T> void runTestCycles(
      final DogTag.Factory<T> dogTagFactory,
      final T t0,
      final T[] instances,
      final BiFunction<T, T, Boolean> directEqual, final String[] excluded, final DogTag.Factory<T>... moreFactories) {
    System.out.printf("Java version %s%n", System.getProperty("java.version"));
    @SuppressWarnings({"unchecked", "SuspiciousArrayCast"}) final T[] fullInstances = (T[]) new Object[instances.length+1];
    int ii=0;
    for (final T t: instances) {
      fullInstances[ii++] = t;
    }
    fullInstances[ii] = t0;
//    Thread.dumpStack();
    for (int i = 0; i < 4; ++i) {
      System.out.printf("Test %d -  %s%n", i, t0.getClass());
      //noinspection HardcodedFileSeparator
      System.out.printf("%15s \t%5s\t%5s\t%5s\t%5s\t", " ", "DgTg", "R.Eq", "HC", "Eq.B");
      for (int tab=0; tab<moreFactories.length; ++tab) {
        System.out.print("     \t");
      }
      System.out.println("R.Eq/DgTg");
      int index = 0;
      for (final T t : fullInstances) {
        TimingUtility.runTimedTest(dogTagFactory, directEqual, index++, t0, t, 1_000_000, instances.length - 1, excluded, moreFactories);
      }
      System.out.println("\nKey: DgTg: DogTags\n" +
          "     R.Eq: EqualsBuilder.referenceEqual()\n" +
          "       HC: Hand Coded\n" +
          "     Eq.B: new EqualsBuilder()\n");
    }
  }

  // TODO: Move this to main package and make doEqualsTest non-public.

  /** @noinspection SameParameterValue*/
  @SafeVarargs
  static <T> void runTimedTest(
      final DogTag.Factory<T> dogTag,
      final BiFunction<T, T, Boolean> direct,
      final int i,
      final T t1,
      final T t2,
      final int iterations,
      final int count,
      final String[] excluded,
      final DogTag.Factory<T>... more) {
    final BiFunction<T, T, Boolean> dogTagTest = dogTag::doEqualsTest;
    final Runnable dogTagRunner = makeRunner(t1, t2, dogTagTest);
    final BiFunction<T, T, Boolean> refEq = (a, b) -> EqualsBuilder.reflectionEquals(a, b, excluded);
    final Runnable eBRunner = makeRunner(t1, t2, refEq);
    final Runnable directRunner = makeRunner(t1, t2, direct);
    final Runnable eB2Runner = makeRunner(t1, t2, Object::equals);
    final Runnable[] moreRunners = new Runnable[more.length];
    int index = 0;
    for (final DogTag.Factory<T> extra: more) {
      final BiFunction<T, T, Boolean> nextTest = extra::doEqualsTest;
      moreRunners[index++] = makeRunner(t1, t2, nextTest);
    }
    boolean eq = dogTagTest.apply(t1, t2);

    final boolean eqRef = refEq.apply(t1, t2);
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

    final long dtTime = time(dogTagRunner, iterations);
    final long ebTime = time(eBRunner, iterations);
    final long drTime = time(directRunner, iterations);
    final long d2Time = time(eB2Runner, iterations);
    final long[] extraTimes = new long[more.length];
    index = 0;
    for (final Runnable runner: moreRunners) {
      extraTimes[index++] = time(runner, iterations);
    }

    final String label;
    if (i == 0) {
      label = "All are Equal:";
    } else if (i > count) {
      label = "Identity:";
    } else {
      label = String.format("Fields Tried %2d:", (count + 1) - i);
    }
    System.out.printf("%16s\t%5d\t%5d\t%5d\t%5d\t", label, dtTime, ebTime, drTime, d2Time); //NON-NLS
    for (final long time: extraTimes) {
      System.out.printf("%5d\t", time);
    }
    System.out.printf("%8.3f%n", ((double) ebTime) / dtTime);
  }

  private static <T> Runnable makeRunner(final T a, final T b, final BiFunction<T, T, Boolean> equal) {
    return () -> equal.apply(a, b);
  }

  private static long time(final Runnable runner, final int count) {
    runner.run(); // load all classes ahead of time.
    runner.run(); // Avoid spurious early results
    final long start = System.currentTimeMillis();
    for (int ii = 0; ii < count; ++ii) {
      runner.run();
    }
    final long end = System.currentTimeMillis();
    return end - start;
  }

  public static <T> void reverse(final T[] a) {
    int tail = a.length;
    final int half = tail/2;
    for (int i=0; i<half; ++i) {
      final T swap = a[i];
      a[i] = a[--tail];
      a[tail] = swap;
    }
  }
}
