package com.equals;

import java.util.function.Function;

/**
 * Represents a function that produces an longArray-valued result.  This is the
 * {@code long[]}-producing primitive specialization for {@link Function}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsLongArray(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToLongArrayFunction<T> {
  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  long[] applyAsLongArray(T value);
}