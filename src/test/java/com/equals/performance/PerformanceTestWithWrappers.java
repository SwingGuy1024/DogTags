package com.equals.performance;

import java.awt.geom.Point2D;
import java.util.function.BiFunction;
import com.equals.DogTag;
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
public class PerformanceTestWithWrappers {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private DogTag.Factory<TestClass> dogTag = DogTag.create(TestClass.class).makeFactory();

  @SuppressWarnings("NumericCastThatLosesPrecision")
  private static Short toShort(int i) { return (short) i;}

  @SuppressWarnings("NumericCastThatLosesPrecision")
  private static Byte toByte(int i) { return (byte) i; }

  @Ignore
  @Test
  public void timeTest() {
    System.out.printf("Java version = %s%n", System.getProperty("java.version"));
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
    t8.setHotelByte( toByte(34));
    TestClass t9 = t0.duplicate();
    t9.setIndigoChar('P');
    TestClass t10 = t0.duplicate();
    t10.setJulietBoolean(true);
    TestClass t11 = t0.duplicate();
    t11.setKiloShort(toShort(79));
    TestClass t12 = t0.duplicate();
    t12.setLimaDouble(798.23);
    TestClass t13 = t0.duplicate();
    t13.setMikeFloat(423.97F);
    TestClass[] instances = { t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, /* t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, */ t0.duplicate() };
    TimingUtility.reverse(instances);

    final BiFunction<TestClass, TestClass, Boolean> directEqual = PerformanceTestWithWrappers::isEqual;
    TimingUtility.runTestCycles(dogTag, t0, instances, directEqual, EMPTY_STRING_ARRAY);
  }

  @SuppressWarnings("EqualsReplaceableByObjectsCall")
  private static boolean isEqual(TestClass t1, TestClass t2) {
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


  @SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "WeakerAccess", "MagicCharacter", "MagicNumber", "HardCodedStringLiteral", "ImplicitNumericConversion", "EqualsAndHashcode"})
  private static class TestClass {

    TestClass() {
      this(1, "bravo");
    }

    TestClass(Integer alpha, String bravo) {
      alphaInt = alpha;
      bravoString = bravo;
    }

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

    public Integer getAlphaInt() {
      return alphaInt;
    }

    public String getBravoString() {
      return bravoString;
    }

    public Integer getCharlieInt() {
      return charlieInt;
    }

    public void setCharlieInt(Integer charlieInt) {
      this.charlieInt = charlieInt;
    }

    public Long getDeltaLong() {
      return deltaLong;
    }

    public void setDeltaLong(Long deltaLong) {
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

    public Integer getGolfInt() {
      return golfInt;
    }

    public void setGolfInt(Integer golfInt) {
      this.golfInt = golfInt;
    }

    public Byte getHotelByte() {
      return hotelByte;
    }

    public void setHotelByte(Byte hotelByte) {
      this.hotelByte = hotelByte;
    }

    public Character getIndigoChar() {
      return indigoChar;
    }

    public void setIndigoChar(Character indigoChar) {
      this.indigoChar = indigoChar;
    }

    public Boolean isJulietBoolean() {
      return julietBoolean;
    }

    public void setJulietBoolean(Boolean julietBoolean) {
      this.julietBoolean = julietBoolean;
    }

    public Short getKiloShort() {
      return kiloShort;
    }

    public void setKiloShort(Short kiloShort) {
      this.kiloShort = kiloShort;
    }

    public Double getLimaDouble() {
      return limaDouble;
    }

    public void setLimaDouble(Double limaDouble) {
      this.limaDouble = limaDouble;
    }

    public Float getMikeFloat() {
      return mikeFloat;
    }

    public void setMikeFloat(Float mikeFloat) {
      this.mikeFloat = mikeFloat;
    }


    public TestClass duplicate() {
      return duplicate(getAlphaInt(), getBravoString());
    }

    public TestClass duplicate(Integer alpha, String bravo) {
      TestClass tail = new TestClass(alpha, bravo);
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
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof TestClass)) {
        return false;
      }
      TestClass rhs = (TestClass) obj;
      TestClass lhs = this;
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

  public static void main(String[] args) {
    new PerformanceTestWithWrappers().timeTest();
  }
}
