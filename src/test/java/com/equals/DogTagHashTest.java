package com.equals;

import java.lang.reflect.Field;

import static com.equals.TestUtility.*; // verifyMatch__() and verifyNoMatch()
import static org.junit.Assert.*;

import org.junit.Test;

import static com.equals.DogTag.classFrom;

@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "EqualsWhichDoesntCheckParameterClass"})
public class DogTagHashTest {
  @Test
  public void testHashBuilder() {
    TestClassOne tc1 = new TestClassOne(1, 2, 3);
    DogTag.Factory<TestClassOne> factory = DogTag.create(classFrom(tc1)).buildFactory();
    DogTag<TestClassOne> tag = factory.tag(tc1);
    assertEquals(30817, tag.hashCode());

    DogTag.Factory<TestClassOne> revisedFactory = DogTag.create(classFrom(tc1))
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .buildFactory();
    DogTag<TestClassOne> revisedTag = revisedFactory.tag(tc1);
    assertEquals(787738377, revisedTag.hashCode());

    DogTag.Factory<TestClassOne> inclusionFactory = DogTag.createByInclusion(classFrom(tc1), "alpha", "bravo", "charlie")
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .buildFactory();
    DogTag<TestClassOne>  inclusionTag = inclusionFactory.tag(tc1);
    assertEquals(787738377, inclusionTag.hashCode());
  }

