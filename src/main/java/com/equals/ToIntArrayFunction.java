package com.equals;

import java.util.function.Function;

/**
 * Represents a function that produces an intArray-valued result.  This is the
 * {@code int[]}-producing primitive specialization for {@link Function}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsIntArray(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToIntArrayFunction<T> {
  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  int[] applyAsIntArray(T value);
}
