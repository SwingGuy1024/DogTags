package com.equals;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

import static com.equals.TestUtility.*; // for verifyMatch__() and verifyNoMatch()
import static org.junit.Assert.*;

// Todo: Write test of cached hash in inclusion mode

@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter", "ImplicitNumericConversion", "UseOfClone", "AccessStaticViaInstance", "EqualsReplaceableByObjectsCall"})
public class DogTagTest {
  private static final String CHARLIE_INT = "charlieInt";

  @Test
  public void testEquals() {
    DogTagTestBase baseTest1 = new DogTagTestBase(5, "bravo", 7, 5L);
    DogTagTestBase baseTest2 = baseTest1.duplicate();
    DogTagTestBase baseTest3 = new DogTagTestBase(90, "bravo", 7, 5L);
    DogTagTestBase baseTest4 = new DogTagTestBase(5, "bravissimo", 7, 5L);
    DogTagTestBase baseTest5 = new DogTagTestBase(5, "bravo", 44, 5L);
    DogTagTestBase baseTest6 = new DogTagTestBase(5, "bravo", 7, 17L);
    baseTest2.setCharlieInt(12);
    
    DogTag.Factory<DogTagTestBase> excludeC = DogTag.create(DogTagTestBase.class, CHARLIE_INT)
        .makeFactory();

    verifyMatch__(excludeC, baseTest1, baseTest2);
    verifyNoMatch(excludeC, baseTest1, baseTest3);
    verifyNoMatch(excludeC, baseTest1, baseTest4);
    verifyMatch__(excludeC, baseTest1, baseTest5);
    verifyNoMatch(excludeC, baseTest1, baseTest6);
    verifyNoMatch(excludeC, baseTest2, baseTest3);
    verifyNoMatch(excludeC, baseTest2, baseTest4);
    verifyMatch__(excludeC, baseTest2, baseTest5);
    verifyNoMatch(excludeC, baseTest2, baseTest6);
    verifyNoMatch(excludeC, baseTest3, baseTest4);
    verifyNoMatch(excludeC, baseTest3, baseTest5);
    verifyNoMatch(excludeC, baseTest3, baseTest6);
    verifyNoMatch(excludeC, baseTest4, baseTest5);
    verifyNoMatch(excludeC, baseTest4, baseTest6);
    verifyNoMatch(excludeC, baseTest5, baseTest6);

    DogTag.Factory<DogTagTestBase> includeBaseOnly = DogTag.create(DogTagTestBase.class).makeFactory();
    verifyNoMatch(includeBaseOnly, baseTest1, baseTest2);
    assertFalse(includeBaseOnly.doEqualsTest(baseTest1, "String"));

    baseTest1.setCharlieInt(12);
    verifyMatch__(includeBaseOnly, baseTest1, baseTest2);

    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L, "echo", 
        new Point2D.Double(14.2, 2.14), 44, (byte)12, 'I');
    DogTagTestMid midTest2 = midTest.duplicate();
    verifyMatch__(includeBaseOnly, midTest, midTest2);
    midTest2.setIndigoChar('J');
    midTest2.setHotelByte((byte) 99);
    midTest2.setGolfIntTr(77);
    midTest2.setFoxtrotPoint(new Point2D.Double(88.8, 22.2));
    midTest2.setEchoString("Could you repeat that?");
    verifyMatch__(includeBaseOnly, midTest, midTest2); // should still match,
    
