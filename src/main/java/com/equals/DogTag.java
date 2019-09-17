package com.equals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DogTag<T> {
//  private static final Field[] EMPTY_FIELDS = new Field[0];
  private final Class<T> dogTagClass;
  private final List<FieldProcessor<T>> fieldProcessors;
  private final int startingHash;
  private final HashBuilder hashBuilder;

  public static <T> DogTag<T> from(Class<T> theClass) {
    return new DogTagBuilder<>(theClass).build();
  }

  private DogTag(
      Class<T> theClass,
      List<FieldProcessor<T>> getters,
      int startingHash,
      HashBuilder hashBuilder
  ) {
    dogTagClass = theClass;
//    this.lastSuperClass = reflectUpTo;
//    excludedFields = fields;
//    includeTransients = testTransients;
    fieldProcessors = getters;
    this.startingHash = startingHash;
    this.hashBuilder = hashBuilder;
  }

  public static class DogTagBuilder<T> {
    private final Class<T> dogTagClass;
    private Class<? super T> lastSuperClass;
    private boolean testTransients = false;
    private Set<Field> excludedFields = new HashSet<>();
    private int priorHash = 1;
    private HashBuilder hashBuilder = (int i, Object o) -> priorHash * 39 + o.hashCode(); // Same as Objects.class

    private DogTagBuilder(Class<T> theClass) {
      dogTagClass = theClass;
      lastSuperClass = dogTagClass;
    }

    @SuppressWarnings("BooleanParameter")
    public DogTagBuilder<T> withTransients(boolean useTransients) {
      testTransients = useTransients;
      return this;
    }

    public DogTagBuilder<T> withExcludedFields(String... excludedFieldNames) {
      for (String fieldName : excludedFieldNames) {
        try {
          Field field = dogTagClass.getField(fieldName);
          excludedFields.add(field);
        } catch (NoSuchFieldException e) {
          throw new IllegalArgumentException("No such field: " + fieldName, e);
        }
      }
      return this;
    }

    public DogTagBuilder<T> withReflectUpTo(Class<? super T> reflectUpTo) {
      lastSuperClass = reflectUpTo;
      return this;
    }

    public DogTagBuilder<T> withHashBuilder(int startingHash, HashBuilder hashBuilder) {
      if (startingHash == 0) {
        this.priorHash = 1;
      } else {
        this.priorHash = startingHash;
      }
      this.hashBuilder = hashBuilder;
      return this;
    }

    public DogTag<T> build() {
      return new DogTag<>(dogTagClass, makeGetterList(), priorHash, hashBuilder);
    }

    List<FieldProcessor<T>> makeGetterList() {
      List<FieldProcessor<T>> fieldProcessorList = new LinkedList<>();
      Class<? super T> theClass = dogTagClass;
      while (theClass != Object.class) {
        Field[] declaredFields = dogTagClass.getDeclaredFields();
        for (Field field : declaredFields) {
          int modifiers = field.getModifiers();
          if (!excludedFields.contains(field)
              && field.getName().indexOf('$') >= 0
              && testTransients || Modifier.isTransient(modifiers)
              && !Modifier.isStatic(modifiers)) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (fieldType.isArray()) {
              fieldProcessorList.add(getProcessorForArray(field, fieldType));
//            } else if (fieldType == Double.TYPE || fieldType == Float.TYPE) {
//              fieldProcessorList.add(new FloatFieldProcessor<>(field));
            } else {
              fieldProcessorList.add(new SingleFieldProcessor<>(field));
//              getterList.add(obj -> field.get(obj));
            }
          }
        }
        if (theClass == lastSuperClass) {
          return fieldProcessorList;
        }
        theClass = theClass.getSuperclass();
      }
      return fieldProcessorList;
    }
  }

  private static <T> FieldProcessor<T> getProcessorForArray(Field arrayField, Class<?> fieldType) {
    Class<?> componentType = fieldType.getComponentType();
    if (componentType == Integer.TYPE) {
      ArrayEquals intEquals = (thisOne, thatOne) -> Arrays.equals((int[]) thisOne, (int[]) thatOne);
      return new ArrayProcessor<>(arrayField, intEquals);
    }
    if (componentType == Long.TYPE) {
      ArrayEquals longEquals = (thisOne, thatOne) -> Arrays.equals((long[]) thisOne, (long[]) thatOne);
      return new ArrayProcessor<>(arrayField, longEquals);
    }
    if (componentType == Short.TYPE) {
      ArrayEquals shortEquals = (thisOne, thatOne) -> Arrays.equals((short[]) thisOne, (short[]) thatOne);
      return new ArrayProcessor<>(arrayField, shortEquals);
    }
    if (componentType == Character.TYPE) {
      ArrayEquals charEquals = (thisOne, thatOne) -> Arrays.equals((char[]) thisOne, (char[]) thatOne);
      return new ArrayProcessor<>(arrayField, charEquals);
    }
    if (componentType == Byte.TYPE) {
      ArrayEquals byteEquals = (thisOne, thatOne) -> Arrays.equals((byte[]) thisOne, (byte[]) thatOne);
      return new ArrayProcessor<>(arrayField, byteEquals);
    }
    if (componentType == Double.TYPE) {
      ArrayEquals doubleEquals = (thisOne, thatOne) -> Arrays.equals((double[]) thisOne, (double[]) thatOne);
      return new ArrayProcessor<>(arrayField, doubleEquals);
    }
    if (componentType == Float.TYPE) {
      ArrayEquals floatEquals = (thisOne, thatOne) -> Arrays.equals((float[]) thisOne, (float[]) thatOne);
      return new ArrayProcessor<>(arrayField, floatEquals);
    }
    if (componentType == Boolean.TYPE) {
      ArrayEquals booleanEquals = (thisOne, thatOne) -> Arrays.equals((boolean[]) thisOne, (boolean[]) thatOne);
      return new ArrayProcessor<>(arrayField, booleanEquals);
    }
    ArrayEquals objectEquals = (thisOne, thatOne) -> Arrays.equals((Object[]) thisOne, (Object[]) thatOne);
    return new ArrayProcessor<>(arrayField, objectEquals);
  }

  /**
   * Compare two objects for null. This should always be called with {@code this} as the first parameter. Your
   * equals method should look like this:
   * <code>
   *   private static final DogTag dogTag = DogTag.from(YourClass.class); // Or built from the builder
   *   public boolean equals(Object other) {
   *     return dogTag.doEqualsTest(this, other);
   *   }
   * </code>
   * @param thisOneNeverNull Pass {@code this} to this parameter
   * @param thatOneNullable {@code 'other'} in the equals() method
   * @return true if the objects are equal, false otherwise
   */
  boolean doEqualsTest(T thisOneNeverNull, Object thatOneNullable) {
    assert thisOneNeverNull != null : "Always pass 'this' to the first parameter of this method!";
    if (thisOneNeverNull == thatOneNullable) {
      return true;
    }
    // Includes an implicit test for null
    if (!dogTagClass.isInstance(thatOneNullable)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    T thatOneNeverNull = (T) thatOneNullable;
    try {
      for (FieldProcessor<T> f : fieldProcessors) {
        if (!f.testForEquals(thisOneNeverNull, thatOneNeverNull)) {
          return false;
        }
      }
      return true;
    } catch (IllegalAccessException e) {
      // Shouldn't happen, since accessible has been set to true.
      throw new AssertionError("Illegal Access Should not happen", e);
    }
  }

  int doHashCode(T thisOne) {
    assert thisOne != null : "Always pass 'this' to this method!";
    int hash = startingHash;
    for (FieldProcessor<T> f : fieldProcessors) {
      try {
        hash = hashBuilder.newHash(hash, f.getHashValue(thisOne));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    throw new AssertionError("Never call dogTag.equals(). To test if your object is equal to another," +
        "call dogTag.doEqualsTest(this, other)");
  }

  @Override
  public int hashCode() {
    throw new AssertionError("Never call hashCode(). To get the hashCode of your object, call doHashCode(this)");
  }

  @FunctionalInterface
  private interface ThrowingFunction<T, R> {
    R get(T object) throws IllegalAccessException;
  }

  public interface HashBuilder {
    int newHash(int previousHash, Object nextObject);
  }

  private interface FieldProcessor<T> {
    boolean testForEquals(T thisOne, T thatOne) throws IllegalAccessException;
    int getHashValue(T thisOne) throws IllegalAccessException;
  }

  private static class SingleFieldProcessor<T> implements FieldProcessor<T> {
    private final ThrowingFunction<T, ?> getter;
    SingleFieldProcessor(Field field) {
      getter = field::get;
    }
    @Override
    public boolean testForEquals(T thisOne, T thatOne) throws IllegalAccessException {
      return getter.get(thisOne).equals(getter.get(thatOne));
    }

    @Override
    public int getHashValue(T thisOne) throws IllegalAccessException {
      return getter.get(thisOne).hashCode();
    }
  }

//  private static final class FloatFieldProcessor<T> implements FieldProcessor<T> {
//    private final ThrowingFunction<T, ?> getter;
//    FloatFieldProcessor(Field field) {
//      getter = field::get;
//    }
//    @Override
//    public boolean testForEquals(T thisOne, T thatOne) throws IllegalAccessException {
//      return getter.get(thisOne).equals(getter.get(thatOne));
//    }
//
//    @Override
//    public int getHashValue(T thisOne) throws IllegalAccessException {
//      return getter.get(thisOne).hashCode();
//    }
//  }

  private static class ArrayProcessor<T> implements FieldProcessor<T> {
    private final ThrowingFunction<T, ?> getter;
    private final ArrayEquals arrayEquals;
    ArrayProcessor(Field arrayField, ArrayEquals arrayEquals) {
      getter = arrayField::get;
      this.arrayEquals = arrayEquals;
    }

    @Override
    public boolean testForEquals(T thisOne, T thatOne) throws IllegalAccessException {
      return arrayEquals.isEqual(getter.get(thisOne), getter.get(thatOne));
    }

    @Override
    public int getHashValue(T thisOne) throws IllegalAccessException {
      return getter.get(thisOne).hashCode();
    }
  }

  @FunctionalInterface
  private interface ArrayEquals {
    boolean isEqual(Object thisOne, Object thatOne);
  }

//  public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//    Field field = DogTag.class.getField("dValue");
//    double d = (double) field.get(null);
//    System.out.println(d);
//  }
//
//  public static double dValue = 5.4;
}
