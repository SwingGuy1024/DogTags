package com.equals;

import java.util.function.Function;

/**
 * Represents a function that produces an byteArray-valued result.  This is the
 * {@code byte[]}-producing primitive specialization for {@link Function}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsByteArray(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToByteArrayFunction<T> {
  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  byte[] applyAsByteArray(T value);
}