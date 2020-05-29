package com.equals;

import java.util.function.Function;

/**
 * Represents a function that produces an doubleArray-valued result.  This is the
 * {@code double[]}-producing primitive specialization for {@link Function}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsDoubleArray(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToDoubleArrayFunction<T> {
  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  double[] applyAsDoubleArray(T value);
}