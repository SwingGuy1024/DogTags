package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 5/28/20
 * <p>Time: 5:32 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class TestOrderedByAnnotation {
  static class OrderedByAnnotationClass {
    @DogTagInclude(order = 30)
    private final int third=3;

    @DogTagInclude
    private final int fourth=4;

    @DogTagInclude(order = 10)
    private final int first=1;

    @DogTagInclude(order = DogTag.DEFAULT_ORDER_VALUE + 10)
    private final int last=5;

    @DogTagInclude(order = 20)
    private final int second=2;

    private static final DogTag.Factory<OrderedByAnnotationClass> factory = DogTag.createByInclusion(OrderedByAnnotationClass.class).buildFactory();
    
    private final DogTag<OrderedByAnnotationClass> dogTag = factory.tag(this);
  }

  @Test
  public void testOrdering() {
    OrderedByAnnotationClass orderedClass = new OrderedByAnnotationClass();
    DogTag.Factory<OrderedByAnnotationClass> factory = OrderedByAnnotationClass.factory;
    testForOrder(orderedClass, factory);
    DogTag.Factory<OrderedByAnnotationClass> customFactory = DogTag.createByInclusion(OrderedByAnnotationClass.class, TestIncludeWithOrder.class)
        .buildFactory();
    testForOrder(orderedClass, customFactory);
  }

  private <T> void testForOrder(final T orderedClass, final DogTag.Factory<T> factory) {
    try {
      Field fieldProcessorField = DogTag.ReflectiveFactory.class.getDeclaredField("fieldProcessors");
      fieldProcessorField.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Object> fieldProcessors = (List<Object>) fieldProcessorField.get(factory);
      int expectedValue = 0;
      for (Object fieldProcessor : fieldProcessors) {
        DogTag.FieldProcessor<?> fp0 = (DogTag.FieldProcessor<?>) fieldProcessor;
        Field getter = fp0.getClass().getDeclaredField("hashMethod"); // The hash value of an Integer is equal to its value.
        getter.setAccessible(true);
        @SuppressWarnings("unchecked")
        DogTag.ToIntThrowingFunction<T> g = (DogTag.ToIntThrowingFunction<T>) getter.get(fp0);
        int hash = g.get(orderedClass);
        assertEquals(++expectedValue, hash);
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
      fail();
    }
  }

  static class OrderedByCustomAnnotationClass {
    @TestIncludeWithOrder(order = 30)
    private final int third = 2;

    @TestIncludeWithOrder(order = 40)
    private final int fourth = 3;

    @TestIncludeWithOrder // no order is specified, so it uses the annotation's default order of 0
    private final int first = 1;

    @TestIncludeWithOrder(order = DogTag.DEFAULT_ORDER_VALUE + 10)
    private final int last = 4;

    private final int second = 9; // second is unannotated, so it gets skipped.

    private static final DogTag.Factory<OrderedByCustomAnnotationClass> factory
        = DogTag.createByInclusion(OrderedByCustomAnnotationClass.class, TestIncludeWithOrder.class)
        .buildFactory();

    private final DogTag<OrderedByCustomAnnotationClass> dogTag = factory.tag(this);
  }

  @Test
  public void testForCustomOrdering() {
    OrderedByCustomAnnotationClass orderedByClass = new OrderedByCustomAnnotationClass();
    DogTag.Factory<OrderedByCustomAnnotationClass> factory = OrderedByCustomAnnotationClass.factory;
    testForCustomOrder(orderedByClass, factory);
  }
  private <T> void testForCustomOrder(final T orderedClass, final DogTag.Factory<T> factory) {
    try {
      Field fieldProcessorField = DogTag.ReflectiveFactory.class.getDeclaredField("fieldProcessors");
      fieldProcessorField.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Object> fieldProcessors = (List<Object>) fieldProcessorField.get(factory);
      int count = 0;
      for (Object fieldProcessor : fieldProcessors) {
        DogTag.FieldProcessor<?> fp0 = (DogTag.FieldProcessor<?>) fieldProcessor;
        Field getter = fp0.getClass().getDeclaredField("hashMethod"); // The hash value of an Integer is equal to its value.
        getter.setAccessible(true);
        @SuppressWarnings("unchecked")
        DogTag.ToIntThrowingFunction<T> g = (DogTag.ToIntThrowingFunction<T>) getter.get(fp0);
        int hash = g.get(orderedClass);
        assertEquals(++count, hash);
      }
      assertEquals(4, count);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
      fail();
    }
  }


  @Test
  public void testBadAnnotation() {
    try {
      DogTag.createByInclusion(OrderedByAnnotationClass.class, TestIncludeWrongTarget.class)
          .buildFactory();
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E2:"));
    }
  }
  
  @Test
  public void testNotAnnotation() {
    try {
      DogTag.createByNamedAnnotation(OrderedByAnnotationClass.class, OrderedByAnnotationClass.class.getName())
          .buildFactory();
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E5:"));
    }
    
    try {
      DogTag.createByNamedAnnotation(OrderedByAnnotationClass.class, "Not a class name")
          .buildFactory();
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), StringContains.containsString("E4:"));
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface TestIncludeWithOrder {
    int DEFAULT_ORDER_VALUE = 0;

    int order() default DEFAULT_ORDER_VALUE;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface TestIncludeUnordered { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
  public @interface TestIncludeWrongTarget {
  }
}
