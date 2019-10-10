package com.equals;

import java.lang.reflect.Field;

import static com.equals.TestUtility.*; // verifyMatch__() and verifyNoMatch()
import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "ResultOfObjectAllocationIgnored", "EqualsWhichDoesntCheckParameterClass"})
public class DogTagHashTest {
  @Test
  public void testHashBuilder() {
    TestClassOne tc1 = new TestClassOne(1, 2, 3);
    DogTag.Factory<TestClassOne> factory = DogTag.create(TestClassOne.class).makeFactory();
    DogTag<TestClassOne> tag = factory.tag(tc1);
    assertEquals(30817, tag.doHashCode());

    DogTag.Factory<TestClassOne> revisedFactory = DogTag.create(TestClassOne.class)
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .makeFactory();
    DogTag<TestClassOne> revisedTag = revisedFactory.tag(tc1);
    assertEquals(787738377, revisedTag.doHashCode());

    DogTag.Factory<TestClassOne> inclusionFactory = DogTag.createByInclusion(TestClassOne.class, "alpha", "bravo", "charlie")
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .makeFactory();
    DogTag<TestClassOne>  inclusionTag = inclusionFactory.tag(tc1);
    assertEquals(787738377, inclusionTag.doHashCode());
  }

  @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
  @Test
  public void testCache() throws NoSuchFieldException, IllegalAccessException {
    DogTag.Factory<TestClassWithCache> factoryFinal = DogTag.create(TestClassWithCache.class, "foxTrot")
        .withCachedHash(true)
        .build();

    TestClassWithCache t1 = new TestClassWithCache(1, 2, 3);
    TestClassWithCache t2 = new TestClassWithCache(1, 2, 4);
    TestClassWithCache t3 = new TestClassWithCache(1, 6, 3);
    TestClassWithCache t4 = new TestClassWithCache(5, 2, 3);
    TestClassWithCache t5 = new TestClassWithCache(5, 2, 4);

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
    verifyMatch__(factoryFinal, t1, t2);
    verifyNoMatch(factoryFinal, t1, t3);
    verifyNoMatch(factoryFinal, t1, t4);
    verifyNoMatch(factoryFinal, t1, t5);
    verifyNoMatch(factoryFinal, t2, t3);
    verifyNoMatch(factoryFinal, t2, t4);
    verifyNoMatch(factoryFinal, t2, t5);
    verifyNoMatch(factoryFinal, t3, t4);
    verifyNoMatch(factoryFinal, t3, t5);
    verifyMatch__(factoryFinal, t4, t5);


    int h1 = dt1.doHashCode();
    int h2 = dt2.doHashCode();
    int h3 = dt3.doHashCode();
    int h4 = dt4.doHashCode();
    int h5 = dt5.doHashCode();

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

    assertEquals(h1, dt1.doHashCode());
    assertEquals(h2, dt2.doHashCode());
    assertEquals(h3, dt3.doHashCode());
    assertEquals(h4, dt4.doHashCode());
    assertEquals(h5, dt5.doHashCode());

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

    DogTag.Factory<TestClassWithCache> factoryInclusion = DogTag.createByInclusion(TestClassWithCache.class, "delta", "echo")
        .withCachedHash(true)
        .build();

    DogTag<TestClassWithCache> dtOne = factoryInclusion.tag(t1);
    h1 = dtOne.doHashCode();
    setEcho(t1, 99); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dtOne.doHashCode());
    setEcho(t1, 98); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dtOne.doHashCode());
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
  private static class TestClassOne {
    int alpha;
    int bravo;
    int charlie;

    TestClassOne(int alpha, int bravo, int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private final DogTag<TestClassOne> dogTag = DogTag.from(TestClassOne.class, this);

    @Override
    public int hashCode() {
      return dogTag.doHashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(that);
    }
  }

  @SuppressWarnings({"PackageVisibleField", "unused"})
  private static class TestClassWithCache {
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

    private final DogTag<TestClassWithCache> dogTag = DogTag.create(TestClassWithCache.class)
        .withFinalFieldsOnly(true) // This should implicitly set withCachedHash to true 
        .build(this);

    @Override
    public int hashCode() {
      return dogTag.doHashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(that);
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