    DogTag.Factory<DogTagTestBase> includeAllButC = DogTag.createByInclusion(DogTagTestBase.class,
            "alphaInt",
            "bravoString",
            "deltaLong"
        )
        .makeFactory();
    verifyMatch__(includeAllButC, baseTest1, baseTest2);
    verifyNoMatch(includeAllButC, baseTest1, baseTest3);
    verifyNoMatch(includeAllButC, baseTest1, baseTest4);
    verifyMatch__(includeAllButC, baseTest1, baseTest5);
    verifyNoMatch(includeAllButC, baseTest1, baseTest6);
    verifyNoMatch(includeAllButC, baseTest2, baseTest3);
    verifyNoMatch(includeAllButC, baseTest2, baseTest4);
    verifyMatch__(includeAllButC, baseTest2, baseTest5);
    verifyNoMatch(includeAllButC, baseTest2, baseTest6);
    verifyNoMatch(includeAllButC, baseTest3, baseTest4);
    verifyNoMatch(includeAllButC, baseTest3, baseTest5);
    verifyNoMatch(includeAllButC, baseTest3, baseTest6);
    verifyNoMatch(includeAllButC, baseTest4, baseTest5);
    verifyNoMatch(includeAllButC, baseTest4, baseTest6);
    verifyNoMatch(includeAllButC, baseTest5, baseTest6);
  }

  @Test
  public void testTransient() {
    DogTag.Factory<DogTagTestMid> defaultFactory = DogTag.create(DogTagTestMid.class).makeFactory(); // Tests may construct their own DogTags.
    DogTagTestMid mid1 = new DogTagTestMid(12, "bravo", 3, 4L, "echo", new Point2D.Double(14.2, 2.14), 7, (byte)8, 'I');
    DogTagTestMid mid2 = mid1.duplicate();
    mid2.setGolfIntTr(77); // transient value
    verifyMatch__(defaultFactory, mid1, mid2);
    
    mid2.setFoxtrotPoint(new Point2D.Double(3.3, 4.4));
    verifyNoMatch(defaultFactory, mid1, mid2);

    DogTag.Factory<DogTagTestMid> FactoryWithTransients = DogTag.create(DogTagTestMid.class)
        .withTransients(true)
        .makeFactory();
    mid2.setFoxtrotPoint((Point2D) mid1.getFoxtrotPoint().clone()); // reset Point2D
    verifyNoMatch(FactoryWithTransients, mid1, mid2);
    mid2.setGolfIntTr(mid1.getGolfIntTr());
    verifyMatch__(FactoryWithTransients, mid1, mid2);

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();

    DogTag.Factory<DogTagTestTail> FactoryTail = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestMid.class)
        .withTransients(true)
        .makeFactory();
    tail1.setGolfIntTr(-10);
    tail2.setGolfIntTr(43);
    verifyNoMatch(FactoryTail, tail1, tail2);

    tail2.setGolfIntTr(-10); // tail2 now matches tail1 in this field
    tail2.setCharlieInt(1024); // Shouldn't affect equality. In super.super, but not in super.
    tail2.setDeltaLong(65537L*65537L);
    verifyMatch__(FactoryTail, tail1, tail2);
  }
  
  @Test
  public void testFinalOnly() {
    DogTagTestBase base1 = new DogTagTestBase(1, "bravo", 2, 3L);
    DogTagTestBase base2 = new DogTagTestBase(1, "bravo", 22, 4L);
    DogTagTestBase base3 = new DogTagTestBase(2, "bravo", 2, 3);
    DogTagTestBase base4 = new DogTagTestBase(1, "Boo!", 22, 4L);
    DogTagTestBase base5 = new DogTagTestBase(1, "bravo", 2, 3L);
    DogTagTestBase base6 = new DogTagTestBase(1, null, 2, 3L);
    DogTagTestBase base7 = new DogTagTestBase(1, null, 2, 3L);

    DogTag.Factory<DogTagTestBase> baseFactory = DogTag.create(DogTagTestBase.class)
        .withFinalFieldsOnly(true)
        .makeFactory();
    
    verifyMatch__(baseFactory, base1, base2);
    verifyNoMatch(baseFactory, base1, base3);
    verifyNoMatch(baseFactory, base1, base4);
    verifyMatch__(baseFactory, base1, base5);
    verifyNoMatch(baseFactory, base1, base6);
    verifyNoMatch(baseFactory, base2, base3);
    verifyNoMatch(baseFactory, base2, base4);
    verifyMatch__(baseFactory, base2, base5);    // transitivity test
    verifyNoMatch(baseFactory, base2, base6);
    verifyNoMatch(baseFactory, base3, base4);
    verifyNoMatch(baseFactory, base3, base5);
    verifyNoMatch(baseFactory, base3, base6);
    verifyNoMatch(baseFactory, base4, base5);
    verifyNoMatch(baseFactory, base4, base6);
    verifyNoMatch(baseFactory, base5, base6);
    verifyMatch__(baseFactory, base6, base7);
    
    DogTag.Factory<DogTagTestBase> Factory2 = DogTag.create(DogTagTestBase.class, "bravoString")
        .makeFactory();

    verifyNoMatch(Factory2, base1, base2);
    verifyNoMatch(Factory2, base1, base3);
    verifyNoMatch(Factory2, base1, base4);
    verifyMatch__(Factory2, base1, base5);
    verifyMatch__(Factory2, base1, base6);
    verifyNoMatch(Factory2, base2, base3);
    verifyMatch__(Factory2, base2, base4);
    verifyNoMatch(Factory2, base2, base5);
    verifyNoMatch(Factory2, base2, base6);
    verifyNoMatch(Factory2, base3, base4);
    verifyNoMatch(Factory2, base3, base5);
    verifyNoMatch(Factory2, base3, base6);
    verifyNoMatch(Factory2, base4, base5);
    verifyNoMatch(Factory2, base4, base6);
    verifyMatch__(Factory2, base5, base6);

    DogTag.Factory<DogTagTestBase> factory3 = DogTag.create(DogTagTestBase.class, "alphaInt")
        .withFinalFieldsOnly(true)
        .makeFactory();

    verifyMatch__(factory3, base1, base2);
    verifyMatch__(factory3, base1, base3);
    verifyNoMatch(factory3, base1, base4);
    verifyMatch__(factory3, base1, base5);
    verifyNoMatch(factory3, base1, base6);
    verifyMatch__(factory3, base2, base3);    // transitivity test
    verifyNoMatch(factory3, base2, base4);
    verifyMatch__(factory3, base2, base5);    // transitivity test
    verifyNoMatch(factory3, base2, base4);
    verifyNoMatch(factory3, base3, base6);
    verifyMatch__(factory3, base3, base5);    // transitivity test
    verifyNoMatch(factory3, base4, base5);
    verifyNoMatch(factory3, base4, base6);
    verifyNoMatch(factory3, base5, base6);
    verifyMatch__(factory3, base6, base7);
  }

  @Test
  public void testSuperClasses() {
    // The names reflect where the differences are.
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();
    DogTagTestTail tail3 = new DogTagTestTail();
    DogTagTestTail tail4 = new DogTagTestTail();
    DogTagTestTail tail5 = new DogTagTestTail();
    DogTagTestTail tail6 = new DogTagTestTail();
    DogTagTestTail mid_1 = new DogTagTestTail();
    DogTagTestTail mid_2 = new DogTagTestTail();
    DogTagTestTail base1 = new DogTagTestTail();
    DogTagTestTail base2 = new DogTagTestTail();
    tail2.setKiloShort((short) 999);
    tail2.setJulietBoolean(!tail1.isJulietBoolean());
    tail3.setKiloShort((short) 987);
    tail3.setJulietBoolean(!tail1.isJulietBoolean());
    tail3.setLimaDouble(703.14);
    tail4.setPapaLongArray(new long[] {9L, 8L});
    tail5.setVictorDoubleArray(new double[] { 5.3, 4.9 });
    tail6.setLimaDouble(2.718281828);
    mid_1.setEchoString("mid_1 string");
    mid_2.setFoxtrotPoint(new Point2D.Double(98.7, 65.4));
    base1.setCharlieInt(7654);
    base2.setDeltaLong(96L);

    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class, "kiloShort", "julietBoolean")
        .withReflectUpTo(DogTagTestBase.class)
        .makeFactory();

    verifyMatch__(factory, tail1, tail2);
    verifyNoMatch(factory, tail1, tail3);
    verifyNoMatch(factory, tail1, tail4);
    verifyNoMatch(factory, tail1, tail5);
    verifyNoMatch(factory, tail1, tail6);
    verifyNoMatch(factory, tail1, mid_1);
    verifyNoMatch(factory, tail1, mid_2);
    verifyNoMatch(factory, tail1, base1);
    verifyNoMatch(factory, tail1, base2);

    tail2.setIndigoChar('X');
    tail2.setHotelByte((byte) 126);

    DogTag.Factory<DogTagTestTail> factoryToMid = DogTag.create(DogTagTestTail.class, "kiloShort", "julietBoolean", "hotelByte", "indigoChar")
        .withReflectUpTo(DogTagTestMid.class)
        .makeFactory();
    verifyMatch__(factoryToMid, tail1, tail2);
    verifyNoMatch(factoryToMid, tail1, tail3);
    verifyNoMatch(factoryToMid, tail1, tail4);
    verifyNoMatch(factoryToMid, tail1, tail5);
    verifyNoMatch(factoryToMid, tail1, tail6);
    verifyNoMatch(factoryToMid, tail1, mid_1);
    verifyNoMatch(factoryToMid, tail1, mid_2);
    verifyMatch__(factoryToMid, tail1, base1);
    verifyMatch__(factoryToMid, tail1, base2);

    DogTag.Factory<DogTagTestTail> factoryNoSuper = DogTag.create(DogTagTestTail.class, "kiloShort", "julietBoolean")
        .withReflectUpTo(DogTagTestTail.class)
        .makeFactory();
    verifyMatch__(factoryNoSuper, tail1, tail2);
    verifyNoMatch(factoryNoSuper, tail1, tail3);
    verifyNoMatch(factoryNoSuper, tail1, tail4);
    verifyNoMatch(factoryNoSuper, tail1, tail5);
    verifyNoMatch(factoryNoSuper, tail1, tail6);
    verifyMatch__(factoryNoSuper, tail1, mid_1);
    verifyMatch__(factoryNoSuper, tail1, mid_2);
    verifyMatch__(factoryNoSuper, tail1, base1);
    verifyMatch__(factoryNoSuper, tail1, base2);
  }

  @Test
  public void testGoodExcludedFieldName() {
    DogTag.Factory<DogTagTestBase> factory = DogTag.create(DogTagTestBase.class, CHARLIE_INT)
        .makeFactory();
    DogTagTestBase base1 = new DogTagTestBase(5, "bravo", 6, 8L);
    DogTagTestBase base2 = base1.duplicate();
    base2.setCharlieInt(12);

    verifyMatch__(factory, base1, base2);
    base2.setDeltaLong(88L);
    verifyNoMatch(factory, base1, base2);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testBadExcludedFieldName() {
    DogTag.create(DogTagTestTail.class, CHARLIE_INT)
        .withReflectUpTo(DogTagTestTail.class)// CHARLIE_INT is a superclass method, but the superclass wasn't included.
        .makeFactory();
  }

  @Test(expected=IllegalArgumentException.class)
  public void testBadExcludedFieldName2() {
    DogTag.create(DogTagTestTail.class, "hotelByte")
        .withReflectUpTo(DogTagTestTail.class)
        .makeFactory();
  }

  @Test
  public void testNoStatic() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag<DogTagTestTail> dogTag = DogTag.from(DogTagTestTail.class, tail);
    int hashCode = dogTag.doHashCode();

    // Change the static value and get a new hashCode.
    tail.setStaticInt(tail.getStaticInt()*500);
    int revisedHashCode = dogTag.doHashCode();

    // If it's not using the static value, the hashCode won't change when the static value changes.
    assertEquals(hashCode, revisedHashCode);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadFieldName() {
    try {
      // Include fields from all three classes
      DogTag.create(DogTagTestTail.class, "kiloShort", "indigoChar", "alphaInt", "missing")
          .withReflectUpTo(DogTagTestBase.class)
          .makeFactory();
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
  public void testFloat() {
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
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class, "novemberIntArray", "operaStringArray")
        .makeFactory();
    for (float f1: notNumbers) {
      for (float f2: notNumbers) {
        tail1.setMikeFloat(f1);
        tail2.setMikeFloat(f2);
        verifyMatch__(factory, tail1, tail2);
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
  public void testDouble() {
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
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class, "novemberIntArray", "operaStringArray")
        .makeFactory();
    for (double f1: notNumbers) {
      for (double f2: notNumbers) {
        tail1.setLimaDouble(f1);
        tail2.setLimaDouble(f2);
        verifyMatch__(factory, tail1, tail2);
      }
    }
  }

  @Test
  public void testIntArrays() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class).makeFactory();

    verifyMatch__(factory, tail1, tail2);

    tail1.setNovemberIntArray(new int[] {1, 1, 2, 3, 5});
    verifyNoMatch(factory, tail1, tail2);
  }

  @Test
  public void testNull() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();
    tail2.setFoxtrotPoint(null);

    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class)
        .withReflectUpTo(DogTagTestBase.class)
        .makeFactory();
    verifyNoMatch(factory, tail1, tail2);

    factory.doHashCodeInternal(tail2); // just make sure we can get a hashCode with that null member.
  }

  @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "LiteralAsArgToStringEquals", "ResultOfMethodCallIgnored"})
  @Test(expected = AssertionError.class)
  public void testDisabledEqualsMethod() {
    DogTagTestBase dummy = new DogTagTestBase(1, "s", 1, 1L);
    DogTag<DogTagTestBase> dogTag = DogTag.from(DogTagTestBase.class, dummy);
    dogTag.equals("xx");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test(expected = AssertionError.class)
  public void testDisabledHashCodeMethod() {
    DogTagTestBase dummy = new DogTagTestBase(1, "s", 1, 1L);
    DogTag<DogTagTestBase> dogTag = DogTag.from(DogTagTestBase.class, dummy);
    dogTag.hashCode();
  }

  @Test
  public void testArrays() {
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(DogTagTestTail.class).makeFactory();

    // ints
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    verifyMatch__(factory, tail1, tail2);
    tail2.setNovemberIntArray(new int[] {3, 2, 1}); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setNovemberIntArray(new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1 }); // same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setNovemberIntArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // Strings:
    tail2 = tail1.duplicate();
    tail2.setOperaStringArray(new String[] { "Whiskey", "Tango", "Foxtrot" }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setOperaStringArray(new String[] { "Mercury", "Venus", "Earth", "Mars", "Jupiter",
        "Saturn", "Uranus", "Neptune", "Oort Cloud" }); // same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setOperaStringArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // longs:
    tail2 = tail1.duplicate();
    tail2.setPapaLongArray(new long[] { 999999999999L, 888888888888L, 777777777777L }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setPapaLongArray(new long[] { 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L }); // Same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setPapaLongArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // shorts:
    tail2 = tail1.duplicate();
    tail2.setQuebecShortArray(new short[] {100, 200, 300}); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setQuebecShortArray(new short[] { 100, 200, 300, 400, 500, 600, 700, 800, 900 }); // Same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setQuebecShortArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // bytes:
    tail2 = tail1.duplicate();
    tail2.setRomeoByteArray(new byte[] { 1, 3, 5, 7, 9 }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setRomeoByteArray(new byte[] { 8, 7, 6, 5, 4, 3, 2, 1, 0 }); // Same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setRomeoByteArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // chars:
    tail2 = tail1.duplicate();
    tail2.setSierraCharArray("duplicate".toCharArray()); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setSierraCharArray("same size".toCharArray());
    verifyNoMatch(factory, tail1, tail2);
    tail2.setSierraCharArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // booleans:
    tail2 = tail1.duplicate();
    tail2.setTangoBooleanArray(new boolean[] { true, true, true, false, false, false }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setTangoBooleanArray(new boolean[] { true, true, true, true, true, false, true, true, false }); // Same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setTangoBooleanArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // floats:
    tail2 = tail1.duplicate();
    tail2.setUniformFloatArray(new float[] { 1.1f, 2.22f, 3.333f, 4.4444f }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setUniformFloatArray(new float[] {10.0F, 20.0F, 30.0F, 40.0F, 50.0F, 60.0F, 70.0F, 80.0F, 90.0F}); // Same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setUniformFloatArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // doubles:
    tail2 = tail1.duplicate();
    tail2.setVictorDoubleArray(new double[] { 4.4444, 3.333, 2.22, 1.11 }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setVictorDoubleArray(new double[] { 9.9, 8.8, 7.7, 6.6, 5.5, 4.4, 3.3, 2.2, 1.1 });
    verifyNoMatch(factory, tail1, tail2);
    tail2.setWhiskeyObjectArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // Objects:
    tail2 = tail1.duplicate();
    tail2.setWhiskeyObjectArray(new Object[] { "String", Double.valueOf("3.14159") }); // different length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setWhiskeyObjectArray(new Object[] { Float.MAX_VALUE, "Not a Number", Double.NEGATIVE_INFINITY } ); // same length
    verifyNoMatch(factory, tail1, tail2);
    tail2.setWhiskeyObjectArray(null);
    verifyNoMatch(factory, tail1, tail2);

    // MultiDimensions
    int[][] twoDInt = { {1, 2}, {3, 4}, {5, 6} };
    tail1.setWhiskeyObjectArray(twoDInt);
    tail2.setWhiskeyObjectArray(twoDInt);
    verifyMatch__(factory, tail1, tail2);
    int[][] twoDIntB = { {1, 2}, {3, 4}, {50, 60} };
    tail2.setWhiskeyObjectArray(twoDIntB);
    verifyNoMatch(factory, tail1, tail2);
    int[][][] threeDArray = { { { 1, 2 }, { 3, 4 }, { 5, 6 } } };
    tail2.setWhiskeyObjectArray(threeDArray);
    verifyNoMatch(factory, tail1, tail2);
  }

  @Test
  public void paradigmTest() {
    // Here we test the full paradigm used by the DogTag class. The other test classes don't actually implement a
    // DogTag-based equals() or hashCode() method. So we test with a class that does.
    ParadigmTest pt1 = new ParadigmTest("a", 1, 1.0f);
    ParadigmTest pt2 = new ParadigmTest("b", 1, 1.0f);
    ParadigmTest pt3 = new ParadigmTest("a", 2, 1.0f);
    ParadigmTest pt4 = new ParadigmTest("a", 1, 3.0f);
    ParadigmTest pt5 = new ParadigmTest("a", 1, 1.0f);

    // We don't use TestUtility.verifyMatch() because we need to test a direct call to isEquals and hashCode from the
    // tagged objects themselves.
    testNoMatch(pt1, pt2);
    testNoMatch(pt1, pt3);
    testNoMatch(pt1, pt4);
    testMatch__(pt1, pt5);
    testNoMatch(pt2, pt3);
    testNoMatch(pt2, pt4);
    testNoMatch(pt2, pt5);
    testNoMatch(pt3, pt4);
    testNoMatch(pt3, pt5);
    testNoMatch(pt4, pt5);
  }

  @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsWithItself", "LiteralAsArgToStringEquals", "EqualsBetweenInconvertibleTypes"})
  private void testMatch__(ParadigmTest a, ParadigmTest b) {
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertTrue(a.equals(a));
    assertTrue(b.equals(b));
    assertFalse(a.equals(null));
    assertFalse(b.equals(null));
    assertFalse(a.equals("a"));
    assertFalse(b.equals("b"));
    assertEquals(a.hashCode(), b.hashCode());
  }

  @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsWithItself", "LiteralAsArgToStringEquals", "EqualsBetweenInconvertibleTypes"})
  private void testNoMatch(ParadigmTest a, ParadigmTest b) {
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
    assertTrue(a.equals(a));
    assertTrue(b.equals(b));
    assertFalse(a.equals(null));
    assertFalse(b.equals(null));
    assertFalse(a.equals("a"));
    assertFalse(b.equals("b"));
    assertNotEquals(a.hashCode(), b.hashCode());
  }

//  @SuppressWarnings("ResultOfObjectAllocationIgnored")
//  @Test(expected = AssertionError.class)
//  public void testBadClass() {
//    // Test for case where dogTag is not static. This would seriously slow down the equals and hashCode methods, since
//    // the reflection would happen whenever the class is instantiated instead of once when it's loaded. But it won't
//    // even work, because it would get included in the list of fields, so equals() and hashCode() methods will throw
//    // an AssertionError! So we throw an exception when the dogTag is constructed. We test that exception here.
//
//    new BadClass();
//  }

  //////////////////////

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

    public static void setStaticInt(int i) {
      staticInt = i;
    }

    public int getStaticInt() {
      return staticInt;
    }
  }

  @SuppressWarnings({"WeakerAccess", "UseOfClone"})
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
    }

    private boolean julietBoolean;
    private short kiloShort;
    private double limaDouble;
    private float mikeFloat;

    private int[] novemberIntArray;
    private String[] operaStringArray;
    private long[] papaLongArray = { 1L, 3L, 6L, 10L, 15L, 21L, 28L, 36L, 45L };
    private short[] quebecShortArray = { 0, 1, 4, 9, 16, 25, 36, 49, 64, 81 };
    private byte[] romeoByteArray = { 127, 63, 31, 15, 7, 3, 1, 2, 3 };
    private char[] sierraCharArray = "charArray".toCharArray();
    private boolean[] tangoBooleanArray = { false, true, true, false, true, false, false, true, true, false };
    private float[] uniformFloatArray = { 1.4F, 2.0F, 2.8F, 4.0F, 5.6F, 8.0F, 11.0F, 16.0F, 22.0F};
    private double[] victorDoubleArray = { 0.1, 0.02, 0.003, 0.0004, 0.00005, 0.000006, 0.0000007, 0.00000008, 9.0 };
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
  }
  
  private class ParadigmTest {
    private String alphaString;
    private int bravoInt;
    private float charlieFloat;
    
    ParadigmTest(String alpha, int bravo, float charlie) {
      alphaString = alpha;
      bravoInt = bravo;
      charlieFloat = charlie;
    }

    private final DogTag<ParadigmTest> dogTag = DogTag.from(ParadigmTest.class, this);

    @Override
    public int hashCode() {
      return dogTag.doHashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
      return dogTag.doEqualsTest(obj);
    }
  }

//  private static class BadClass {
//    private final BadClass badInstance = new BadClass();
//    @SuppressWarnings("unused")
//    private static final DogTag<BadClass> dogTag = DogTag.create(BadClass.class); // STATIC! Should throw an AssertionError.
//  }

}
