package com.equals.performance;

import java.awt.geom.Point2D;
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
 */
@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "MagicCharacter", "SameParameterValue"})
public class PerformanceTestWithWrappersUnCached {
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
    System.out.printf("Java version = %s%n", System.getProperty("java.version"));
    final TestClass t0 = new TestClass(1111, "bravo");
    final TestClass t1 = t0.duplicate(2222, "bravo");
    final TestClass t2 = t0.duplicate(1111, "bravisimo");
    final TestClass t3 = t0.duplicate();
    t3.setCharlieInt(9999);
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
    t9.setIndigoChar('\u3412');
    final TestClass t10 = t0.duplicate();
    t10.setJulietBoolean(true);
    final TestClass t11 = t0.duplicate();
    t11.setKiloShort(toShort(790));
    final TestClass t12 = t0.duplicate();
    t12.setLimaDouble(798.23);
    final TestClass t13 = t0.duplicate();
    t13.setMikeFloat(423.97F);

    final TestClass[] instances = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13,
        /* t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, */ t0.duplicate() };
    TimingUtility.reverse(instances);

    final BiFunction<TestClass, TestClass, Boolean> directEqual = PerformanceTestWithWrappersUnCached::isEqual;
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
        && t1.getMikeFloat().equals(t2.getMikeFloat());
  }


  @SuppressWarnings({"WeakerAccess", "MagicCharacter", "MagicNumber", "HardCodedStringLiteral", "EqualsAndHashcode"})
  private static class TestClass {

    TestClass(final Integer alpha, final String bravo) {
      alphaInt = alpha;
      bravoString = bravo;
    }

    private final Integer alphaInt;
    private final String bravoString;
    private Integer charlieInt = 333;
    private Long deltaLong = 444L;
    private String echoString = "echo";
    private Point2D foxtrotPoint = new Point2D.Double(6.54, 4.56);
    private Integer golfInt = 777;
    private Byte hotelByte = toByte(888);
    private Character indigoChar = 'I';
    private Boolean julietBoolean = false;
    private Short kiloShort = 11111;
    private Double limaDouble = 12.0;
    private Float mikeFloat = 13.13F;

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
      return tail;
    }

    @SuppressWarnings({"NonFinalFieldReferenceInEquals", "ObjectInstantiationInEqualsHashCode"})
    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof TestClass)) {
        return false;
      }
      final TestClass rhs = (TestClass) obj;
      final TestClass lhs = this;
      return new EqualsBuilder()
          .append(lhs.alphaInt, rhs.alphaInt)
          .append(lhs.bravoString, rhs.bravoString)
          .append(lhs.charlieInt, rhs.charlieInt)
          .append(lhs.deltaLong, rhs.deltaLong)
          .append(lhs.echoString, rhs.echoString)
          .append(lhs.foxtrotPoint, rhs.foxtrotPoint)
          .append(lhs.golfInt, rhs.golfInt)
          .append(lhs.hotelByte, rhs.hotelByte)
          .append(lhs.indigoChar, rhs.indigoChar)
          .append(lhs.julietBoolean, rhs.julietBoolean)
          .append(lhs.kiloShort, rhs.kiloShort)
          .append(lhs.limaDouble, rhs.limaDouble)
          .append(lhs.mikeFloat, rhs.mikeFloat)
          .isEquals();
    }

    private static final DogTag.Factory<TestClass> dogTag = DogTag.startWithAll(TestClass.class).build();
  }

  private static String dup(final String s) {
    final String s2 = '1' + s;
    return s2.substring(1);
  }

  public static void main(final String[] args) {
    new PerformanceTestWithWrappersUnCached().timeTest();
  }
}
