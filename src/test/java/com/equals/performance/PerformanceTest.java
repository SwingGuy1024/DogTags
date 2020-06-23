package com.equals.performance;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiFunction;
import com.equals.DogTag;
import com.equals.DogTagExclude;
import com.equals.DogTagInclude;
import com.equals.TimingUtility;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Ignore;
import org.junit.Test;

import static com.equals.DogTag.classFrom;
import static com.equals.TimingUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/19/19
 * <p>Time: 11:36 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "MagicCharacter", "HardcodedLineSeparator", "UnnecessaryBoxing", "StringOperationCanBeSimplified"})
public class PerformanceTest {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  // TODO: Use DogTagInclude & DogTagExclude somewhere in this file, to remove package-private warning

  @Ignore
  @Test
  public void timeTest() {
    System.out.println("Time Test. Comparing objects with 14 fields, including arrays\n-------------------------------------------------------------");
    TestClass t0 = new TestClass(1, "bravo");
    TestClass t1 = t0.duplicate(11, "bravo");
    TestClass t2 = t0.duplicate(1, "bravisimo");
    TestClass t3 = t0.duplicate();
    t3.setCharlieInt(99);
    TestClass t4 = t0.duplicate();
    t4.setDeltaLong(999L);
    TestClass t5 = t0.duplicate();
    t5.setEchoString("Repeat");
    TestClass t6 = t0.duplicate();
    t6.setFoxtrotPoint(new Point2D.Double(88.4, 928.5));
    TestClass t7 = t0.duplicate();
    t7.setGolfInt(798);
    TestClass t8 = t0.duplicate();
    t8.setHotelByte((byte) 34);
    TestClass t9 = t0.duplicate();
    t9.setIndigoChar('P');
    TestClass t10 = t0.duplicate();
    t10.setJulietBoolean(true);
    TestClass t11 = t0.duplicate();
    t11.setKiloShort((short) 79);
    TestClass t12 = t0.duplicate();
    t12.setLimaDouble(798.23);
    TestClass t13 = t0.duplicate();
    t13.setMikeFloat(423.97F);
    TestClass t14 = t0.duplicate();
    t14.setNovemberIntArray(15, 25, 42);
    TestClass t15 = t0.duplicate();
    t15.setOperaStringArray("papa", "quebec", "romeo", "tango", "whiskey");
    TestClass t16 = t0.duplicate();
    t16.setPapaLongArray(3L, 6L, 9L, 12L, 15L, 18L, 21L, 24L, 27L);
    TestClass t17 = t0.duplicate();
    t17.setQuebecShortArray((short) 4, (short) 8, (short) 12, (short) 16, (short) 20, (short) 24, (short) 28, (short) 32, (short) 36);
    TestClass t18 = t0.duplicate();
    t18.setRomeoByteArray((byte) 2, (byte) 4, (byte) 6, (byte) 8, (byte) 10, (byte) 12, (byte) 14, (byte) 16, (byte) 18);
    TestClass t19 = t0.duplicate();
    t19.setSierraCharArray("different".toCharArray());
    TestClass t20 = t0.duplicate();
    t20.setTangoBooleanArray(false, false, false, true, true, true, true, true, true);
    TestClass t21 = t0.duplicate();
    t21.setUniformFloatArray(5.34F, 32.7F);
    TestClass t22 = t0.duplicate();
    t22.setVictorDoubleArray(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);
    TestClass t23 = t0.duplicate();
    t23.setWhiskeyObjectArray("Whiskey", 33.5F, Boolean.FALSE);
    TestClass[] instances = {t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t0.duplicate()};
    reverse(instances);

    DogTag.DogTagReflectiveBuilder<TestClass> classDogTagExclusionBuilder = DogTag.startWithAll(classFrom(t0));
    DogTag.Factory<TestClass> dogTagfactory = classDogTagExclusionBuilder.build();

    final BiFunction<TestClass, TestClass, Boolean> directEqual = TestClass::isEqual;
//    TimingUtility.runTestCycles(dogTagfactory, t0, instances, directEqual, EMPTY_STRING_ARRAY);
    DogTag.Factory<TestClass> lambdaFactory = DogTag.startEmpty(TestClass.class)
        .addArray(TestClass::getNovemberIntArray)
        .addArray(TestClass::getOperaStringArray)
        .addArray(TestClass::getPapaLongArray)
        .addArray(TestClass::getQuebecShortArray)
        .addArray(TestClass::getRomeoByteArray)
        .addArray(TestClass::getSierraCharArray)
        .addArray(TestClass::getTangoBooleanArray)
        .addArray(TestClass::getUniformFloatArray)
        .addArray(TestClass::getVictorDoubleArray)
        .addArray(TestClass::getWhiskeyObjectArray)
        .addSimple(TestClass::getAlphaInt)
        .addObject(TestClass::getBravoString)
        .addSimple(TestClass::getCharlieInt)
        .addSimple(TestClass::getDeltaLong)
        .addObject(TestClass::getEchoString)
        .addObject(TestClass::getFoxtrotPoint)
        .addSimple(TestClass::getGolfInt)
        .addSimple(TestClass::getHotelByte)
        .addSimple(TestClass::getIndigoChar)
        .addSimple(TestClass::isJulietBoolean)
        .addSimple(TestClass::getKiloShort)
        .addSimple(TestClass::getLimaDouble)
        .addSimple(TestClass::getMikeFloat)
        .build();
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    DogTag.Factory<TestClass> lambdaFactoryByField = DogTag.startEmpty(TestClass.class)
        .addArray((TestClass t) -> t.novemberIntArray)
        .addArray((TestClass t) -> t.operaStringArray)
        .addArray((TestClass t) -> t.papaLongArray)
        .addArray((TestClass t) -> t.quebecShortArray)
        .addArray((TestClass t) -> t.romeoByteArray)
        .addArray((TestClass t) -> t.sierraCharArray)
        .addArray((TestClass t) -> t.tangoBooleanArray)
        .addArray((TestClass t) -> t.uniformFloatArray)
        .addArray((TestClass t) -> t.victorDoubleArray)
        .addArray((TestClass t) -> t.whiskeyObjectArray)
        .addSimple(TestClass::getAlphaInt)
        .addObject(TestClass::getBravoString)
        .addSimple(TestClass::getCharlieInt)
        .addSimple(TestClass::getDeltaLong)
        .addObject(TestClass::getEchoString)
        .addObject(TestClass::getFoxtrotPoint)
        .addSimple(TestClass::getGolfInt)
        .addSimple(TestClass::getHotelByte)
        .addSimple(TestClass::getIndigoChar)
        .addSimple(TestClass::isJulietBoolean)
        .addSimple(TestClass::getKiloShort)
        .addSimple(TestClass::getLimaDouble)
        .addSimple(TestClass::getMikeFloat)
        .build();
    TimingUtility.runTestCycles(dogTagfactory, t0, instances, directEqual, EMPTY_STRING_ARRAY, lambdaFactory, lambdaFactoryByField);
        
  }

