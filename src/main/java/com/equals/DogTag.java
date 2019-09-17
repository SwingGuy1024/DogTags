package com.equals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Fast equals() and hashCode() methods to use Reflection to easily get all the desired fields, and produce an equals()
 * method and a HashCode method that are guaranteed to be consistent with each other.
 * <p>
 * Unlike Apache's EqualsBuilder, most of the slow reflective calls are not done at runtime, but at class-load time, 
 * so they are only done once for each class. This gives us a big improvement in performance.
 * <p>
 * Example usage:
 * <pre>
 *   public class MyClass {
 *     // Define various fields, getters, and methods here.
 *     
 *     private static final{@literal DogTag<MyClass>} dogTag = DogTag.from(MyClass.class);
 *     
 *    {@literal @Override}
 *     public boolean equals(Object that) {
 *       return dogTag.doEqualsTest(this, that);
 *     }
 *     
 *     public int hashCode() {
 *       return dogTag.doHashCode(this);
 *     }
 *   }
 * </pre>
 * <p>
 * The equals comparison is done according to the guidelines set down in EffectiveJava by Joshua Bloch. The hashCode
 * is generated using the same calculations done with {@code java.lang.Objects.hash(Object...)}, although you are free
 * to provide a different hash calculator. Floats and Doubles are done to avoid problems with comparing Nan values.
 * <p>
 * Options include testing transient fields, excluding fields, and specifying a superclass to include in the 
 * reflective process. These options are invoked by using the DogTagBuilder class.
 * <pre>
 *   public class MyClass extends MyBaseClass {
 *     // Define various fields, getters, and methods here.
 *
 *     private static final{@literal DogTag<MyClass>} dogTag = DogTag.create(MyClass.class)
 *       .withTransients(true)                // defaults to false
 *       .withExcludedFields("size", "date")  // defaults to all non-transient and non-static fields
 *       .withReflectUpTo(MyBaseClass.class)  // defaults to no superclasses included
 *       .build();
 *
 *    {@literal @Override}
 *     public boolean equals(Object that) {
 *       return dogTag.doEqualsTest(this, that);
 *     }
 *
 *     public int hashCode() {
 *       return dogTag.doHashCode(this);
 *     }
 *   }
 * </pre>
 * @param <T>
 */
public class DogTag<T> {
  // Todo: Add a withValidatedExcludedFields()? 
  // Todo: Add a field validator assertion?
  // Todo: Add business key support, using annotations
  // Todo: Add exclusion support using annotations.
  // Todo: Add use-only-final-fields option
  // Todo: Add assertion for static dogTag class?
  private final Class<T> dogTagClass;
  private final List<FieldProcessor<T>> fieldProcessors;
  private final int startingHash;
  private final HashBuilder hashBuilder;

  public static <T> DogTag<T> from(Class<T> theClass) {
    return new DogTagBuilder<>(theClass).build();
  }

  public static <T> DogTagBuilder<T> create(Class<T> theClass) {
    return new DogTagBuilder<>(theClass);
  }

  private DogTag(
      Class<T> theClass,
      List<FieldProcessor<T>> getters,
      int startingHash,
      HashBuilder hashBuilder
  ) {
    dogTagClass = theClass;
    fieldProcessors = getters;
    this.startingHash = startingHash;
    this.hashBuilder = hashBuilder;
  }

  public static final class DogTagBuilder<T> {
    private final Class<T> dogTagClass;
    private Class<? super T> lastSuperClass;
    private boolean testTransients = false;
    private Set<Field> excludedFields = new HashSet<>();
    private int priorHash = 1;
    private HashBuilder hashBuilder = (int i, Object o) -> (priorHash * 39) + o.hashCode(); // Same as Objects.class

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
          //noinspection MagicCharacter
          if (!excludedFields.contains(field)
              && (field.getName().indexOf('$') >= 0)
              && (testTransients || Modifier.isTransient(modifiers))
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
   * <pre>
   *   private static final{@literal DogTag<YourClass>} dogTag = DogTag.from(YourClass.class); // Or built from the builder
   *   public boolean equals(Object that) {
   *     return dogTag.doEqualsTest(this, that);
   *   }
   * </pre>
   * @param thisOneNeverNull Pass {@code this} to this parameter
   * @param thatOneNullable {@code 'other'} in the equals() method
   * @return true if the objects are equal, false otherwise
   */
  boolean doEqualsTest(T thisOneNeverNull, Object thatOneNullable) {
    assert thisOneNeverNull != null : "Always pass 'this' to the first parameter of this method!";
    //noinspection ObjectEquality
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

  /**
   * Get the hashCode from an instance of the containing class, consistent with {@code equals()}
   * For example:
   * <pre>
   *  {@literal @Override}
   *   public int hashCode() {
   *     return dogTag.doHashCode(this);
   *   }
   * </pre>
   * @param thisOne Pass 'this' to this parameter
   * @return The hashCode
   */
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

  @FunctionalInterface
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
      //noinspection EqualsReplaceableByObjectsCall
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
