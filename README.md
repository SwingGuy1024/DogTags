# DogTags
Java Utility to generate a fast equals and hash code generation using reflection.
While this capability is covered by `org.apache.commons.lang3.builder.EqualsBuilder`, performance tests show that DogTags run anywhere from 1.5 to 20 times faster, depending on circumstances. The performance gains are greatest for objects with no similarities

Sample usage:

    public class MyClass {
      // fields and methods
      
      private static final DogTag<MyClass> dogTag = DogTag.from(MyClass.class);
      
      @Override
      public boolean equals(Object that) {
        return dogTag.doEqualsTest(this, that);
      }
      
      @Override
      public int hashCode() {
        return dogTag.doHashCode(this);
      }
    }

Because the reflection is done when building the static DogTag instance, it is only done once when the class is loaded. This gives you a big performance improvement over the Apache Commons utilities to use reflection to create equals() and hashCode() methods.

The hash code is guaranteed to be consistent with equals(). The equals() method is written according to the guidelines given in **Effective Java**, by Joshua Bloch.

Available options are discussed below.

## Alternative Approaches 

### 1. Separate Factory

I am considering rewriting the API to work a bit differently. Sample usage would look like this:

    public class MyClass {
      // fields and methods
      
      private static final DogTag.Factory<MyClass> dtFactory = DogTag.from(MyClass.class);
      private final DogTag<MyClass> dogTag = dtFactory.tag(this);
      
      @Override
      public boolean equals(Object other) {
        return dogTag.doEqualsTest(other);
      }
      
      @Override
      public int hashCode() {
        return dogTag.doHashCode();
      }
    }

The 2 chief differences are:

1. The `this` parameter has been removed from both the equals and hashCode implementations.

1. There is one more line of set-up code

#### Advantages

1) There is less to go wrong in the equals and hashCode implementations.

1) It is less work for the user to set up a cached hash code. 

1) The API for a hashed code would also be simpler.

3) The API for a hashed code would also be simpler. In fact, it would look no different than the standard hashCode() method. All the details are encapsulated away.

#### Disadvantages

1) There is more to go wrong in the setup code. Possible mistakes: 

  a. Failure to make the factory static

  b. Making the dog tag static

  c. Some users will be tempted to save a line of code by doing this:
  
    private final DogTag<MyClass> dogTag = DogTag.from(MyClass.class).tag(this);
    
This combines the factory (`DogTag.from(myClass.class)`) and the object construction (`.makeDogTag()`) into a single line. But this would mean the a new factory would get built with each new instance. This would still be faster than EqualsBuilder, but it would be a huge performance hit.

However, each of these mistakes could be detected by the builder and an exception can be thrown.
  
  
2) The setup code is more verbose

### 2. Use private Map<Class<?>, DogTag.Factory>

I could get rid of the public factory class by storing all factories in a private Map with the Class instance as the map key. I'm not sure of the wisdom of this approach, but the sample usage would look like this:

    public class MyClass {
      // fields and methods here ...
      
      private final DogTag<MyClass> dogTag = DogTag.from(MyClass.class, this);
      
      @Override
      public boolean equals(Object other) {
        return dogTag.doEqualsTest(other);
      }
      
      @Override
      public int hashCode() {
        return dogTag.doHashCode();
      }
    }
    
Or, for more complex build situations:
   
    public class MyClass extends MyBaseClass {
      // fields and methods
      
      private final DogTag<MyClass> dogTag = DogTag.create(MyClass.class, this)
        .withAnnotation(MyExcludeField.class)
        .withReflectUpTo(MyBaseClass.class)
        .build();
      
      @Override
      public boolean equals(Object other) {
        return dogTag.doEqualsTest(other);
      }
      
      @Override
      public int hashCode() {
        return dogTag.doHashCode();
      }
    }

In each example, the first time MyClass is instantiated, it would create a Factory and put it into the map. During every subsequent instantiation, it would reuse the Factory found in the map

Keeping all the DogTag factories in a Map feels like a lot of overhead, but it really doesn't add much overhead, since all the factories would be constructed even without the map. These factories wouldn't hold any additional data than the static DogTag instances for each class in the first approach. 

This approach has the advantage of having the cleanest public API.

## Building

Build using Maven. DogTags src has no dependencies, and test depends on junit and commons.lang3 (for performance comparisons with EqualsBuilder).

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

1. They don't detect cyclic dependencies. It is the responsibility of the class designers to keep cyclic dependencies out of their code. Runtime detection only slows down the code. This potential problem is better fixed in the code ahead of time, during your development phase.

1. They don't recurse into member classes. Each class has the responsibility to implement its own `equals()` and `hashCode()` methods, and to make them consistent. DogTags assume that all members have fulfilled the method contracts. DogTags guarantee that its `equals()` and `hashCode()` methods will fulfill their contracts correctly, but it always defers the work of comparing two member objects or hashing their values to their own methods. If think you need it to recurse into a custom type, give that type it's own DogTag.

1. They don't eliminate the need for unit testing. Your `equals()` and `hashCode()` methods may not give you exactly what you want on the first try, so they still need to be unit tested. Many things can go wrong. For example, you may be using an option incorrectly, or left out a needed option, or have a cyclic dependency. And since your code will be subject to maintenance, bugs could creep in later, and your unit tests may help catch them. Test your code.

1. They don't completely replace the Apache EqualsBuilder class. They're a much faster replacement for the `EqualsBuilder.reflectionEquals()` and `EqualsBuilder.reflectionHashCode()` methods, but using EqualsBuilder to build an equals method that does not use reflection is still faster than DogTags, although it's more work and requires more maintenance.
