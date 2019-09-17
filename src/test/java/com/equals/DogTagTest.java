package com.equals;

import java.awt.geom.Point2D;
import org.junit.Test;

@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber"})
public class DogTagTest {
  @Test
  public void testEquals() {
    DogTagTestBase baseTest = new DogTagTestBase(5, "bravo", 7, 5L);
    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L,"echo", new Point2D.Double(14.2, 2.14), 44);
  }
  
  @Test
  public void testEqualsDefault() {
    DogTag dogTag = DogTag.from(DogTagTestBase.class); // Tests may construct their own DogTags.
  }

  // Things to test:
  // arrays of all types
  // different length arrays
  // different value arrays
  // mismatching Nan for floats and doubles
  // superclasses
  // transients
  // excluded fields
  // excluded fields in superclasses
  // illegal superclass
  // illegal field name?
  // overridden getters!.

  @SuppressWarnings({"unused", "AssignmentOrReturnOfFieldWithMutableType", "EqualsWhichDoesntCheckParameterClass", "WeakerAccess", "PublicConstructorInNonPublicClass"})
  private static class DogTagTestBase {
    private final int alphaInt;
    private final String bravoString;
    private int charlieInt;
    private long deltaLong;

    public DogTagTestBase(int alphaInt, String bravoString, int charlieInt, long deltaLong) {
      this.alphaInt = alphaInt;
      this.bravoString = bravoString;
      this.charlieInt = charlieInt;
      this.deltaLong = deltaLong;
    }

    public int getAlphaInt() {
      return alphaInt;
    }

    public String getBravoString() {
      return bravoString;
    }

    public int getCharlieInt() {
      return charlieInt;
    }

    public void setCharlieInt(int charlieInt) {
      this.charlieInt = charlieInt;
    }

    public long getDeltaLong() {
      return deltaLong;
    }

    public void setDeltaLong(long deltaLong) {
      this.deltaLong = deltaLong;
    }
  }
  
  private static class DogTagTestMid extends DogTagTestBase {
    private String echoString;
    private Point2D foxtrotPoint;
    private transient int golfIntTr;
    private byte hotelByte;
    private char indigoChar;

    public DogTagTestMid(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, Point2D foxtrotPoint, int golfIntTr) {
      super(alphaInt, bravoString, charlieInt, deltaLong);
      this.echoString = echoString;
      this.foxtrotPoint = foxtrotPoint;
      this.golfIntTr = golfIntTr;
    }

      public String getEchoString () {
        return echoString;
      }

      public void setEchoString (String echoString){
        this.echoString = echoString;
      }

      public Point2D getFoxtrotPoint () {
        return foxtrotPoint;
      }

      public void setFoxtrotPoint (Point2D foxtrotPoint){
        this.foxtrotPoint = foxtrotPoint;
      }

      public int getGolfIntTr () {
        return golfIntTr;
      }

      public void setGolfIntTr ( int golfIntTr){
        this.golfIntTr = golfIntTr;
      }

    public byte getHotelByte() {
      return hotelByte;
    }

    public void setHotelByte(byte hotelByte) {
      this.hotelByte = hotelByte;
    }

    public char getIndigoChar() {
      return indigoChar;
    }

    public void setIndigoChar(char indigoChar) {
      this.indigoChar = indigoChar;
    }

  }
  
  private static class DogTagTestTail extends DogTagTestMid {
    
    public DogTagTestTail(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, Point2D foxtrotPoint, int golfIntTr,
                          boolean julietBoolean, short kiloShort, double limaDouble, float mikeFloat) {
      super(alphaInt, bravoString, charlieInt, deltaLong, echoString, foxtrotPoint, golfIntTr);
      this.julietBoolean = julietBoolean;
      this.kiloShort = kiloShort;
      this.limaDouble = limaDouble;
      this.mikeFloat = mikeFloat;
    }
    private boolean julietBoolean;
    private short kiloShort;
    private double limaDouble;
    private float mikeFloat;

    private int[] hotelIntArray;
    private String[] indigoStringArray;


    public int[] getHotelIntArray() {
      return hotelIntArray;
    }

    public void setHotelIntArray(int[] hotelIntArray) {
      this.hotelIntArray = hotelIntArray;
    }

    public String[] getIndigoStringArray() {
      return indigoStringArray;
    }

    public void setIndigoStringArray(String[] indigoStringArray) {
      this.indigoStringArray = indigoStringArray;
    }

    public boolean isJulietBoolean() {
      return julietBoolean;
    }

    public void setJulietBoolean(boolean julietBoolean) {
      this.julietBoolean = julietBoolean;
    }

    public short getKiloShort() {
      return kiloShort;
    }

    public void setKiloShort(short kiloShort) {
      this.kiloShort = kiloShort;
    }

    public double getLimaDouble() {
      return limaDouble;
    }

    public void setLimaDouble(double limaDouble) {
      this.limaDouble = limaDouble;
    }

    public float getMikeFloat() {
      return mikeFloat;
    }

    public void setMikeFloat(float mikeFloat) {
      this.mikeFloat = mikeFloat;
    }
    
    private static final DogTag<DogTagTestBase> dogTag = DogTag.create(DogTagTestBase.class)
        .withTransients(true)
        .withExcludedFields("charlieInt", "echoString")
        .build();

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
      return dogTag.doEqualsTest(this, obj);
    }
  }


}
