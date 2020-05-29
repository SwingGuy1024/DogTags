package com.equals;

import java.lang.reflect.Field;
import java.util.LinkedList;
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
  @SuppressWarnings("unused")
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
  }

  private <T> void testForOrder(final T orderedClass, final DogTag.Factory<T> factory) {
    try {
      Field fieldProcessorField = DogTag.ReflectiveFactory.class.getDeclaredField("fieldProcessors");
      fieldProcessorField.setAccessible(true);
      @SuppressWarnings("unchecked")
      LinkedList<Object> fieldProcessors = (LinkedList<Object>) fieldProcessorField.get(factory);
      int expectedValue = 0;
      for (Object fieldProcessor : fieldProcessors) {
        DogTag.FieldProcessor fp0 = (DogTag.FieldProcessor) fieldProcessor;
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
}
