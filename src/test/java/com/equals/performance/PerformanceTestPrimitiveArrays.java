package com.equals.performance;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BiFunction;
import com.equals.DogTag;
import com.equals.TimingUtility;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/19/19
 * <p>Time: 11:36 PM
 *
 * @author Miguel Mu\u00f1oz
 * @noinspection MagicCharacter
 */
@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral"})
public class PerformanceTestPrimitiveArrays {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  @SuppressWarnings("NumericCastThatLosesPrecision")
  private static Short toShort(final int i) {
    return (short) i;
  }

  @SuppressWarnings("NumericCastThatLosesPrecision")
  private static Byte toByte(final int i) {
    return (byte) i;
  }

  @Ignore
  @Test
  public void timeTest() {
    final TestClass t0 = new TestClass(1, "bravo");
    final TestClass t1 = t0.duplicate(11, "bravo");
    final TestClass t2 = t0.duplicate(1, "bravisimo");
    final TestClass t3 = t0.duplicate();
    t3.setCharlieInt(99);
    final TestClass t4 = t0.duplicate();
    t4.setDeltaLong(999L);
    final TestClass t5 = t0.duplicate();
    t5.setEchoString("Repeat");
    final TestClass t6 = t0.duplicate();
    t6.setFoxtrotPoint(new Point2D.Double(88.4, 928.5));
    final TestClass t7 = t0.duplicate();
    t7.setGolfInt(798);
    final TestClass t8 = t0.duplicate();
    t8.setHotelByte( toByte(34));
    final TestClass t9 = t0.duplicate();
    t9.setIndigoChar('P');
    final TestClass t10 = t0.duplicate();
    t10.setJulietBoolean(true);
    final TestClass t11 = t0.duplicate();
    t11.setKiloShort(toShort(79));
    final TestClass t12 = t0.duplicate();
    t12.setLimaDouble(798.23);
    final TestClass t13 = t0.duplicate();
    t13.setMikeFloat(423.97F);
    final TestClass t14 = t0.duplicate();
    t14.setNovemberIntArray(15, 25, 42);
    final TestClass t15 = t0.duplicate();
    t15.setOperaStringArray("papa", "quebec", "romeo", "tango", "whiskey");
    final TestClass t16 = t0.duplicate();
    t16.setPapaLongArray(3L, 6L, 9L, 12L, 15L, 18L, 21L, 24L, 27L);
    final TestClass t17 = t0.duplicate();
    t17.setQuebecShortArray(toShort(4), toShort(8), toShort(12), toShort(16), toShort(20), toShort(24), toShort(28), toShort(32), toShort(36));
    final TestClass t18 = t0.duplicate();
    t18.setRomeoByteArray(toByte(2), toByte(4), toByte(6), toByte(8), toByte(10), toByte(12), toByte(14), toByte(16), toByte(18));
    final TestClass t19 = t0.duplicate();
    t19.setSierraCharArray("different".toCharArray());
    final TestClass t20 = t0.duplicate();
    t20.setTangoBooleanArray(false, false, false, true, true, true, true, true, true);
    final TestClass t21 = t0.duplicate();
    t21.setUniformFloatArray(5.34F, 32.7F);
    final TestClass t22 = t0.duplicate();
    t22.setVictorDoubleArray(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);
    final TestClass t23 = t0.duplicate();
    t23.setWhiskeyObjectArray("Whiskey", 33.5F, Boolean.FALSE);
    final TestClass[] instances = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t0.duplicate() };
    TimingUtility.reverse(instances);

