package com.equals;

import java.awt.geom.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public class DogTagTest {
  @Test
  public void testEquals() {
    DogTagTestBase baseTest1 = new DogTagTestBase(5, "bravo", 7, 5L);
    DogTagTestBase baseTest2 = baseTest1.duplicate();
    baseTest2.setCharlieInt(12);
    
    DogTag<DogTagTestBase> excludeC = DogTag.create(DogTagTestBase.class)
        .withExcludedFields("charlieInt")
        .build();

    testForMatch(excludeC, baseTest1, baseTest2);
    testForMatch(excludeC, baseTest2, baseTest1);
//    assertTrue(excludeC.doEqualsTest(baseTest1, baseTest2));
//    assertTrue(excludeC.doEqualsTest(baseTest2, baseTest1));
    
    DogTag<DogTagTestBase> includeAll = DogTag.from(DogTagTestBase.class);
    assertFalse(includeAll.doEqualsTest(baseTest1, baseTest2));
    assertFalse(includeAll.doEqualsTest(baseTest2, baseTest1));
    
    assertFalse(includeAll.doEqualsTest(baseTest1, includeAll));
    
    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L, "echo", 
        new Point2D.Double(14.2, 2.14), 44, (byte)12, 'I');
  }

  @Test
  public void testEqualsTransient() {
    DogTag<DogTagTestMid> dogTag = DogTag.from(DogTagTestMid.class); // Tests may construct their own DogTags.
    DogTagTestMid mid1 = new DogTagTestMid(12, "bravo", 3, 4L, "echo", new Point2D.Double(14.2, 2.14), 7, (byte)8, 'I');
    DogTagTestMid mid2 = mid1.duplicate();
    mid2.setGolfIntTr(77);
    
    assertTrue(dogTag.doEqualsTest(mid1, mid2));
    
    mid2.setFoxtrotPoint(new Point2D.Double(3.3, 4.4));
    assertFalse(dogTag.doEqualsTest(mid1, mid2));
  }
  
  private <T> void testForMatch(DogTag<T> dogTag, T t1, T t2) {
    assertTrue(dogTag.doEqualsTest(t1, t2));
    assertEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2));
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
  // custom hash methods

  @SuppressWarnings({"unused", "AssignmentOrReturnOfFieldWithMutableType", "EqualsWhichDoesntCheckParameterClass", 
      "WeakerAccess", "PublicConstructorInNonPublicClass"})
  private static class DogTagTestBase {
    private final int alphaInt;
    private final String bravoString;
    private int charlieInt;
    private long deltaLong;
    private static int staticInt = 5;

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
    
    public DogTagTestBase duplicate() {
      return new DogTagTestBase(getAlphaInt(), getBravoString(), getCharlieInt(), getDeltaLong());
    }
  }

  @SuppressWarnings("WeakerAccess")
  private static class DogTagTestMid extends DogTagTestBase {
    private String echoString;
    private Point2D foxtrotPoint;
    private transient int golfIntTr;
    private byte hotelByte;
    private char indigoChar;

    DogTagTestMid(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, 
                         Point2D foxtrotPoint, int golfIntTr, byte hotelByte, char indigoChar) {
      super(alphaInt, bravoString, charlieInt, deltaLong);
      this.echoString = echoString;
      this.foxtrotPoint = foxtrotPoint;
      this.golfIntTr = golfIntTr;
      this.hotelByte = hotelByte;
      this.indigoChar = indigoChar;
    }

    public String getEchoString() {
      return echoString;
    }

    public void setEchoString(String echoString) {
      this.echoString = echoString;
    }

    public Point2D getFoxtrotPoint() {
      return foxtrotPoint;
    }

    public void setFoxtrotPoint(Point2D foxtrotPoint) {
      this.foxtrotPoint = foxtrotPoint;
    }

    public int getGolfIntTr() {
      return golfIntTr;
    }

    public void setGolfIntTr(int golfIntTr) {
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
    
    @Override
    public DogTagTestMid duplicate() {
      final Point2D pt = getFoxtrotPoint();
      return new DogTagTestMid(getAlphaInt(), getBravoString(), getCharlieInt(), getDeltaLong(), getEchoString(), 
          new Point2D.Double(pt.getX(), pt.getY()), getGolfIntTr(), getHotelByte(), getIndigoChar());
    }
  }

  @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "AssignmentOrReturnOfFieldWithMutableType"})
  private static class DogTagTestTail extends DogTagTestMid {

    DogTagTestTail(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, 
                          Point2D foxtrotPoint, int golfIntTr, byte hotelByte, char indigoChar,
                          boolean julietBoolean, short kiloShort, double limaDouble, float mikeFloat) {
      super(alphaInt, bravoString, charlieInt, deltaLong, echoString, foxtrotPoint, golfIntTr, hotelByte, indigoChar);
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
    
    @Override
    public DogTagTestTail duplicate() {
      return new DogTagTestTail(getAlphaInt(), getBravoString(), getCharlieInt(), getDeltaLong(), getEchoString(), getFoxtrotPoint(), getGolfIntTr(), getHotelByte(), 
          getIndigoChar(), isJulietBoolean(), getKiloShort(), getLimaDouble(), getMikeFloat());
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
