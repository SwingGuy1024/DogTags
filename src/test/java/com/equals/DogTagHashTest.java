package com.equals;

import static org.junit.Assert.*;

import org.junit.Test;

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
  }
  
  @Test
  public void testCache() {
    DogTag<TestClassWithCache> dogTagFinal = DogTag.create(TestClassWithCache.class)
        .withFinalFieldsOnly(true)
        .build();
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

  @SuppressWarnings("PackageVisibleField")
  private static class TestClassWithCache {
    private final int alpha;
    private final int echo;
    private int foxTrot;

    TestClassWithCache(int alpha, int echo, int foxTrot) {
      this.alpha = alpha;
      this.echo = echo;
      this.foxTrot = foxTrot;
    }

    public int getAlpha() {
      return alpha;
    }

    public int getEcho() {
      return echo;
    }

    public int getFoxTrot() {
      return foxTrot;
    }

    public void setFoxTrot(final int foxTrot) {
      this.foxTrot = foxTrot;
    }

    private static final DogTag<TestClassWithCache> dogTag = DogTag.from(TestClassWithCache.class);

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
}
