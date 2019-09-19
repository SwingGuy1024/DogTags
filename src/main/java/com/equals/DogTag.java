package com.equals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Fast {@code equals()} and {@code hashCode()} methods that use Reflection to easily get all the desired fields,
 * and produce an {@code equals()} method and a {@code hashCode()} method that are guaranteed to be consistent with
 * each other.
 * <p>
 * Unlike Apache's EqualsBuilder, most of the slow reflective calls are not done when {@code equals()} is called,
 * but at class-load time, so they are only done once for each class. This gives us a big improvement in performance.
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
 * to provide a different hash calculator. Floats and Doubles will treat all NaN values as equal.
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
 *       .withReflectUpTo(MyBaseClass.class)  // defaults to the type parameter, which is MyClass in this example
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
 * <p>
 * <strong>Notes:</strong><p>
 *   The DogTag instance should always be declared static, or performance will suffer. It's prudent to also declare it
 *   final, since you shouldn't ever need to change its value.
 * <p>
 *   The DogTag methods {@code equals()} and {@code hashCode()} are disabled, to prevent their accidental use instead
 *   of {@code doEqualsTest()} and {@code doHashCode()} The two correct methods both start with "do" to avoid
 *   confusion. If you accidentally call either of the disabled methods, they will throw an {@code AssertionError}.
 * <p>
 *   You should always pass {@code this} as the first parameter of {@code doEqualsTest()} and as the parameter of
 *   {@code doHashCode()}.
 * <p>
 *   Static fields are never used.
 * @param <T> Type using a DogTag equals and hashCode method
 * @author Miguel Muñoz
 */
@SuppressWarnings("WeakerAccess")
public final class DogTag<T> {
  // Todo: Add business key support, using annotations
  // Todo: Add exclusion support using annotations.
  // Todo: Specify field order by annotations?
  // Todo: new additive create() class that includes nothing by default.
  private final Class<T> targetClass;
  private final List<FieldProcessor<T>> fieldProcessors;
  private final int startingHash;
  private final HashBuilder hashBuilder;

  /**
   * Instantiate a DogTag for class T, using default options. The default options are: No transient fields,
   * all other non-static fields are included, and no superclass fields are included.
   * @param theClass The instance of {@literal Class<T>} for type T
   * @param <T> The type of class using equals() and hashCode()
   * @return An instance of {@literal DogTag<T>}
   */
  public static <T> DogTag<T> from(Class<T> theClass) {
    return new DogTagBuilder<>(theClass).build();
  }

  /**
   * Instantiate a builder, allowing you to create a DogTag for class T with custom options specified. From this
   * builder, you should call the build() method to generate the dogTag.
   * <p>
   *   For example:
   *   <pre>
   *     {@literal DogTag<MyClass>} dogTag = DogTag.create(MyClass.class)
   *         .withTransients(true) // options are specified here
   *         .build();
   *   </pre>
   * <p>
   *   Options may be specified in any order.
   * @param theClass The instance of {@literal Class<T>} for type T
   * @param <T> The type of class using equals() and hashCode()
   * @return A builder for a {@literal DogTag<T>}, which can build your DogTag, once the options are specified.
   */
  public static <T> DogTagBuilder<T> create(Class<T> theClass) {
    return new DogTagBuilder<>(theClass);
  }

  private DogTag(
      Class<T> theClass,
      List<FieldProcessor<T>> getters,
      int startingHash,
      HashBuilder hashBuilder
  ) {
    targetClass = theClass;
    fieldProcessors = getters;
    this.startingHash = startingHash;
    this.hashBuilder = hashBuilder;
  }

  public static final class DogTagBuilder<T> {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final Class<T> targetClass;
    private Class<? super T> lastSuperClass;
    private boolean testTransients = false;
    private String[] excludedFieldNames = EMPTY_STRING_ARRAY;
    private int startingHash = 1;
    private boolean finalFieldsOnly = false;
    @SuppressWarnings("MagicNumber")
    private static final HashBuilder defaultHashBuilder = (int i, Object o) -> (i * 31) + o.hashCode(); // Same as Objects.class
    private HashBuilder hashBuilder = defaultHashBuilder; // Reuse the same HashBuilder

    private DogTagBuilder(Class<T> theClass) {
      targetClass = theClass;
      lastSuperClass = targetClass;
    }

    /**
     * Set the Transient option, before building the DogTag. Defaults to false. When true, transient fields will be
     * included, provided they meet all the other criteria. For example, if the finalFieldsOnly option is also true, 
     * then only final and final transient fields are included.
     * @param useTransients true if you want transient fields included in the equals and hashCode methods
     * @return this, for method chaining
     */
    @SuppressWarnings("BooleanParameter")
    public DogTagBuilder<T> withTransients(boolean useTransients) {
      testTransients = useTransients;
      return this;
    }

