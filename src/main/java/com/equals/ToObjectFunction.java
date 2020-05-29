package com.equals;

import java.util.function.Function;

/**
 * Represents a function that produces an Object-valued result.  This is the
 * {@code Object}-producing primitive specialization for {@link Function}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsObject(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToObjectFunction<T> {

  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  Object applyAsObject(T value);
}
