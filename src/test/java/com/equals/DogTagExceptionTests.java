package com.equals;

import org.hamcrest.core.StringContains;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 5/27/20
 * <p>Time: 6:06 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class DogTagExceptionTests {

  @Test(expected=IllegalArgumentException.class)
  public void testStaticDogTag() {
    try {
      final StaticDogTag dogTag = new StaticDogTag();
    } catch (final ExceptionInInitializerError e) {
      assertThat(e.getCause().getMessage(), StringContains.containsString("E8:"));
      throw (RuntimeException) e.getCause();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonStaticFactory() {
    try {
      new NonStaticFactory();
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E9:"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonStaticLambdaFactory() {
    try {
      new NonStaticLambdaFactory();
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E13:"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingLambdaFactory() {
    try {
      new MissingLambdaFactory();
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E14:"));
      throw e;
    }
  }

  @Test
  public void testNonFinalClassUsingFrom() {
    try {
      new NonFinalClassUsingFrom();
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E11:"));
    }
  }

  private static class StaticDogTag {
    private static final DogTag.Factory<StaticDogTag> factory = DogTag.startWithAll(StaticDogTag.class).build();
    private static final DogTag<StaticDogTag> badDogTag = factory.tag(new StaticDogTag());
  }

  private static class NonStaticFactory {
    private final DogTag.Factory<NonStaticFactory> factory = DogTag.startWithAll(NonStaticFactory.class).build();
  }

  private static class NonStaticLambdaFactory {
    public int getAlpha() {
      return 5;
    }
    private final DogTag.Factory<NonStaticLambdaFactory> factory = DogTag.startEmpty(NonStaticLambdaFactory.class)
        .addSimple(NonStaticLambdaFactory::getAlpha)
        .build();
  }

  private static class MissingLambdaFactory {
    private final DogTag<MissingLambdaFactory> dogTag = DogTag.startEmpty(MissingLambdaFactory.class)
        .build()
        .tag(this);
  }

  private static class NonFinalClassUsingFrom {
    private final int alpha = 0;
    private int bravo = 1;
//    private final DogTag<NonFinalClassUsingFrom> dogTag = DogTag.from(this);
  }
}