  @Ignore
  @Test
  public void testNoArrays() {

    System.out.println("Test No Arrays. Comparing objects with 12 fields, all primitives or Strings");
    // Test 2: No Arrays
//    final String[] excludedFields = {"novemberIntArray", "operaStringArray", "papaLongArray", "quebecShortArray",
//        "romeoByteArray", "sierraCharArray", "tangoBooleanArray", "uniformFloatArray", "victorDoubleArray",
//        "whiskeyObjectArray"};
//    DogTag<SingleValueTestClass> dogTagNoArrays = DogTag.createFromClass(SingleValueTestClass.class)
//        .withExcludedFields(excludedFields)
//        .getFactory();
    SingleValueTestClass dummy = new SingleValueTestClass(1, "x");
    DogTag.Factory<SingleValueTestClass> dogTagNoArrays = DogTag.startWithAll(classFrom(dummy)).build();
    SingleValueTestClass t0 = new SingleValueTestClass(1, "bravo");
    SingleValueTestClass t1 = new SingleValueTestClass(11, "bravo");
//    SingleValueTestClass t2 = new SingleValueTestClass(1, "bravisimo");
    SingleValueTestClass t3 = new SingleValueTestClass(1, "bravo");
    t3.setCharlieInt(33);
    SingleValueTestClass t4 = new SingleValueTestClass(1, "bravo");
    t4.setDeltaLong(44L);
    SingleValueTestClass t5 = new SingleValueTestClass(1, "bravo");
    t5.setEchoString("ECHO");
    SingleValueTestClass t6 = new SingleValueTestClass(1, "bravo");
    t6.setGolfInt(77);
    SingleValueTestClass t7 = new SingleValueTestClass(1, "bravo");
    t7.setHotelByte((byte) 88);
    SingleValueTestClass t8 = new SingleValueTestClass(1, "bravo");
    t8.setIndigoChar('i');
    SingleValueTestClass t9 = new SingleValueTestClass(1, "bravo");
    t9.setJulietBoolean(true);
    SingleValueTestClass t10 = new SingleValueTestClass(1, "bravo");
    t10.setKiloShort((short) 111);
    SingleValueTestClass t11 = new SingleValueTestClass(1, "bravo");
    t11.setLimaDouble(120.0);
    SingleValueTestClass t12 = new SingleValueTestClass(1, "bravo");
    t12.setMikeFloat(130.013F);
    SingleValueTestClass tDup = new SingleValueTestClass(1, "bravo");

    SingleValueTestClass[] pInstances = { tDup, t12, t11, t10, t9, t8, t7, t6, t5, t4, t3, /* t2,*/ t1};

    DogTag.Factory<SingleValueTestClass> lambdaFactory = DogTag.startEmpty(SingleValueTestClass.class)
        .addSimple(SingleValueTestClass::getAlphaInt)
        .addSimple(SingleValueTestClass::getCharlieInt)
        .addSimple(SingleValueTestClass::getDeltaLong)
        .addObject(SingleValueTestClass::getEchoString)
        .addSimple(SingleValueTestClass::getGolfInt)
        .addSimple(SingleValueTestClass::getHotelByte)
        .addSimple(SingleValueTestClass::getIndigoChar)
        .addSimple(SingleValueTestClass::isJulietBoolean)
        .addSimple(SingleValueTestClass::getKiloShort)
        .addSimple(SingleValueTestClass::getLimaDouble)
        .addSimple(SingleValueTestClass::getMikeFloat)
        .build();
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    DogTag.Factory<SingleValueTestClass> lambdaFieldFactory = DogTag.startEmpty(SingleValueTestClass.class)
        .addSimple((SingleValueTestClass t) -> t.alphaInt)
        .addSimple((SingleValueTestClass t) -> t.charlieInt)
        .addSimple((SingleValueTestClass t) -> t.deltaLong)
        .addObject((SingleValueTestClass t) -> t.echoString)
        .addSimple((SingleValueTestClass t) -> t.golfInt)
        .addSimple((SingleValueTestClass t) -> t.hotelByte)
        .addSimple((SingleValueTestClass t) -> t.indigoChar)
        .addSimple((SingleValueTestClass t) -> t.julietBoolean)
        .addSimple((SingleValueTestClass t) -> t.kiloShort)
        .addSimple((SingleValueTestClass t) -> t.limaDouble)
        .addSimple((SingleValueTestClass t) -> t.mikeFloat)
        .build();
    TimingUtility.runTestCycles(dogTagNoArrays, t0, pInstances, PerformanceTest::singleValueDirectEqual, EMPTY_STRING_ARRAY, lambdaFactory, lambdaFieldFactory);
  }


