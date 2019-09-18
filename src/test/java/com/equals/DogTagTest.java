package com.equals;

import java.awt.geom.Point2D;
import java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public class DogTagTest {

//  private static final int[] EMPTY_INT_ARRAY = new int[0];
//  private static final String[] EMPTY_STRING_ARRAY = new String[0];

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

    baseTest1.setCharlieInt(12);
    assertTrue(includeAll.doEqualsTest(baseTest1, baseTest2));
    assertTrue(includeAll.doEqualsTest(baseTest2, baseTest1));

    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L, "echo", 
        new Point2D.Double(14.2, 2.14), 44, (byte)12, 'I');
  }

  @Test
  public void testEqualsTransient() {
    DogTag<DogTagTestMid> dogTag = DogTag.from(DogTagTestMid.class); // Tests may construct their own DogTags.
    DogTagTestMid mid1 = new DogTagTestMid(12, "bravo", 3, 4L, "echo", new Point2D.Double(14.2, 2.14), 7, (byte)8, 'I');
    DogTagTestMid mid2 = mid1.duplicate();
    mid2.setGolfIntTr(77); // transient value
    
    assertTrue(dogTag.doEqualsTest(mid1, mid2));
    
    mid2.setFoxtrotPoint(new Point2D.Double(3.3, 4.4));
    assertFalse(dogTag.doEqualsTest(mid1, mid2));

    mid2.setFoxtrotPoint((Point2D) mid1.getFoxtrotPoint().clone()); // reset Point2D
    DogTag<DogTagTestMid> dogTagWithTransients = DogTag.create(DogTagTestMid.class)
        .withTransients(true)
        .build();
    assertFalse(dogTagWithTransients.doEqualsTest(mid1, mid2));
    mid2.setGolfIntTr(mid1.getGolfIntTr());
    assertTrue(dogTagWithTransients.doEqualsTest(mid1, mid2));

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();

    DogTag<DogTagTestTail> dogTagTail = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestMid.class)
        .withTransients(true)
        .build();
    tail1.setGolfIntTr(-10);
    tail2.setGolfIntTr(43);
    assertFalse(dogTagTail.doEqualsTest(tail1, tail2));

  }

  @Test
  public void testSuperClasses() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();

    DogTag<DogTagTestTail> dogTag = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestBase.class)
        .withExcludedFields("kiloShort", "julietBoolean")
        .build();

    testForMatch(dogTag, tail1, tail2);

    DogTag<DogTagTestTail> dogTagSuper = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestMid.class)
        .withExcludedFields("kiloShort", "julietBoolean", "hotelByte", "indigoChar")
        .build();
    testForMatch(dogTagSuper, tail1, tail2);
  }

  @Test
  public void testNoStatic() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag<DogTagTestTail> dogTag = DogTag.from(DogTagTestTail.class);
    int hashCode = dogTag.doHashCode(tail);

    // Change the static value and get a new hashCode.
    tail.setStaticInt(tail.getStaticInt()*500);
    int revisedHashCode = dogTag.doHashCode(tail);

    // If it's not using the static value, the hashCode won't change when the static value changes.
    assertEquals(hashCode, revisedHashCode);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadFieldName() {
    try {
      DogTag.create(DogTagTestTail.class)
          .withReflectUpTo(DogTagTestBase.class)
          // Include fields from all three classes
          .withExcludedFields("kiloShort", "indigoChar", "alphaInt", "missing")
          .build();
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("missing"));
      throw e;
    }
  }

  @Test
  public void floatTest() {
    // All these values are Not a Number or NaN. (See Float#intBitsToFloat)
    // When tested with ==, all NaN values will return false, even if they're the same NaN value.
    // When tested with Float.equals(), they all return true, even if they're different NaN values.
    float notA = Float.NaN;
    float notB = -Float.NaN;
    float notC = Float.intBitsToFloat(0x7f900000);
    float notD = Float.intBitsToFloat(0x7fA00000);
    float notE = Float.intBitsToFloat(0xff900000);
    float notF = Float.intBitsToFloat(0xffa00000);
    float[] notNumbers = { notA, notB, notC, notD, notE, notF };

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    DogTag<DogTagTestTail> dogTag = DogTag.create(DogTagTestTail.class)
        .withExcludedFields("novemberIntArray", "operaStringArray")
        .build();
    for (float f1: notNumbers) {
      for (float f2: notNumbers) {
        tail1.setMikeFloat(f1);
        tail2.setMikeFloat(f2);
        testForMatch(dogTag, tail1, tail2);
      }
    }
  }

  @Test
  public void doubleTest() {
    // All these values are Not a Number, or NaN. (See Double#longBitsToDouble)
    // When tested with ==, all NaN values will return false, even if they're the same NaN value.
    // When tested with Double.equals(), they all return true, even if they're different NaN values.
    double notA = Double.NaN;
    double notB = -Double.NaN;
    double notC = Double.longBitsToDouble(0x7ff9000000000000L);
    double notD = Double.longBitsToDouble(0x7ffA000000000000L);
    double notE = Double.longBitsToDouble(0xfff9000000000000L);
    double notF = Double.longBitsToDouble(0xfffa000000000000L);
    double[] notNumbers = { notA, notB, notC, notD, notE, notF };

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    DogTag<DogTagTestTail> dogTag = DogTag.create(DogTagTestTail.class)
        .withExcludedFields("novemberIntArray", "operaStringArray")
        .build();
    for (double f1: notNumbers) {
      for (double f2: notNumbers) {
        tail1.setLimaDouble(f1);
        tail2.setLimaDouble(f2);
        testForMatch(dogTag, tail1, tail2);
      }
    }
  }

  @Test
  public void testIntArrays() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    DogTag<DogTagTestTail> dogTag = DogTag.from(DogTagTestTail.class);

    testForMatch(dogTag, tail1, tail2);

    tail1.setNovemberIntArray(new int[] {1, 1, 2, 3, 5});
    assertFalse(dogTag.doEqualsTest(tail1, tail2));
  }

  //////////////////////
  
  private <T> void testForMatch(DogTag<T> dogTag, T t1, T t2) {
    assertTrue(dogTag.doEqualsTest(t1, t2));
    assertEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2));
  }

  // Things to test:
  // (fix) hash of null values.
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

  @SuppressWarnings({"unused", "AssignmentOrReturnOfFieldWithMutableType",
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

    public void setStaticInt(int i) {
      staticInt = i;
    }

    public int getStaticInt() {
      return staticInt;
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
          (Point2D) pt.clone(), getGolfIntTr(), getHotelByte(), getIndigoChar());
    }
  }

  @SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "WeakerAccess"})
  private static class DogTagTestTail extends DogTagTestMid {

    DogTagTestTail(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, 
                          Point2D foxtrotPoint, int golfIntTr, byte hotelByte, char indigoChar,
                          boolean julietBoolean, short kiloShort, double limaDouble, float mikeFloat,
                          int[] novemberIntArray, String[] operaStringArray) {
      super(alphaInt, bravoString, charlieInt, deltaLong, echoString, foxtrotPoint, golfIntTr, hotelByte, indigoChar);
      this.julietBoolean = julietBoolean;
      this.kiloShort = kiloShort;
      this.limaDouble = limaDouble;
      this.mikeFloat = mikeFloat;
      this.novemberIntArray = novemberIntArray;
      this.operaStringArray = operaStringArray;
    }

    DogTagTestTail() {
      super(1, "bravo", 3, 4L, "echo",
          new Point2D.Double(6.54, 4.56), 7, (byte)8, 'I');
      novemberIntArray = new int[] { 11, 12, 13 };
      operaStringArray = new String[] { "papa", "quebec", "romeo", "sierra", "tango" };
      // todo: Add more fields!
    }

    private boolean julietBoolean;
    private short kiloShort;
    private double limaDouble;
    private float mikeFloat;

    private int[] novemberIntArray; // = EMPTY_INT_ARRAY;
    private String[] operaStringArray; // = EMPTY_STRING_ARRAY;

    public int[] getNovemberIntArray() {
      return novemberIntArray;
    }

    public void setNovemberIntArray(int[] novemberIntArray) {
      this.novemberIntArray = novemberIntArray;
    }

    public String[] getOperaStringArray() {
      return operaStringArray;
    }

    public void setOperaStringArray(String[] operaStringArray) {
      this.operaStringArray = operaStringArray;
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
      DogTagTestTail tail = new DogTagTestTail(getAlphaInt(), getBravoString(), getCharlieInt(), getDeltaLong(), getEchoString(),
          getFoxtrotPoint(), getGolfIntTr(), getHotelByte(), getIndigoChar(), isJulietBoolean(), getKiloShort(),
          getLimaDouble(), getMikeFloat(), getNovemberIntArray(), getOperaStringArray());
      int[] n = getNovemberIntArray();
      tail.setNovemberIntArray(Arrays.copyOf(n, n.length));
      String[] osa = getOperaStringArray();
      tail.setOperaStringArray(Arrays.copyOf(osa, osa.length));
      return tail;
    }

//    private static final DogTag<DogTagTestBase> dogTag = DogTag.create(DogTagTestBase.class)
//        .withTransients(true)
//        .withExcludedFields("charlieInt", "bravoString")
//        .build();
//
//    @Override
//    public int hashCode() {
//      return dogTag.doHashCode(this);
//    }
//
//    @Override
//    public boolean equals(final Object obj) {
//      return dogTag.doEqualsTest(this, obj);
//    }
  }


}
