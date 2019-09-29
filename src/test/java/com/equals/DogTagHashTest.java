package com.equals;

import java.lang.reflect.Field;

import static com.equals.TestUtility.*; // verifyMatch__() and verifyNoMatch()
import static org.junit.Assert.*;

import org.junit.Test;

/** @noinspection HardCodedStringLiteral*/
public class DogTagHashTest {
  @SuppressWarnings("MagicNumber")
  @Test
  public void testHashBuilder() {
    TestClassOne tc1 = new TestClassOne(1, 2, 3);
    DogTag<TestClassOne> dogTag = DogTag.from(TestClassOne.class);
    assertEquals(30817, dogTag.doHashCode(tc1));

    DogTag<TestClassOne> revisedDogTag = DogTag.create(TestClassOne.class)
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .build();
    assertEquals(787738377, revisedDogTag.doHashCode(tc1));
    
    DogTag<TestClassOne> inclusionDogTag = DogTag.createByInclusion(TestClassOne.class, "alpha", "bravo", "charlie")
        .withHashBuilder(1, (int i, Object v) -> (i * 4567) + v.hashCode())
        .build();
    assertEquals(787738377, inclusionDogTag.doHashCode(tc1));
  }
  
  @Test
  public void testCache() throws NoSuchFieldException, IllegalAccessException {
    DogTag<TestClassWithCache> dogTagFinal = DogTag.create(TestClassWithCache.class, "foxTrot")
        .withCachedHash(true)
        .build();
    CachedHash cachedHash1 = dogTagFinal.makeCachedHash();
    CachedHash cachedHash2 = dogTagFinal.makeCachedHash();
    CachedHash cachedHash3 = dogTagFinal.makeCachedHash();
    CachedHash cachedHash4 = dogTagFinal.makeCachedHash();
    CachedHash cachedHash5 = dogTagFinal.makeCachedHash();

    TestClassWithCache t1 = new TestClassWithCache(1, 2, 3);
    TestClassWithCache t2 = new TestClassWithCache(1, 2, 4);
    TestClassWithCache t3 = new TestClassWithCache(1, 6, 3);
    TestClassWithCache t4 = new TestClassWithCache(5, 2, 3);
    TestClassWithCache t5 = new TestClassWithCache(5, 2, 4);
    
    Field hashField = CachedHash.class.getDeclaredField("hash");
    hashField.setAccessible(true);
    Field setField = CachedHash.class.getDeclaredField("set");
    setField.setAccessible(true);
    
    assertTrue(dogTagFinal.doEqualsTest(t1, t2)); // use equals at least once before testing initial cache

    assertEquals(0, (int) ((Integer) hashField.get(cachedHash1)));
    assertEquals(0, (int) ((Integer) hashField.get(cachedHash2)));
    assertEquals(0, (int) ((Integer) hashField.get(cachedHash3)));
    assertEquals(0, (int) ((Integer) hashField.get(cachedHash4)));
    assertEquals(0, (int) ((Integer) hashField.get(cachedHash5)));
    assertFalse((Boolean) setField.get(cachedHash1));
    assertFalse((Boolean) setField.get(cachedHash2));
    assertFalse((Boolean) setField.get(cachedHash3));
    assertFalse((Boolean) setField.get(cachedHash4));
    assertFalse((Boolean) setField.get(cachedHash5));

    // These verify methods don't use the cached hash. We're just testing that we didn't break the equals() method.
    verifyMatch__(dogTagFinal, t1, t2);
    verifyNoMatch(dogTagFinal, t1, t3);
    verifyNoMatch(dogTagFinal, t1, t4);
    verifyNoMatch(dogTagFinal, t1, t5);
    verifyNoMatch(dogTagFinal, t2, t3);
    verifyNoMatch(dogTagFinal, t2, t4);
    verifyNoMatch(dogTagFinal, t2, t5);
    verifyNoMatch(dogTagFinal, t3, t4);
    verifyNoMatch(dogTagFinal, t3, t5);
    verifyMatch__(dogTagFinal, t4, t5);


    int h1 = dogTagFinal.doHashCode(t1, cachedHash1);
    int h2 = dogTagFinal.doHashCode(t2, cachedHash2);
    int h3 = dogTagFinal.doHashCode(t3, cachedHash3);
    int h4 = dogTagFinal.doHashCode(t4, cachedHash4);
    int h5 = dogTagFinal.doHashCode(t5, cachedHash5);

    assertNotEquals(0, (int) ((Integer) hashField.get(cachedHash1)));
    assertNotEquals(0, (int) ((Integer) hashField.get(cachedHash2)));
    assertNotEquals(0, (int) ((Integer) hashField.get(cachedHash3)));
    assertNotEquals(0, (int) ((Integer) hashField.get(cachedHash4)));
    assertNotEquals(0, (int) ((Integer) hashField.get(cachedHash5)));
    assertTrue((Boolean) setField.get(cachedHash1));
    assertTrue((Boolean) setField.get(cachedHash2));
    assertTrue((Boolean) setField.get(cachedHash3));
    assertTrue((Boolean) setField.get(cachedHash4));
    assertTrue((Boolean) setField.get(cachedHash5));

    assertEquals(h1, dogTagFinal.doHashCode(t1, cachedHash1));
    assertEquals(h2, dogTagFinal.doHashCode(t2, cachedHash2));
    assertEquals(h3, dogTagFinal.doHashCode(t3, cachedHash3));
    assertEquals(h4, dogTagFinal.doHashCode(t4, cachedHash4));
    assertEquals(h5, dogTagFinal.doHashCode(t5, cachedHash5));
    
    // Equal objects should have equal hash codes.
    assertEquals(h1, h2);
    assertEquals(h4, h5);
    
    // Class TestClassWithCache has its own DogTag and CachedHash built-in. The DogTag was built with different options.
    // It specified withFinalFieldsOnly, and left out withCachedHash, which should be set implicitly. Now we repeat 
    // some of those tests with the built-in objects, to make verify the withCachedHash property got set.

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
    
    DogTag<TestClassWithCache> dogTagInclusion = DogTag.createByInclusion(TestClassWithCache.class, "delta", "echo")
        .withCachedHash(true)
        .build();
    
    final CachedHash cachedHash = dogTagInclusion.makeCachedHash();
    h1 = dogTagInclusion.doHashCode(t1, cachedHash);
    //noinspection MagicNumber
    setEcho(t1, 99); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dogTagInclusion.doHashCode(t1, cachedHash));
    //noinspection MagicNumber
    setEcho(t1, 98); // If the hash code is cached, changing an included field shouldn't change the hash code.
    assertEquals(h1, dogTagInclusion.doHashCode(t1, cachedHash));
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

