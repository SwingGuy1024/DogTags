# DogTags
Java Utility to generate a fast equals and hash code generation using reflection.
While this capability is covered by `org.apache.commons.lang3.builder.EqualsBuilder`, performance tests show that DogTags run anywhere 
from 1.5 to 20 times faster, depending on circumstances. The performance gains are greatest for objects with no similarities. 

DogTags achieves this gain by doing all the reflective inspections once, ahead of time, instead of each time the `equals()` or `hashCode()` 
methods are called.

There are two ways to use it. They are very similar. They are Reflect-On-Class-Load and Reflect-On-First_instantiation.

## Sample Usages:

### Reflect on Class Load

    public class MyClass extends Serializable {
      private static final DogTag.Factory<MyClass> factory = DogTag.create(MyClass.class).build()
      private transient final DogTag<MyClass> dogTag = factory.tag(this);
      
      // ... (fields and methods omitted for brevity)
      
      @Override
      public boolean equals(Object obj) {
        return dogTag.equals(obj);
      }
      
      @Override
      public int hashCode() {
        return dogTag.hashCode();
      }
    }
The reflection happens when the static DogTag.Factory instance is constructed. Since this is required to be a static field, this will 
only happen once for each class. (Failure to declare the factory static will result in a runtime exception when the class loads.) Each 
instance of MyClass gets its own DogTag instance.

### Reflect-On-First-Instantiation

    public class MyClass {
      // ... (fields and methods here)
      
      private final DogTag<MyClass> dogTag = DogTag.from(this);
      
      @Override
      public boolean equals(Object obj) {
        return dogTag.equals(obj);
      }
      
      @Override
      public int hashCode() {
        return dogTag.hashCode();
      }
    }

The reflection happens the first time an instance of `MyClass` is constructed. The static `DogTag.Factory` is built, and is held invisibly in an internal `HashMap`, with the Class object as the key. 

Because the reflection is done when building an invisible DogTag.Factory instance, it is only done once when the class is loaded or first instantiated. This gives you a big performance improvement over the Apache Commons utilities to use reflection to create equals() and hashCode() methods.

The hash code is guaranteed to be consistent with equals(). The equals() method is written according to the guidelines given in **Effective Java**, by Joshua Bloch.

Available options are discussed below.

    
In both approaches, options may be specified during creation. The option methods are on internal Builder classes, so the same methods are available in both approaches:
   
    public class MyClass extends MyBaseClass {
      // ... (fields and methods here)
      
      private static final DogTag.Factory<MyClass> factory = DogTag.create(this)
        .withAnnotation(MyExcludeAnnotation.class)
        .withReflectUpTo(MyBaseClass.class)
        .build();

      private final DogTag<MyClass> dogTag = factory.tag(this);
      
      @Override
      public boolean equals(Object other) {
        return dogTag.equals(other);
      }
      
      @Override
      public int hashCode() {
        return dogTag.hashCode();
      }
    }

In each example, the first time MyClass is instantiated, it would create a Factory and put it into the map. During every subsequent instantiation, it would reuse the Factory found in the map

Keeping all the DogTag factories in a Map feels like a lot of overhead, but it really doesn't add much overhead, since all the factories would be constructed even without the map. These factories wouldn't hold any additional data than the static DogTag instances for each class in the first approach. 

This approach has the advantage of having the cleanest public API.

## Options

### All Modes

`withHashBuilder(int startingHash, HashBuilder hashBuilder)`
`withCachedHash(boolean useCachedHash)`

### Reflective DogTags (Inclusion and Exclusion)


#### Inclusion Only Options
  `withInclusionAnnotation(Class<? extends Annotation> annotationClass)`

#### Exclusion Only Options
`withExclusionAnnotation(Class<? extends Annotation> annotationClass)`
`withTransients(boolean useTransients)`
`withFinalFieldsOnly(boolean useFinalFieldsOnly)`
`withReflectUpTo(Class<? super T> reflectUpToClass`

## Building

Build using Maven. The DogTags `src` folder has no dependencies, and the `test` folder depends on junit and commons.lang3 (for performance comparisons with EqualsBuilder).

