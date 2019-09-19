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
