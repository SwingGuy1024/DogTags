# Proposed API Ideas

## Exclusion Mode

Exclusion mode uses reflection to include everything in the specified class except certain specified fields. 

Start with all fields in class Alpha, exclude fields alpha and bravo.

    public class Alpha {
      // fields and methods omitted for brevity
      
      DogTag.Factory<Alpha> factory = DogTag.startWithAll(Alpha.class)
          .excludeFields("alpha", "bravo")
          .build();
      DogTag<Alpha> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }

Start with all fields in class Bravo, include transients, and enable the cached hash.

    public class Bravo {
      // fields and methods omitted for brevity

      DogTag.Factory<Bravo> factory = DogTag.startWithAll(Bravo.class)
          .withTestTransients(true)
          .withCachedHash(true);
          .build();
      DogTag<Bravo> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }
    
Start with all fields in class Charlie, calling the super.equals() method first.

    public class Charlie extends Alpha {
      // fields and methods omitted for brevity

      DogTag.Factory<Charlie> factory = DogTag.startWithAll(Charlie.class)
          .withSuperEquals()
          .build();
      DogTag<Charlie> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }

Start with all fields in classes Delta and its superclass, excluding fields echo and foxtrot

    public class Delta extends Bravo {
      // fields and methods omitted for brevity

      DogTag.Factory<Delta> factory = DogTag.startWithAll(Delta.class)
          .exclude("echo", "foxtrot")
          .withReflectUpTo(Bravo.class)
          .build();
      DogTag<Delta> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }
The reflectUpTo option defaults to the current class. To include fields in a super class, you must either call 
`withReflectUpTo()` and specify an ancestor class, or call `withSuperEquals()` to call the equals method of the 
super class directly. By default, super class fields are not included. 

## Inclusion Mode

Inclusion mode starts with nothing and requires `addXxx()` methods to add properties by lambda expressions or method references.

Start with no fields, include getters for fields echo and foxtrot, and the field golf.

    public class Alpha {
      // fields and methods omitted for brevity
      
      DogTag.Factory<Alpha> factory = DogTag.startEmpty(Alpha.class)
          .add(Alpha::getEcho)      // add the getEcho() method
          .add(Alpha::getFoxtrot)   // add the getFoxtrot() method
          .add((Alpha a) -> a.golf)  // add the golf field
          .build();
      DogTag<Alpha> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }

Start with no fields, include getters for fields echo and foxtrot, and the field golf, and enable the cached hash.

    public class Bravo {
      // fields and methods omitted for brevity

      DogTag.Factory<Bravo> factory = DogTag.startEmpty(Bravo.class)
          .add(Alpha::getEcho)      // add the getEcho() method
          .add(Alpha::getFoxtrot)   // add the getFoxtrot() method
          .add((Alpha a) -> a.golf)  // add the golf field
          .withCachedHash(true);
          .build();
      DogTag<Bravo> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }
    
Start with no fields, include getters for fields echo and foxtrot, and the field golf, calling the super.equals() method first.

    public class Charlie extends Alpha {
      // fields and methods omitted for brevity

      DogTag.Factory<Charlie> factory = DogTag.startEmpty(Charlie.class)
          .add(Charlie::getEcho)      // add the getEcho() method
          .add(Charlie::getFoxtrot)   // add the getFoxtrot() method
          .add((Charlie a) -> a.golf)  // add the golf field
          .addSuperMethods(Alpha::equals, Alpha::hashCode) // Would this work?
          .addSuperMethods(super::equals, super::hashCode) // Would this work?
          .build();
      DogTag<Charlie> dogTag = factory.tag(this);

      public booelan equals(Object that) { return dogTag.equals(that); }
      public int hashCode() { return dogTag.hashCode(); }
    }



## Builder Structure
DogTagBaseBuilder

DogTagReflectionBuilder -> DogTagBaseBuilder

DogTagLambdaBuilder -> DogTagBaseBuilder
