package com.equals.performance;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import com.equals.DogTag;
import com.equals.DogTagExclude;
import com.equals.DogTagInclude;
import org.junit.Ignore;
import org.junit.Test;

import static com.equals.performance.TimingUtility.reverse;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/19/19
 * <p>Time: 11:36 PM
 *
 * @author Miguel Mu\u00f1oz
 * @noinspection EqualsReplaceableByObjectsCall, AccessingNonPublicFieldOfAnotherObject, UseOfClone
 */
@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "MagicCharacter", "HardcodedLineSeparator", "UnnecessaryBoxing", "RedundantStringConstructorCall"})
public class PerformanceTest {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  // TODO: Use DogTagInclude & DogTagExclude somewhere in this file, to remove package-private warning

  @Ignore
  @Test
  public void timeTest() {
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
    TestClass[] instances = { t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t0.duplicate() };
    reverse(instances);

//    final BiFunction<TestClass, TestClass, Boolean> directEqual = PerformanceTest::isEqual;
//    TimingUtility.runTestCycles(dogTag, t0, instances, directEqual, EMPTY_STRING_ARRAY);
    
    // Test 2: No Arrays
//    final String[] excludedFields = {"novemberIntArray", "operaStringArray", "papaLongArray", "quebecShortArray",
//        "romeoByteArray", "sierraCharArray", "tangoBooleanArray", "uniformFloatArray", "victorDoubleArray",
//        "whiskeyObjectArray"};
//    DogTag<SingleValueTestClass> dogTagNoArrays = DogTag.create(SingleValueTestClass.class)
//        .withExcludedFields(excludedFields)
//        .build();
    DogTag<SingleValueTestClass> dogTagNoArrays = DogTag.from(SingleValueTestClass.class);

    TestClass[] pInstances = { t0.duplicate(), t13, t12, t11, t10, t9, t8, t7, t6, t5, t4, t3, t2, t1, t0 };
    
    TimingUtility.runTestCycles(dogTagNoArrays, t0, pInstances, PerformanceTest::singleValueDirectEqual, EMPTY_STRING_ARRAY);
  }

  @SuppressWarnings("EqualsReplaceableByObjectsCall")
  private static boolean isEqual(TestClass t1, TestClass t2) {
    //noinspection ObjectEquality
    if (t1 == t2) {
      return true;
    }
    return (t1.getAlphaInt() == t2.getAlphaInt())
        && t1.getBravoString().equals(t2.getBravoString())
        && (t1.getCharlieInt() == t2.getCharlieInt())
        && (t1.getDeltaLong() == t2.getDeltaLong())
        && t1.getEchoString().equals(t2.getEchoString())
        && t1.getFoxtrotPoint().equals(t2.getFoxtrotPoint())
        && (t1.getHotelByte() == t2.getHotelByte())
        && (t1.isJulietBoolean() == t2.isJulietBoolean())
        && (t1.getKiloShort() == t2.getKiloShort())
        && Double.valueOf(t1.getLimaDouble()).equals(t2.getLimaDouble())
        && Float.valueOf(t1.getMikeFloat()).equals(t2.getMikeFloat())
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
  
  private static boolean singleValueDirectEqual(SingleValueTestClass t1, SingleValueTestClass t2) {
    //noinspection ObjectEquality
    if (t1 == t2) {
      return true;
    }
    return (t1.getAlphaInt() == t2.getAlphaInt())
        && Objects.equals(t1.getBravoString(), t2.getBravoString())
        && (t1.getCharlieInt() == t2.getCharlieInt())
        && (t1.getDeltaLong() == t2.getDeltaLong())
        && Objects.equals(t1.getEchoString(), t2.getEchoString())
        && Objects.equals(t1.getFoxtrotPoint(), t2.getFoxtrotPoint())
        && (t1.getHotelByte() == t2.getHotelByte())
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

  /** @noinspection CachedNumberConstructorCall, deprecation */
  @SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "WeakerAccess", "MagicCharacter", "MagicNumber", "HardCodedStringLiteral", "ImplicitNumericConversion"})
  private static class TestClass extends SingleValueTestClass {