  @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
  @Test
  public void testCache() throws NoSuchFieldException, IllegalAccessException {

    TestClassWithCache t1 = new TestClassWithCache(1, 2, 3);
    TestClassWithCache t2 = new TestClassWithCache(1, 2, 4);
    TestClassWithCache t3 = new TestClassWithCache(1, 6, 3);
    TestClassWithCache t4 = new TestClassWithCache(5, 2, 3);
    TestClassWithCache t5 = new TestClassWithCache(5, 2, 4);

    DogTag.Factory<TestClassWithCache> factoryFinal = DogTag.create(classFrom(t1), "foxTrot")
        .withCachedHash(true)
        .getFactory();

    DogTag<TestClassWithCache> dt1 = factoryFinal.tag(t1);
    DogTag<TestClassWithCache> dt2 = factoryFinal.tag(t2);
    DogTag<TestClassWithCache> dt3 = factoryFinal.tag(t3);
    DogTag<TestClassWithCache> dt4 = factoryFinal.tag(t4);
    DogTag<TestClassWithCache> dt5 = factoryFinal.tag(t5);
    
    Field cachedField = DogTag.class.getDeclaredField("cachedHash");
    cachedField.setAccessible(true);

    assertTrue(factoryFinal.doEqualsTest(t1, t2)); // use equals at least once before testing initial cache

    assertEquals(0, cachedField.getInt(t1.dogTag));
    assertEquals(0, cachedField.getInt(t2.dogTag));
    assertEquals(0, cachedField.getInt(t3.dogTag));
    assertEquals(0, cachedField.getInt(t4.dogTag));
    assertEquals(0, cachedField.getInt(t5.dogTag));

    // These verify methods don't use the cached hash. We're just testing that we didn't break the equals() method.
    verifyMatches(factoryFinal, t1, t2);
    verifyNoMatch(factoryFinal, t1, t3);
    verifyNoMatch(factoryFinal, t1, t4);
    verifyNoMatch(factoryFinal, t1, t5);
    verifyNoMatch(factoryFinal, t2, t3);
    verifyNoMatch(factoryFinal, t2, t4);
    verifyNoMatch(factoryFinal, t2, t5);
    verifyNoMatch(factoryFinal, t3, t4);
    verifyNoMatch(factoryFinal, t3, t5);
    verifyMatches(factoryFinal, t4, t5);


    int h1 = dt1.hashCode();
    int h2 = dt2.hashCode();
    int h3 = dt3.hashCode();
    int h4 = dt4.hashCode();
    int h5 = dt5.hashCode();

    assertNotEquals(0, cachedField.get(dt1));
    assertNotEquals(0, cachedField.get(dt2));
    assertNotEquals(0, cachedField.get(dt3));
    assertNotEquals(0, cachedField.get(dt4));
    assertNotEquals(0, cachedField.get(dt5));
    
    assertEquals(h1, factoryFinal.doHashCodeInternal(t1));
    assertEquals(h2, factoryFinal.doHashCodeInternal(t2));
    assertEquals(h3, factoryFinal.doHashCodeInternal(t3));
    assertEquals(h4, factoryFinal.doHashCodeInternal(t4));
    assertEquals(h5, factoryFinal.doHashCodeInternal(t5));

    assertEquals(h1, dt1.hashCode());
    assertEquals(h2, dt2.hashCode());
    assertEquals(h3, dt3.hashCode());
    assertEquals(h4, dt4.hashCode());
    assertEquals(h5, dt5.hashCode());

    // Equal objects should have equal hash codes.
    assertEquals(h1, h2);
    assertEquals(h4, h5);

    // Class TestClassWithCache has its own DogTag that uses a cached hash code. The DogTag factory was built with 
    // different options.
    // It specified withFinalFieldsOnly, and left out withCachedHash, which should be set implicitly. Now we repeat 
    // some of those tests with the built-in objects, to verify the withCachedHash property got set.

    assertEquals(h1, t1.hashCode());
    assertEquals(h2, t2.hashCode());
    assertEquals(h3, t3.hashCode());
    assertEquals(h4, t4.hashCode());
    assertEquals(h5, t5.hashCode());

    assertEquals(t1.hashCode(), t1.hashCode());
    assertEquals(t2.hashCode(), t2.hashCode());
    assertEquals(t3.hashCode(), t3.hashCode());
    assertEquals(t4.hashCode(), t4.hashCode());
    assertEquals(t5.hashCode(), t5.hashCode());

    assertEquals(t1.toString(), String.valueOf(t1.hashCode()));

    // Test cache in inclusion mode

    DogTag.Factory<TestClassWithCache> factoryInclusion = DogTag.createByInclusion(classFrom(t1), "delta", "echo")
        .withCachedHash(true)
        .getFactory();

    DogTag<TestClassWithCache> dtOne = factoryInclusion.tag(t1);
    h1 = dtOne.hashCode();
    setEcho(t1, 99); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dtOne.hashCode());
    setEcho(t1, 98); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dtOne.hashCode());
  }
  
  @Test
  public void testLambdaWithHash() {
    TestClassWithCache c123 = new TestClassWithCache(1, 2, 3);
    TestClassWithCache c523 = new TestClassWithCache(5, 2, 3);
    TestClassWithCache c163 = new TestClassWithCache(1, 6, 3);
    TestClassWithCache c124 = new TestClassWithCache(1, 2, 4);
    TestClassWithCache cDup = new TestClassWithCache(1, 2, 3);
    
    DogTag.Factory<TestClassWithCache> cachedFactory = DogTag.createByLambda(TestClassWithCache.class)
        .add(TestClassWithCache::getDelta)
        .add(TestClassWithCache::getEcho)
        .add(TestClassWithCache::getFoxTrot)
        .withCachedHash(true)
        .buildFactory();
    DogTag.Factory<TestClassWithCache> unCachedFactory = DogTag.createByLambda(TestClassWithCache.class)
        .add(TestClassWithCache::getDelta)
        .add(TestClassWithCache::getEcho)
        .add(TestClassWithCache::getFoxTrot)
        .buildFactory();
    
    verifyNoMatch(cachedFactory, c123, c523);
    verifyNoMatch(cachedFactory, c123, c163);
    verifyNoMatch(cachedFactory, c123, c124);
    verifyMatches(cachedFactory, c123, cDup);

    verifyNoMatch(unCachedFactory, c123, c523);
    verifyNoMatch(unCachedFactory, c123, c163);
    verifyNoMatch(unCachedFactory, c123, c124);
    verifyMatches(unCachedFactory, c123, cDup);

    DogTag<TestClassWithCache> dt123Cached = cachedFactory.tag(c123);
    DogTag<TestClassWithCache> dtDupCached = cachedFactory.tag(cDup);
    DogTag<TestClassWithCache> dt123UnCached = unCachedFactory.tag(c123);
    DogTag<TestClassWithCache> dtDupUnCached = unCachedFactory.tag(cDup);

    verifyMatches(dt123Cached, dtDupCached);
    verifyMatches(dt123UnCached, dtDupUnCached);
    cDup.setFoxTrot(7);
    assertNotEquals(dt123Cached, dtDupCached);
    assertEquals(dt123Cached.hashCode(), dtDupCached.hashCode());
    assertNotEquals(dt123UnCached, dtDupUnCached);
    assertNotEquals(dt123UnCached.hashCode(), dtDupUnCached.hashCode());
  }

  @Test(expected = AssertionError.class)
  public void testNonFinalCache() {
    TestClassWithCache tc1 = new TestClassWithCache(1, 2, 3);
    DogTag.create(classFrom(tc1))
        .withCachedHash(true)
        .buildFactory();
  }

  private void setEcho(TestClassWithCache instance, int newValue) {
    try {
      Field echoField = TestClassWithCache.class.getDeclaredField("echo");
      echoField.setAccessible(true);
      echoField.set(instance, newValue);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  @SuppressWarnings("PackageVisibleField")
  private static final class TestClassOne {
    int alpha;
    int bravo;
    int charlie;

    TestClassOne(int alpha, int bravo, int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private final DogTag<TestClassOne> dogTag = DogTag.from(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.equals(that);
    }
  }

  @SuppressWarnings("unused")
  private static final class TestClassWithCache {
    private final int delta;
    private final int echo;
    private int foxTrot;

    TestClassWithCache(int delta, int echo, int foxTrot) {
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

    void setFoxTrot(final int foxTrot) {
      this.foxTrot = foxTrot;
    }

    private final DogTag<TestClassWithCache> dogTag = DogTag.create(classFrom(this))
        .withFinalFieldsOnly(true) // This should implicitly set withCachedHash to true 
        .buildFactory()
        .tag(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.equals(that);
    }

    @Override
    public String toString() {
      try {
        Field hashField = DogTag.class.getDeclaredField("cachedHash");
        hashField.setAccessible(true);
        return String.valueOf(hashField.getInt(this.dogTag));
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
  }
}