    /**
     * Specify the optional excluded fields, before building the DogTag.
     * The fields must be in the class specified by the type parameter for the DogTag, or any superclass included by
     * the {@code withReflectUpTo()} option. Defaults to an empty array.
     * <strong>Note:</strong> If you are also using the reflectUpTo option, this option must be specified afterwards.
     * @param excludedFieldNames The names of fields to exclude from the equals and hashCode calculations.
     *                           Invalid field names will throw an IllegalArgumentException.
     * @return this, for method chaining
     */
    public DogTagBuilder<T> withExcludedFields(String... excludedFieldNames) {
      //noinspection AssignmentOrReturnOfFieldWithMutableType
      this.excludedFieldNames = excludedFieldNames;
      return this;
    }

    /**
     * Search through classes starting with dogTagClass, up to and including lastSuperClass, for a field with the
     * specified name.
     * Can this be defeated by clashing private fields?
     * @param fieldName The Field name
     * @return A Field, from one of the classes in the range from dogTagClass to lastSuperClass.
     */
    private Field getFieldFromName(String fieldName) {
      Class<?> theClass = targetClass;
      boolean inProgress = true;
      while (inProgress) {
        if (theClass == lastSuperClass) {
          inProgress = false;
        }
        try {
          return theClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
          theClass = theClass.getSuperclass();
        }
      }

      // If we only searched through one class...
      if (targetClass == lastSuperClass) {
        // ... we send a simpler error message.
        throw new IllegalArgumentException(String.format("Field %s not found in class %s", fieldName, targetClass));
      }
      throw new IllegalArgumentException(String.format("Field '%s' not found from class %s to superClass %s", fieldName, targetClass, lastSuperClass));
    }

    /**
     * Specify the superclass of the DogTag Type parameter class to include in the equals and hash code calculations.
     * The classes inspected for fields to use are those of the type class, the specified superclass, and any
     * class that is a superclass of the type class and a subclass of the specified superclass.
     * @param reflectUpTo The superclass, up to which are inspected for fields to include.
     * @return this, for method chaining
     */
    public DogTagBuilder<T> withReflectUpTo(Class<? super T> reflectUpTo) {
      lastSuperClass = reflectUpTo;
      return this;
    }

    /**
     * Specify a custom formula for building a single hash value out of a series of hash values. The default
     * formula matches the one used by java.util.Objects.hash(Object...)
     * @param startingHash The starting value.
     * @param hashBuilder The formula for adding additional hash values.
     * @return this, for method chaining
     * @see HashBuilder
     */
    public DogTagBuilder<T> withHashBuilder(int startingHash, HashBuilder hashBuilder) {
      this.startingHash = startingHash;
      this.hashBuilder = hashBuilder;
      return this;
    }

    /**
     * Set the finalFieldsOnly option, before building the DogTag. Defaults to false.
     * @param finalFieldsOnly true if you want to limit fields to only those that are declared final. If the transient
     *                        option is also true, then only final and final transient fields are included.
     * @return this, for method chaining
     */
    public DogTagBuilder<T> withFinalFieldsOnly(boolean finalFieldsOnly) {
      this.finalFieldsOnly = finalFieldsOnly;
      return this;
    }

    /**
     * Once options are specified, build the DogTag instance for the Type. Options may be specified in any order.
     * @return A {@code DogTag<T>} that uses the specified options.
     */
    public DogTag<T> build() {
      return new DogTag<>(targetClass, makeGetterList(), startingHash, hashBuilder);
    }