  /**
   * Make sure we get an AssertionError if the CachedHash is declared static
   */
  @Test(expected = AssertionError.class)
  public void testStaticCache() {
    //noinspection ResultOfObjectAllocationIgnored
    new TestStaticCache(5);
  }

  /**
   * Make sure we get an AssertionError if the DogTag uses a non-final field with a CachedHash
   */
  @Test(expected = AssertionError.class)
  public void testNonFinalField() {
    //noinspection ResultOfObjectAllocationIgnored
    new TestNonFinalCache(5);
  }

  /**
   * Make sure we get an AssertionError if the withCachedHash option is not turned on
   */
  @Test(expected = AssertionError.class)
  public void testWithoutCache() {
    //noinspection ResultOfObjectAllocationIgnored
    new TestWithoutCache(5);
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

    private static final DogTag<TestClassOne> dogTag = DogTag.from(TestClassOne.class);

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(this, that);
    }
  }

  /** @noinspection unused*/
  @SuppressWarnings("PackageVisibleField")
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

    private static final DogTag<TestClassWithCache> dogTag = DogTag.create(TestClassWithCache.class)
        .withFinalFieldsOnly(true) // This should implicitly set withCachedHash to true 
        .build();
    
    private final CachedHash cachedHash = dogTag.makeCachedHash();

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this, cachedHash);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(this, that);
    }

    @Override
    public String toString() {
      return cachedHash.toString();
    }
  }
  
  /** @noinspection EqualsWhichDoesntCheckParameterClass, unused */
  private static class TestStaticCache {
    private final int golf;
    TestStaticCache(int golf) {
      this.golf = golf;
    }

    public int getGolf() {
      return golf;
    }

    private static final DogTag<TestStaticCache> dogTag = DogTag.create(TestStaticCache.class)
    .withFinalFieldsOnly(true)
    .build();
    
    private static final CachedHash cachedHash = dogTag.makeCachedHash();

    @Override
    public boolean equals(final Object that) {
      return dogTag.doEqualsTest(this, that);
    }

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this, cachedHash);
    }
  }

  /** @noinspection EqualsWhichDoesntCheckParameterClass, unused */
  private static class TestNonFinalCache {
    private int golf;

    TestNonFinalCache(int golf) {
      this.golf = golf;
    }

    public int getGolf() {
      return golf;
    }

    public void setGolf(final int golf) {
      this.golf = golf;
    }

    private static final DogTag<TestNonFinalCache> dogTag = DogTag.create(TestNonFinalCache.class)
        .withCachedHash(true)
        .build();

    private static final CachedHash cachedHash = dogTag.makeCachedHash();

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this, cachedHash);
    }

    @Override
    public boolean equals(final Object that) {
      return dogTag.doEqualsTest(this, that);
    }
  }
  
  /** @noinspection unused*/
  private static class TestWithoutCache {
    private final int golf;

    TestWithoutCache(final int golf) {
      this.golf = golf;
    }

    public int getGolf() {
      return golf;
    }
    
    private static final DogTag<TestWithoutCache> dogTag = DogTag.from(TestWithoutCache.class);
    private final CachedHash cachedHash = dogTag.makeCachedHash();
  }
}