    final BiFunction<TestClass, TestClass, Boolean> directEqual = PerformanceTestPrimitiveArrays::isEqual;
    TimingUtility.runTestCycles(TestClass.dogTag, t0, instances, directEqual, EMPTY_STRING_ARRAY);
  }

  @SuppressWarnings("EqualsReplaceableByObjectsCall")
  private static boolean isEqual(final TestClass t1, final TestClass t2) {
    //noinspection ObjectEquality
    if (t1 == t2) {
      return true;
    }
    return (t1.getAlphaInt().equals(t2.getAlphaInt()))
        && t1.getBravoString().equals(t2.getBravoString())
        && (t1.getCharlieInt().equals(t2.getCharlieInt()))
        && (t1.getDeltaLong().equals(t2.getDeltaLong()))
        && t1.getEchoString().equals(t2.getEchoString())
        && t1.getFoxtrotPoint().equals(t2.getFoxtrotPoint())
        && t1.getGolfInt().equals(t2.getGolfInt())
        && (t1.getHotelByte().equals(t2.getHotelByte()))
        && (t1.getIndigoChar().equals(t2.getIndigoChar()))
        && (t1.isJulietBoolean().equals(t2.isJulietBoolean()))
        && (t1.getKiloShort().equals(t2.getKiloShort()))
        && t1.getLimaDouble().equals(t2.getLimaDouble())
        && t1.getMikeFloat().equals(t2.getMikeFloat())
        && Arrays.equals(t1.getNovemberIntArray(), t2.getNovemberIntArray())
        && Arrays.equals(t1.getOperaStringArray(), t2.getOperaStringArray())
        && Arrays.equals(t1.getPapaLongArray(), t2.getPapaLongArray())
        && Arrays.equals(t1.getQuebecShortArray(), t2.getQuebecShortArray())
        && Arrays.equals(t1.getRomeoByteArray(), t2.getRomeoByteArray())
        && Arrays.equals(t1.getSierraCharArray(), t2.getSierraCharArray())
        && Arrays.equals(t1.getTangoBooleanArray(), t2.getTangoBooleanArray())
        && Arrays.equals(t1.getUniformFloatArray(), t2.getUniformFloatArray())
        && Arrays.equals(t1.getVictorDoubleArray(), t2.getVictorDoubleArray())
        && Arrays.equals(t1.getWhiskeyObjectArray(), t2.getWhiskeyObjectArray());
  }


  @SuppressWarnings(
      {"AssignmentOrReturnOfFieldWithMutableType", "WeakerAccess", "MagicCharacter", "MagicNumber", "HardCodedStringLiteral", "EqualsAndHashcode"})
  private static class TestClass {

    TestClass(final Integer alpha, final String bravo) {
      alphaInt = alpha;
      bravoString = bravo;
    }

    @SuppressWarnings("unused")
    private static int staticInt = 5;
    private final Integer alphaInt;
    private final String bravoString;
    private Integer charlieInt = 3;
    private Long deltaLong = 4L;
    private String echoString = "echo";
    private Point2D foxtrotPoint = new Point2D.Double(6.54, 4.56);
    private Integer golfInt = 7;
    private Byte hotelByte = toByte(8);
    private Character indigoChar = 'I';
    private Boolean julietBoolean = false;
    private Short kiloShort = 11;
    private Double limaDouble = 12.0;
    private Float mikeFloat = 13.13F;
    private int[] novemberIntArray = { 11, 12, 13 };
    private String[] operaStringArray = { "papa", "quebec", "romeo", "sierra", "tango" };
    private long[] papaLongArray = {1L, 3L, 6L, 10L, 15L, 21L, 28L, 36L, 45L};
    private short[] quebecShortArray = {0, 1, 4, 9, 16, 25, 36, 49, 64, 81};
    private byte[] romeoByteArray = {127, 63, 31, 15, 7, 3, 1, 2, 3};
    private char[] sierraCharArray = "charArray".toCharArray();
    private boolean[] tangoBooleanArray = {false, true, true, false, true, false, false, true, true, false};
    private float[] uniformFloatArray = {1.4F, 2.0F, 2.8F, 4.0F, 5.6F, 8.0F, 11.0F, 16.0F, 22.0F};
    private double[] victorDoubleArray = {0.1, 0.02, 0.003, 0.0004, 0.00005, 0.000006, 0.0000007, 0.00000008, 9.0};
    private Object[] whiskeyObjectArray = {new Point2D.Float(1.2f, 2.4f), "string", new HashSet<>()};

    public Integer getAlphaInt() {
      return alphaInt;
    }

    public String getBravoString() {
      return bravoString;
    }

    public Integer getCharlieInt() {
      return charlieInt;
    }

    public void setCharlieInt(final Integer charlieInt) {
      this.charlieInt = charlieInt;
    }

    public Long getDeltaLong() {
      return deltaLong;
    }

    public void setDeltaLong(final Long deltaLong) {
      this.deltaLong = deltaLong;
    }

    public String getEchoString() {
      return echoString;
    }

    public void setEchoString(final String echoString) {
      this.echoString = echoString;
    }

    public Point2D getFoxtrotPoint() {
      return foxtrotPoint;
    }

    public void setFoxtrotPoint(final Point2D foxtrotPoint) {
      this.foxtrotPoint = foxtrotPoint;
    }

    public Integer getGolfInt() {
      return golfInt;
    }

    public void setGolfInt(final Integer golfInt) {
      this.golfInt = golfInt;
    }

    public Byte getHotelByte() {
      return hotelByte;
    }

    public void setHotelByte(final Byte hotelByte) {
      this.hotelByte = hotelByte;
    }

    public Character getIndigoChar() {
      return indigoChar;
    }

    public void setIndigoChar(final Character indigoChar) {
      this.indigoChar = indigoChar;
    }

    public Boolean isJulietBoolean() {
      return julietBoolean;
    }

    public void setJulietBoolean(final Boolean julietBoolean) {
      this.julietBoolean = julietBoolean;
    }

    public Short getKiloShort() {
      return kiloShort;
    }

    public void setKiloShort(final Short kiloShort) {
      this.kiloShort = kiloShort;
    }

    public Double getLimaDouble() {
      return limaDouble;
    }

    public void setLimaDouble(final Double limaDouble) {
      this.limaDouble = limaDouble;
    }

    public Float getMikeFloat() {
      return mikeFloat;
    }

    public void setMikeFloat(final Float mikeFloat) {
      this.mikeFloat = mikeFloat;
    }

    public int[] getNovemberIntArray() {
      return novemberIntArray;
    }

    public void setNovemberIntArray(final int... novemberIntArray) {
      this.novemberIntArray = novemberIntArray;
    }

    public String[] getOperaStringArray() {
      return operaStringArray;
    }

    public void setOperaStringArray(final String... operaStringArray) {
      this.operaStringArray = operaStringArray;
    }

    public long[] getPapaLongArray() {
      return papaLongArray;
    }

    public void setPapaLongArray(final long... papaLongArray) {
      this.papaLongArray = papaLongArray;
    }

    public short[] getQuebecShortArray() {
      return quebecShortArray;
    }

    public void setQuebecShortArray(final short... quebecShortArray) {
      this.quebecShortArray = quebecShortArray;
    }

    public byte[] getRomeoByteArray() {
      return romeoByteArray;
    }

    public void setRomeoByteArray(final byte... romeoByteArray) {
      this.romeoByteArray = romeoByteArray;
    }

    public char[] getSierraCharArray() {
      return sierraCharArray;
    }

    public void setSierraCharArray(final char... sierraCharArray) {
      this.sierraCharArray = sierraCharArray;
    }

    public boolean[] getTangoBooleanArray() {
      return tangoBooleanArray;
    }

    public void setTangoBooleanArray(final boolean... tangoBooleanArray) {
      this.tangoBooleanArray = tangoBooleanArray;
    }

    public float[] getUniformFloatArray() {
      return uniformFloatArray;
    }

    public void setUniformFloatArray(final float... uniformFloatArray) {
      this.uniformFloatArray = uniformFloatArray;
    }

    public double[] getVictorDoubleArray() {
      return victorDoubleArray;
    }

    public void setVictorDoubleArray(final double... victorDoubleArray) {
      this.victorDoubleArray = victorDoubleArray;
    }

    public Object[] getWhiskeyObjectArray() {
      return whiskeyObjectArray;
    }

    public void setWhiskeyObjectArray(final Object... whiskeyObjectArray) {
      this.whiskeyObjectArray = whiskeyObjectArray;
    }

    public TestClass duplicate() {
      return duplicate(getAlphaInt(), getBravoString());
    }

    public TestClass duplicate(final Integer alpha, final String bravo) {
      final TestClass tail = new TestClass(alpha, bravo);
      tail.setCharlieInt(getCharlieInt());
      tail.setDeltaLong(getDeltaLong());
      tail.setEchoString(dup(getEchoString()));
      tail.setFoxtrotPoint(new Point2D.Double(getFoxtrotPoint().getX(), getFoxtrotPoint().getY()));
      tail.setGolfInt(getGolfInt());
      tail.setHotelByte(getHotelByte());
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

      @SuppressWarnings("ObjectInstantiationInEqualsHashCode")
      @Override
      public boolean equals (final Object obj) {
        if (this == obj) {
          return true;
        }
        if (!(obj instanceof TestClass)) {
          return false;
        }
        final TestClass rhs = (TestClass) obj;
        final TestClass lhs = this;
        return new EqualsBuilder()
            /* Using getter methods rather than direct access doesn't change the performance */
//            .append(lhs.alphaInt, rhs.alphaInt)
//            .append(lhs.bravoString, rhs.bravoString)
//            .append(lhs.charlieInt, rhs.charlieInt)
//            .append(lhs.deltaLong, rhs.deltaLong)
//            .append(lhs.echoString, rhs.echoString)
//            .append(lhs.foxtrotPoint, rhs.foxtrotPoint)
//            .append(lhs.golfInt, rhs.golfInt)
//            .append(lhs.hotelByte, rhs.hotelByte)
//            .append(lhs.indigoChar, rhs.indigoChar)
//            .append(lhs.julietBoolean, rhs.julietBoolean)
//            .append(lhs.kiloShort, rhs.kiloShort)
//            .append(lhs.limaDouble, rhs.limaDouble)
//            .append(lhs.mikeFloat, rhs.mikeFloat)
//            .append(lhs.novemberIntArray, rhs.novemberIntArray)
//            .append(lhs.operaStringArray, rhs.operaStringArray)
//            .append(lhs.papaLongArray, rhs.papaLongArray)
//            .append(lhs.quebecShortArray, rhs.quebecShortArray)
//            .append(lhs.romeoByteArray, rhs.romeoByteArray)
//            .append(lhs.sierraCharArray, rhs.sierraCharArray)
//            .append(lhs.tangoBooleanArray, rhs.tangoBooleanArray)
//            .append(lhs.uniformFloatArray, rhs.uniformFloatArray)
//            .append(lhs.victorDoubleArray, rhs.victorDoubleArray)
//            .append(lhs.whiskeyObjectArray, rhs.whiskeyObjectArray)

            .append(lhs.getAlphaInt(), rhs.getAlphaInt())
            .append(lhs.getBravoString(), rhs.getBravoString())
            .append(lhs.getCharlieInt(), rhs.getCharlieInt())
            .append(lhs.getDeltaLong(), rhs.getDeltaLong())
            .append(lhs.getEchoString(), rhs.getEchoString())
            .append(lhs.getFoxtrotPoint(), rhs.getFoxtrotPoint())
            .append(lhs.getGolfInt(), rhs.getGolfInt())
            .append(lhs.getHotelByte(), rhs.getHotelByte())
            .append(lhs.getIndigoChar(), rhs.getIndigoChar())
            .append(lhs.isJulietBoolean(), rhs.isJulietBoolean())
            .append(lhs.getKiloShort(), rhs.getKiloShort())
            .append(lhs.getLimaDouble(), rhs.getLimaDouble())
            .append(lhs.getMikeFloat(), rhs.getMikeFloat())
            .append(lhs.getNovemberIntArray(), rhs.getNovemberIntArray())
            .append(lhs.getOperaStringArray(), rhs.getOperaStringArray())
            .append(lhs.getPapaLongArray(), rhs.getPapaLongArray())
            .append(lhs.getQuebecShortArray(), rhs.getQuebecShortArray())
            .append(lhs.getRomeoByteArray(), rhs.getRomeoByteArray())
            .append(lhs.getSierraCharArray(), rhs.getSierraCharArray())
            .append(lhs.getTangoBooleanArray(), rhs.getTangoBooleanArray())
            .append(lhs.getUniformFloatArray(), rhs.getUniformFloatArray())
            .append(lhs.getVictorDoubleArray(), rhs.getVictorDoubleArray())
            .append(lhs.getWhiskeyObjectArray(), rhs.getWhiskeyObjectArray())
            .isEquals();
      }

    private static final DogTag.Factory<TestClass> dogTag = DogTag.startWithAll(TestClass.class).build();
  }

  private static String dup(final String s) {
    final String s2 = '1' + s;
    return s2.substring(1);
  }

  private static String[] dupArray(final String[] array) {
    final String[] dup = new String[array.length];
    int i=0;
    for (final String s: array) {
      dup[i++] = dup(s);
    }
    return dup;
  }

  public static void main(final String[] args) {
    new PerformanceTestPrimitiveArrays().timeTest();
  }
}
