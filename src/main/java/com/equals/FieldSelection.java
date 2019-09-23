package com.equals;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/23/19
 * <p>Time: 4:48 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum FieldSelection {
  InclusionMode {
    boolean isFieldIncluded(Field theField, DogTag.DogTagBuilder<?> builder) {
      return builder.isUsedInInclusionMode(theField);
    }
  },
  ExclusionMode {
    @Override
    boolean isFieldIncluded(final Field theField, final DogTag.DogTagBuilder<?> builder) {
      return builder.isUsedInExclusionMode(theField);
    }
  };
  
  abstract boolean isFieldIncluded(Field theField, DogTag.DogTagBuilder<?> builder);
}