    private List<FieldProcessor<T>> makeGetterList() {
      Set<Field> excludedFields = new HashSet<>();
      for (String fieldName : excludedFieldNames) {
        excludedFields.add(getFieldFromName(fieldName));
      }

      List<FieldProcessor<T>> fieldProcessorList = new LinkedList<>();
      Class<? super T> theClass = targetClass;

      // We shouldn't ever reach Object.class unless someone specifies it as the reflect-up-to superclass.
      while (theClass != Object.class) {
        Field[] declaredFields = theClass.getDeclaredFields();
        for (Field field : declaredFields) {
          int modifiers = field.getModifiers();
          if ((field.getType() == DogTag.class) && !Modifier.isStatic(modifiers)) {
            throw new AssertionError("Your DogTag instance must be static. Private and final are also suggested.");
          }
          //noinspection MagicCharacter
          if (!Modifier.isStatic(modifiers)
              && (testTransients || !Modifier.isTransient(modifiers))
              && (!finalFieldsOnly || Modifier.isFinal(modifiers))
              && !excludedFields.contains(field)
              && (field.getName().indexOf('$') < 0)
          ) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (fieldType.isArray()) {
              fieldProcessorList.add(getProcessorForArray(field, fieldType));
            } else {
              fieldProcessorList.add(new FieldProcessor<>(field, Objects::equals, Objects::hashCode));
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
    BiFunction<Object, Object, Boolean> arrayEquals;
    Function<Object, Integer> arrayHash;
    if (componentType == Integer.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((int[]) thisOne, (int[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((int[]) array);
    } else if (componentType == Long.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((long[]) thisOne, (long[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((long[]) array);
    } else if (componentType == Short.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((short[]) thisOne, (short[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((short[]) array);
    } else if (componentType == Character.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((char[]) thisOne, (char[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((char[]) array);
    } else if (componentType == Byte.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((byte[]) thisOne, (byte[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((byte[]) array);
    } else if (componentType == Double.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((double[]) thisOne, (double[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((double[]) array);
    } else if (componentType == Float.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((float[]) thisOne, (float[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((float[]) array);
    } else if (componentType == Boolean.TYPE) {
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((boolean[]) thisOne, (boolean[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((boolean[]) array);
    } else {
      // componentType is Object.class or some subclass of it. It is not a primitive. It may be an array, if the
      // field is a multi-dimensional array.
      assert !componentType.isPrimitive() : componentType;
      arrayEquals = (thisOne, thatOne) -> Arrays.equals((Object[]) thisOne, (Object[]) thatOne);
      arrayHash = (array) -> Arrays.hashCode((Object[]) array);
    }
    return new FieldProcessor<>(arrayField, arrayEquals, arrayHash);
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
    if (!targetClass.isInstance(thatOneNullable)) {
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
      throw new AssertionError("Illegal Access should not happen", e);
    }
  }

  /**
   * Get the hash code from an instance of the containing class, consistent with {@code equals()}
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
    assert thisOne != null : "Always pass 'this' to this method! That guarantees it won't be null.";
    int hash = startingHash;
    try {
      for (FieldProcessor<T> f : fieldProcessors) {
        hash = hashBuilder.newHash(hash, f.getHashValue(thisOne));
      }
    } catch (IllegalAccessException e) {
      // Shouldn't happen, because accessible has been set to true.
      throw new AssertionError("Illegal Access shouldn't happen", e);
    }
    return hash;
  }

  /**
   * This method is disabled, to avoid confusion with the {@code doEqualsTest()} method. You should never need this
   * anyway. Calling this method will throw an AssertionError.
   * @param obj Unused
   * @return never returns
   */
  @Override
  public boolean equals(Object obj) {
    throw new AssertionError("Never call dogTag.equals(). To test if your object is equal to another," +
        "call dogTag.doEqualsTest(this, other)");
  }

  /**
   * This method is disabled, to avoid confusion with the doHashCode() method. You should never need this anyway.
   * Calling this method will throw an AssertionError.
   * @return never returns
   */
  @Override
  public int hashCode() {
    throw new AssertionError("Never call hashCode(). To get the hashCode of your object, call doHashCode(this)");
  }

  @FunctionalInterface
  private interface ThrowingFunction<T, R> {
    R get(T object) throws IllegalAccessException;
  }

  /**
   * You are free to install your own hash builder by implementing this interface. For each object used in the hash
   * code calculation, this method calculates a new value from the current hashCode in progress, and the hash code of
   * the next object in the chain.
   * <p>
   *   Strictly speaking, for a series of hash values, H<sub>n</sub>, the formula combines them into a single hash
   *   value by processing each value in turn, according to this formula: <p>
   *     &nbsp;&nbsp;V<sub>n</sub> = newHash(V<sub>n-1</sub>, H<sub>n</sub>)<p>
   *   where V<sub>n</sub> is the new hash value, V<sub>n-1</sub> is the result from the previous iteration, and 
   *   newHash() is the implemented method. For the first iteration, the calling method must specify a starting value
   *   for V<sub>n-1</sub>, which is usually one or zero. The final value of V<sub>n</sub> is returned. 
   *   The default implementation uses this formula:<p>
   *     &nbsp;&nbsp;V<sub>n</sub> = (31 * V<sub>n-1</sub>) + H<sub>n</sub><p>
   *   This is the same calculation performed by the {@code java.util.Objects.hash(Object...)} method.
   */
  @FunctionalInterface
  public interface HashBuilder {
    int newHash(int previousHash, Object nextObject);
  }

  /**
   * This class knows how to extract a value from a field, determine if two fields are equal, and get the hash code. 
   * Every object used by equals and hash code calculations is either a single value or an array. Arrays must be
   * handled differently from single values. This class holds the getter for a field, and the methods for equals and
   * hash code. The getter varies according to the field type, but the equals() and hashCode() vary based on whether 
   * the field is single-value or an array. The appropriate methods are passed in to the constructor.
   * @param <F> The field type
   */
  private static class FieldProcessor<F> {
    private final ThrowingFunction<F, ?> getter;
    private final BiFunction<Object, Object, Boolean> equalMethod; // This will either from Arrays, or Objects::equals 
    private final Function<Object, Integer> hashMethod; // This will be either from Arrays, or Objects::hashCode
    
    FieldProcessor(Field field, BiFunction<Object, Object, Boolean> equalMethod, Function<Object, Integer> hashMethod) {
      this.getter = field::get;
      this.equalMethod = equalMethod;
      this.hashMethod = hashMethod;
    }
    
    public boolean testForEquals(F thisOne, F thatOne) throws IllegalAccessException {
      return equalMethod.apply(getter.get(thisOne), getter.get(thatOne));
    }
    
    public int getHashValue(F thisOne) throws IllegalAccessException {
      return hashMethod.apply(getter.get(thisOne));
    }
  }
}
