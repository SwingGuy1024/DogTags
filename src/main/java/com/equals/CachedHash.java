package com.equals;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/21/19
 * <p>Time: 9:53 AM
 *
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
