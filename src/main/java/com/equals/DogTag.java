package com.equals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * <p><big><strong>Warning:</strong> This code is in alpha development. The API is still in flux, but the major features are mostly in
 * place. The API for creating DogTags will be still changing, and the documentation is not up-to-date, so don't use this in production
 * code yet.</big></p>
 * <p>Reasonably fast and easily maintainable {@code equals()} and {@code hashCode()} methods. Guarantees that {@code equals()}
 * complies with its contract, and that {@code hashCode()} is consistent with {@code equals()}, according to its contract. (There is one
 * exception to this guarantee, described in the cachedHash option, which must be used correctly if it is enabled. When that option is
 * disabled, the guarantee is solid</p>
 * <p> Requires Java 1.8+.</p>
 * <p>Two modes are available:</p>
 * <ul>
 *   <li>Reflection, which includes all non-transient fields by default</li>
 *   <li>Non-Reflective, where methods are specified using method references</li>
 * </ul>
 * <p>In the two reflective modes, users may use annotations to specify which fields to include or exclude, or may specify them directly
 * by providing field names as Strings. In the non-reflective mode, you specify what to include by passing method references.</p>
 * <p>For performance reasons, all reflection (except calls to Method.invoke()) is done when the class loads, or in some cases when the
 * first instance is created, depending on how the DogTags are created. This not only has good performance, it also improves
 * maintainability, since equals() and hashCode() use the same fields or methods.</p>
 * <p><strong>How it works:</strong></p>
 * <p>To implement {@code equals()} and {@code hashCode()} in a class called MyClass, you give every instance a DogTag. The DogTag is
 * created when the instance is created, and is used to implement the {@code equals()} and {@code hashCode()} methods. Since both
 * methods use the DogTag, they're guaranteed to be consistent.</p>
 * <p> <strong><big>Usage Examples:</big></strong></p>
 * <p><strong>Use reflection to include everything except the "alpha" property, including transients.</strong></p>
 * <p>This illustrates the standard way to create a DogTag using reflection. You first create a static factory. You set options and
 * specify fields to exclude in the factory. Then each instance calls the factory's {@code tag()} method to create its dogTag. Then
 * you implement the {@code equals()} and {@code hashCode()} methods by delegating the work to the DogTag. The DogTag's {@code equals()}
 * method tests the other object for null, then tests if it's an instance of your class, then uses the previously reflected methods to
 * determine equality. Except for the {@code Method.invoke()} method, all reflective calls are done when the static factory is created,
 * which boosts performance. This happens once, when the class is loaded.</p>
 * <pre>
 *   public final class MyClass implements Serializable {
 *     // Define various fields, getters, and methods here.
 *
 *     private static final{@literal DogTag.Factory<MyClass>} factory
 *           = DogTag.startWithAll(MyClass.class)
 *         .excludeFields("alpha") // exclude the alpha property
 *         .withTransients(true)   // Specify your options.
 *         .build();
 *     private final {@literal DogTag<MyClass>} dogTag = factory.tag(this);
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
 * <p><strong>Use reflection to include everything by default</strong></p>
 * <p> This example includes every non-transient, non-static field except the DogTag itself. (DogTags and static fields are never
 * included.) This is the simplest way to use DogTags, but offers the least flexibility. When done this way, the reflective calls
 * to MyClass.class are done when the first instance is created, rather than when the class loads. This may only be used on final classes
 * to avoid transitivity and symmetry issues. When the class is Serializable, the DogTag should be declared transient.</p>
 * <pre>
 *   public final class MyClass implements Serializable {
 *     // Define various fields, getters, and methods here.
 *
 *     // from() may only be used with final classes.
 *     private transient final{@literal DogTag<MyClass>} dogTag = DogTag.from(this);
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
 * <p><strong>Use method references to include properties called alpha, bravo, and charlieArray.</strong></p>
 * <p>This specifies three getter methods to use in the equals and hash code calculations, and enables the cachedHash option.</p>
 * <pre>
 *   public final class MyClass {
 *     // Various fields, getters, setters, and methods omitted for brevity.
 *
 *     // exclude the dogTag field
 *     private static final{@literal DogTag.Factory<MyClass>} factory = DogTag.startEmpty(MyClass.class)
 *         .useCachedHash(true)
 *         .addSimple(MyClass::getAlpha)
 *         .addSimple(MyClass::getBravo)
 *         .addArray(MyClass::getCharlieArray)
 *         .build();
 *     private final {@literal DogTag<MyClass>} dogTag = factory.tag(this);
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
 * <p>Note that the {@code equals()} method does not need to test the objects for identity, null, or if they're of the proper class. That
 * is done by the {@code equals()} method in the DogTag itself. The equals comparison is implemented according to the guidelines set down
 * in <strong>Effective Java</strong> by Joshua Bloch. The hashCode is generated using the same formula as
 * {@code java.lang.Objects.hash(Object...)}, although you are free to provide a different hash calculator. Floats and Doubles set to NaN
 * are all treated as equal, unless they are in arrays. All arrays are compared using the methods in {@code java.util.Arrays}. So for
 * arrays of float and double values, NaN are treated according to the rules for ==, which means NaN values are never equal to
 * anything.</p>
 * <p> Options include testing transient fields, excluding specific fields by name, using a cached hash code under special
 * circumstances, and specifying a superclass to limit the reflective process. These options are invoked by using one
 * of the methods beginning with "with".
 * <p>
 * Here is an example of how to specify options:
 * <pre>
 *   public class MyClass extends MyBaseClass {
 *     // Define various fields, getters, and methods here.
 *
 *     // Excludes size and date fields. Defaults to all non-transient, non-static fields.
 *     private static final{@literal DogTag.Factory<MyClass>} dogTag = DogTag.startWithAll(this, "size", "date")
 *       .withTransients(true)            // defaults to false
 *       .withReflectUpTo(MyClass.class)  // defaults to Object.class
 *       .build();
 *     private final {@literal DogTag<MyClass>} dogTag = factory.tag(this);
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
 * <p>If the class is serializable, the DogTag fields should be declared transient.</p>
 * <p><strong>Notes:</strong></p>
 * <p> The following mistakes will cause RuntimeExceptions to be thrown when the class is loaded:</p>
 *    <ul>
 *      <li>Declaring your DogTag instance static. </li>
 *      <li>Not declaring a DogTag.Factory static.</li>
 *      <li>Not declaring a static factory (unless you are using {@code DogTag.from()})</li>
 *    </ul>
 * <p>
 * When using reflection, static fields are always excluded, as are the DogTag and DogTag.Factory fields.
 * <p> For each member Object used in the equals and hash code calculations, the DogTag will call the object's
 * {@code equals()} and {@code hashCode()} methods. DogTags do not recurse into member objects.
 * <p>
 * For performance reasons, DogTags make no effort to prevent cyclic dependencies. It is the responsibility of the
 * user to exclude any fields that could cause a cyclic dependency.
 * </p>
 * <p><em>A word about error codes:</em> Each exception message begins with an error code. The purpose of this is to let me write unit
 * tests that check for the proper error message, while still allowing us to change the text message without breaking unit tests.</p>
 * @param <T> Type that uses a DogTag equals and hashCode method
 * @author Miguel Muñoz
 * @see <a href="https://github.com/SwingGuy1024/DogTags/blob/master/README.md">ReadMe.md</a>
 */
@SuppressWarnings("HardCodedStringLiteral")
public abstract class DogTag<T> {
//  public static final int DEFAULT_ORDER_VALUE = DogTagInclude.DEFAULT_ORDER_VALUE;
  private final Factory<T> factory;
  private final T instance;
  private int cachedHash;

  protected Factory<T> getFactory() {
    return factory;
  }

  protected T getInstance() {
    return instance;
  }

  public abstract static class Factory<T> {
    public final DogTag<T> tag(final T t) {
      return constructor.apply(t); // Call the DogTag constructor that was specified in the Factory constructor
    }

    protected abstract boolean doEqualsTest(T thisOneNeverNull, Object thatOneNullable);
    protected abstract int doHashCodeInternal(T thisOne);
    protected abstract Function<T, DogTag<T>> chooseConstructor(boolean useCache);

    private final int startingHash;
    private final HashBuilder hashBuilder;
    private final Function<T, DogTag<T>> constructor;

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    final int doCachedHashCode(final T thisOne, final DogTag<T> dogTag) {
      if (dogTag.cachedHash == 0) {
        dogTag.cachedHash = doHashCodeInternal(thisOne);
      }
      return dogTag.cachedHash;
    }

    protected Factory(final boolean useCache, final HashBuilder hashBuilder, final int startingHash) {
      this.constructor = chooseConstructor(useCache);
      this.hashBuilder = hashBuilder;
      this.startingHash = startingHash;
    }

    protected final int getStartingHash() {
      return startingHash;
    }

    protected final HashBuilder getHashBuilder() {
      return hashBuilder;
    }
  }

  public static final class ReflectiveFactory<T> extends Factory<T> {
    private final Class<T> targetClass;
    private final Collection<FieldProcessor<T>> fieldProcessors;

    private ReflectiveFactory(
        final Class<T> theClass,
        final Collection<FieldProcessor<T>> getters,
        final int startingHash,
        final HashBuilder hashBuilder,
        final boolean useCache
    ) {
      super(useCache, hashBuilder, startingHash);
      targetClass = theClass;
      fieldProcessors = Collections.unmodifiableCollection(getters);
    }

    @Override
    protected Function<T, DogTag<T>> chooseConstructor(final boolean useCache) {
      return useCache ?
          (t) -> new CachingDogTag<>(this, t) :
          (t) -> new NonCachingDogTag<>(this, t);
    }

    /**
     * This is the implementation of the equals() method of a DogTag instance. This sits in the inner Factory class
     * for testing purposes.
     * @param thisOneNeverNull The instance wrapped by the DogTag should get passed to this parameter
     * @param thatOneNullable  {@code 'other'} in the equals() method
     * @return true if the objects are equal, false otherwise
     */
    @Override
    @SuppressWarnings("ObjectEquality")
    protected boolean doEqualsTest(final T thisOneNeverNull, final Object thatOneNullable) {
      if (thisOneNeverNull == thatOneNullable) {
        return true;
      }

      // Includes an implicit test for null
      if (!targetClass.isInstance(thatOneNullable)) {
        return false;
      }

      final T thatOneNeverNull = targetClass.cast(thatOneNullable);

      // Putting the try/catch here instead of inside the testForEquals() method doubles the speed.
      try {
        for (final FieldProcessor<T> f : fieldProcessors) {
          if (!f.testForEquals(thisOneNeverNull, thatOneNeverNull)) {
            return false;
          }
        }
        return true;
      } catch (final IllegalAccessException e) { // Shouldn't happen: Field.accessible has been set to true.
        throw new AssertionError("E1: Illegal Access should not happen", e);
      }
    }

    /**
     * This is the implementation of the hashCode() method of a DogTag instance. This sits in the inner Factory class
     * for testing purposes.
     * @param thisOne The instance wrapped by the DogTag should get passed to this parameter
     * @return The hashCode
     */
    @Override
    protected int doHashCodeInternal(final T thisOne) {
      int hash = getStartingHash();

      // Putting the try/catch here instead of inside the testForEquals() method doubles the speed.
      try {
        for (final FieldProcessor<T> f : fieldProcessors) {
          hash = getHashBuilder().newHash(hash, f.getHashValue(thisOne));
        }
      } catch (final IllegalAccessException e) { // Shouldn't happen: Field.accessible has been set to true.
        throw new AssertionError("E3: Illegal Access shouldn't happen", e);
      }
      return hash;
    }
  }

  private DogTag(final Factory<T> factory, final T instance) {
    this.factory = factory;
    this.instance = instance;
  }

  /**
   * Instantiate a builder for a DogTag.Factory for class T that uses reflection to build a DogTag. By default, the factory will include
   * all non-transient, non-static fields in the DogTag, although you may change this behavior by calling additional methods before
   * building the factory.
   * You may exclude specific fields by calling {@code excludeFields(String... excludedFields)} specifying fields by name. The
   * fields must be in the class specified by the type parameter for the DogTag, or any superclass included by the
   * {@code withReflectUpTo()} option.
   * <p>
   * This builder allows you to specify options before building your DogTag. The getFactory() method generates the DogTag.
   * All of the default options used by the {@code from()} method are used here, but they may be overridden. All the
   * methods to set options begin with the word "with."
   * <p>
   * For example:
   * <pre>
   *     private static final{@literal DogTag.Factory<MyClass>} factory = DogTag.startWithAll(this)
   *         .excludeFields("date", "source")
   *         .withTransients(true) // options are specified here
   *         .build();
   *     private final{@literal DogTag<MyClass>} dogTag = factory.tag(this);
   *     public boolean equals(Object other) { return dogTag.equals(other); }
   *     public int hashCode() { return dogTag.hashCode(); }
   *   </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param theClass           The class of type T
   * @param <T>                The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can set options and build your DogTag.Factory.
   */
  public static <T> DogTagReflectiveBuilder<T> startWithAll(final Class<T> theClass) {
    return new DogTagReflectiveBuilder<>(theClass);
  }

  /**
   * Convenience method because getClass() returns {@literal Class<?> instead of Class<T>}. Using this lets you avoid suppressing
   * the unchecked warning when you would otherwise say {@code Class<X> xClass = x.getClass();}
   * @param t an object
   * @param <T> The inferred type of the object
   * @return the class of T, as a {@literal Class<T>} object
   */
  public static <T> Class<T> classFrom(final T t) {
    @SuppressWarnings("unchecked") final Class<T> targetClass = (Class<T>) t.getClass(); // I need to cast this because the getClass() method returns Class<?>
    return targetClass;
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
   *   {@literal DogTag.Factory<MyClass>} factory = DogTag.startEmpty(MyClass.class)
   *     .addObject(MyClass::getName)       // Add object by method reference
   *     .addObject(MyClass::getSsNumber    // Add object by method reference
   *     .addObject((MyClass m) -> m.email) // Add by fields like this
   *     .build();
   *   {@literal DogTag<MyClass>} dogTag = factory.tag(this);
   * </pre>
   * <p>
   * Options may be specified in any order.
   *
   * @param targetClass The class of type T
   * @param <T>        The type of the enclosing class.
   * @return A builder for a {@literal DogTag<T>}, from which you can specify methods, set options, and build your DogTag.
   */
  public static <T> LambdaFactory.LambdaBuilder<T> startEmpty(final Class<T> targetClass) {
    return new LambdaFactory.LambdaBuilder<>(targetClass);
  }


  abstract static class DogTagBaseBuilder<T> {
    private final Class<T> targetClass;
    private int startingHash = 1;
    private boolean useCachedHash = false;
    private static final HashBuilder defaultHashBuilder = (int h, Object o) -> (h * 31) + o.hashCode(); // Same as Objects.class
    private HashBuilder hashBuilder = defaultHashBuilder; // Reuse the same HashBuilder

    DogTagBaseBuilder(final Class<T> theClass) {
      targetClass = theClass;
    }

    /**
     * Once all options are set and all fields or methods specified, build the Factory.
     * @return A DogTag.Factory to tag instances of T
     */
    public abstract Factory<T> build();

    protected Class<T> getTargetClass() {
      return targetClass;
    }

    protected int getStartingHash() {
      return startingHash;
    }

    protected void setStartingHash(final int startingHash) {
      this.startingHash = startingHash;
    }

    protected boolean isUseCachedHash() {
      return useCachedHash;
    }

    protected void setUseCachedHash(final boolean useCachedHash) {
      this.useCachedHash = useCachedHash;
    }

    protected HashBuilder getHashBuilder() {
      return hashBuilder;
    }

    public void setHashBuilder(final HashBuilder hashBuilder) {
      this.hashBuilder = hashBuilder;
    }
  }

  public static final class DogTagReflectiveBuilder<T> extends DogTagBaseBuilder<T> {

    private DogTagReflectiveBuilder(final Class<T> theClass) {
      this(theClass, DogTagExclude.class);
    }


    // fields initialized in constructor
    private final Set<String> selectedFieldNames = new HashSet<>();
    private final List<Class<? extends Annotation>> exclusionAnnotationList;

    private Class<? super T> lastSuperClass = Object.class;    // not final. May change with options.

    // pre-initialized fields
    private boolean testTransients = false;

    private DogTagReflectiveBuilder(final Class<T> theClass, final Class<? extends Annotation> defaultSelectionAnnotation) {
      super(theClass);
      exclusionAnnotationList = new LinkedList<>();
      exclusionAnnotationList.add(defaultSelectionAnnotation);
    }

    private void addSelectedFields(final String... selectedFields) {
      selectedFieldNames.addAll(Arrays.asList(selectedFields));
    }

    private void addExclusionAnnotation(final Class<? extends Annotation> selectionAnnotation) {
      exclusionAnnotationList.add(selectionAnnotation);
    }

    /**
     * Specify a list of field names to exclude
     *
     * @param excludedFields The names of fields to exclude from the equals and hash code calculations. The
     *                       fields must be in the class specified by the type parameter for the DogTag, or any superclass included by the
     *                       {@code withReflectUpTo()} option.
     * @return this, for method chaining.
     */
    public DogTagReflectiveBuilder<T> excludeFields(final String... excludedFields) {
      addSelectedFields(excludedFields);
      return this;
    }

    /**
     * Specify an annotation class to be used to exclude fields, which may be used in addition to the
     * {@code DogTagExclude} annotation. This allows you to use an annotation of your choice, for compatibility with
     * other systems or your own framework. The {@code DogTagExclude} annotation will still work.
     *
     * @param annotationClass The class object of the custom annotation you may use to specify which fields to
     *                        exclude.
     * @return this, for method chaining.
     */
    public DogTagReflectiveBuilder<T> withExclusionAnnotation(final Class<? extends Annotation> annotationClass) {
      addExclusionAnnotation(validateAnnotationClass(annotationClass));
      return this;
    }

    /**
     * Set the Transient option, before building the DogTag. Defaults to false. This is used only in exclusion mode.
     * When true, transient fields will be included, provided they meet all the other criteria. (DogTag fields and their factories are
     * never included regardless of their transient state.
     * <p>
     * Calling this method when using inclusion mode will have no effect.
     *
     * @param useTransients true if you want transient fields included in the equals and hashCode methods
     * @return this, for method chaining
     */
    public DogTagReflectiveBuilder<T> withTransients(final boolean useTransients) {
      setTransients(useTransients);
      return this;
    }

    /**
     * Specify the highest superclass of the DogTag Type parameter class to include in the equals and hash code calculations.
     * The ancestor classes included in the {@code equals()} and {@code hashCode()} methods are those of the type class, the
     * {@code reflectUpTo} superclass, and any class that is both a superclass of the type class and a subclass of the
     * {@code reflectUpTo} superclass. Defaults to Object.class.
     *
     * @param reflectUpTo The superclass, up to which are inspected for fields to include.
     * @return this, for method chaining
     */
    public DogTagReflectiveBuilder<T> withReflectUpTo(final Class<? super T> reflectUpTo) {
      setReflectUpTo(reflectUpTo);
      return this;
    }

    /**
     * Tests the field to determine if was explicitly included or excluded;
     *
     * @param selectedFields The specified selectedFields
     * @param theField       The field to test for inclusion
     * @return true if it to be included, false otherwise.
     */
//    @Override
    private boolean isFieldUsed(final Set<Field> selectedFields, final Field theField) {
      return !isExcluded(selectedFields, theField);
    }

    /**
     * Search through classes starting with the target class, up to and including lastSuperClass, for a field with the
     * specified name.
     * @param fieldName The Field name
     * @return A Field, from one of the classes in the range from the target class to lastSuperClass.
     */
    private Field getFieldFromName(final String fieldName) {
      final Class<T> targetClass = getTargetClass();
      Class<?> theClass = targetClass;
      Class<?> previousClass = targetClass;
      boolean inProgress = true;
      while (inProgress) {
        try {
          return theClass.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
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
        throw new IllegalArgumentException(String.format("E6: Field %s not found in class %s", fieldName, targetClass)); // NON-NLS
      }
      throw new IllegalArgumentException(
          String.format("E7: Field '%s' not found from class %s to superClass %s", fieldName, targetClass, lastSuperClass)  // NON-NLS
      );
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
    public DogTagReflectiveBuilder<T> withHashBuilder(final int startingHash, final HashBuilder hashBuilder) {
      setStartingHash(startingHash);
      setHashBuilder(hashBuilder);
      return this;
    }

    /**
     * Sets the UseCachedHash option. For speed, this will cache the hash value the first time {@code hashCode()} is called, and continue
     * using the cached value.
     * <p>
     * This option should be used with caution. If enabled and used incorrectly, it will break the guarantee that
     * {@code hashCode()} and {@code equals()} will be consistent. Even if you only specify final fields, this will fail if the final
     * fields are themselves mutable. For a hashCode to be eligible for the correct use of this option, your class should be
     * <strong>deeply immutable</strong>. To be deeply immutable, the following two conditions must
     * be true of every field you include in your DogTag:
     * <br>
     * 1. The field should be final.<br>
     * 2. If the field is an Object, it should itself be deeply immutable.
     * <p>
     * The second condition, of course, makes these conditions recursive. Strings and primitive wrapper classes like {@code Integer} are
     * deeply immutable by default.
     * </p><p>
     * In other words, for any field anywhere in the object tree that is used to calculate the hash code, that field cannot change value
     * once the outermost element of the tree is constructed.
     *
     * One reason why this should be used with caution is that code maintenance can inadvertently break deep immutability.
     * @param useCachedHash true to enable the cached hash
     * @return this, for method chaining
     */
    public DogTagReflectiveBuilder<T> withCachedHash(final boolean useCachedHash) {
      setUseCachedHash(useCachedHash);
      return this;
    }

    private void setTransients(final boolean useTransients) {
      this.testTransients = useTransients;
    }

    private void setReflectUpTo(final Class<? super T> superClass) {
      this.lastSuperClass = superClass;
    }

    @Override
    public Factory<T> build() {
      return new ReflectiveFactory<>(getTargetClass(), makeGetterList(), getStartingHash(), getHashBuilder(), isUseCachedHash());
    }

    private Collection<FieldProcessor<T>> makeGetterList() {
      final Set<Field> excludedFields = new HashSet<>();
      collectMatchingFields(selectedFieldNames, excludedFields);

      // two different inclusion modes exist. Each requires a different kind of collection.
      final Collection<FieldProcessor<T>> fieldProcessorList = createEmptyFieldProcessorList();
      Class<? super T> theClass = getTargetClass();
      final int index = 0;
      boolean isStaticFactoryMissing = true;

      // We shouldn't ever reach Object.class unless someone specifies it as the reflect-up-to superclass.
      while (theClass != Object.class) {
        final Field[] declaredFields = theClass.getDeclaredFields();
        for (final Field field : declaredFields) {
          final Class<?> fieldType = field.getType();
          final int modifiers = field.getModifiers();
          final boolean isStatic = Modifier.isStatic(modifiers);
          final boolean isDogTag = fieldType == DogTag.class;
          final boolean isFactory = fieldType == DogTag.Factory.class;
          if (isDogTag && isStatic) {
            throw new IllegalArgumentException(String.format("E8: %s - Your DogTag instance must be not static. Private and final are recommended.",
                getTargetClass()));
          }
          if (isFactory && !isStatic) {
            // I'm not sure it's possible to construct an object with a non-static factory without throwing a
            // StackOverflowError or NullPointerException, but in case I'm wrong, we disallow a non-static Factory.
            throw new IllegalArgumentException(String.format("E9: %s - Your DogTag.Factory must be static. Private and final are recommended.",
                getTargetClass()));
          }
          if (isFactory) {
            isStaticFactoryMissing = false;
          }

          // Test if the field should be included. This tests for inclusion, regardless of the selectionMode.
          //noinspection MagicCharacter
          if (!isStatic
              && !isDogTag
              && (testTransients || !Modifier.isTransient(modifiers))
              && isFieldUsed(excludedFields, field)
              && (field.getName().indexOf('$') < 0) // disallow anonymous inner class fields
          ) {
            field.setAccessible(true); // move this into getFPForType?
            final FieldProcessor<T> fieldProcessor = getFieldProcessorForType(field, fieldType);

            //The wrapper contains ordering information for lists that specify an order
            fieldProcessorList.add(fieldProcessor);
          }
        }
        if (theClass == lastSuperClass) {
          break;
        }
        theClass = theClass.getSuperclass();
      }

      if (isStaticFactoryMissing) {
        throw new IllegalArgumentException(String.format("E12: No static DogTag.Factory found in %s", getTargetClass()));
      }

      // Now that they're in the proper order, we extract them from the list of wrappers and add them to the final list.
//      List<FieldProcessor<T>> finalList = new LinkedList<>();
//      for (FieldProcessorWrapper<T> wrapper: fieldProcessorList) {
//        finalList.add(wrapper.getFieldProcessor());
//      }
      return fieldProcessorList;
    }

    // Todo: Test annotated field overridden by non-annotated field. What should it do?
    private FieldProcessor<T> getFieldProcessorForType(final Field field, final Class<?> fieldType) {
      final FieldProcessor<T> fieldProcessor;
      if (fieldType.isArray()) {
        fieldProcessor = getProcessorForArray(field, fieldType);
      } else if (fieldType.isPrimitive()) {
        fieldProcessor = getProcessorForPrimitive(field, fieldType);
      } else {
        final ToBooleanBiFunction<T> objectToBooleanBiFunction
            = (thisOne, thatOne) -> Objects.equals(field.get(thisOne), (field.get(thatOne)));
        final ToIntThrowingFunction<T> hashFunction = (t) -> Objects.hashCode(field.get(t));
        fieldProcessor = new FieldProcessor<>(objectToBooleanBiFunction, hashFunction);
      }
      return fieldProcessor;
    }

    @SuppressWarnings("BoundedWildcard")
    private void collectMatchingFields(final Set<String> fieldNames, final Set<Field> searchField) {
      for (final String fieldName : fieldNames) {
        searchField.add(getFieldFromName(fieldName));
      }
    }

    private boolean isExcluded(final Set<Field> selectedFields, final Field theField) {
      return selectedFields.contains(theField) || isAnnotatedWith(theField, exclusionAnnotationList);
    }

    private boolean isAnnotatedWith(final Field field, final List<Class<? extends Annotation>> annotationList) {
      for (final Class<? extends Annotation> annotationClass : annotationList) {

        // This will fail if the user is using two annotations on the same field, and one without an order field is detected first.
        if (field.isAnnotationPresent(annotationClass)) {
          recordOrder(field, annotationClass);
          return true;
        }
      }
     return false;
    }

    /**
     * I used to do this with lambda expressions, like this:
     * <pre>
     *         if (fieldType == Integer.TYPE) {
     *         primitiveEquals = (thisOne, thatOne) {@literal ->} primitiveField.getInt(thisOne) == primitiveField.getInt(thatOne);
     *         primitiveHash = primitiveField::getInt;
     * </pre>
     * That turned out to be slightly slower than the current implementation.
     * @param primitiveField The field
     * @param fieldType The type of the field
     * @param <T> The type of DogTag
     * @return A field processor for the specified field.
     */
    private static <T> FieldProcessor<T> getProcessorForPrimitive(final Field primitiveField, final Class<?> fieldType) {

      ToBooleanBiFunction<T> primitiveEquals = null;
      ToIntThrowingFunction<T> primitiveHash = null;

      // I can't use a switch statement, because fieldType isn't a number, String, or enum!
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
        primitiveEquals = (thisOne, thatOne)
            -> Float.floatToIntBits(primitiveField.getFloat(thisOne)) == Float.floatToIntBits(primitiveField.getFloat(thatOne));
        primitiveHash = (instance) -> Float.hashCode(primitiveField.getFloat(instance));
      } else if (fieldType == Boolean.TYPE) {
        primitiveEquals = (thisOne, thatOne) -> primitiveField.getBoolean(thisOne) == primitiveField.getBoolean(thatOne);
        primitiveHash = (instance) -> Boolean.hashCode(primitiveField.getBoolean(instance));
      }
      assert primitiveEquals != null : fieldType; // implies primitiveHash is also not null
      return new FieldProcessor<>(primitiveEquals, primitiveHash);
    }

    private static <T> FieldProcessor<T> getProcessorForArray(final Field field, final Class<?> fieldType) {
      final Class<?> componentType = fieldType.getComponentType();
      final ToBooleanBiFunction<T> arrayEquals;
      final ToIntThrowingFunction<T> arrayHash;

      // I can't use a switch statement, because fieldType isn't a number, String, or enum!
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
        arrayEquals = (thisOne, thatOne) -> Arrays.deepEquals((Object[]) field.get(thisOne), (Object[]) field.get(thatOne));
        arrayHash = (array) -> Arrays.deepHashCode((Object[]) field.get(array));
      }
      return new FieldProcessor<>(arrayEquals, arrayHash);
    }

    /**
     * Record the order of the field in a temporary map. Only used in Inclusion mode.
     * @param field The field to record
     * @param nullableAnnotationClass The annotation class used on the field. May be null, if the fields is not included with
     *                                an annotation class.
     */
    @SuppressWarnings("NoopMethodInAbstractClass")
    protected void recordOrder(final Field field, final Class<? extends Annotation> nullableAnnotationClass) { } // default implementation does nothing.

    // All inherited "with<Option> methods must be overridden to return DogTagReflectiveBuilder instead of the
    // default DogTagFullBuilder:

//    @Override
    private Collection<FieldProcessor<T>> createEmptyFieldProcessorList() {
      return new LinkedList<>();
    }

////    @Override
//    protected int getOrderForField(final Field field) {
//      return DEFAULT_ORDER_VALUE;
//    }
  }

  private static Class<? extends Annotation> validateAnnotationClass(final Class<?> annotationClass) {
    if (annotationClass.isAnnotation()) {
      @SuppressWarnings("unchecked") final Class<? extends Annotation> aClass = (Class<? extends Annotation>) annotationClass;
      final Target target = aClass.getAnnotation(Target.class);
      for (final ElementType elementType: target.value()) {
        if (elementType == ElementType.FIELD) {
          return aClass;
        }
      }
      throw new IllegalArgumentException(String.format("E2: Specified %s does not target Fields", aClass));
    }
    throw new IllegalArgumentException(String.format("E5: Specified %s is not an annotation", annotationClass));
  }

  /**
   * Compare two wrapped objects for null. Your equals method should call this method like this:
   * <pre>
   *  {@literal @Override}
   *   public boolean equals(Object obj) {
   *     return dogTag.equals(obj);
   *   }
   * </pre>
   * You should only use this to compare objects that are wrapped by DogTags. It is not meant to be used to compare DogTag instances.
   * @param obj The object to compare with the wrapped instance of T
   * @return true if the wrapped object is equal to {@code obj}, false otherwise
   */
  @Override
  public abstract boolean equals(Object obj);

  /**
   * Return the hash code of the wrapped instance of T
   * @return The hash code of the wrapped instance.
   */
  @Override
  public abstract int hashCode();

  // These two interfaces declare a thrown exception that will actually never get thrown. I could wrap or ignore the
  // exception inside the methods declared here, but that slows down performance by a factor of 2.
  @FunctionalInterface
  interface ToIntThrowingFunction<T> {
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
  static final class FieldProcessor<T> {
    private final ToBooleanBiFunction<T> compareFieldMethod; //
    private final ToIntThrowingFunction<T> hashMethod; // This will be from either Arrays or Objects::hashCode

    private FieldProcessor(final ToBooleanBiFunction<T> equalMethod, final ToIntThrowingFunction<T> hashMethod) {
      compareFieldMethod = equalMethod;
      this.hashMethod = hashMethod;
    }

    private boolean testForEquals(final T thisOne, final T thatOne) throws IllegalAccessException {
      return compareFieldMethod.eval(thisOne, thatOne);
    }

    private int getHashValue(final T thisOne) throws IllegalAccessException {
      return hashMethod.get(thisOne);
    }
  }

//  private static final class FieldProcessorWrapper<T> implements Comparable<FieldProcessorWrapper<T>> {
//    private final FieldProcessor<T> fieldProcessor;
//    private final int order;
//    private final int index;
//    private FieldProcessorWrapper(FieldProcessor<T> fieldProcessor, int order, int index) {
//      this.fieldProcessor = fieldProcessor;
//      this.order = order;
//      this.index = index;
//    }
//
//    @Override
//    public int compareTo(final FieldProcessorWrapper<T> that) {
//
//      int orderCmp = Integer.compare(this.order, that.order);
//      return (orderCmp == 0) ? Integer.compare(this.index, that.index) : orderCmp;
//    }
//
//    // These two methods don't get executed, but I keep them because later maintenance may find a use for them.
//    @Override
//    public boolean equals(final Object obj) {
//      if (obj instanceof FieldProcessorWrapper) {
//        @SuppressWarnings("unchecked")
//        FieldProcessorWrapper<T> that = (FieldProcessorWrapper<T>) obj;
//        return (this.order == that.order) && (this.fieldProcessor == that.fieldProcessor);
//      }
//      return false;
//    }
//
//    @Override
//    public int hashCode() {
//      return fieldProcessor.hashCode();
//    }
//
//    FieldProcessor<T> getFieldProcessor() { return fieldProcessor; }
//  }

  private static final class NonCachingDogTag<N> extends DogTag<N> {

    private NonCachingDogTag(final Factory<N> factory, final N instance) {
      super(factory, instance);
    }

    @Override
    public int hashCode() {
      return getFactory().doHashCodeInternal(this.getInstance());
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return getFactory().doEqualsTest(getInstance(), that);
    }

  }

  private static final class CachingDogTag<N> extends DogTag<N> {

    private CachingDogTag(final Factory<N> factory, final N instance) {
      super(factory, instance);
    }

    @Override
    public int hashCode() {
      return getFactory().doCachedHashCode(getInstance(), this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return getFactory().doEqualsTest(getInstance(), that);
    }
  }

  private static final class LambdaDogTag<X> extends DogTag<X> {

    private LambdaDogTag(final Factory<X> factory, final X instance) {
      super(factory, instance);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return getFactory().doEqualsTest(getInstance(), that);
    }

    @Override
    public int hashCode() {
      return getFactory().doHashCodeInternal(getInstance());
    }
  }

  private static final class CachingLambdaDogTag<X> extends DogTag<X> {

    private CachingLambdaDogTag(final Factory<X> factory, final X instance) {
      super(factory, instance);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      return getFactory().doEqualsTest(getInstance(), that);
    }

    @Override
    public int hashCode() {
      return getFactory().doCachedHashCode(getInstance(), this);
    }
  }

  public static final class LambdaFactory<T> extends Factory<T> {
    private final List<EqualHandler<T>> equalHandlerList;
    private final List<HashHandler<T>> hashHandlerList;
    private final Class<T> targetClass;

    LambdaFactory(
        final Class<T> theClass,
        final int startingHash,
        final HashBuilder hashBuilder,
        final boolean useCache,
        final List<EqualHandler<T>> equalHandlerList,
        final List<HashHandler<T>> hashHandlerList
    ) {
      super(useCache, hashBuilder, startingHash);
      targetClass = theClass;
      this.equalHandlerList = Collections.unmodifiableList(equalHandlerList);
      this.hashHandlerList = Collections.unmodifiableList(hashHandlerList);
    }

    @Override
    protected Function<T, DogTag<T>> chooseConstructor(final boolean useCache) {
      return useCache ?
          (t) -> new CachingLambdaDogTag<>(this, t) :
          (t) -> new LambdaDogTag<>(this, t);
    }

    // TODO: Handle this using Iterable
    Collection<EqualHandler<T>> getEqualHandlerList() {
      return equalHandlerList;
    }

    Collection<HashHandler<T>> getHashHandlerList() {
      return hashHandlerList;
    }

    Class<T> getTargetClass() {
      return targetClass;
    }

    @Override
    protected boolean doEqualsTest(final T thisOne, final Object thatOne) {
      //noinspection ObjectEquality
      if (thisOne == thatOne) {
        return true;
      }
      if (thatOne == null) {
        return false;
      }
      final Class<T> thisClass = getTargetClass();
      if (!thisClass.isAssignableFrom(thatOne.getClass())) {
        return false;
      }
      final T thatOneNotNull = thisClass.cast(thatOne);
      final Collection<EqualHandler<T>> equalHandlers = getEqualHandlerList();

      /* This imperative loop outperforms the stream expression that's commented out below. */
      for (final EqualHandler<T> handler : equalHandlers) {
        if (!handler.doEqual(thisOne, thatOneNotNull)) {
          return false;
        }
      }
      return true;

      /* This stream expression takes anywhere from 45% to 150% longer than the old-fashioned imperative implementation above. */
//      return equalHandlers.stream()
//          .allMatch(h -> h.doEqual(thisOne, thatOneNotNull));
    }

    @Override
    public int doHashCodeInternal(final T thisOne) {
      final List<Integer> hashValues = new LinkedList<>();
      final Collection<HashHandler<T>> hashHandlers = getHashHandlerList();
      for (final HashHandler<T> hashHandler : hashHandlers) {
        hashValues.add(hashHandler.doHashCode(thisOne));
      }
      return Arrays.deepHashCode(hashValues.toArray());
    }

    public static class LambdaBuilder<T> extends DogTagBaseBuilder<T> {
//      private final List<FieldHandler<T>> fieldHandlerList = new LinkedList<>();
      private final List<EqualHandler<T>> equalHandlerList = new LinkedList<>();
      private final List<HashHandler<T>> hashHandlerList = new LinkedList<>();

      LambdaBuilder(final Class<T> theClass) {
        super(theClass);
      }

      /**
       * Sets the usedCachedHash option. This will cache the hash value the first time {@code hashCode()} is called, and continue using
       * the cached value.
       * <p>
       * This option should be used with extreme caution. If enabled and used incorrectly, it will break the guarantee that
       * {@code hashCode()} and {@code equals()} will be consistent. Even if you only specify final fields, this will fail if the final
       * fields are themselves mutable objects. For a hashCode to be eligible for the correct use of this option, the following two
       * conditions must be true of every field you include in your DogTag:
       * <br>
       * 1. It must be impossible to change the value once the owning object has been constructed.<br>
       * 2. If the field is an Object, All if its internal fields used in its hash code calculation must meet both of these conditions.
       * <p>
       * The second, of course, makes this condition recursive. In other words, if any field anywhere in the object tree of any
       * field is used to calculate a hash code, that field cannot change value once the outermost element of the tree
       * is constructed.
       * <p>Of course, if all your Object fields are Strings, primitives, or primitive wrapper classes, and all are final, then
       * your class qualifies.</p>
       * @param useCachedHash True if a cached hash should be used
       * @return this, for method chaining
       */
      public LambdaBuilder<T> withCachedHash(final boolean useCachedHash) {
        setUseCachedHash(useCachedHash);
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToIntFunction<T> intFunction) {
        equalHandlerList.add((thisOne, thatOne) -> intFunction.applyAsInt(thisOne) == intFunction.applyAsInt(thatOne));
        hashHandlerList.add(intFunction::applyAsInt);
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToLongFunction<T> longFunction) {
        equalHandlerList.add((thisOne, thatOne) -> longFunction.applyAsLong(thisOne) == longFunction.applyAsLong(thatOne));
        hashHandlerList.add(thisOne -> Long.hashCode(longFunction.applyAsLong(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToCharFunction<T> charFunction) {
        equalHandlerList.add((thisOne, thatOne) -> charFunction.applyAsChar(thisOne) == charFunction.applyAsChar(thatOne));
        hashHandlerList.add(thisOne -> Character.hashCode(charFunction.applyAsChar(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToByteFunction<T> byteFunction) {
        equalHandlerList.add((thisOne, thatOne) -> byteFunction.applyAsByte(thisOne) == byteFunction.applyAsByte(thatOne));
        hashHandlerList.add(thisOne -> Byte.hashCode(byteFunction.applyAsByte(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToShortFunction<T> shortFunction) {
        equalHandlerList.add((thisOne, thatOne) -> shortFunction.applyAsShort(thisOne) == shortFunction.applyAsShort(thatOne));
        hashHandlerList.add(thisOne -> Short.hashCode(shortFunction.applyAsShort(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToFloatFunction<T> floatFunction) {
        equalHandlerList.add((thisOne, thatOne)
            -> Float.floatToIntBits(floatFunction.applyAsFloat(thisOne)) == Float.floatToIntBits(floatFunction.applyAsFloat(thatOne)));
        hashHandlerList.add(thisOne -> Float.hashCode(floatFunction.applyAsFloat(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToDoubleFunction<T> doubleFunction) {
        equalHandlerList.add((thisOne, thatOne)
            -> Double.doubleToLongBits(doubleFunction.applyAsDouble(thisOne)) == Double.doubleToLongBits(doubleFunction.applyAsDouble(thatOne)));
        hashHandlerList.add(thisOne -> Double.hashCode(doubleFunction.applyAsDouble(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addObject(final ToObjectFunction<T> objectFunction) {
        equalHandlerList.add((thisOne, thatOne) -> {

          // method overloading can't distinguish between Object and Object[], so the user may accidentally call this instead of
          // the addObjectArray() method. To make this class foolproof, we test for arrays here and call the Arrays.deepEquals() method
          // if appropriate.
          final Object thisArray = objectFunction.applyAsObject(thisOne);
          final Object thatThing = objectFunction.applyAsObject(thatOne);
          if ((thisArray != null) && thisArray.getClass().isArray()) {
            return (thatThing != null) && thatThing.getClass().isArray() && Arrays.deepEquals((Object[]) thisArray, (Object[]) thatThing);
          }
          return Objects.equals(thisArray, thatThing);
        });
        hashHandlerList.add(thisOne -> {

          // method overloading can't distinguish between Object and Object[], so the user may accidentally call this instead of
          // the addObjectArray() method. To make this class foolproof, we test for arrays here and call the Arrays.deepHashCode() method
          // if appropriate.
          final Object thisObject = objectFunction.applyAsObject(thisOne);
          if ((thisObject != null) && thisObject.getClass().isArray()) {
            return Arrays.deepHashCode((Object[]) thisObject);
          }
          return (Objects.hashCode(thisObject));
        });
        return this;
      }

      public LambdaBuilder<T> addSimple(final ToBooleanFunction<T> booleanFunction) {
        equalHandlerList.add((thisOne, thatOne) -> booleanFunction.applyAsBoolean(thisOne) == booleanFunction.applyAsBoolean(thatOne));
        hashHandlerList.add(thisOne -> Boolean.hashCode(booleanFunction.applyAsBoolean(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToIntArrayFunction<T> intArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(intArrayFunction.applyAsIntArray(thisOne), intArrayFunction.applyAsIntArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(intArrayFunction.applyAsIntArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToLongArrayFunction<T> longArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(longArrayFunction.applyAsLongArray(thisOne), longArrayFunction.applyAsLongArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(longArrayFunction.applyAsLongArray(thisOne)));
        return this;
      }


      public LambdaBuilder<T> addArray(final ToCharArrayFunction<T> charArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(charArrayFunction.applyAsCharArray(thisOne), charArrayFunction.applyAsCharArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(charArrayFunction.applyAsCharArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToByteArrayFunction<T> byteArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(byteArrayFunction.applyAsByteArray(thisOne), byteArrayFunction.applyAsByteArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(byteArrayFunction.applyAsByteArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToShortArrayFunction<T> shortArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(shortArrayFunction.applyAsShortArray(thisOne), shortArrayFunction.applyAsShortArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(shortArrayFunction.applyAsShortArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToFloatArrayFunction<T> floatArrayFunction) {
        equalHandlerList.add((thisOne, thatOne) -> Arrays.equals(floatArrayFunction.applyAsFloatArray(thisOne), floatArrayFunction.applyAsFloatArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(floatArrayFunction.applyAsFloatArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToDoubleArrayFunction<T> doubleArrayFunction) {
        equalHandlerList.add((thisOne, thatOne)
            -> Arrays.equals(doubleArrayFunction.applyAsDoubleArray(thisOne), doubleArrayFunction.applyAsDoubleArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(doubleArrayFunction.applyAsDoubleArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToBooleanArrayFunction<T> booleanArrayFunction) {
        equalHandlerList.add((thisOne, thatOne)
            -> Arrays.equals(booleanArrayFunction.applyAsBooleanArray(thisOne), booleanArrayFunction.applyAsBooleanArray(thatOne)));
        hashHandlerList.add((thisOne) -> Arrays.hashCode(booleanArrayFunction.applyAsBooleanArray(thisOne)));
        return this;
      }

      public LambdaBuilder<T> addArray(final ToObjectArrayFunction<T> objectArrayFunction) {
        // Because this could be a multi-dimensional array, we call the deep version of equals and hashCode.
          equalHandlerList.add((thisOne, thatOne)
              -> Arrays.deepEquals(objectArrayFunction.applyAsObjectArray(thisOne), objectArrayFunction.applyAsObjectArray(thatOne)));
          hashHandlerList.add((thisOne) -> Arrays.deepHashCode(objectArrayFunction.applyAsObjectArray(thisOne)));
        return this;
      }

      @Override
      public Factory<T> build() {
        // Check for a static factory
        boolean isFactoryMissing = true;
        final Class<T> targetClass = getTargetClass();

        // We don't need to recurse into subclasses.
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for (final Field m: declaredFields) {
          if (m.getType().equals(DogTag.Factory.class)) {
            final int modifiers = m.getModifiers();
            if (Modifier.isStatic(modifiers)) {
              isFactoryMissing = false;
              break;
            }
            throw new IllegalArgumentException(String.format("E13: %s - Your DogTag.Factory must be static. Private and final are recommended.", targetClass));
          }
        }
        if (isFactoryMissing) {
          throw new IllegalArgumentException(String.format("E14: No static DogTag.Factory found in %s.", targetClass));
        }
        return new LambdaFactory<>(targetClass, getStartingHash(), getHashBuilder(), isUseCachedHash(), equalHandlerList, hashHandlerList);
      }
    }
  }

  @FunctionalInterface
  private interface EqualHandler<T> {
    boolean doEqual(T thisOne, T thatOne);
  }

  @FunctionalInterface
  private interface HashHandler<T> {
    int doHashCode(T t);
  }

}
