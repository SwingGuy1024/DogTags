package com.equals;

import java.lang.reflect.Field;

// verifyMatch__() and verifyNoMatch()
import static com.equals.TestUtility.verifyMatches;
import static com.equals.TestUtility.verifyNoMatch;

import org.junit.Test;

import static com.equals.DogTag.classFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "EqualsWhichDoesntCheckParameterClass"})
public class DogTagHashTest {
  @Test
  public void testHashBuilder() {
    final TestClassOne tc1 = new TestClassOne(1, 2, 3);
    final DogTag.Factory<TestClassOne> factory = DogTag.startWithAll(classFrom(tc1)).build();
    final DogTag<TestClassOne> tag = factory.tag(tc1);
    assertEquals(30817, tag.hashCode());

    final DogTag.Factory<TestClassOne> revisedFactory = DogTag.startWithAll(classFrom(tc1))
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .build();
    final DogTag<TestClassOne> revisedTag = revisedFactory.tag(tc1);
    assertEquals(787738377, revisedTag.hashCode());

//    DogTag.Factory<TestClassOne> inclusionFactory = DogTag.createByInclusion(classFrom(tc1), "alpha", "bravo", "charlie")
//        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
//        .build();
//    DogTag<TestClassOne>  inclusionTag = inclusionFactory.tag(tc1);
//    assertEquals(787738377, inclusionTag.hashCode());
  }

  @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
  @Test
  public void testCache() throws NoSuchFieldException, IllegalAccessException {

    final TestClassWithCache t123 = new TestClassWithCache(1, 2, 3);
    final TestClassWithCache t124 = new TestClassWithCache(1, 2, 4);
    final TestClassWithCache t163 = new TestClassWithCache(1, 6, 3);
    final TestClassWithCache t523 = new TestClassWithCache(5, 2, 3);
    final TestClassWithCache t524 = new TestClassWithCache(5, 2, 4);

    final DogTag.Factory<TestClassWithCache> factoryFinal = DogTag.startWithAll(classFrom(t123))
        .excludeFields("foxTrot")
        .withCachedHash(true)
        .build();

    final DogTag<TestClassWithCache> dt123 = factoryFinal.tag(t123);
    final DogTag<TestClassWithCache> dt124 = factoryFinal.tag(t124);
    final DogTag<TestClassWithCache> dt163 = factoryFinal.tag(t163);
    final DogTag<TestClassWithCache> dt523 = factoryFinal.tag(t523);
    final DogTag<TestClassWithCache> dt524 = factoryFinal.tag(t524);

    final Field cachedField = DogTag.class.getDeclaredField("cachedHash");
    cachedField.setAccessible(true);

    assertTrue(factoryFinal.doEqualsTest(t123, t124)); // use equals at least once before testing initial cache

    assertEquals(0, cachedField.getInt(t123.dogTag));
    assertEquals(0, cachedField.getInt(t124.dogTag));
    assertEquals(0, cachedField.getInt(t163.dogTag));
    assertEquals(0, cachedField.getInt(t523.dogTag));
    assertEquals(0, cachedField.getInt(t524.dogTag));

    // These verify methods don't use the cached hash. We're just testing that we didn't break the equals() method.
    verifyMatches(factoryFinal, t123, t124);
    verifyNoMatch(factoryFinal, t123, t163);
    verifyNoMatch(factoryFinal, t123, t523);
    verifyNoMatch(factoryFinal, t123, t524);
    verifyNoMatch(factoryFinal, t124, t163);
    verifyNoMatch(factoryFinal, t124, t523);
    verifyNoMatch(factoryFinal, t124, t524);
    verifyNoMatch(factoryFinal, t163, t523);
    verifyNoMatch(factoryFinal, t163, t524);
    verifyMatches(factoryFinal, t523, t524);


    final int h123 = dt123.hashCode();
    final int h124 = dt124.hashCode();
    final int h163 = dt163.hashCode();
    final int h523 = dt523.hashCode();
    final int h524 = dt524.hashCode();

    assertNotEquals(0, cachedField.get(dt123));
    assertNotEquals(0, cachedField.get(dt124));
    assertNotEquals(0, cachedField.get(dt163));
    assertNotEquals(0, cachedField.get(dt523));
    assertNotEquals(0, cachedField.get(dt524));

    assertEquals(h123, factoryFinal.doHashCodeInternal(t123));
    assertEquals(h124, factoryFinal.doHashCodeInternal(t124));
    assertEquals(h163, factoryFinal.doHashCodeInternal(t163));
    assertEquals(h523, factoryFinal.doHashCodeInternal(t523));
    assertEquals(h524, factoryFinal.doHashCodeInternal(t524));

    assertEquals(h123, dt123.hashCode());
    assertEquals(h124, dt124.hashCode());
    assertEquals(h163, dt163.hashCode());
    assertEquals(h523, dt523.hashCode());
    assertEquals(h524, dt524.hashCode());

    // Equal objects should have equal hash codes.
    assertEquals(h123, h124);
    assertEquals(h523, h524);

    // Class TestClassWithCache has its own DogTag that uses a cached hash code. The DogTag factory was built with
    // different options.
    // It specified withFinalFieldsOnly, and left out withCachedHash, which should be set implicitly. Now we repeat
    // some of those tests with the built-in objects, to verify the withCachedHash property got set.

    assertEquals(h123, t123.hashCode());
    assertEquals(h124, t124.hashCode());
    assertEquals(h163, t163.hashCode());
    assertEquals(h523, t523.hashCode());
    assertEquals(h524, t524.hashCode());

    assertEquals(t123.hashCode(), t123.hashCode());
    assertEquals(t124.hashCode(), t124.hashCode());
    assertEquals(t163.hashCode(), t163.hashCode());
    assertEquals(t523.hashCode(), t523.hashCode());
    assertEquals(t524.hashCode(), t524.hashCode());

    assertEquals(t123.toString(), String.valueOf(t123.hashCode()));

    // Test cache in inclusion mode

//    DogTag.Factory<TestClassWithCache> factoryInclusion = DogTag.createByInclusion(classFrom(t123), "delta", "echo")
//        .withCachedHash(true)
//        .getFactory();

//    DogTag<TestClassWithCache> dtOne = factoryInclusion.tag(t123);
//    h123 = dtOne.hashCode();
//    setEcho(t123, 99); // If the hash code is cached, changing an included field shouldn't change the hash code.
//    assertEquals(h123, dtOne.hashCode());
//    setEcho(t123, 98); // If the hash code is cached, changing an included field shouldn't change the hash code.
//    assertEquals(h123, dtOne.hashCode());
  }

