package com.equals;

import static org.junit.Assert.*;

import org.junit.Test;

public class DogTagHashBuilderTest {
  @Test
  public void testHashBuilder() {
    TestClassOne tc1 = new TestClassOne(1, 2, 3);
    DogTag<TestClassOne> dogTag = DogTag.from(TestClassOne.class);
    assertEquals(60921, dogTag.doHashCode(tc1));

    DogTag<TestClassOne> revisedDogTag = DogTag.create(TestClassOne.class)
        .withHashBuilder(1, (int i, Object v) -> i * 4567 + v.hashCode())
        .build();
    assertEquals(787738377, revisedDogTag.doHashCode(tc1));
  }

  private static class TestClassOne {
    int alpha;
    int bravo;
    int charlie;

    public TestClassOne(int alpha, int bravo, int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private static final DogTag<TestClassOne> dogTag = DogTag.create(TestClassOne.class)
        .withExcludedFields("dogTag")
        .build();

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