  private static boolean singleValueDirectEqual(SingleValueTestClass t1, SingleValueTestClass t2) {
    //noinspection ObjectEquality
    if (t1 == t2) {
      return true;
    }
    return (t1.getAlphaInt() == t2.getAlphaInt())
//        && Objects.equals(t1.getBravoString(), t2.getBravoString())
        && (t1.getCharlieInt() == t2.getCharlieInt())
        && (t1.getDeltaLong() == t2.getDeltaLong())
        && Objects.equals(t1.getEchoString(), t2.getEchoString())
//        && Objects.equals(t1.getFoxtrotPoint(), t2.getFoxtrotPoint())
        && (t1.getGolfInt() == t2.getGolfInt())
        && (t1.getHotelByte() == t2.getHotelByte())
        && (t1.getIndigoChar() == t2.getIndigoChar())
        && (t1.isJulietBoolean() == t2.isJulietBoolean())
        && (t1.getKiloShort() == t2.getKiloShort())
        // Use boxing to avoid caching, for a proper test.
        && Objects.equals(Double.valueOf(t1.getLimaDouble()), t2.getLimaDouble())
        && Objects.equals(Float.valueOf(t1.getMikeFloat()), t2.getMikeFloat());
  }

  private static String dup(String s) {
    String s2 = '1' + s;
    return s2.substring(1);
  }

  private static String[] dupArray(String[] array) {
    String[] dup = new String[array.length];
    int i=0;
    for (String s: array) {
      dup[i++] = dup(s);
    }
    return dup;
  }

