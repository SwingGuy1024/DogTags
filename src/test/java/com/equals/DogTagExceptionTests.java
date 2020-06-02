package com.equals;

import org.hamcrest.core.StringContains;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 5/27/20
 * <p>Time: 6:06 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class DogTagExceptionTests {
  
  @Test(expected=AssertionError.class)
  public void testStaticDogTag() {
    try {
      StaticDogTag dogTag = new StaticDogTag();
    } catch (AssertionError e) {
      assertThat(e.getMessage(), StringContains.containsString("E8:"));
      throw e;
    }
  }
  
  @Test(expected = AssertionError.class)
  public void testNonStaticFactory() {
    try {
      new NonStaticFactory();
    } catch (AssertionError e) {
      assertThat(e.getMessage(), StringContains.containsString("E9:"));
      throw e;
    }
  }
  
  @Test(expected = AssertionError.class)
  public void testNonFinalCachedValue() {
    try {
      new NonFinalCached();
    } catch (AssertionError e) {
      assertThat(e.getMessage(), StringContains.containsString("E10:"));
      throw e;
    }
  }
  
  private static class StaticDogTag {
    private static final DogTag.Factory<StaticDogTag> factory = DogTag.create(StaticDogTag.class).buildFactory();
    private static final DogTag<StaticDogTag> badDogTag = factory.tag(new StaticDogTag());
  }
  
  private static class NonStaticFactory {
    private final DogTag.Factory<StaticDogTag> factory = DogTag.create(StaticDogTag.class).buildFactory();
  }
  
  private static class NonFinalCached {
    private final int alpha = 0;
    private int bravo = 1;
    private static final DogTag.Factory<NonFinalCached> factory = DogTag
        .create(NonFinalCached.class)
        .withCachedHash(true)
        .buildFactory();
    private final DogTag<NonFinalCached> dogTag = factory.tag(this);
  }
}
