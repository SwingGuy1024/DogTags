# DogTags
Java Utility to generate a faster ReflectionEquals package

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

Available options are including transient fields, limiting the reflection to final fields, excluding specific fields, using your own hash code generator, and including ancestor classes in the reflection.

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

#### Advantages:

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

## What DogTags Don't Do
In the interest of speed and professional coding practices, there are some things DogTags do not do.

1. They won't detect cyclic dependencies. It is the responsibility of the class designers to keep cyclic dependencies out of their code. Runtime detection only slows you down. This potential problem is better fixed in the code ahead of time, during your development phase.

1. They won't recurse into member classes. Each class has the responsibility to implement its own `equals()` and `hashCode()` methods, and to make them consistent. DogTags assume that all members have fulfilled the method contracts. DogTags guarantee that its `equals()` and `hashCode()` methods will fulfill their contracts correctly, but it always defers the work of comparing two member objects or hashing their values to their own methods. If think you need it to recurse into a custom type, give that type it's own DogTag.

1. They don't eliminate the need for unit testing. Your `equals()` and `hashCode()` methods may not give you exactly what you want on the first try, so they still need to be unit tested. Many things can go wrong. You may be using an option incorrectly. You might have forgotten to include or exclude a certain field, or forgotten to include a needed super class. You may be caching a hashCode that can change value. Also, as your code will be subject to maintenance, bugs could creep in later, and your unit tests may help catch them. Test your code.