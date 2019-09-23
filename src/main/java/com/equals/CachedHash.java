package com.equals;

/**
 * A CachedHash is used to cache a hash value of a class that only uses final fields. It has no public constructors or
 * API. You may construct it by calling the {@code makeCachedHash()} method from the dogTag instance. It is only meant 
 * to be used in a hashCode method like this:
 * <pre>
 *   private static final{@literal DogTag<SomeObject>} dogTag = DogTag.create(SomeObject.class)
 *       .withCachedHash(true)
 *       .build();
 *
 *   private final CachedHash cachedHash = dogTag.makeCachedHash();
 *   
 *  {@literal @Override}
 *   public int hashCode() {
 *     return doHashCode(this, cachedHash);
 *   }
 * </pre>
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/21/19
 * <p>Time: 9:53 AM
 *
 * @see DogTag#makeCachedHash
 * @see DogTag.DogTagBuilder#withFinalFieldsOnly(boolean) 
 * @author Miguel Mu\u00f1oz
 */
public class CachedHash {
  private int hash = 0;
  private boolean set = false;
  
  int getHash() { return hash; }
  void setHash(int hash) {
    this.hash = hash;
    set=true;
  }
  
  boolean isSet() { return set; }

  @Override
  public String toString() {
    return String.valueOf(hash);
  }
}