  @Test
  public void testLambdaWithHash() {
    final TestClassWithCache c123 = new TestClassWithCache(1, 2, 3);
    final TestClassWithCache c523 = new TestClassWithCache(5, 2, 3);
    final TestClassWithCache c163 = new TestClassWithCache(1, 6, 3);
    final TestClassWithCache c124 = new TestClassWithCache(1, 2, 4);
    final TestClassWithCache cDup = new TestClassWithCache(1, 2, 3);

    final DogTag.Factory<TestClassWithCache> cachedFactory = DogTag.startEmpty(TestClassWithCache.class)
        .addSimple(TestClassWithCache::getDelta)
        .addSimple(TestClassWithCache::getEcho)
        .addSimple(TestClassWithCache::getFoxTrot)
        .withCachedHash(true)
        .build();
    final DogTag.Factory<TestClassWithCache> unCachedFactory = DogTag.startEmpty(TestClassWithCache.class)
        .addSimple(TestClassWithCache::getDelta)
        .addSimple(TestClassWithCache::getEcho)
        .addSimple(TestClassWithCache::getFoxTrot)
        .build();

    verifyNoMatch(cachedFactory, c123, c523);
    verifyNoMatch(cachedFactory, c123, c163);
    verifyNoMatch(cachedFactory, c123, c124);
    verifyMatches(cachedFactory, c123, cDup);

    verifyNoMatch(unCachedFactory, c123, c523);
    verifyNoMatch(unCachedFactory, c123, c163);
    verifyNoMatch(unCachedFactory, c123, c124);
    verifyMatches(unCachedFactory, c123, cDup);

    final DogTag<TestClassWithCache> dt123Cached = cachedFactory.tag(c123);
    final DogTag<TestClassWithCache> dtDupCached = cachedFactory.tag(cDup);
    final DogTag<TestClassWithCache> dt123UnCached = unCachedFactory.tag(c123);
    final DogTag<TestClassWithCache> dtDupUnCached = unCachedFactory.tag(cDup);

    verifyMatches(dt123Cached, dtDupCached);
    verifyMatches(dt123UnCached, dtDupUnCached);
    cDup.setFoxTrot(7);
    assertNotEquals(dt123Cached, dtDupCached);
    assertEquals(dt123Cached.hashCode(), dtDupCached.hashCode());
    assertNotEquals(dt123UnCached, dtDupUnCached);
    assertNotEquals(dt123UnCached.hashCode(), dtDupUnCached.hashCode());
  }

  private void setEcho(final TestClassWithCache instance, final int newValue) {
    try {
      final Field echoField = TestClassWithCache.class.getDeclaredField("echo");
      echoField.setAccessible(true);
      echoField.set(instance, newValue);
    } catch (final NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  @SuppressWarnings({"PackageVisibleField", "ConstantConditions"})
  private static final class TestClassOne {
    int alpha;
    int bravo;
    int charlie;
    private static final DogTag.Factory<?> factory = null; // prevent superfluous test failure

    TestClassOne(final int alpha, final int bravo, final int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private final DogTag<TestClassOne> dogTag = null; // DogTag.from(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return dogTag.equals(that);
    }
  }

  @SuppressWarnings("unused")
  private static final class TestClassWithCache {
    private final int delta;
    private final int echo;
    @DogTagExclude
    private int foxTrot;
    private static final DogTag.Factory<?> unused = null; // prevent superfluous test failure

    TestClassWithCache(final int delta, final int echo, final int foxTrot) {
      this.delta = delta;
      this.echo = echo;
      this.foxTrot = foxTrot;
    }

    public int getDelta() {
      return delta;
    }

    public int getEcho() {
      return echo;
    }

    public int getFoxTrot() {
      return foxTrot;
    }

    @SuppressWarnings("SameParameterValue")
    void setFoxTrot(final int foxTrot) {
      this.foxTrot = foxTrot;
    }

    private final DogTag<TestClassWithCache> dogTag = DogTag.startWithAll(classFrom(this))
        .withCachedHash(true)
        .build()
        .tag(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return dogTag.equals(that);
    }

    @Override
    public String toString() {
      try {
        final Field hashField = DogTag.class.getDeclaredField("cachedHash");
        hashField.setAccessible(true);
        return String.valueOf(hashField.getInt(this.dogTag));
      } catch (final NoSuchFieldException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
  }
}
