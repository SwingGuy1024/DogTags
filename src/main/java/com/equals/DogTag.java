package com.equals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
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
 *     // fields are examined by reflection here, when the class is loaded.
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
 * The equals comparison is implemented according to the guidelines set down in <strong>EffectiveJava</strong> by Joshua 
 * Bloch. The hashCode is generated using the same formula used as {@code java.lang.Objects.hash(Object...)}, 
 * although you are free to provide a different hash calculator. Floats and Doubles set to NaN are all treated as equal.
 * <p>
 * Options include testing transient fields, excluding fields, using a cached hash code, and specifying a superclass to
 * include in the reflective process. These options are invoked by using the DogTagBuilder class.
 * <pre>
 *   public class MyClass extends MyBaseClass {
 *     // Define various fields, getters, and methods here.
 *
 *     private static final{@literal DogTag<MyClass>} dogTag = DogTag.create(MyClass.class)
 *       .withTransients(true)                // defaults to false
 *       .withExcludedFields("size", "date")  // defaults to non-transient, non-static fields
 *       .withReflectUpTo(MyBaseClass.class)  // defaults to no superclasses
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
 * When relying only on final fields, the hash code may be cached by enabling the withCachedHash option, and
 * using a {@code CachedHash}. To use a cached hash code, {@code the hashCode()} method must be implemented this way:
 * <pre>
 *   private static final {@literal DogTag<MyClass>} dogTag = DogTag.create() // must be static!
 *       .withCachedHash(true)
 *       // other options specified here
 *       .build();
 *
 *   private final CachedHash cachedHash = dogTag.makeCachedHash(); // must NOT be static!
 *
 *  {@literal @Override}
 *   public int doHashCode() {
 *     return doHashCode(this, cachedHash);
 *   }
 *
 *   // Your equals() method does not change.
 *  {@literal @Override}
 *   public boolean equals(Object that) { return dogTag.doEqualsTest(this, that); }
 * </pre>
 * <strong>Warning</strong> Simply using final fields in your DogTag isn't sufficient to implement a properly written
 * cached hash code. All your final fields must themselves be primitives or immutable classes, or they must be classes
 * that also rely only on final fields of immutable values to generate their hash code. While you may use a CachedHash
 * with a DogTag generated with only final fields, you are not required to do so.
 * <p>
 * <strong>Notes:</strong><p>
 *   The following mistakes will generate AssertionErrors:
 *    <ul>
 *     <li>Failing to declaring a DogTag static</li>
 *     <li>Declaring a CachedHash static</li>
 *     <li>Enabling the CachedHash option while using a non-final field</li>
 *     <li>Trying to get a CachedHash from a DogTag that was not built with the CachedHash option enabled</li>
 *     <li>Calling either DogTag.equals() or DogTag.hashCode().</li>
 *    </ul>
 *   The DogTag instance needs to be static or performance will suffer. It's prudent to also declare it
 *   private and final, since you shouldn't ever need to change its value.
 * <p>
 *   The CashedHash instance can't be static or all instances will share the same hash code.
 * <p>
 *   The DogTag methods {@code equals()} and {@code hashCode()} are disabled to prevent their accidental use instead
 *   of {@code doEqualsTest()} and {@code doHashCode()} The two correct methods both start with "do" to avoid
 *   confusion.
 * <p>
 *   You should always pass {@code this} as the first parameter of {@code doEqualsTest()} and {@code doHashCode()}.
 * <p>
 *   Static fields are never used.
 * <p>
 * TODO: Still to do:
 * Add an option to specify case sensitivity.
 * Add a way to specify the order of the fields
 * 
 * @param <T> Type that uses a DogTag equals and hashCode method
 * @author Miguel Muñoz
 */
@SuppressWarnings("WeakerAccess")
public final class DogTag<T> {
  // Todo: Add business key support, using annotations
  // Todo: Add exclusion support using annotations.
  // Todo: Specify field order by annotations?
  // Todo: new additive create() class that includes nothing by default?
  private final Class<T> targetClass;
  private final List<FieldProcessor<T>> fieldProcessors;
  private final int startingHash;
  private final HashBuilder hashBuilder;
  private final boolean useCache;

  /**
   * Instantiate a DogTag for class T, using default options. The default options are: All Fields are included except 
   * transient and static fields, as well as fields annotated with {@code @DogTagExclude}. No superclass fields are 
   * included.
   * @param theClass The instance of enclosing {@literal Class<T>} for type T
   * @param <T> The type of the enclosing class.
   * @return An instance of {@literal DogTag<T>}. This should be a static member of the enclosing class
   */
  public static <T> DogTag<T> from(Class<T> theClass) {
    return new DogTagExclusionBuilder<>(theClass).build();
  }

  /**
   * Instantiate a builder for a DogTag for class T, specifying a list of names of fields to be excluded. The fields 
   * must be in the class specified by the type parameter for the DogTag, or any superclass included by the 
   * {@code withReflectUpTo()} option. Defaults to an empty array.
   * <p>
   * The builder allows you to specify options before building your DogTag. The build() method generates the DogTag.
   * All of the default options used by the {@code from()} method are used here, but they may be overridden. All the 
   * methods to set options begin with the word "with."
   * <p>
   *   For example:
   *   <pre>
   *     {@literal DogTag<MyClass>} dogTag = DogTag.create(MyClass.class, "date", "source")
   *         .withTransients(true) // options are specified here
   *         .build();
   *   </pre>
   * <p>
   *   Options may be specified in any order.
   * @param theClass The instance of enclosing {@literal Class<T>} for type T
   * @param excludedFieldNames The names of fields to exclude from the equals and hash code calculations
   * @param <T> The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagBuilder<T> create(Class<T> theClass, String... excludedFieldNames) {
    return new DogTagExclusionBuilder<>(theClass, excludedFieldNames);
  }

  /**
   * Instantiate a builder for a DogTag for class T, specifying an annotation class to be used to exclude fields, which
   * may be used the same way as the {@code DogTagExclude} annotation. 
   * The builder allows you to specify options before building your DogTag. The build() method generates the DogTag.
   * All of the default options used by the {@code from()} method are used here, but they may be overridden. All the 
   * methods to set options begin with the word "with."
   * <p>
   * For example:
   * <pre>
   *     {@literal DogTag<MyClass>} dogTag = DogTag.create(MyClass.class, MyExclusionAnnotation.class)
   *         .withTransients(true) // options are specified here
   *         .build();
   *   </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param theClass The instance of enclosing {@literal Class<T>} for type T
   * @param exclusionAnnotation The class object of the custom annotation you may use to specify which fields to
   *                            exclude.
   * @param <T>      The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagBuilder<T> create(Class<T> theClass, Class<? extends Annotation> exclusionAnnotation) {
    return new DogTagExclusionBuilder<>(theClass, exclusionAnnotation);
  }

  /**
   * Instantiate a builder for a DogTag for class T using inclusion mode, specifying a list of names of fields to be 
   * included. The fields must be in the class specified by the type parameter for the DogTag, or any superclass 
   * included by the {@code withReflectUpTo()} option. Defaults to an empty array.
   * <p>
   * In Inclusion mode, none of the fields are included by default. They must be included by specifying their
   * name in this method, or by annotating the fields with {@literal @DogTagInclude} or a custom annotation of your
   * choice.
   * The builder allows you to specify options before building your DogTag. The build() method generates the DogTag.
   * All the methods to set options begin with the word "with."
   * <p>
   * For example:
   * <pre>
   *     {@literal DogTag<MyClass>} dogTag = DogTag.createForInclusion(MyClass.class, "name", "ssNumber", "email")
   *         .withReflectUpTo(MyBaseClass.class) // options are specified here
   *         .build();
   * </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param theClass   The instance of enclosing {@literal Class<T>} for type T
   * @param fieldNames The names of fields to exclude from the equals and hash code calculations
   * @param <T>        The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagBuilder<T> createForInclusion(Class<T> theClass, String... fieldNames) {
    return new DogTagInclusionBuilder<>(theClass, fieldNames);
  }

  /**
   * Instantiate a builder for a DogTag for class T using inclusion mode, specifying a custom annotation to use to
   * specify which fields should be included. In Inclusion mode, none of the fields are included by default. They must 
   * be included by specifying their
   * name (in another method), or by annotation the fields with {@literal @DogTagInclude} or a custom annotation of your
   * choice.
   * The builder allows you to specify options before building your DogTag. The build() method generates the DogTag.
   * All the methods to set options begin with the word "with."
   * <p>
   * For example:
   * <pre>
   *     {@literal DogTag<MyClass>} dogTag = DogTag.createForInclusion(MyClass.class, "name", "ssNumber", "email")
   *         .withReflectUpTo(MyBaseClass.class) // options are specified here
   *         .build();
   * </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param theClass   The instance of enclosing {@literal Class<T>} for type T
   * @param inclusionAnnotation The class object of the custom annotation used to specify which fields to include
   * @param <T>        The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagBuilder<T> createForInclusion(Class<T> theClass, Class<? extends Annotation> inclusionAnnotation) {
    return new DogTagInclusionBuilder<>(theClass, inclusionAnnotation);
  }

  private DogTag(
      Class<T> theClass,
      List<FieldProcessor<T>> getters,
      int startingHash,
      HashBuilder hashBuilder,
      boolean useCache
  ) {
    targetClass = theClass;
    fieldProcessors = getters;
    this.startingHash = startingHash;
    this.hashBuilder = hashBuilder;
    this.useCache = useCache;
  }
  
  static final class DogTagExclusionBuilder<T> extends DogTagBuilder<T> {

    DogTagExclusionBuilder(Class<T> theClass, String... excludedFields) {
      this(theClass, null, excludedFields);
    }

    DogTagExclusionBuilder(Class<T> theClass, Class<? extends Annotation> exclusionAnnotation) {
      this(theClass, exclusionAnnotation, EMPTY_STRING_ARRAY);
    }

    DogTagExclusionBuilder(Class<T> theClass, Class<? extends Annotation> exclusionAnnotation, String... excludedFields) {
      super(theClass, false, DogTagExclude.class, excludedFields);
      if (exclusionAnnotation != null) {
        addSelectionAnnotation(exclusionAnnotation);
      }
    }

    @Override
    protected boolean isFieldSelected(final Field theField) {
      return !isSelected(theField);
    }
  }
  
  /** @noinspection AssignmentToSuperclassField*/
  static final class DogTagInclusionBuilder<T> extends DogTagBuilder<T> {
    DogTagInclusionBuilder(Class<T> theClass, String... includedFields) {
      this(theClass, null, includedFields);
    }
    
    DogTagInclusionBuilder(Class<T> theClass, Class<? extends Annotation> inclusionAnnotation) {
      this(theClass, inclusionAnnotation, EMPTY_STRING_ARRAY);
    }

    DogTagInclusionBuilder(Class<T> theClass, Class<? extends Annotation> inclusionAnnotation, String... includedFields) {
      super(theClass, true, DogTagInclude.class, includedFields);
      if (inclusionAnnotation != null) {
        addSelectionAnnotation(inclusionAnnotation);
      }
    }

    @Override
    protected boolean isFieldSelected(final Field theField) {
      return isSelected(theField);
    }
  }

  abstract static class DogTagBuilder<T> {
    static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    // fields initialized in constructor
    private final boolean useInclusionMode;
    private final Class<T> targetClass;
    private final String[] selectedFieldNames;
    private Class<? super T> lastSuperClass;    // not final. May change with options.
    
    // pre-initialized fields
    private boolean testTransients = false;
    private int startingHash = 1;
    private boolean finalFieldsOnly = false;
    private boolean useCachedHash = false;
    private static final HashBuilder defaultHashBuilder = (int h, Object o) -> (h * 31) + o.hashCode(); // Same as Objects.class
    private HashBuilder hashBuilder = defaultHashBuilder; // Reuse the same HashBuilder
    private final List<Class<? extends Annotation>> flaggedList;
    private Set<Field> selectedFields = new HashSet<>();

    protected DogTagBuilder(Class<T> theClass, boolean inclusionMode, Class<? extends Annotation> defaultSelectionAnnotation, String[] selectedFieldNames) {
      targetClass = theClass;
      lastSuperClass = targetClass;
      this.selectedFieldNames = Arrays.copyOf(selectedFieldNames, selectedFieldNames.length);
      flaggedList = new LinkedList<>(Collections.singleton(defaultSelectionAnnotation));
      useInclusionMode = inclusionMode;
    }
    
    protected void addSelectionAnnotation(Class<? extends Annotation> selectionAnnotation) {
      flaggedList.add(selectionAnnotation);
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
     * Set the finalFieldsOnly option, before building the DogTag. Defaults to false. Setting this to true also
     * sets use CachedHash to true. Enabling this option also enables the useCachedHash option, although the use of
     * a cached hash code remains optional.
     * @param finalFieldsOnly true if you want to limit fields to only those that are declared final. If the transient
     *                        option is also true, then only final and final transient fields are included.
     * @return this, for method chaining
     */
    public DogTagBuilder<T> withFinalFieldsOnly(boolean finalFieldsOnly) {
      this.finalFieldsOnly = finalFieldsOnly;
      if (finalFieldsOnly) {
        useCachedHash = true;
      }
      return this;
    }

    /**
     * Use a CachedHash to cache the hash value when only final fields are used to build the DogTag. Enabling the  
     * finalFieldsOnly option automatically enables this option, but you may use this method to set it manually.
     * @see CachedHash
     */
    public DogTagBuilder<T> withCachedHash(boolean useCachedHash) {
      this.useCachedHash = useCachedHash;
      return this;
    }

    /**
     * Once options are specified, build the DogTag instance for the Type. Options may be specified in any order.
     * @return A {@code DogTag<T>} that uses the specified options.
     */
    public DogTag<T> build() {
      return new DogTag<>(targetClass, makeGetterList(), startingHash, hashBuilder, useCachedHash);
    }

    private List<FieldProcessor<T>> makeGetterList() {
      collectMatchingFields(selectedFieldNames, selectedFields);

      List<FieldProcessor<T>> fieldProcessorList = new LinkedList<>();
      Class<? super T> theClass = targetClass;

      // We shouldn't ever reach Object.class unless someone specifies it as the reflect-up-to superclass.
      while (theClass != Object.class) {
        Field[] declaredFields = theClass.getDeclaredFields();
        for (Field field : declaredFields) {
          int modifiers = field.getModifiers();
          boolean isCache = field.getType() == CachedHash.class;
          final boolean isStatic = Modifier.isStatic(modifiers);
          final boolean fieldIsFinal = Modifier.isFinal(modifiers);
          if ((field.getType() == DogTag.class) && !isStatic) {
            throw new AssertionError("Your DogTag instance must be static. Private and final are recommended.");
          }

          // Test if the field should be included. This tests for inclusion, regardless of the selectionMode.
          //noinspection MagicCharacter
          if (!isStatic
              && !isCache
              // transients are tested only in exclusion mode
              && (testTransients || useInclusionMode || !Modifier.isTransient(modifiers))
              && (!finalFieldsOnly || fieldIsFinal)
              && isFieldSelected(field)
              && (field.getName().indexOf('$') < 0)
          ) {
            if (useCachedHash && !fieldIsFinal) {
              throw new AssertionError(
                  String.format("The 'withCachedHash' option may not be used with non-final field %s.", field.getName())
              );
            }
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (fieldType.isArray()) {
              fieldProcessorList.add(getProcessorForArray(field, fieldType));
            } else {
              fieldProcessorList.add(new FieldProcessor<>(field, Objects::equals, Objects::hashCode));
            }
          } else {
            if (isCache && isStatic) {
              throw new AssertionError("Your CachedHash instance cannot be static. Private and final are recommended");
            }
          }
        }
        if (theClass == lastSuperClass) {
          break;
        }
        theClass = theClass.getSuperclass();
      }
      return fieldProcessorList;
    }

    private void collectMatchingFields(final String[] fieldNames, final Set<Field> searchField) {
      for (String fieldName : fieldNames) {
        searchField.add(getFieldFromName(fieldName));
      }
    }
    
    protected boolean isSelected(Field theField) {
      return selectedFields.contains(theField) || isAnnotatedWith(theField, flaggedList);
    }
    
    protected abstract boolean isFieldSelected(Field theField);

    private boolean isAnnotatedWith(Field field, List<Class<? extends Annotation>> annotationList) {
      for (Class<? extends Annotation> annotationClass : annotationList) {
        if (field.isAnnotationPresent(annotationClass)) {
          return true;
        }
      }
      return false;
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
  }

  /**
   * Compare two objects for null. This should always be called with {@code this} as the first parameter. Your
   * equals method should look like this:
   * <pre>
   *   private static final{@literal DogTag<YourClass>} dogTag = DogTag.from(YourClass.class); // Or built from the builder
   *
   *  {@literal @Override}
   *   public boolean equals(Object that) {
   *     return dogTag.doEqualsTest(this, that);
   *   }
   * </pre>
   * @param thisOneNeverNull Pass {@code this} to this parameter
   * @param thatOneNullable {@code 'other'} in the equals() method
   * @return true if the objects are equal, false otherwise
   */
  public boolean doEqualsTest(T thisOneNeverNull, Object thatOneNullable) {
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
  public int doHashCode(T thisOne) {
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

  /** get the hashCode from the cache if it has already been calculated, or if it hasn't, calculate it and store it
   * in the Cache. This should only be used with DogTags created with fields that can't change value.
   * @see CachedHash
   * @param thisOne The instance to hash
   * @param cachedHash The cache that stores the previous value
   * @return The hash code.
   */
  public int doHashCode(T thisOne, CachedHash cachedHash) {
    if (!cachedHash.isSet()) {
      cachedHash.setHash(doHashCode(thisOne));
    }
    return cachedHash.getHash();
  }

  /**
   * Make a HashedCache instance for each instance of your class. This should only be called if you built your DogTag
   * using the 'withCachedHash' option set to true. CachedHashes only work if all the specified fields are final. You
   * do not need to specify the withFinalFieldsOnly option to use a CachedHash as long as all the fields you include
   * are final. A CachedHash will always work when the withFinalFieldsOnly option is true.
   * <p>
   * To use a CachedHash, your hash code method should be implemented like this:
   * <pre>
   *   private static final {@literal DogTag<MyClass>} dogTag = DogTag.create() // must be static!
   *       .withCachedHash(true)
   *       // other options specified here
   *       .build();
   *       
   *   private final CachedHash cachedHash = dogTag.makeCachedHash(); // must NOT be static!
   *
   *  {@literal @Override}
   *   public int doHashCode() {
   *     return doHashCode(this, cachedHash);
   *   }
   * 
   *   // Your equals() method does not change.
   *  {@literal @Override}
   *   public boolean equals(Object that) { return dogTag.doEqualsTest(this, that); } 
   * </pre>
   * For simplicity, the CachedHash class does not have a public API.
   * @return A CachedHash to cache the hash value for improved performance.
   */
  public CachedHash makeCachedHash() {
    if (useCache) {
      return new CachedHash();
    }
    throw new AssertionError("A CachedHash can only be used if the 'withCachedHash' option is true");
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
  private static final class FieldProcessor<F> {
    private final ThrowingFunction<F, ?> getter;
    private final BiFunction<Object, Object, Boolean> equalMethod; // This will be from either Arrays or Objects::equals 
    private final Function<Object, Integer> hashMethod; // This will be from either Arrays or Objects::hashCode
    
    private FieldProcessor(Field field, BiFunction<Object, Object, Boolean> equalMethod, Function<Object, Integer> hashMethod) {
      this.getter = field::get;
      this.equalMethod = equalMethod;
      this.hashMethod = hashMethod;
    }
    
    private boolean testForEquals(F thisOne, F thatOne) throws IllegalAccessException {
      return equalMethod.apply(getter.get(thisOne), getter.get(thatOne));
    }
    
    private int getHashValue(F thisOne) throws IllegalAccessException {
      return hashMethod.apply(getter.get(thisOne));
    }
  }
}
