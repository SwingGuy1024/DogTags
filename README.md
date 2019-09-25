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

## Alternative Approach

I am considering rewriting the API to work a bit differently. Sample usage would look like this:

    public class MyClass {
      // fields and methods
      
      private static final DogTagFactory<MyClass> dogTagFactory = DogTag.from(MyClass.class);
      
      private final DogTag<MyClass> dogTag = dogTagFactory.makeDogTag(this);
      
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

1) The `this` parameter has been removed from both the equals and hashCode implementations.

2) There is one more line of set-up code

### Advantages:

1) There is less to go wrong in the equals and hashCode implementations.

2) It is less work for the user to set up a cached hash code. 

3) The API for a hashed code would also be simpler.

### Disadvantages

1) There is more to go wrong in the setup code. Possible mistakes: 

  a) Failure to make the factory static
  b) make the dog tag static
  c) Some users will be tempted to save a line of code by doing this:
  
    private final DogTag<MyClass> = DogTag.from(myClass.class).makeDogTag();
    
This combines the factory (`DogTag.from(myClass.class)`) and the object construction (`.makeDogTag()`) into a single line. This would mean the a new factory would get built with each new instance construction. This would still be faster than EqualsBuilder, but it would still be a huge performance hit.
  
### Second Alternative

It may be possible to combine both approaches in the same code base.