package com.equals;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <strong>Much of the documentation is out of date. It will be updated shortly</strong><p>
 * Fast {@code equals()} and {@code hashCode()} methods. Guarantees that {@code equals()} complies with its contract, 
 * and that {@code hashCode()} is consistent with {@code equals()}, according to its contract.
 * <p>
 * Unlike Apache's {@code EqualsBuilder.reflectionEquals()} method, most of the slow reflective calls are not done when 
 * {@code equals()} is called, but at class-load time, or when the first instance is instantiated, so they are only 
 * done once for each class. This gives you a big improvement in performance. It also improves maintainability, since
 * equals and hashCode use the same construction.
 * <p>
 * Example usage:
 * <pre>
 *   public class MyClass {
 *     // Define various fields, getters, and methods here.
 *     
 *     private final{@literal DogTag<MyClass>} dogTag = DogTag.from(this);
 *     
 *    {@literal @Override}
 *     public boolean equals(Object that) {
 *       return dogTag.equals(that);
 *     }
 *
 *    {@literal @Override}
 *     public int hashCode() {
 *       return dogTag.hashCode();
 *     }
 *   }
 * </pre>
 * <p>
 * The equals comparison is implemented according to the guidelines set down in <strong>Effective Java</strong> by 
 * Joshua Bloch. The hashCode is generated using the same formula as {@code java.lang.Objects.hash(Object...)}, 
 * although you are free to provide a different hash calculator. Floats and Doubles set to NaN are all treated as equal,
 * unless they are in arrays. All arrays are compared using the methods in {@code java.util.Arrays}. So for arrays of
 * float and double values, NaN are treated according to the rules for ==, which means NaN values are never equal to 
 * anything.
 * <p>
 * Options include testing transient fields, excluding specific fields by name, using a cached hash code under special
 * circumstances, and specifying a superclass to limit the reflective process. These options are invoked by using one 
 * of the methods beginning with "create".
 * <p>
 * Here is an example of how to specify options:
 * <pre>
 *   public class MyClass extends MyBaseClass {
 *     // Define various fields, getters, and methods here.
 *
 *     // Excludes size and date fields. Defaults to all non-transient, non-static fields.
 *     private final{@literal DogTag<MyClass>} dogTag = DogTag.create(this, "size", "date")
 *       .withTransients(true)            // defaults to false
 *       .withReflectUpTo(MyClass.class)  // defaults to Object.class
 *       .build();
 *
 *    {@literal @Override}
 *     public boolean equals(Object obj) {    // Implementations of equals() and hashCode() 
 *       return dogTag.equals(obj);           // are the same as before.
 *     }
 *
 *    {@literal @Override}
 *     public int hashCode() {
 *       return dogTag.hashCode();
 *     }
 *   }
 * </pre>
 * If the class is serializable, the DogTag fields should be declared transient.
 * <strong>Notes:</strong><p>
 *   The following mistake will generate AssertionErrors:
 *    <ul>
 *      <li>Declaring your DogTag instance static. </li>
 *      <li>Not Declaring a DogTag.Factory static. (These are rarely needed)</li>
 *    </ul>
 * <p>
 * Static fields are always excluded. The DogTag field is also excluded, as is a (rarely used) DogTag.Factory field.
 * <p>
 * For each member Object used in the equals and hash code calculations, the DogTag will call the object's 
 * {@code equals()} and {@code hashCode()} methods. DogTags do not recurse into member objects.
 * <p>
 * For performance reasons, DogTags make no effort to prevent cyclic dependencies. It is the responsibility of the 
 * user to exclude any fields that could cause a cyclic dependency.
 * TODO: Still to do:
 * Add a way to specify the order of the fields
 * 
 * @param <T> Type that uses a DogTag equals and hashCode method
 * @author Miguel Muñoz
 */
@SuppressWarnings("WeakerAccess")
public final class DogTag<T> {
  private static final Map<Class<?>, Factory<?>> factoryMap = new HashMap<>();
  private final Factory<T> factory;
  private final WeakReference<T> thisRef;
  private int cachedHash;
  