  @SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "WeakerAccess", "MagicNumber", "HardCodedStringLiteral", "EqualsAndHashcode"})
  private static class TestClass extends SingleValueTestClass {

    TestClass(int alpha, String bravo) {
      super(alpha, bravo);
    }

    @DogTagExclude // Just to get rid of the warning in DogTagExclude.java that says 'class may be package private.'
    private static final int unused = 0;

    @DogTagInclude // Just to get rid of the warning in DogTagInclude.java that says 'class may be package private.'
    private int[] novemberIntArray = {11, 12, 13};
    private String[] operaStringArray = {"papa", "quebec", "romeo", "sierra", "tango"};
    private long[] papaLongArray = {1L, 3L, 6L, 10L, 15L, 21L, 28L, 36L, 45L};
    private short[] quebecShortArray = {0, 1, 4, 9, 16, 25, 36, 49, 64, 81};
    private byte[] romeoByteArray = {127, 63, 31, 15, 7, 3, 1, 2, 3};
    private char[] sierraCharArray = "charArray".toCharArray();
    private boolean[] tangoBooleanArray = {false, true, true, false, true, false, false, true, true, false};
    private float[] uniformFloatArray = {1.4F, 2.0F, 2.8F, 4.0F, 5.6F, 8.0F, 11.0F, 16.0F, 22.0F};
    private double[] victorDoubleArray = {0.1, 0.02, 0.003, 0.0004, 0.00005, 0.000006, 0.0000007, 0.00000008, 9.0};
    private Object[] whiskeyObjectArray = {new Point2D.Float(1.2f, 2.4f), "string", new HashSet<>()};

    public int[] getNovemberIntArray() {
      return novemberIntArray;
    }

    public void setNovemberIntArray(int... novemberIntArray) {
      this.novemberIntArray = novemberIntArray;
    }

    public String[] getOperaStringArray() {
      return operaStringArray;
    }

    public void setOperaStringArray(String... operaStringArray) {
      this.operaStringArray = operaStringArray;
    }

    public long[] getPapaLongArray() {
      return papaLongArray;
    }

    public void setPapaLongArray(long... papaLongArray) {
      this.papaLongArray = papaLongArray;
    }

    public short[] getQuebecShortArray() {
      return quebecShortArray;
    }

    public void setQuebecShortArray(short... quebecShortArray) {
      this.quebecShortArray = quebecShortArray;
    }

    public byte[] getRomeoByteArray() {
      return romeoByteArray;
    }

    public void setRomeoByteArray(byte... romeoByteArray) {
      this.romeoByteArray = romeoByteArray;
    }

    public char[] getSierraCharArray() {
      return sierraCharArray;
    }

    public void setSierraCharArray(char... sierraCharArray) {
      this.sierraCharArray = sierraCharArray;
    }

    public boolean[] getTangoBooleanArray() {
      return tangoBooleanArray;
    }

    public void setTangoBooleanArray(boolean... tangoBooleanArray) {
      this.tangoBooleanArray = tangoBooleanArray;
    }

    public float[] getUniformFloatArray() {
      return uniformFloatArray;
    }

    public void setUniformFloatArray(float... uniformFloatArray) {
      this.uniformFloatArray = uniformFloatArray;
    }

    public double[] getVictorDoubleArray() {
      return victorDoubleArray;
    }

    public void setVictorDoubleArray(double... victorDoubleArray) {
      this.victorDoubleArray = victorDoubleArray;
    }

    public Object[] getWhiskeyObjectArray() {
      return whiskeyObjectArray;
    }

    public void setWhiskeyObjectArray(Object... whiskeyObjectArray) {
      this.whiskeyObjectArray = whiskeyObjectArray;
    }

    public TestClass duplicate() {
      return duplicate(getAlphaInt(), getBravoString());
    }

    public TestClass duplicate(int alpha, String bravo) {
      TestClass tail = new TestClass(alpha, bravo);
      tail.setCharlieInt(getCharlieInt());
      tail.setDeltaLong(getDeltaLong());
      tail.setEchoString(dup(getEchoString()));
      tail.setFoxtrotPoint(new Point2D.Double(getFoxtrotPoint().getX(), getFoxtrotPoint().getY()));
      tail.setGolfInt(getGolfInt());
      //noinspection CachedNumberConstructorCall,deprecation
      tail.setHotelByte(new Byte(getHotelByte())); // To avoid identity test
      tail.setIndigoChar(getIndigoChar());
      tail.setJulietBoolean(isJulietBoolean());
      tail.setKiloShort(getKiloShort());
      tail.setLimaDouble(getLimaDouble());
      tail.setMikeFloat(getMikeFloat());
      tail.setNovemberIntArray(Arrays.copyOf(getNovemberIntArray(), getNovemberIntArray().length));
      tail.setOperaStringArray(dupArray(getOperaStringArray()));
      tail.setPapaLongArray(Arrays.copyOf(getPapaLongArray(), getPapaLongArray().length));
      tail.setQuebecShortArray(Arrays.copyOf(getQuebecShortArray(), getQuebecShortArray().length));
      tail.setRomeoByteArray(Arrays.copyOf(getRomeoByteArray(), getRomeoByteArray().length));
      tail.setSierraCharArray(Arrays.copyOf(getSierraCharArray(), getSierraCharArray().length));
      tail.setTangoBooleanArray(Arrays.copyOf(getTangoBooleanArray(), getTangoBooleanArray().length));
      tail.setUniformFloatArray(Arrays.copyOf(getUniformFloatArray(), getUniformFloatArray().length));
      tail.setVictorDoubleArray(Arrays.copyOf(getVictorDoubleArray(), getVictorDoubleArray().length));
      tail.setWhiskeyObjectArray(getWhiskeyObjectArray());
      return tail;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) { return true; }
      if (!(obj instanceof TestClass)) { return false; }
      TestClass tc = (TestClass) obj;
      return new EqualsBuilder()
          .append(getNovemberIntArray(), tc.getNovemberIntArray())
          .append(getOperaStringArray(), tc.getOperaStringArray())
          .append(getPapaLongArray(), tc.getPapaLongArray())
          .append(getQuebecShortArray(), tc.getQuebecShortArray())
          .append(getRomeoByteArray(), tc.getRomeoByteArray())
          .append(getSierraCharArray(), tc.getSierraCharArray())
          .append(getTangoBooleanArray(), tc.getTangoBooleanArray())
          .append(getUniformFloatArray(), tc.getUniformFloatArray())
          .append(getVictorDoubleArray(), tc.getVictorDoubleArray())
          .append(getWhiskeyObjectArray(), tc.getWhiskeyObjectArray())
          .append(getAlphaInt(), tc.getAlphaInt())
          .append(getBravoString(), tc.getBravoString())
          .append(getCharlieInt(), tc.getCharlieInt())
          .append(getDeltaLong(), tc.getDeltaLong())
          .append(getEchoString(), tc.getEchoString())
          .append(getFoxtrotPoint(), tc.getFoxtrotPoint())
          .append(getGolfInt(), tc.getGolfInt())
          .append(getHotelByte(), tc.getHotelByte())
          .append(getIndigoChar(), tc.getIndigoChar())
          .append(isJulietBoolean(), tc.isJulietBoolean())
          .append(getKiloShort(), tc.getKiloShort())
          .append(getLimaDouble(), tc.getLimaDouble())
          .append(getMikeFloat(), tc.getMikeFloat())
          .isEquals();
    }

    public static Boolean isEqual(TestClass thisOne, TestClass thatOne) {
      //noinspection ObjectEquality
      if (thisOne == thatOne) {
        return true;
      }
      return
          Arrays.equals(thisOne.getNovemberIntArray(), thatOne.getNovemberIntArray()) &&
          Arrays.equals(thisOne.getOperaStringArray(), thatOne.getOperaStringArray()) &&
          Arrays.equals(thisOne.getPapaLongArray(), thatOne.getPapaLongArray()) &&
          Arrays.equals(thisOne.getQuebecShortArray(), thatOne.getQuebecShortArray()) &&
          Arrays.equals(thisOne.getRomeoByteArray(), thatOne.getRomeoByteArray()) &&
          Arrays.equals(thisOne.getSierraCharArray(), thatOne.getSierraCharArray()) &&
          Arrays.equals(thisOne.getTangoBooleanArray(), thatOne.getTangoBooleanArray()) &&
          Arrays.equals(thisOne.getUniformFloatArray(), thatOne.getUniformFloatArray()) &&
          Arrays.equals(thisOne.getVictorDoubleArray(), thatOne.getVictorDoubleArray()) &&
          Arrays.equals(thisOne.getWhiskeyObjectArray(), thatOne.getWhiskeyObjectArray()) &&
          (thisOne.getAlphaInt() == thatOne.getAlphaInt()) &&
          thisOne.getBravoString().equals(thatOne.getBravoString()) &&
          (thisOne.getCharlieInt() == thatOne.getCharlieInt()) &&
          (thisOne.getDeltaLong() == thatOne.getDeltaLong()) &&
          thisOne.getEchoString().equals(thatOne.getEchoString()) &&
          thisOne.getFoxtrotPoint().equals(thatOne.getFoxtrotPoint()) &&
          (thisOne.getGolfInt() == thatOne.getGolfInt()) &&
          (thisOne.getHotelByte() == thatOne.getHotelByte()) &&
          (thisOne.getIndigoChar() == thatOne.getIndigoChar()) &&
          (thisOne.isJulietBoolean() == thatOne.isJulietBoolean()) &&
          (thisOne.getKiloShort() == thatOne.getKiloShort()) &&
          (Double.compare(thisOne.getLimaDouble(), thatOne.getLimaDouble()) == 0) &&
          (Float.compare(thisOne.getMikeFloat(), thatOne.getMikeFloat()) == 0);
    }

    private static DogTag.Factory<TestClass> notUsed; // to avoid construction exception
  }

  @SuppressWarnings({"WeakerAccess", "EqualsAndHashcode"})
  private static class SingleValueTestClass {
    SingleValueTestClass(int alpha, String bravo) {
      alphaInt = alpha;
      // I use new String(String) to avoid the identity check when comparing two identical Strings. 
      bravoString = new String(bravo);
    }

    private final int alphaInt;
    private final String bravoString;
    private int charlieInt = 3;
    private long deltaLong = 4L;
    // I use new String(String) to avoid the identity check when comparing two identical Strings. 
    private String echoString = new String("echo");
    private Point2D foxtrotPoint = new Point2D.Double(6.54, 4.56);
    private int golfInt = 7;
    private byte hotelByte = 8;
    private char indigoChar = 'I';
    private boolean julietBoolean = false;
    private short kiloShort = 11;
    private double limaDouble = 12.0;
    private float mikeFloat = 13.13F;

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

    public int getGolfInt() {
      return golfInt;
    }

    public void setGolfInt(int golfInt) {
      this.golfInt = golfInt;
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
    public boolean equals(final Object obj) {
      if (obj == null) { return false; }
      if (obj == this) { return true; }
      if (!(obj instanceof SingleValueTestClass)) {
        return false;
      }
      SingleValueTestClass rhs = (SingleValueTestClass) obj;
      return new EqualsBuilder()
          .append(getAlphaInt(), rhs.getAlphaInt())
          .append(getBravoString(), rhs.getBravoString())
          .append(getCharlieInt(), rhs.getCharlieInt())
          .append(getDeltaLong(), rhs.getDeltaLong())
          .append(getEchoString(), rhs.getEchoString())
          .append(getFoxtrotPoint(), rhs.getFoxtrotPoint())
          .append(getGolfInt(), rhs.getGolfInt())
          .append(getHotelByte(), rhs.getHotelByte())
          .append(getIndigoChar(), rhs.getIndigoChar())
          .append(isJulietBoolean(), rhs.isJulietBoolean())
          .append(getKiloShort(), rhs.getKiloShort())
          .append(getLimaDouble(), rhs.getLimaDouble())
          .append(getMikeFloat(), rhs.getMikeFloat())
          .isEquals();
    }

    private static DogTag.Factory<SingleValueTestClass> notUsed; // to avoid construction exception
  }
  
  @SuppressWarnings("EqualsAndHashcode")
  private static class TwoStringClass {
    private String alpha;
    private String bravo;

    TwoStringClass(String a, String b) {
      // I use new String(String) to avoid the identity check when comparing two identical Strings. 
      alpha = new String(a);
      bravo = new String(b);
    }

    public String getAlpha() { return alpha; }
    public String getBravo() { return bravo; }

    @Override
    public boolean equals(final Object obj) {
      if (obj == null) { return false; }
      if (obj == this) { return true; }
      if (!(obj instanceof TwoStringClass)) {
        return false;
      }
      TwoStringClass rhs = (TwoStringClass) obj;
      //noinspection NonFinalFieldReferenceInEquals
      return new EqualsBuilder()
          .append(alpha, rhs.alpha)
          .append(bravo, rhs.bravo)
          .isEquals();
    }

    private static final DogTag.Factory<TwoStringClass> dogTag = DogTag.startWithAll(TwoStringClass.class).build();
    private static final DogTag.Factory<TwoStringClass> referenceFactory = DogTag.startEmpty(TwoStringClass.class)
        .addObject(TwoStringClass::getAlpha)
        .addObject(TwoStringClass::getBravo)
        .build();
    private static final DogTag.Factory<TwoStringClass> lambdaFactory = DogTag.startEmpty(TwoStringClass.class)
        .addObject((TwoStringClass t) -> t.alpha)
        .addObject((TwoStringClass t) -> t.bravo)
        .build();
  }

  @SuppressWarnings({"EqualsReplaceableByObjectsCall", "AccessingNonPublicFieldOfAnotherObject"})
  private static boolean handCoded(TwoStringClass one, TwoStringClass two) {
    return one.alpha.equals(two.alpha) && one.bravo.equals(two.bravo);
  }

  @Ignore
  @Test
  public void testTwoStrings() {
    System.out.println("TestTwoStrings(): Test comparison of class with two strings.\n------------------------------------------------------------");
    TwoStringClass t0 = new TwoStringClass("ALPHA", "BRAVO");
    TwoStringClass t1 = new TwoStringClass("alpha", "BRAVO");
    TwoStringClass t2 = new TwoStringClass("alpha", "bravo");
    TwoStringClass t3 = new TwoStringClass("iALPHA".substring(1), "iBRAVO".substring(1));

    TwoStringClass[] array = { t3, t2, t1 };
    TimingUtility.runTestCycles(TwoStringClass.dogTag, t0, array, PerformanceTest::handCoded, EMPTY_STRING_ARRAY, 
        TwoStringClass.referenceFactory, TwoStringClass.lambdaFactory);
  }

  // todo: Add 26 strings

  @SuppressWarnings({"EqualsAndHashcode", "UseOfClone", "PackageVisibleField"})
  private static class S26 implements Cloneable {
    // I use new String(String) to avoid the identity check when comparing two identical Strings. 
    String a = new String("alpha");
    String b = new String("bravo");
    String c = new String("Charlie");
    String d = new String("delta");
    String e = new String("echo");
    String f = new String("foxtrot");
    String g = new String("golf");
    String h = new String("hotel");
    String i = new String("indigo");
    String j = new String("Juliet");
    String k = new String("kilo");
    String l = new String("lambda");
    String m = new String("Mike");
    String n = new String("November");
    String o = new String("opera");
    String p = new String("papa");
    String q = new String("Quebec");
    String r = new String("Romeo");
    String s = new String("sierra");
    String t = new String("tango");
    String u = new String("uniform");
    String v = new String("Victor");
    String w = new String("whiskey");
    String x = new String("x-ray");
    String y = new String("yankee");
    String z = new String("zulu");

    @Override
    public S26 clone() throws CloneNotSupportedException {
      return (S26) super.clone();
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof S26)) {
        return false;
      }
      S26 other = (S26) obj;
      return new EqualsBuilder()
          .append(a, other.a)
          .append(b, other.b)
          .append(c, other.c)
          .append(d, other.d)
          .append(e, other.e)
          .append(f, other.f)
          .append(g, other.g)
          .append(h, other.h)
          .append(i, other.i)
          .append(j, other.j)
          .append(k, other.k)
          .append(l, other.l)
          .append(m, other.m)
          .append(n, other.n)
          .append(o, other.o)
          .append(p, other.p)
          .append(q, other.q)
          .append(r, other.r)
          .append(s, other.s)
          .append(t, other.t)
          .append(u, other.u)
          .append(v, other.v)
          .append(w, other.w)
          .append(x, other.x)
          .append(y, other.y)
          .append(z, other.z)
          .isEquals();
    }
    private static DogTag.Factory<S26> notUsed;
  }

  @SuppressWarnings("EqualsReplaceableByObjectsCall")
  private static boolean handCoded26(S26 a, S26 b) {
    return a.a.equals(b.a)
        && a.b.equals(b.b)
        && a.c.equals(b.c)
        && a.d.equals(b.d)
        && a.e.equals(b.e)
        && a.f.equals(b.f)
        && a.g.equals(b.g)
        && a.h.equals(b.h)
        && a.i.equals(b.i)
        && a.j.equals(b.j)
        && a.k.equals(b.k)
        && a.l.equals(b.l)
        && a.m.equals(b.m)
        && a.n.equals(b.n)
        && a.o.equals(b.o)
        && a.p.equals(b.p)
        && a.q.equals(b.q)
        && a.r.equals(b.r)
        && a.s.equals(b.s)
        && a.t.equals(b.t)
        && a.u.equals(b.u)
        && a.v.equals(b.v)
        && a.w.equals(b.w)
        && a.x.equals(b.x)
        && a.y.equals(b.y)
        && a.z.equals(b.z)
        ;
  }

  @Ignore
  @Test
  public void test26() throws CloneNotSupportedException {
    System.out.println("test26() Comparison of objects with 26 Strings.\n-----------------------------------------------");
    S26 original = new S26();
    S26 aa = new S26();
    aa.a = "mismatch";
    S26 bb = new S26();
    bb.b = "mismatch";
    S26 cc = new S26();
    cc.c = "mismatch";
    S26 dd = new S26();
    dd.d = "mismatch";
    S26 ee = new S26();
    ee.e = "mismatch";
    S26 ff = new S26();
    ff.f = "mismatch";
    S26 gg = new S26();
    gg.g = "mismatch";
    S26 hh = new S26();
    hh.h = "mismatch";
    S26 ii = new S26();
    ii.i = "mismatch";
    S26 jj = new S26();
    jj.j = "mismatch";
    S26 kk = new S26();
    kk.k = "mismatch";
    S26 ll = new S26();
    ll.l = "mismatch";
    S26 mm = new S26();
    mm.m = "mismatch";
    S26 nn = new S26();
    nn.n = "mismatch";
    S26 oo = new S26();
    oo.o = "mismatch";
    S26 pp = new S26();
    pp.p = "mismatch";
    S26 qq = new S26();
    qq.q = "mismatch";
    S26 rr = new S26();
    rr.r = "mismatch";
    S26 ss = new S26();
    ss.s = "mismatch";
    S26 tt = new S26();
    tt.t = "mismatch";
    S26 uu = new S26();
    uu.u = "mismatch";
    S26 vv = new S26();
    vv.v = "mismatch";
    S26 ww = new S26();
    ww.w = "mismatch";
    S26 xx = new S26();
    xx.x = "mismatch";
    S26 yy = new S26();
    yy.y = "mismatch";
    S26 zz = new S26();
    zz.z = "mismatch";

    @SuppressWarnings("UseOfClone")
    S26 clone = original.clone();

    DogTag.Factory<S26> dogTag = DogTag.startWithAll(classFrom(zz)).build();

    S26[] i = {aa, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk, ll, mm, nn, oo, pp, qq, rr, ss, tt, uu, vv, ww, xx, yy, zz, clone };
    reverse(i);

    TimingUtility.runTestCycles(dogTag, original, i, PerformanceTest::handCoded26, EMPTY_STRING_ARRAY);
  }

  public static void main(String[] args) {
    S26 s1 = new S26();
    S26 s2 = new S26();
    
    PerformanceTest performanceTest = new PerformanceTest();
    performanceTest.timeTest();
    performanceTest.testNoArrays();
    try {
      performanceTest.test26();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    performanceTest.testTwoStrings();
  }

  @SuppressWarnings({"EqualsAndHashcode", "UseOfClone", "PackageVisibleField"})
  private static class S26Interned implements Cloneable {
    String a = "alpha";
    String b = "bravo";
    String c = "Charlie";
    String d = "delta";
    String e = "echo";
    String f = "foxtrot";
    String g = "golf";
    String h = "hotel";
    String i = "indigo";
    String j = "Juliet";
    String k = "kilo";
    String l = "lambda";
    String m = "Mike";
    String n = "November";
    String o = "opera";
    String p = "papa";
    String q = "Quebec";
    String r = "Romeo";
    String s = "sierra";
    String t = "tango";
    String u = "uniform";
    String v = "Victor";
    String w = "whiskey";
    String x = "x-ray";
    String y = "yankee";
    String z = "zulu";

    @Override
    public S26Interned clone() throws CloneNotSupportedException {
      return (S26Interned) super.clone();
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof S26Interned)) {
        return false;
      }
      S26Interned other = (S26Interned) obj;
      return new EqualsBuilder()
          .append(a, other.a)
          .append(b, other.b)
          .append(c, other.c)
          .append(d, other.d)
          .append(e, other.e)
          .append(f, other.f)
          .append(g, other.g)
          .append(h, other.h)
          .append(i, other.i)
          .append(j, other.j)
          .append(k, other.k)
          .append(l, other.l)
          .append(m, other.m)
          .append(n, other.n)
          .append(o, other.o)
          .append(p, other.p)
          .append(q, other.q)
          .append(r, other.r)
          .append(s, other.s)
          .append(t, other.t)
          .append(u, other.u)
          .append(v, other.v)
          .append(w, other.w)
          .append(x, other.x)
          .append(y, other.y)
          .append(z, other.z)
          .isEquals();
    }

    public String getA() { return a;}
    public String getB() { return b;}
    public String getC() { return c;}
    public String getD() { return d;}
    public String getE() { return e;}
    public String getF() { return f;}
    public String getG() { return g;}
    public String getH() { return h;}
    public String getI() { return i;}
    public String getJ() { return j;}
    public String getK() { return k;}
    public String getL() { return l;}
    public String getM() { return m;}
    public String getN() { return n;}
    public String getO() { return o;}
    public String getP() { return p;}
    public String getQ() { return q;}
    public String getR() { return r;}
    public String getS() { return s;}
    public String getT() { return t;}
    public String getU() { return u;}
    public String getV() { return v;}
    public String getW() { return w;}
    public String getX() { return x;}
    public String getY() { return y;}
    public String getZ() { return z;}
    private static final DogTag.Factory<S26Interned> reflectionFactory = DogTag.startWithAll(S26Interned.class).build();
    private static final DogTag.Factory<S26Interned> referenceFactory = DogTag.startEmpty(S26Interned.class)
        .addObject(S26Interned::getA)
        .addObject(S26Interned::getB)
        .addObject(S26Interned::getC)
        .addObject(S26Interned::getD)
        .addObject(S26Interned::getE)
        .addObject(S26Interned::getF)
        .addObject(S26Interned::getG)
        .addObject(S26Interned::getH)
        .addObject(S26Interned::getI)
        .addObject(S26Interned::getJ)
        .addObject(S26Interned::getK)
        .addObject(S26Interned::getL)
        .addObject(S26Interned::getM)
        .addObject(S26Interned::getN)
        .addObject(S26Interned::getO)
        .addObject(S26Interned::getP)
        .addObject(S26Interned::getQ)
        .addObject(S26Interned::getR)
        .addObject(S26Interned::getS)
        .addObject(S26Interned::getT)
        .addObject(S26Interned::getU)
        .addObject(S26Interned::getV)
        .addObject(S26Interned::getW)
        .addObject(S26Interned::getX)
        .addObject(S26Interned::getY)
        .addObject(S26Interned::getZ)
        .build();
    private static final DogTag.Factory<S26Interned> lambdaFactory = DogTag.startEmpty(S26Interned.class)
        .addObject((S26Interned s) -> s.a)
        .addObject((S26Interned s) -> s.b)
        .addObject((S26Interned s) -> s.c)
        .addObject((S26Interned s) -> s.d)
        .addObject((S26Interned s) -> s.e)
        .addObject((S26Interned s) -> s.f)
        .addObject((S26Interned s) -> s.g)
        .addObject((S26Interned s) -> s.h)
        .addObject((S26Interned s) -> s.i)
        .addObject((S26Interned s) -> s.j)
        .addObject((S26Interned s) -> s.k)
        .addObject((S26Interned s) -> s.l)
        .addObject((S26Interned s) -> s.m)
        .addObject((S26Interned s) -> s.n)
        .addObject((S26Interned s) -> s.o)
        .addObject((S26Interned s) -> s.p)
        .addObject((S26Interned s) -> s.q)
        .addObject((S26Interned s) -> s.r)
        .addObject((S26Interned s) -> s.s)
        .addObject((S26Interned s) -> s.t)
        .addObject((S26Interned s) -> s.u)
        .addObject((S26Interned s) -> s.v)
        .addObject((S26Interned s) -> s.w)
        .addObject((S26Interned s) -> s.x)
        .addObject((S26Interned s) -> s.y)
        .addObject((S26Interned s) -> s.z)
        .build();
  }
}