    TestClass() {
      this(1, "bravo");
    }

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
    private Object[] whiskeyObjectArray = {new Point2D.Float(1.2f, 2.4f), "string", new HashSet()};

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

    @Override
    public TestClass duplicate() {
      return duplicate(getAlphaInt(), getBravoString());
    }

    @Override
    public TestClass duplicate(int alpha, String bravo) {
      TestClass tail = new TestClass(alpha, bravo);
      tail.setCharlieInt(getCharlieInt());
      tail.setDeltaLong(getDeltaLong());
      tail.setEchoString(dup(getEchoString()));
      tail.setFoxtrotPoint(new Point2D.Double(getFoxtrotPoint().getX(), getFoxtrotPoint().getY()));
      tail.setGolfInt(getGolfInt());
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
  }

  /** @noinspection CachedNumberConstructorCall*/
  @SuppressWarnings("WeakerAccess")
  private static class SingleValueTestClass {
    SingleValueTestClass() {
      this(1, "bravo");
    }

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
    public SingleValueTestClass duplicate() {
      return duplicate(getAlphaInt(), getBravoString());
    }

    /** @noinspection deprecation*/
    public SingleValueTestClass duplicate(int alpha, String bravo) {
      SingleValueTestClass tail = new SingleValueTestClass(alpha, bravo);
      tail.setCharlieInt(getCharlieInt());
      tail.setDeltaLong(getDeltaLong());
      tail.setEchoString(dup(getEchoString()));
      tail.setFoxtrotPoint(new Point2D.Double(getFoxtrotPoint().getX(), getFoxtrotPoint().getY()));
      tail.setGolfInt(getGolfInt());
      tail.setHotelByte(new Byte(getHotelByte())); // To avoid identity test
      tail.setIndigoChar(getIndigoChar());
      tail.setJulietBoolean(isJulietBoolean());
      tail.setKiloShort(getKiloShort());
      tail.setLimaDouble(getLimaDouble());
      tail.setMikeFloat(getMikeFloat());
      return tail;
    }

  }
  
  private static class TwoStringClass {
    private String alpha;
    private String bravo;
    
    TwoStringClass(String a, String b) {
      // I use new String(String) to avoid the identity check when comparing two identical Strings. 
      alpha = new String(a);
      bravo = new String(b);
    }
  }

  private static boolean handCoded(TwoStringClass one, TwoStringClass two) {
    return one.alpha.equals(two.alpha) && one.bravo.equals(two.bravo);
  }

  @Ignore
  @Test
  public void testTwoStrings() {
    TwoStringClass t0 = new TwoStringClass("ALPHA", "BRAVO");
    TwoStringClass t1 = new TwoStringClass("alpha", "BRAVO");
    TwoStringClass t2 = new TwoStringClass("alpha", "bravo");
    TwoStringClass t3 = new TwoStringClass("iALPHA".substring(1), "iBRAVO".substring(1));
    
    DogTag<TwoStringClass> dogTag = DogTag.from(TwoStringClass.class);
    TwoStringClass[] array = { t3, t2, t1, t0};
    TimingUtility.runTestCycles(dogTag, t0, array, PerformanceTest::handCoded, EMPTY_STRING_ARRAY);
  }
  
  // todo: Add 26 strings
  
  /** @noinspection PackageVisibleField, UseOfClone */
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
  }
  
  private static boolean handCoded26(S26 a, S26 b) {
    //noinspection EqualsReplaceableByObjectsCall
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
    
    S26 clone = original.clone();
    
    DogTag<S26> dogTag = DogTag.from(S26.class);
    
    S26[] i = {original, aa, bb, cc, dd, ee, ff, gg, hh, ii, jj, kk, ll, mm, nn, oo, pp, qq, rr, ss, tt, uu, vv, ww, xx, yy, zz, clone };
    reverse(i);

    TimingUtility.runTestCycles(dogTag, original, i, PerformanceTest::handCoded26, EMPTY_STRING_ARRAY);
  }
}