  // package access for testing
  public static final class Factory<T> {
    private final Class<T> targetClass;
    private final List<FieldProcessor> fieldProcessors;
    private final int startingHash;
    private final HashBuilder hashBuilder;
    private final boolean useCache;

    private Factory(
        Class<T> theClass,
        List<FieldProcessor> getters,
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

    public DogTag<T> tag(T instance) {
      return new DogTag<>(this, instance);
    }

    /**
     * This is the implementation of the equals() method of a DogTag instance. This sits in the inner Factory class
     * for testing purposes.
     * @param thisOneNeverNull The instance wrapped by the DogTag should get passed to this parameter
     * @param thatOneNullable  {@code 'other'} in the equals() method
     * @return true if the objects are equal, false otherwise
     */
    @SuppressWarnings("ObjectEquality")
    boolean doEqualsTest(T thisOneNeverNull, Object thatOneNullable) {
      assert thisOneNeverNull != null : "Always pass 'this' to the first parameter of this method!";
      if (thisOneNeverNull == thatOneNullable) {
        return true;
      }

      // Includes an implicit test for null
      if (!targetClass.isInstance(thatOneNullable)) {
        return false;
      }

      @SuppressWarnings("unchecked")
      T thatOneNeverNull = (T) thatOneNullable;

      // Putting the try/catch here instead of inside the testForEquals() method doubles the speed.
      try {
        for (FieldProcessor f : fieldProcessors) {
          if (!f.testForEquals(thisOneNeverNull, thatOneNeverNull)) {
            return false;
          }
        }
        return true;
      } catch (IllegalAccessException e) { // Shouldn't happen: Field.accessible has been set to true.
        throw new AssertionError("Illegal Access should not happen", e);
      }
    }

    /**
     * This is the implementation of the hashCode() method of a DogTag instance. This sits in the inner Factory class
     * for testing purposes.
     * @param thisOne The instance wrapped by the DogTag should get passed to this parameter
     * @return The hashCode
     */
    int doHashCodeInternal(T thisOne) {
      assert thisOne != null : "Always pass 'this' to this method! That guarantees it won't be null.";
      int hash = startingHash;

      // Putting the try/catch here instead of inside the testForEquals() method doubles the speed.
      try {
        for (FieldProcessor f : fieldProcessors) {
          hash = hashBuilder.newHash(hash, f.getHashValue(thisOne));
        }
      } catch (IllegalAccessException e) { // Shouldn't happen: Field.accessible has been set to true.
        throw new AssertionError("Illegal Access shouldn't happen", e);
      }
      return hash;
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private int doCachedHashCode(final T thisOne, DogTag<T> dogTag) {
      if (dogTag.cachedHash == 0) {
        dogTag.cachedHash = doHashCodeInternal(thisOne);
      }
      return dogTag.cachedHash;
    }
  }

  private DogTag(Factory<T> factory, T instance) {
    this.factory = factory;
    // Use a WeakReference to avoid a circular reference that could delay garbage collection.
    thisRef = new WeakReference<>(instance);
  }

  private static <T> Factory<T> getForClass(Class<T> theClass) {
    @SuppressWarnings("unchecked")
    final Factory<T> factory = (Factory<T>) factoryMap.get(theClass);
    return factory;
  }

  /**
   * Instantiate a DogTag for an instance of class T, using default options. The default options are: All Fields are 
   * included except transient and static fields, as well as fields annotated with {@code @DogTagExclude}. All 
   * superclass fields are included.
   * <p>
   * The first time this method is called for a class, it will create a DogTag.Factory and store it. Subsequent calls
   * will retrieve that factory and re-use it.
   * @param instance The instance of enclosing class. Pass {@code this} to this parameter
   * @param <T> The type of the enclosing class.
   * @return An instance of {@literal DogTag<T>}. This should be a non-static member of the enclosing class.
   */
  public static <T> DogTag<T> from(T instance) {
    return new DogTagExclusionBuilder<>(instance).getFactory().tag(instance);
  }

  /**
   * Instantiate a builder for a DogTag for class T, specifying an optional list of names of fields to be excluded. The
   * fields must be in the class specified by the type parameter for the DogTag, or any superclass included by the 
   * {@code withReflectUpTo()} option. Defaults to an empty array.
   * <p>
   * This builder allows you to specify options before building your DogTag. The getFactory() method generates the DogTag.
   * All of the default options used by the {@code from()} method are used here, but they may be overridden. All the 
   * methods to set options begin with the word "with."
   * <p>
   *   For example:
   *   <pre>
   *     private final{@literal DogTag<MyClass>} dogTag = DogTag.create(this, "date", "source")
   *         .withTransients(true) // options are specified here
   *         .build();
   *   </pre>
   * <p>
   *   Options may be specified in any order.
   * @param instance The instance of the enclosing class
   * @param excludedFieldNames The names of fields to exclude from the equals and hash code calculations
   * @param <T> The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagExclusionBuilder<T> create(T instance, String... excludedFieldNames) {
    return new DogTagExclusionBuilder<>(instance, excludedFieldNames);
  }
  
  /**
   * Instantiate a builder for a DogTag for class T using inclusion mode, specifying a list of names of fields to be 
   * included. The fields must be in the class specified by the type parameter for the DogTag, or any superclass. 
   * Defaults to an empty array.
   * <p>
   * In Inclusion mode, none of the fields are included by default. They must be included by specifying their
   * name in this method, or by annotating the fields with {@literal @DogTagInclude} or a custom annotation of your
   * choice. Fields are searched first in the Target class T, then in its most immediate superclasses, and on through
   * the superclasses until it reaches Object.class.
   * <p>
   * The builder allows you to specify options before building your DogTag. The build() method generates the 
   * DogTag. All the methods to set options begin with the word "with."
   * <p>
   * For example:
   * <pre>
   *   {@literal DogTag<MyClass>} dogTag = DogTag.createByInclusion(this, "name", "ssNumber", "email")
   *     .withInclusionAnnotation(MyIncludeAnnotation.class) // options are specified here
   *     .build();
   * </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param instance   The instance of enclosing class. Pass {@code this} to this parameter.
   * @param fieldNames The names of fields to exclude from the equals and hash code calculations
   * @param <T>        The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.
   */
  public static <T> DogTagInclusionBuilder<T>   createByInclusion(T instance, String... fieldNames) {
    return new DogTagInclusionBuilder<>(instance, fieldNames);
  }

  static final class DogTagInclusionBuilder<T> extends DogTagFullBuilder<T> {

    // Temporarily removed. This will be put back to support ordered inclusion fields
//    @Override
//    Collection<Field> getFieldCandidates(final Class<T> targetClass) {
//      // Temporary. We will replace this by other methods for different inclusion modes.
//      return Arrays.asList(targetClass.getDeclaredFields());
//    }

    DogTagInclusionBuilder(T instance, String... includedFields) {
      super(instance, false, DogTagInclude.class, includedFields);
    }

    @Override
    protected boolean isFieldUsed(final Field theField) {
      return isSelected(theField);
    }

    /**
     * Specify an annotation class to be used to include fields, which may be used in addition to the
     * {@code DogTagInclude} annotation. This allows you to use an annotation of your choice, for compatibility with
     * other systems or your own framework. The {@code DogTagInclude} annotation will still work.
     *
     * @param annotationClass The class object of the custom annotation you may use to specify which fields to
     *                        exclude.
     * @return this, for method chaining.
     */
    @SuppressWarnings("SameParameterValue")
    DogTagInclusionBuilder<T> withInclusionAnnotation(Class<? extends Annotation> annotationClass) {
      addSelectionAnnotation(annotationClass);
      return this;
    }

    @Override // JavaDocs are in the super class
    public DogTagInclusionBuilder<T> withHashBuilder(final int startingHash, final HashBuilder hashBuilder) {
      return (DogTagInclusionBuilder<T>) super.withHashBuilder(startingHash, hashBuilder);
    }

    @Override // JavaDocs are in the super class
    public DogTagInclusionBuilder<T> withCachedHash(final boolean useCachedHash) {
      return (DogTagInclusionBuilder<T>) super.withCachedHash(useCachedHash);
    }
  }

  public static final class DogTagExclusionBuilder<T> extends DogTagFullBuilder<T> {

    // Temporarily removed. This will be put back to support ordered inclusion fields
//    @Override
//    Collection<Field> getFieldCandidates(Class<T> targetClass) {
//      return Arrays.asList(targetClass.getDeclaredFields());
//    }

    private DogTagExclusionBuilder(T instance, String... excludedFields) {
      super(instance, false, DogTagExclude.class, excludedFields);
    }

    /**
     * Specify an annotation class to be used to exclude fields, which may be used in addition to the
     * {@code DogTagExclude} annotation. This allows you to use an annotation of your choice, for compatibility with
     * other systems or your own framework. The {@code DogTagExclude} annotation will still work.
     * @param annotationClass The class object of the custom annotation you may use to specify which fields to
     *                            exclude.
     * @return this, for method chaining.
     */
    public DogTagExclusionBuilder<T> withExclusionAnnotation(Class<? extends Annotation> annotationClass) {
      if (annotationClass != null) {
        addSelectionAnnotation(annotationClass);
      }
      return this;
    }

    /**
     * Set the Transient option, before building the DogTag. Defaults to false. This is used only in exclusion mode.
     * When true, transient fields will be included, provided they meet all the other criteria. For example, if the
     * finalFieldsOnly option is also true, then only final and final transient fields are included.
     * <p>
     * Calling this method when using inclusion mode will have no effect.
     *
     * @param useTransients true if you want transient fields included in the equals and hashCode methods
     * @return this, for method chaining
     */
    public DogTagExclusionBuilder<T> withTransients(boolean useTransients) {
      setTransients(useTransients);
      return this;
    }

    /**
     * Set the finalFieldsOnly option, before building the DogTag. Defaults to false. This option is only used in
     * Exclusion mode. Enabling this option also enables the cachedHash option, although the use of a cached hash code
     * remains optional.
     * <p>
     * Calling this method when using inclusion mode will have no effect.
     *
     * @param finalFieldsOnly true if you want to limit fields to only those that are declared final. If the transient
     *                        option is also true, then only final and final transient fields are included.
     * @return this, for method chaining
     */
    public DogTagExclusionBuilder<T> withFinalFieldsOnly(boolean finalFieldsOnly) {
      setFinalFieldsOnly(finalFieldsOnly);
      if (finalFieldsOnly) {
        withCachedHash(true);
      }
      return this;
    }

    /**
     * Specify the superclass of the DogTag Type parameter class to include in the equals and hash code calculations.
     * The classes inspected for fields to use are those of the type class, the specified superclass, and any
     * class that is a superclass of the type class and a subclass of the specified superclass. Defaults to
     * Object.class.
     *
     * @param reflectUpTo The superclass, up to which are inspected for fields to include.
     * @return this, for method chaining
     */
    public DogTagExclusionBuilder<T> withReflectUpTo(Class<? super T> reflectUpTo) {
      setReflectUpTo(reflectUpTo);
      return this;
    }

    @Override
    protected boolean isFieldUsed(final Field theField) {
      return !isSelected(theField);
    }

  // All inherited "with<Option> methods must be overridden to return DogTagExclusionBuilder instead of the
  // default DogTagFullBuilder:

    @Override // JavaDocs are in the super class
    public DogTagExclusionBuilder<T> withHashBuilder(final int startingHash, final HashBuilder hashBuilder) {
      return (DogTagExclusionBuilder<T>) super.withHashBuilder(startingHash, hashBuilder);
    }

    @Override // JavaDocs are in the super class
    public DogTagExclusionBuilder<T> withCachedHash(final boolean useCachedHash) {
      return (DogTagExclusionBuilder<T>) super.withCachedHash(useCachedHash);
    }
  }

  public abstract static class DogTagBaseBuilder<T> {
    private T instance;
    protected final Class<T> targetClass;

    DogTagBaseBuilder(T instance) {
      this.instance = instance;
      @SuppressWarnings("unchecked")
      final Class<T> theClass = (Class<T>) instance.getClass();
      targetClass = theClass;
    }

    /**
     * Once options are specified, find the DogTag.Factory for type T, or build a new one if one doesn't exist yet.
     * Options may be specified in any order.
     * @return A {@literal DogTag.Factory<T>} that builds instances of {@literal DogTag<T>} using the specified options.
     */
    public Factory<T> getFactory() {
      Factory<T> factory = getForClass(targetClass);
      if (factory == null) {
        factory = constructFactory();
        factoryMap.put(targetClass, factory);
      }
      return factory;
    }

    protected Factory <T> constructFactory() {
      return new Factory<>(targetClass, makeGetterList(), 0, null, false);
    }

    protected abstract List<FieldProcessor> makeGetterList();

    /**
     * Build the DogTag instance from the factory, creating the factory if it doesn't exist yet.
     * @return The DogTag for the instance held in the builder.
     */
    public DogTag<T> build() {
      return getFactory().tag(instance);
    }
  }

  public abstract static class DogTagFullBuilder<T> extends DogTagBaseBuilder<T> {

    // fields initialized in constructor
    private final boolean useInclusionMode;
    private final String[] selectedFieldNames;
    private final List<Class<? extends Annotation>> flaggedList;

    private Class<? super T> lastSuperClass = Object.class;    // not final. May change with options.

    // pre-initialized fields
    private int startingHash = 1;
    @SuppressWarnings("BooleanVariableAlwaysNegated")
    private boolean finalFieldsOnly = false;
    private boolean useCachedHash = false;
    private static final HashBuilder defaultHashBuilder = (int h, Object o) -> (h * 31) + o.hashCode(); // Same as Objects.class
    private HashBuilder hashBuilder = defaultHashBuilder; // Reuse the same HashBuilder
    private Set<Field> selectedFields = new HashSet<>();
    private boolean testTransients = false;

    // Temporarily removed. This will be put back to support ordered inclusion fields
//    abstract Collection<Field> getFieldCandidates(Class<T> targetClass);

    protected DogTagFullBuilder(T instance, boolean inclusionMode, Class<? extends Annotation> defaultSelectionAnnotation, String[] selectedFieldNames) {
      super(instance);
      this.selectedFieldNames = Arrays.copyOf(selectedFieldNames, selectedFieldNames.length);
      flaggedList = new LinkedList<>(Collections.singleton(defaultSelectionAnnotation));
      useInclusionMode = inclusionMode;
    }

    protected void addSelectionAnnotation(Class<? extends Annotation> selectionAnnotation) {
      flaggedList.add(selectionAnnotation);
    }

    /**
     * Search through classes starting with dogTagClass, up to and including lastSuperClass, for a field with the
     * specified name.
     * @param fieldName The Field name
     * @return A Field, from one of the classes in the range from dogTagClass to lastSuperClass.
     */
    private Field getFieldFromName(String fieldName) {
      Class<?> theClass = targetClass;
      Class<?> previousClass = targetClass;
      boolean inProgress = true;
      while (inProgress) {
        try {
          return theClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
          previousClass = theClass;

          // This test must come before moving to the superClass
          if (theClass == lastSuperClass) {
            inProgress = false;
          } else {
            theClass = theClass.getSuperclass();

            // This test must come after moving to the superClass
            if (theClass == Object.class) {
              inProgress = false;
            }
          }
        }
      }

      // If we only searched through one class...
      if (targetClass == previousClass) {
        // ... we send a simpler error message.
        throw new IllegalArgumentException(String.format("Field %s not found in class %s", fieldName, targetClass));
      }
      throw new IllegalArgumentException(String.format("Field '%s' not found from class %s to superClass %s", fieldName, targetClass, lastSuperClass));
    }

    /**
     * Specify a custom formula for building a single hash value out of a series of hash values. The default
     * formula matches the one used by java.util.Objects.hash(Object...)
     *
     * @param startingHash The starting value.
     * @param hashBuilder  The formula for adding additional hash values.
     * @return this, for method chaining
     * @see HashBuilder
     */
    public DogTagFullBuilder<T> withHashBuilder(int startingHash, HashBuilder hashBuilder) {
      this.startingHash = startingHash;
      this.hashBuilder = hashBuilder;
      return this;
    }

    /**
     * Cache the hash value when only final fields are used to build the DogTag. Enabling the
     * finalFieldsOnly option automatically enables this option, but you may use this method to set it manually.
     * <p>
     * This option should be used with extreme caution. Even if you specify only final fields, this will fail if, for
     * example, the final fields are themselves mutable. For a hashCode to be eligible for the correct use of this
     * option, the following two conditions must be true of every field you include in your DogTag:
     * <br>
     * 1. It must not be possible to change the value once the owning object has been constructed.<br>
     * 2. If the field is an Object, All if its internal fields used in its hash code calculation must meet both of these conditions.
     * <p>
     * The second, of course, makes this recursive. In other words, if any field anywhere in the object tree of any
     * field is used to calculate a hash code, that field cannot change value once the outermost element of the tree
     * is constructed.
     */
    public DogTagFullBuilder<T> withCachedHash(boolean useCachedHash) {
      this.useCachedHash = useCachedHash;
      return this;
    }

    protected void setFinalFieldsOnly(boolean finalFieldsOnly) {
      this.finalFieldsOnly = finalFieldsOnly;
    }

    protected void setTransients(boolean useTransients) {
      this.testTransients = useTransients;
    }

    protected void setReflectUpTo(Class<? super T> superClass) { this.lastSuperClass = superClass; }

    /**
     * Extracted from getFactory() to be used for testing. This allows you to make a factory without adding it to the Map,
     * which is crucial for performance tests.
     * @return A factory from the previously set options
     */
    @Override
    public Factory<T> constructFactory() {
      return new Factory<>(targetClass, makeGetterList(), startingHash, hashBuilder, useCachedHash);
    }

    @Override
    protected List<FieldProcessor> makeGetterList() {
      collectMatchingFields(selectedFieldNames, selectedFields);

      List<FieldProcessor> fieldProcessorList = new LinkedList<>();
      Class<? super T> theClass = targetClass;

      // We shouldn't ever reach Object.class unless someone specifies it as the reflect-up-to superclass.
      while (theClass != Object.class) {
        Field[] declaredFields = theClass.getDeclaredFields();
        for (Field field : declaredFields) {
          final Class<?> fieldType = field.getType();
          int modifiers = field.getModifiers();
          final boolean isStatic = Modifier.isStatic(modifiers);
          final boolean fieldIsFinal = Modifier.isFinal(modifiers);
          final boolean isDogTag = fieldType == DogTag.class;
          final boolean isFactory = fieldType == DogTag.Factory.class;
          if (isDogTag && isStatic) {
            throw new AssertionError("Your DogTag instance must be not static. Private and final are recommended.");
          }
          if (isFactory && !isStatic) {
            // I'm not sure it's possible to construct an object with a non-static factory without throwing a
            // StackOverflowError or NullPointerException, but in case I'm wrong, we disallow a non-static Factory.
            throw new AssertionError("Your DogTag.Factory must be static.");
          }

          // Test if the field should be included. This tests for inclusion, regardless of the selectionMode.
          //noinspection MagicCharacter
          if (!isStatic
              && !isDogTag
              // transients are tested only in exclusion mode
              && (testTransients || useInclusionMode || !Modifier.isTransient(modifiers))
              && (!finalFieldsOnly || useInclusionMode || fieldIsFinal)
              && isFieldUsed(field)
              && (field.getName().indexOf('$') < 0)
          ) {
            if (useCachedHash && !fieldIsFinal) {
              throw new AssertionError(
                  String.format("The 'withCachedHash' option may not be used with non-final field %s.", field.getName())
              );
            }
            field.setAccessible(true);
            FieldProcessor fieldProcessor = getFieldProcessorForType(field, fieldType);
            fieldProcessorList.add(fieldProcessor);
          }
        }
        if (theClass == lastSuperClass) {
          break;
        }
        theClass = theClass.getSuperclass();
      }
      return fieldProcessorList;
    }

    private FieldProcessor getFieldProcessorForType(Field field, Class<?> fieldType) {
      FieldProcessor fieldProcessor;
      if (fieldType.isArray()) {
        fieldProcessor = getProcessorForArray(field, fieldType);
      } else if (fieldType.isPrimitive()) {
        fieldProcessor = getProcessorForPrimitive(field, fieldType);
      } else {
        ToBooleanBiFunction<Object> objectToBooleanBiFunction
            = (thisOne, thatOne) -> Objects.equals(field.get(thisOne), (field.get(thatOne)));
        ToIntThrowingFunction<Object> hashFunction = (t) -> Objects.hashCode(field.get(t));
        fieldProcessor = new FieldProcessor(objectToBooleanBiFunction, hashFunction);
      }
      return fieldProcessor;
    }

    @SuppressWarnings("BoundedWildcard")
    private void collectMatchingFields(final String[] fieldNames, final Set<Field> searchField) {
      for (String fieldName : fieldNames) {
        searchField.add(getFieldFromName(fieldName));
      }
    }

    protected boolean isSelected(Field theField) {
      return selectedFields.contains(theField) || isAnnotatedWith(theField, flaggedList);
    }

    protected abstract boolean isFieldUsed(final Field theField);

    private boolean isAnnotatedWith(Field field, List<Class<? extends Annotation>> annotationList) {
      for (Class<? extends Annotation> annotationClass : annotationList) {
        if (field.isAnnotationPresent(annotationClass)) {
          return true;
        }
      }
      return false;
    }

    private static FieldProcessor getProcessorForPrimitive(Field primitiveField, Class<?> fieldType) {
      ToBooleanBiFunction<Object> primitiveEquals = null;
      ToIntThrowingFunction<Object> primitiveHash = null;
      if (fieldType == Integer.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getInt(thisOne) == primitiveField.getInt(thatOne);
        primitiveHash = primitiveField::getInt;
      } else if (fieldType == Long.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getLong(thisOne) == primitiveField.getLong(thatOne);
        primitiveHash = (instance) -> Long.hashCode(primitiveField.getLong(instance));
      } else if (fieldType == Short.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getShort(thisOne) == primitiveField.getShort(thatOne);
        primitiveHash = primitiveField::getShort;
      } else if (fieldType == Character.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getChar(thisOne) == primitiveField.getChar(thatOne);
        primitiveHash = (instance) -> Character.hashCode(primitiveField.getChar(instance));
      } else if (fieldType == Byte.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getByte(thisOne) == primitiveField.getByte(thatOne);
        primitiveHash = primitiveField::getByte;
      } else if (fieldType == Double.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> Double.doubleToLongBits(primitiveField.getDouble(thisOne))
            == Double.doubleToLongBits(primitiveField.getDouble(thatOne));
        primitiveHash = (instance) -> Double.hashCode(primitiveField.getDouble(instance));
      } else if (fieldType == Float.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> Float.floatToIntBits(primitiveField.getFloat(thisOne)) == Float.floatToIntBits(primitiveField.getFloat(thatOne));
        primitiveHash = (instance) -> Float.hashCode(primitiveField.getFloat(instance));
      } else if (fieldType == Boolean.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getBoolean(thisOne) == primitiveField.getBoolean(thatOne);
        primitiveHash = (instance) -> Boolean.hashCode(primitiveField.getBoolean(instance));
      }
      assert primitiveEquals != null : fieldType; // implies primitiveHash is also not null
      return new FieldProcessor(primitiveEquals, primitiveHash);
    }

    private static FieldProcessor getProcessorForArray(Field field, Class<?> fieldType) {
      Class<?> componentType = fieldType.getComponentType();
      ToBooleanBiFunction<Object> arrayEquals;
      ToIntThrowingFunction<Object> arrayHash;
      if (componentType == Integer.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((int[]) field.get(thisOne), (int[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((int[]) field.get(array));
      } else if (componentType == Long.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((long[]) field.get(thisOne), (long[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((long[]) field.get(array));
      } else if (componentType == Short.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((short[]) field.get(thisOne), (short[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((short[]) field.get(array));
      } else if (componentType == Character.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((char[]) field.get(thisOne), (char[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((char[]) field.get(array));
      } else if (componentType == Byte.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((byte[]) field.get(thisOne), (byte[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((byte[]) field.get(array));
      } else if (componentType == Double.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((double[]) field.get(thisOne), (double[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((double[]) field.get(array));
      } else if (componentType == Float.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((float[]) field.get(thisOne), (float[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((float[]) field.get(array));
      } else if (componentType == Boolean.TYPE) {
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((boolean[]) field.get(thisOne), (boolean[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((boolean[]) field.get(array));
      } else {
        // componentType is Object.class or some subclass of it. It is not a primitive. It may be an array, if the
        // field is a multi-dimensional array.
        assert !componentType.isPrimitive() : componentType;
        arrayEquals = (thisOne, thatOne) -> Arrays.equals((Object[]) field.get(thisOne), (Object[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.hashCode((Object[]) field.get(array));
      }
      return new FieldProcessor(arrayEquals, arrayHash);
    }
  }

  /**
   * Compare two wrapped objects for null. Your equals method should look like this:
   * <pre>
   *  {@literal @Override}
   *   public boolean equals(Object obj) {
   *     return dogTag.equals(obj);
   *   }
   * </pre>
   * You should only use this to compare wrapped objects. It is not meant to be used to compare DogTag instances.
   * @param obj The object to compare with the wrapped instance of T
   * @return true if the wrapped object is equal to {@code obj}, false otherwise
   */
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object obj) {
    return factory.doEqualsTest(thisRef.get(), obj);
  }

  /**
   * Return the hash code of the wrapped instance of T
   * @return The hash code of the wrapped instance.
   */
  @Override
  public int hashCode() {
    final T thisOne = this.thisRef.get();
    //noinspection AccessingNonPublicFieldOfAnotherObject
    return factory.useCache? factory.doCachedHashCode(thisOne, this) : factory.doHashCodeInternal(thisOne);
  }

  // These two interfaces declare a thrown exception that will actually never get thrown. I could wrap or ignore the
  // exception inside the methods declared here, but that slows down performance by a factor of 2. I don't know why.
  @FunctionalInterface
  private interface ToIntThrowingFunction<T> {
    int get(T object) throws IllegalAccessException;
  }

  @FunctionalInterface
  private interface ToBooleanBiFunction<T>  {
    boolean eval(T thisOne, T thatOne) throws IllegalAccessException;
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
   *   for H<sub>0</sub>, which is usually one or zero. The final value of V<sub>n</sub> is returned. 
   *   The default implementation sets a starting value of 1, and uses this formula:<p>
   *     &nbsp;&nbsp;V<sub>n</sub> = (31 * V<sub>n-1</sub>) + H<sub>n</sub><p>
   *   This is the same formula used by the {@code java.util.Objects.hash(Object...)} method.
   */
  @FunctionalInterface
  public interface HashBuilder {
    int newHash(int previousHash, Object nextObject);
  }

  /**
   * This class knows how to extract a value from a field, determine if two fields are equal, and get the hash code,
   * based on the stored reflected objects. 
   * Every object used by equals and hash code calculations is either a single value or an array. Arrays must be
   * handled differently from single values, and methods returning primitives must be handled differently than those
   * returning Objects. This class holds the methods for equals and hash code. The appropriate methods are passed in to
   * the constructor.
   */
  private static final class FieldProcessor {
    private final ToBooleanBiFunction<Object> compareFieldMethod; //
    private final ToIntThrowingFunction<Object> hashMethod; // This will be from either Arrays or Objects::hashCode
    
    private FieldProcessor(ToBooleanBiFunction<Object> equalMethod, ToIntThrowingFunction<Object> hashMethod) {
      compareFieldMethod = equalMethod;
      this.hashMethod = hashMethod;
    }
    
    private boolean testForEquals(Object thisOne, Object thatOne) throws IllegalAccessException {
      return compareFieldMethod.eval(thisOne, thatOne);
    }
    
    private int getHashValue(Object thisOne) throws IllegalAccessException {
      return hashMethod.get(thisOne);
    }
  }
}
