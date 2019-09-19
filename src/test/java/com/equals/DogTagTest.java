package com.equals;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public class DogTagTest {
  public static final String CHARLIE_INT = "charlieInt";

//  private static final int[] EMPTY_INT_ARRAY = new int[0];
//  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  @Test
  public void testEquals() {
    DogTagTestBase baseTest1 = new DogTagTestBase(5, "bravo", 7, 5L);
    DogTagTestBase baseTest2 = baseTest1.duplicate();
    baseTest2.setCharlieInt(12);
    
    DogTag<DogTagTestBase> excludeC = DogTag.create(DogTagTestBase.class)
        .withExcludedFields(CHARLIE_INT)
        .build();

    testForMatch(excludeC, baseTest1, baseTest2);

    DogTag<DogTagTestBase> includeBaseOnly = DogTag.from(DogTagTestBase.class);
    testMisMatch(includeBaseOnly, baseTest1, baseTest2);
    assertFalse(includeBaseOnly.doEqualsTest(baseTest1, "String"));

    baseTest1.setCharlieInt(12);
    testForMatch(includeBaseOnly, baseTest1, baseTest2);

    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L, "echo", 
        new Point2D.Double(14.2, 2.14), 44, (byte)12, 'I');
    DogTagTestMid midTest2 = midTest.duplicate();
    testForMatch(includeBaseOnly, midTest, midTest2);
    midTest2.setIndigoChar('J');
    midTest2.setHotelByte((byte) 99);
    midTest2.setGolfIntTr(77);
    midTest2.setFoxtrotPoint(new Point2D.Double(88.8, 22.2));
    midTest2.setEchoString("Could you repeat that?");
    testForMatch(includeBaseOnly, midTest, midTest2); // should still match,
  }

  @Test
  public void testTransient() {
    DogTag<DogTagTestMid> defaultDogTag = DogTag.from(DogTagTestMid.class); // Tests may construct their own DogTags.
    DogTagTestMid mid1 = new DogTagTestMid(12, "bravo", 3, 4L, "echo", new Point2D.Double(14.2, 2.14), 7, (byte)8, 'I');
    DogTagTestMid mid2 = mid1.duplicate();
    mid2.setGolfIntTr(77); // transient value
    testForMatch(defaultDogTag, mid1, mid2);
    
    mid2.setFoxtrotPoint(new Point2D.Double(3.3, 4.4));
    testMisMatch(defaultDogTag, mid1, mid2);

    DogTag<DogTagTestMid> dogTagWithTransients = DogTag.create(DogTagTestMid.class)
        .withTransients(true)
        .build();
    mid2.setFoxtrotPoint((Point2D) mid1.getFoxtrotPoint().clone()); // reset Point2D
    testMisMatch(dogTagWithTransients, mid1, mid2);
    mid2.setGolfIntTr(mid1.getGolfIntTr());
    testForMatch(dogTagWithTransients, mid1, mid2);

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();

    DogTag<DogTagTestTail> dogTagTail = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestMid.class)
        .withTransients(true)
        .build();
    tail1.setGolfIntTr(-10);
    tail2.setGolfIntTr(43);
    testMisMatch(dogTagTail, tail1, tail2);

    tail2.setGolfIntTr(-10); // tail2 now matches tail1 in this field
    tail2.setCharlieInt(1024); // Shouldn't affect equality. In super.super, but not in super.
    tail2.setDeltaLong(65537L*65537L);
    testForMatch(dogTagTail, tail1, tail2);
  }

  @Test
  public void testSuperClasses() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();
    tail2.setKiloShort((short) 999);
    tail2.setJulietBoolean(!tail1.isJulietBoolean());

    DogTag<DogTagTestTail> dogTag = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestBase.class)
        .withExcludedFields("kiloShort", "julietBoolean")
        .build();

    testForMatch(dogTag, tail1, tail2);

    tail2.setIndigoChar('X');
    tail2.setHotelByte((byte) 126);

    DogTag<DogTagTestTail> dogTagSuper = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestMid.class)
        .withExcludedFields("kiloShort", "julietBoolean", "hotelByte", "indigoChar")
        .build();
    testForMatch(dogTagSuper, tail1, tail2);

    // The withExcludedFields() and reflectUpTo() methods are the only options that interact with each other.
    // Here, we verify that the two methods may be called in either order.
    DogTag<DogTagTestTail> reversedDogTag = DogTag.create(DogTagTestTail.class)
        .withExcludedFields("kiloShort", "julietBoolean", "hotelByte", "indigoChar")
        .withReflectUpTo(DogTagTestMid.class)
        .build();
    testForMatch(reversedDogTag, tail1, tail2);
  }

  @Test
  public void testGoodExcludedFieldName() {
    DogTag<DogTagTestBase> dogTag = DogTag.create(DogTagTestBase.class)
        .withExcludedFields(CHARLIE_INT)
        .build();
    DogTagTestBase base1 = new DogTagTestBase(5, "bravo", 6, 8L);
    DogTagTestBase base2 = base1.duplicate();
    base2.setCharlieInt(12);

    testForMatch(dogTag, base1, base2);
    base2.setDeltaLong(88L);
    testMisMatch(dogTag, base1, base2);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testBadExcludedFieldName() {
    DogTag.create(DogTagTestTail.class)
        .withExcludedFields(CHARLIE_INT)
        .build();
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

  /**
   * We want every kind of NaN to test equals. To do this, we call Float.equals(), which relies on the
   * Float.intBitsToFloat() method. Using == won't work.
   * For info on all the different kinds of NaN, look at JavaDocs for Float.intBitsToFloat(int):
   * @see Float#intBitsToFloat(int)
   */
  @Test
  public void floatTest() {
    // All these values are Not a Number or NaN. (See Float#intBitsToFloat)
    // When tested with ==, all NaN values will return false, even if they're the same NaN value.
    // When tested with Float.equals(), they all return true, even if they're different NaN values.
    float notA = Float.NaN;
    float notB = -Float.NaN;
    float notC = Float.intBitsToFloat(0x7f900000); // All 4 of these are NaN!
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

  /**
   * We want every kind of NaN to test equals. To do this, we call Double.equals(), which relies on the
   * Double.longBitsToDouble() method. Using == won't work.
   * For info on all the different kinds of NaN, look at JavaDocs for Double.longBitsToDouble(int):
   * @see Double#longBitsToDouble(long)
   */
  @Test
  public void doubleTest() {
    // All these values are Not a Number, or NaN. (See Double#longBitsToDouble)
    // When tested with ==, all NaN values will return false, even if they're the same NaN value.
    // When tested with Double.equals(), they all return true, even if they're different NaN values.
    double notA = Double.NaN;
    double notB = -Double.NaN;
    double notC = Double.longBitsToDouble(0x7ff9000000000000L); // All 4 of these are NaN!
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
    testMisMatch(dogTag, tail1, tail2);
  }

  @Test
  public void testNull() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();
    tail2.setFoxtrotPoint(null);
    
    DogTag<DogTagTestTail> dogTag = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestBase.class)
        .build();
    testMisMatch(dogTag, tail1, tail2);

    dogTag.doHashCode(tail2); // just make sure we can get a hashCode with that null member.
  }

  @Test
  public void testArrays() {
    DogTag<DogTagTestTail> dogTag = DogTag.from(DogTagTestTail.class);

    // ints
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    testForMatch(dogTag, tail1, tail2);
    tail2.setNovemberIntArray(new int[] {3, 2, 1}); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setNovemberIntArray(new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1 }); // same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setNovemberIntArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // Strings:
    tail2 = tail1.duplicate();
    tail2.setOperaStringArray(new String[] { "Whiskey", "Tango", "Foxtrot" }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setOperaStringArray(new String[] { "Mercury", "Venus", "Earth", "Mars", "Jupiter",
        "Saturn", "Uranus", "Neptune", "Oort Cloud" }); // same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setOperaStringArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // longs:
    tail2 = tail1.duplicate();
    tail2.setPapaLongArray(new long[] { 999999999999L, 888888888888L, 777777777777L }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setPapaLongArray(new long[] { 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L }); // Same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setPapaLongArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // shorts:
    tail2 = tail1.duplicate();
    tail2.setQuebecShortArray(new short[] {100, 200, 300}); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setQuebecShortArray(new short[] { 100, 200, 300, 400, 500, 600, 700, 800, 900 }); // Same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setQuebecShortArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // bytes:
    tail2 = tail1.duplicate();
    tail2.setRomeoByteArray(new byte[] { 1, 3, 5, 7, 9 }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setRomeoByteArray(new byte[] { 8, 7, 6, 5, 4, 3, 2, 1, 0 }); // Same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setRomeoByteArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // chars:
    tail2 = tail1.duplicate();
    tail2.setSierraCharArray("duplicate".toCharArray()); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setSierraCharArray("sameLengt".toCharArray());
    testMisMatch(dogTag, tail1, tail2);
    tail2.setSierraCharArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // booleans:
    tail2 = tail1.duplicate();
    tail2.setTangoBooleanArray(new boolean[] { true, true, true, false, false, false }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setTangoBooleanArray(new boolean[] { true, true, true, true, true, false, true, true, false }); // Same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setTangoBooleanArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // floats:
    tail2 = tail1.duplicate();
    tail2.setUniformFloatArray(new float[] { 1.1f, 2.22f, 3.333f, 4.4444f }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setUniformFloatArray(new float[] { 10F, 20F, 30F, 40F, 50F, 60F, 70F, 80F, 90F }); // Same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setUniformFloatArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // doubles:
    tail2 = tail1.duplicate();
    tail2.setVictorDoubleArray(new double[] { 4.4444, 3.333, 2.22, 1.11 }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setVictorDoubleArray(new double[] { 9.9, 8.8, 7.7, 6.6, 5.5, 4.4, 3.3, 2.2, 1.1 });
    testMisMatch(dogTag, tail1, tail2);
    tail2.setWhiskeyObjectArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // Objects:
    tail2 = tail1.duplicate();
    tail2.setWhiskeyObjectArray(new Object[] { "String", Double.valueOf("3.14159") }); // different length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setWhiskeyObjectArray(new Object[] { Float.MAX_VALUE, "Not a Number", Double.NEGATIVE_INFINITY } ); // same length
    testMisMatch(dogTag, tail1, tail2);
    tail2.setWhiskeyObjectArray(null);
    testMisMatch(dogTag, tail1, tail2);

    // MultiDimensions
    int[][] twoDInt = { {1, 2}, {3, 4}, {5, 6} };
    tail1.setWhiskeyObjectArray(twoDInt);
    tail2.setWhiskeyObjectArray(twoDInt);
    testForMatch(dogTag, tail1, tail2);
    int[][] twoDIntB = { {1, 2}, {3, 4}, {50, 60} };
    tail2.setWhiskeyObjectArray(twoDIntB);
    testMisMatch(dogTag, tail1, tail2);
    int[][][] threeDArray = { { { 1, 2 }, { 3, 4 }, { 5, 6 } } };
    tail2.setWhiskeyObjectArray(threeDArray);
    testMisMatch(dogTag, tail1, tail2);
  }

  //////////////////////
  
  private <T> void testForMatch(DogTag<T> dogTag, T t1, T t2) {
    assertTrue(dogTag.doEqualsTest(t1, t2));  // equality test
    assertTrue(dogTag.doEqualsTest(t2, t1));  // symmetry test
    assertTrue(dogTag.doEqualsTest(t1, t1));  // reflexive test
    assertTrue(dogTag.doEqualsTest(t2, t2));  // reflexive test
    assertFalse(dogTag.doEqualsTest(t1, null));
    assertFalse(dogTag.doEqualsTest(t2, null));
    assertEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2)); // hash code consistency test
  }

  private <T> void testMisMatch(DogTag<T> dogTag, T t1, T t2) {
    assertFalse(dogTag.doEqualsTest(t1, t2)); // equality test
    assertFalse(dogTag.doEqualsTest(t2, t1)); // symmetry test
    assertTrue(dogTag.doEqualsTest(t1, t1));  // reflexive test
    assertTrue(dogTag.doEqualsTest(t2, t2));  // reflexive test
    assertNotEquals(dogTag.doHashCode(t1), dogTag.doHashCode(t2));
    assertFalse(dogTag.doEqualsTest(t1, null));
    assertFalse(dogTag.doEqualsTest(t2, null));
  }

  // Things to test:
  // (fix) hash of null values. Done
  // arrays of all types Done
  // different length arrays Done
  // different value arrays Done
  // mismatching Nan for floats and doubles Done
  // superclasses Done
  // transients Done
  // excluded fields Done
  // excluded fields in superclasses Done
  // illegal superclass
  // illegal field name Done
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
    private long[] papaLongArray = { 1L, 3L, 6L, 10L, 15L, 21L, 28L, 36L, 45L };
    private short[] quebecShortArray = { 0, 1, 4, 9, 16, 25, 36, 49, 64, 81 };
    private byte[] romeoByteArray = { 127, 63, 31, 15, 7, 3, 1, 2, 3 };
    private char[] sierraCharArray = "charArray".toCharArray();
    private boolean[] tangoBooleanArray = { false, true, true, false, true, false, false, true, true, false };
    private float[] uniformFloatArray = { 1.4F, 2.0F, 2.8F, 4.0F, 5.6F, 8.0F, 11F, 16F, 22F };
    private double[] victorDoubleArray = { 0.1, 0.02, 0.003, 0.0004, 0.00005, .000006, .0000007, .00000008, 9.0 };
    private Object[] whiskeyObjectArray = { new Point2D.Float(1.2f, 2.4f), "string", new HashSet() };

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

    public long[] getPapaLongArray() {
      return papaLongArray;
    }

    public void setPapaLongArray(long[] papaLongArray) {
      this.papaLongArray = papaLongArray;
    }

    public short[] getQuebecShortArray() {
      return quebecShortArray;
    }

    public void setQuebecShortArray(short[] quebecShortArray) {
      this.quebecShortArray = quebecShortArray;
    }

    public byte[] getRomeoByteArray() {
      return romeoByteArray;
    }

    public void setRomeoByteArray(byte[] romeoByteArray) {
      this.romeoByteArray = romeoByteArray;
    }

    public char[] getSierraCharArray() {
      return sierraCharArray;
    }

    public void setSierraCharArray(char[] sierraCharArray) {
      this.sierraCharArray = sierraCharArray;
    }

    public boolean[] getTangoBooleanArray() {
      return tangoBooleanArray;
    }

    public void setTangoBooleanArray(boolean[] tangoBooleanArray) {
      this.tangoBooleanArray = tangoBooleanArray;
    }

    public float[] getUniformFloatArray() {
      return uniformFloatArray;
    }

    public void setUniformFloatArray(float[] uniformFloatArray) {
      this.uniformFloatArray = uniformFloatArray;
    }

    public double[] getVictorDoubleArray() {
      return victorDoubleArray;
    }

    public void setVictorDoubleArray(double[] victorDoubleArray) {
      this.victorDoubleArray = victorDoubleArray;
    }

    public Object[] getWhiskeyObjectArray() {
      return whiskeyObjectArray;
    }

    public void setWhiskeyObjectArray(Object[] whiskeyObjectArray) {
      this.whiskeyObjectArray = whiskeyObjectArray;
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
      long[] pla = getPapaLongArray();
      tail.setPapaLongArray(Arrays.copyOf(pla, pla.length));
      short[] sqa = getQuebecShortArray();
      tail.setQuebecShortArray(Arrays.copyOf(sqa, sqa.length));
      byte[] rba = getRomeoByteArray();
      tail.setRomeoByteArray(Arrays.copyOf(rba, rba.length));
      char[] csa = getSierraCharArray();
      tail.setSierraCharArray(Arrays.copyOf(csa, csa.length));
      boolean[] bta = getTangoBooleanArray();
      tail.setTangoBooleanArray(Arrays.copyOf(bta, bta.length));
      float[] fua = getUniformFloatArray();
      tail.setUniformFloatArray(Arrays.copyOf(fua, fua.length));
      double[] vda = getVictorDoubleArray();
      tail.setVictorDoubleArray(Arrays.copyOf(vda, vda.length));
      Object[] woa = getWhiskeyObjectArray();
      tail.setWhiskeyObjectArray(woa);
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
