import java.awt.geom.Point2D;
import org.junit.Test;

public class DogTagTest {
  @Test
  public void testEquals() {

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

  private static class DogTagTestBase {
    private final int alphaInt;
    private final String bravoString;
    private int charlieInt;
    private long deltaLong;
    private String echoString;
    private Point2D foxtrotPoint;
    private transient int golfIntTr;
    private byte hotelByte;
    private char indigoChar;
    private boolean julietBoolean;
    private short kiloShort;
    private double limaDouble;
    private float mikeFloat;

    private int[] hotelIntArray;
    private String[] indigoStringArray;

    public DogTagTestBase(int alphaInt, String bravoString, int charlieInt, long deltaLong, String echoString, Point2D foxtrotPoint, int golfIntTr) {
      this.alphaInt = alphaInt;
      this.bravoString = bravoString;
      this.charlieInt = charlieInt;
      this.deltaLong = deltaLong;
      this.echoString = echoString;
      this.foxtrotPoint = foxtrotPoint;
      this.golfIntTr = golfIntTr;
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
  }


}