The project is in an experimental state. It's usable in this state, but the API may change.

### Available options are:

#####  Exclusion and Inclusion mode
Defaults to exclusion mode, where all eligible fields are used unless explicity excluded.
In inclusion mode, no fields are included unless explicitly included. Fields may be marked
for inclusion or exclusion by specifying the field name or annotating the field. DogTags provides annotations for including/excluding, but allows you to use your preferred annotation for compatibility with other tools

##### Transients *(exclusion mode only)*
By default, transients are excluded unless using inclusion mode or enabling the *transients* option. (Transients need no special status in inclusion mode, since nothing is included automatically anyway.) 

##### ReflectUpTo
Allows you to specify an ancestor class to include ancestral fields. By defualt, only fields from the target class are used, but as many ancestors as you need may be included.

##### HashBuilder
By default, hash codes are calculated using the same formula as `Objects.hash()`. But you may provide your own hash calculator instead.

##### FinalFieldsOnly  *(exclusion mode only)*
Only final fields are included. This also allows you to cache the hashed value to improve hashing performance.

##### CachedHash
Cache the hash value for improved performance. This should be used with caution, and must be explicitly enabled. The current design requires your 'hashCode()' implementation to be written in a certain way, but the alternative approaches (below) will eliminate that requirement and encapsulate all the details of the hash cache.

### Planned Options under Consideration
##### Specifying Field Order *(inclusion mode only)*
This will probably be done through the annotations, but could also be specified by the order of the listed properties in inclusion mode.

##### Property Mode
For situations where a security manager prevents you from using reflected fields, or when getting values by property is more appropriate.

## What DogTags Don't Do
In the interest of speed and professional coding practices, there are some things DogTags do not do.

1. They don't detect cyclic dependencies. It is the responsibility of the class designers to keep cyclic dependencies out of their code. Runtime detection only slows down the code. This potential problem is better fixed in the code ahead of time, during your development phase. Cyclic dependencies can be detected in unit tests. Consequently…

1. They don't eliminate the need for unit testing. Your `equals()` and `hashCode()` methods may not give you exactly what you want on the first try, so they still need to be unit tested. Many things can go wrong. For example, you may be using an option incorrectly, or left out a needed option, or have a cyclic dependency. And since your code will be subject to maintenance, bugs could creep in later, and your unit tests may help catch them. Tests of the equals() and hashCode() methods are easy to write. Test your code.

1. They don't recurse into member classes. Each class has the responsibility to implement its own `equals()` and `hashCode()` methods, and to make them consistent. DogTags assume that all members have fulfilled the method contracts correctly. DogTags guarantee that its `equals()` and `hashCode()` methods will fulfill their contracts correctly, but it always defers the work of comparing two member objects or hashing their values to their own methods. If think you need it to recurse into a custom type, give that type it's own DogTag.

1. They don't completely replace the Apache EqualsBuilder class. DogTags are a much faster replacement for the `EqualsBuilder.reflectionEquals()` and `EqualsBuilder.reflectionHashCode()` methods, but using EqualsBuilder to build an equals method that does not use reflection is still faster than DogTags, although it's more work and requires more maintenance.

## Comparison with ReflectionEquals, EqualsBuilder & HashCodeBuilder
DogTags's different modes fill the same niche as all three of the Apache commons-lang classes. That said, there are still advantages to using the Apache classes, but there are other advantages to using DogTags. For example:
### Using Reflection
1. When using reflection, DogTags makes all its reflection queries once, ahead of time, for each class that uses them. This gives DogTags a considerable performance boost.
1. Because the DogTag instance uses the same fields and methods to implement both equals() and hashCode(), it can guarantee that the two
methods will be consistent. Of course, consistency is easy to accomplish. But with larger classes, after undergoing a great deal of maintenance where new fields are added, inconsistencies can creep in. DogTags prevents introducing new fields from causing inconsistent equals() and hashCode() methods, making code maintenance more reliable.
1. DogTags take care of the identity test and `instanceof` test (with its implicit null test). On the downside, the code to set up the DogTag.Factory makes the setup more verbose.

### Using 