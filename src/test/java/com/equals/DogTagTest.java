package com.equals;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import static com.equals.DogTag.classFrom;
import static com.equals.TestUtility.*;
import static org.junit.Assert.*;

// Todo: Write test of cached hash in inclusion mode
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter", "UseOfClone", "AccessStaticViaInstance", "EqualsReplaceableByObjectsCall", "EqualsWhichDoesntCheckParameterClass", "unused"})
public class DogTagTest {
  private static final String CHARLIE_INT = "charlieInt";

  @Test
  public void testEquals() {
    DogTagTestBase baseTest5b75 = new DogTagTestBase(5, "bravo", 7, 5L);
    DogTagTestBase baseTest5bx5 = baseTest5b75.duplicate();
    baseTest5bx5.setCharlieInt(12);
    DogTagTestBase baseTest9b75 = new DogTagTestBase(90, "bravo", 7, 5L);
    DogTagTestBase baseTest5x75 = new DogTagTestBase(5, "bravissimo", 7, 5L);
    DogTagTestBase baseTest5b45 = new DogTagTestBase(5, "bravo", 44, 5L);
    DogTagTestBase baseTest5b7x = new DogTagTestBase(5, "bravo", 7, 17L);
    DogTagTestBase baseTestDupl = new DogTagTestBase(5, "bravo", 7, 5L);
    
    DogTag.Factory<DogTagTestBase> excludeCReflect = DogTag.create(classFrom(baseTest5b75), CHARLIE_INT)
        .build();
    DogTag.Factory<DogTagTestBase> excludeCLambda = DogTag.createByLambda(DogTagTestBase.class)
        .addSimple(DogTagTestBase::getAlphaInt)
        .addObject(DogTagTestBase::getBravoString)
        .addSimple(DogTagTestBase::getDeltaLong)
        .build();

    List<DogTag.Factory<DogTagTestBase>> dList = Arrays.asList(excludeCReflect, excludeCLambda);
    
    for (DogTag.Factory<DogTagTestBase> excludeC : dList){
      verifyMatches(excludeC, baseTest5b75, baseTest5bx5);
      verifyNoMatch(excludeC, baseTest5b75, baseTest9b75);
      verifyNoMatch(excludeC, baseTest5b75, baseTest5x75);
      verifyMatches(excludeC, baseTest5b75, baseTest5b45);
      verifyNoMatch(excludeC, baseTest5b75, baseTest5b7x);
      verifyNoMatch(excludeC, baseTest5bx5, baseTest9b75);
      verifyNoMatch(excludeC, baseTest5bx5, baseTest5x75);
      verifyMatches(excludeC, baseTest5bx5, baseTest5b45);
      verifyNoMatch(excludeC, baseTest5bx5, baseTest5b7x);
      verifyNoMatch(excludeC, baseTest9b75, baseTest5x75);
      verifyNoMatch(excludeC, baseTest9b75, baseTest5b45);
      verifyNoMatch(excludeC, baseTest9b75, baseTest5b7x);
      verifyNoMatch(excludeC, baseTest5x75, baseTest5b45);
      verifyNoMatch(excludeC, baseTest5x75, baseTest5b7x);
      verifyNoMatch(excludeC, baseTest5b45, baseTest5b7x);
      verifyMatches(excludeC, baseTest5b75, baseTestDupl);
    }

    DogTag.Factory<DogTagTestBase> includeBaseOnlyReflect = DogTag.create(classFrom(baseTest5b75)).build();
    DogTag.Factory<DogTagTestBase> includeBaseOnlyLambda = DogTag.createByLambda(DogTagTestBase.class)
        .addSimple(DogTagTestBase::getAlphaInt)
        .addSimple(DogTagTestBase::getDeltaLong)
        .addObject(DogTagTestBase::getBravoString)
        .addSimple(DogTagTestBase::getCharlieInt)
        .build();

    dList = Arrays.asList(includeBaseOnlyReflect, includeBaseOnlyLambda);
    for (DogTag.Factory<DogTagTestBase> includeBaseOnly: dList) {
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest5bx5);
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest5bx5);
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest9b75);
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest5x75);
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest5b45);
      verifyNoMatch(includeBaseOnly, baseTest5b75, baseTest5b7x);
      verifyNoMatch(includeBaseOnly, baseTest5bx5, baseTest9b75);
      verifyNoMatch(includeBaseOnly, baseTest5bx5, baseTest5x75);
      verifyNoMatch(includeBaseOnly, baseTest5bx5, baseTest5b45);
      verifyNoMatch(includeBaseOnly, baseTest5bx5, baseTest5b7x);
      verifyNoMatch(includeBaseOnly, baseTest9b75, baseTest5x75);
      verifyNoMatch(includeBaseOnly, baseTest9b75, baseTest5b45);
      verifyNoMatch(includeBaseOnly, baseTest9b75, baseTest5b7x);
      verifyNoMatch(includeBaseOnly, baseTest5x75, baseTest5b45);
      verifyNoMatch(includeBaseOnly, baseTest5x75, baseTest5b7x);
      verifyNoMatch(includeBaseOnly, baseTest5b45, baseTest5b7x);
      verifyMatches(includeBaseOnly, baseTest5b75, baseTestDupl);
    }

    DogTagTestMid midTest = new DogTagTestMid(5, "bravo", 7, 5L, "echo", 
        new Point2D.Double(14.2, 2.14), 44, (byte)12, 'I');
    DogTagTestMid midTest2 = midTest.duplicate();
    verifyMatches(includeBaseOnlyReflect, midTest, midTest2);
    verifyMatches(includeBaseOnlyLambda, midTest, midTest2);
    midTest2.setIndigoChar('J');
    midTest2.setHotelByte((byte) 99);
    midTest2.setGolfIntTr(77);
    midTest2.setFoxtrotPoint(new Point2D.Double(88.8, 22.2));
    midTest2.setEchoString("Could you repeat that?");
    verifyMatches(includeBaseOnlyReflect, midTest, midTest2); // should still match,
    verifyMatches(includeBaseOnlyLambda, midTest, midTest2); // should still match,
    
    DogTag.Factory<DogTagTestBase> includeAllButCReflect = DogTag.createByInclusion(classFrom(baseTest5b75),
            "alphaInt",
            "bravoString",
            "deltaLong"
        )
        .build();
    DogTag.Factory<DogTagTestBase> includeAllButCLambda = DogTag.createByLambda(DogTagTestBase.class)
        .addSimple(DogTagTestBase::getAlphaInt)
        .addObject(DogTagTestBase::getBravoString)
        .addSimple(DogTagTestBase::getDeltaLong)
        .build();
    dList = Arrays.asList(includeAllButCReflect, includeAllButCLambda);
    
    for (DogTag.Factory<DogTagTestBase> includeAllButC: dList) {
      verifyMatches(includeAllButC, baseTest5b75, baseTest5bx5);
      verifyNoMatch(includeAllButC, baseTest5b75, baseTest9b75);
      verifyNoMatch(includeAllButC, baseTest5b75, baseTest5x75);
      verifyMatches(includeAllButC, baseTest5b75, baseTest5b45);
      verifyNoMatch(includeAllButC, baseTest5b75, baseTest5b7x);
      verifyNoMatch(includeAllButC, baseTest5bx5, baseTest9b75);
      verifyNoMatch(includeAllButC, baseTest5bx5, baseTest5x75);
      verifyMatches(includeAllButC, baseTest5bx5, baseTest5b45);
      verifyNoMatch(includeAllButC, baseTest5bx5, baseTest5b7x);
      verifyNoMatch(includeAllButC, baseTest9b75, baseTest5x75);
      verifyNoMatch(includeAllButC, baseTest9b75, baseTest5b45);
      verifyNoMatch(includeAllButC, baseTest9b75, baseTest5b7x);
      verifyNoMatch(includeAllButC, baseTest5x75, baseTest5b45);
      verifyNoMatch(includeAllButC, baseTest5x75, baseTest5b7x);
      verifyNoMatch(includeAllButC, baseTest5b45, baseTest5b7x);
    }
  }

  @Test
  public void testTransient() {
    DogTagTestMid mid1 = new DogTagTestMid(12, "bravo", 3, 4L, "echo", new Point2D.Double(14.2, 2.14), 7, (byte)8, 'I');
    DogTagTestMid mid2 = mid1.duplicate();
    DogTag.Factory<DogTagTestMid> defaultFactory = DogTag.create(classFrom(mid1)).build(); // Tests may construct their own DogTags.
    mid2.setGolfIntTr(77); // transient value
    verifyMatches(defaultFactory, mid1, mid2);
    
    mid2.setFoxtrotPoint(new Point2D.Double(3.3, 4.4));
    verifyNoMatch(defaultFactory, mid1, mid2);

    DogTag.Factory<DogTagTestMid> FactoryWithTransients = DogTag.create(classFrom(mid1))
        .withTransients(true)
        .build();
    mid2.setFoxtrotPoint((Point2D) mid1.getFoxtrotPoint().clone()); // reset Point2D
    verifyNoMatch(FactoryWithTransients, mid1, mid2);
    mid2.setGolfIntTr(mid1.getGolfIntTr());
    verifyMatches(FactoryWithTransients, mid1, mid2);

    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();

    DogTag.Factory<DogTagTestTail> FactoryTail = DogTag.create(classFrom(tail1))
        .withReflectUpTo(DogTagTestMid.class)
        .withTransients(true)
        .build();
    tail1.setGolfIntTr(-10);
    tail2.setGolfIntTr(43);
    verifyNoMatch(FactoryTail, tail1, tail2);

    tail2.setGolfIntTr(-10); // tail2 now matches tail1 in this field
    tail2.setCharlieInt(1024); // Shouldn't affect equality. In super.super, but not in super.
    tail2.setDeltaLong(65537L*65537L);
    verifyMatches(FactoryTail, tail1, tail2);
  }
  
  @Test
  public void testFinalOnly() {
    DogTagTestBase b1b23 = new DogTagTestBase(1, "bravo", 2, 3L);
    DogTagTestBase b1bx4 = new DogTagTestBase(1, "bravo", 22, 4L);
    DogTagTestBase b2b23 = new DogTagTestBase(2, "bravo", 2, 3);
    DogTagTestBase b1Xx4 = new DogTagTestBase(1, "Boo!", 22, 4L);
    DogTagTestBase D1b23 = new DogTagTestBase(1, "bravo", 2, 3L);
    DogTagTestBase b1n23 = new DogTagTestBase(1, null, 2, 3L);
    DogTagTestBase D1n23 = new DogTagTestBase(1, null, 2, 3L);

    DogTag.Factory<DogTagTestBase> baseFactoryEx = DogTag.create(classFrom(b1b23), "charlieInt", "deltaLong") // include alpha, bravo
        .withCachedHash(true)
        .build();
    DogTag.Factory<DogTagTestBase> lambdaBaseFactory = DogTag.createByLambda(DogTagTestBase.class)
        .addSimple(DogTagTestBase::getAlphaInt)
        .addObject(DogTagTestBase::getBravoString)
        .build();

    List<DogTag.Factory<DogTagTestBase>> factories = Arrays.asList(baseFactoryEx, lambdaBaseFactory);

    for (DogTag.Factory<DogTagTestBase> baseFactory : factories) {
      verifyMatches(baseFactory, b1b23, b1bx4); // c, d differ
      verifyNoMatch(baseFactory, b1b23, b2b23); // a
      verifyNoMatch(baseFactory, b1b23, b1Xx4); // b, c, d
      verifyMatches(baseFactory, b1b23, D1b23); // -
      verifyNoMatch(baseFactory, b1b23, b1n23); // b
      verifyNoMatch(baseFactory, b1bx4, b2b23); // a, c, d
      verifyNoMatch(baseFactory, b1bx4, b1Xx4); // b
      verifyMatches(baseFactory, b1bx4, D1b23); // c, d   // transitivity test
      verifyNoMatch(baseFactory, b1bx4, b1n23); // b, c, d
      verifyNoMatch(baseFactory, b2b23, b1Xx4); // a, b, c, d
      verifyNoMatch(baseFactory, b2b23, D1b23); // a
      verifyNoMatch(baseFactory, b2b23, b1n23); // a, b
      verifyNoMatch(baseFactory, b1Xx4, D1b23); // b, c, d
      verifyNoMatch(baseFactory, b1Xx4, b1n23); // b, c, d
      verifyNoMatch(baseFactory, D1b23, b1n23); // b
      verifyMatches(baseFactory, b1n23, D1n23); // -
    }

    DogTag.Factory<DogTagTestBase> factoryBase2 = DogTag.create(classFrom(b1b23), "bravoString") // include a, c, d
        .build();
    lambdaBaseFactory = DogTag.createByLambda(DogTagTestBase.class)
        .addSimple(DogTagTestBase::getAlphaInt)
        .addSimple(DogTagTestBase::getCharlieInt)
        .addSimple(DogTagTestBase::getDeltaLong)
        .build();
    factories = Arrays.asList(factoryBase2, lambdaBaseFactory);

    for (DogTag.Factory<DogTagTestBase> factory2 : factories) {
      verifyNoMatch(factory2, b1b23, b1bx4);
      verifyNoMatch(factory2, b1b23, b2b23);
      verifyNoMatch(factory2, b1b23, b1Xx4);
      verifyMatches(factory2, b1b23, D1b23);
      verifyMatches(factory2, b1b23, b1n23);
      verifyNoMatch(factory2, b1bx4, b2b23);
      verifyMatches(factory2, b1bx4, b1Xx4);
      verifyNoMatch(factory2, b1bx4, D1b23);
      verifyNoMatch(factory2, b1bx4, b1n23);
      verifyNoMatch(factory2, b2b23, b1Xx4);
      verifyNoMatch(factory2, b2b23, D1b23);
      verifyNoMatch(factory2, b2b23, b1n23);
      verifyNoMatch(factory2, b1Xx4, D1b23);
      verifyNoMatch(factory2, b1Xx4, b1n23);
      verifyMatches(factory2, D1b23, b1n23);
    }

    DogTag.Factory<DogTagTestBase> factory3Base = DogTag.create(classFrom(b1b23), "alphaInt", "charlieInt", "deltaLong")
        .withCachedHash(true)
        .build();
    lambdaBaseFactory = DogTag.createByLambda(DogTagTestBase.class)
        .addObject(DogTagTestBase::getBravoString)
        .build();
    factories = Arrays.asList(factory3Base, lambdaBaseFactory);

    for (DogTag.Factory<DogTagTestBase> factory3 : factories) {
      verifyMatches(factory3, b1b23, b1bx4);
      verifyMatches(factory3, b1b23, b2b23);
      verifyNoMatch(factory3, b1b23, b1Xx4);
      verifyMatches(factory3, b1b23, D1b23);
      verifyNoMatch(factory3, b1b23, b1n23);
      verifyMatches(factory3, b1bx4, b2b23);    // transitivity test
      verifyNoMatch(factory3, b1bx4, b1Xx4);
      verifyMatches(factory3, b1bx4, D1b23);    // transitivity test
      verifyNoMatch(factory3, b1bx4, b1Xx4);
      verifyNoMatch(factory3, b2b23, b1n23);
      verifyMatches(factory3, b2b23, D1b23);    // transitivity test
      verifyNoMatch(factory3, b1Xx4, D1b23);
      verifyNoMatch(factory3, b1Xx4, b1n23);
      verifyNoMatch(factory3, D1b23, b1n23);
      verifyMatches(factory3, b1n23, D1n23);
    }
  }

  @Test
  public void testSuperClasses() {
    // The names reflect where the differences are.
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2_JK = new DogTagTestTail();
    DogTagTestTail tail3_JKL = new DogTagTestTail();
    DogTagTestTail tail4_P = new DogTagTestTail();
    DogTagTestTail tail5_V = new DogTagTestTail();
    DogTagTestTail tail6_L = new DogTagTestTail();
    DogTagTestTail tail7_P = new DogTagTestTail();
    DogTagTestTail mid_1_E = new DogTagTestTail();
    DogTagTestTail mid_2_F = new DogTagTestTail();
    DogTagTestTail base1_C = new DogTagTestTail();
    DogTagTestTail base2_D = new DogTagTestTail();
    
    // Suffix letters tell which fields are different from test1
    tail2_JK.setJulietBoolean(!tail1.isJulietBoolean());
    tail2_JK.setKiloShort((short) 999);
    tail3_JKL.setJulietBoolean(!tail1.isJulietBoolean());
    tail3_JKL.setKiloShort((short) 987);
    tail3_JKL.setLimaDouble(703.14);
    tail4_P.setPapaLongArray(new long[] {9L, 8L});
    tail5_V.setVictorDoubleArray(new double[] { 5.3, 4.9 });
    tail6_L.setLimaDouble(2.718281828);
    tail7_P.setPapaLongArray(null);
    mid_1_E.setEchoString("mid_1_E string");
    mid_2_F.setFoxtrotPoint(new Point2D.Double(98.7, 65.4));
    base1_C.setCharlieInt(7654);
    base2_D.setDeltaLong(96L);

    DogTag.Factory<DogTagTestTail> tailFactory = DogTag.create(classFrom(tail1), "kiloShort", "julietBoolean")
        .withReflectUpTo(DogTagTestBase.class)
        .build();
    DogTag.Factory<DogTagTestTail> lambdaFactory = DogTag.createByLambda(DogTagTestTail.class)
        .addSimple(DogTagTestTail::getAlphaInt)
        .addObject(DogTagTestTail::getBravoString)
        .addSimple(DogTagTestTail::getCharlieInt)
        .addSimple(DogTagTestTail::getDeltaLong)
        .addObject(DogTagTestTail::getEchoString)
        .addObject(DogTagTestTail::getFoxtrotPoint)
        .addSimple(DogTagTestTail::getGolfIntTr)
        .addSimple(DogTagTestTail::getHotelByte)
        .addSimple(DogTagTestTail::getIndigoChar)
        .addSimple(DogTagTestTail::getLimaDouble)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getPapaLongArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    List<DogTag.Factory<DogTagTestTail>> factories = Arrays.asList(tailFactory, lambdaFactory);

    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyMatches(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyNoMatch(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyNoMatch(factory, tail1, tail6_L);
      verifyNoMatch(factory, tail1, tail7_P);
      verifyNoMatch(factory, tail1, mid_1_E);
      verifyNoMatch(factory, tail1, mid_2_F);
      verifyNoMatch(factory, tail1, base1_C);
      verifyNoMatch(factory, tail1, base2_D);
    }

    DogTag.Factory<DogTagTestTail> tail2Factory = DogTag.create(classFrom(tail1), "limaDouble", "charlieInt").build();
    lambdaFactory = DogTag.createByLambda(classFrom(tail1))
        .addSimple(DogTagTestTail::getAlphaInt)
        .addObject(DogTagTestTail::getBravoString)
        .addSimple(DogTagTestTail::getKiloShort)
        .addSimple(DogTagTestTail::getDeltaLong)
        .addObject(DogTagTestTail::getEchoString)
        .addObject(DogTagTestTail::getFoxtrotPoint)
        .addSimple(DogTagTestTail::getGolfIntTr)
        .addSimple(DogTagTestTail::getHotelByte)
        .addSimple(DogTagTestTail::getIndigoChar)
        .addSimple(DogTagTestTail::isJulietBoolean)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getPapaLongArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    
    factories = Arrays.asList(tail2Factory, lambdaFactory);

    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyNoMatch(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyNoMatch(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyMatches(factory, tail1, tail6_L);
      verifyNoMatch(factory, tail1, tail7_P);
      verifyNoMatch(factory, tail1, mid_1_E);
      verifyNoMatch(factory, tail1, mid_2_F);
      verifyMatches(factory, tail1, base1_C);
      verifyNoMatch(factory, tail1, base2_D);
    }

    // -----

    DogTag.Factory<DogTagTestTail> factoryToObject = DogTag.create(classFrom(tail1), "kiloShort", "julietBoolean")
        .withReflectUpTo(DogTagTestTail.class)
        .build();
    lambdaFactory = DogTag.createByLambda(DogTagTestTail.class)
        .addSimple(DogTagTestTail::getLimaDouble)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getPapaLongArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    
    factories = Arrays.asList(factoryToObject, lambdaFactory);
    
    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyMatches(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyNoMatch(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyNoMatch(factory, tail1, tail6_L);
      verifyNoMatch(factory, tail1, tail7_P);
      verifyMatches(factory, tail1, mid_1_E);
      verifyMatches(factory, tail1, mid_2_F);
      verifyMatches(factory, tail1, base1_C);
      verifyMatches(factory, tail1, base2_D);
    }

    tail2Factory = DogTag.create(classFrom(tail1), "limaDouble", "papaLongArray")
        .withReflectUpTo(DogTagTestTail.class)
        .build();
    lambdaFactory = DogTag.createByLambda(classFrom(tail1))
        .addSimple(DogTagTestTail::isJulietBoolean)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();

    factories = Arrays.asList(tail2Factory, lambdaFactory);

    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyNoMatch(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyMatches(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyMatches(factory, tail1, tail6_L);
      verifyMatches(factory, tail1, tail7_P);
      verifyMatches(factory, tail1, mid_1_E);
      verifyMatches(factory, tail1, mid_2_F);
      verifyMatches(factory, tail1, base1_C);
      verifyMatches(factory, tail1, base2_D);
    }


    // -----

    tail2_JK.setIndigoChar('X');
    tail2_JK.setHotelByte((byte) 126);

    DogTag.Factory<DogTagTestTail> factoryToMid = DogTag.create(classFrom(tail1), "kiloShort", "julietBoolean", "hotelByte", "indigoChar")
        .withReflectUpTo(DogTagTestMid.class)
        .build();
    lambdaFactory = DogTag.createByLambda(DogTagTestTail.class)
        .addObject(DogTagTestTail::getEchoString)
        .addObject(DogTagTestTail::getFoxtrotPoint)
        .addSimple(DogTagTestTail::getGolfIntTr)
        .addSimple(DogTagTestTail::getLimaDouble)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getPapaLongArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    factories = Arrays.asList(factoryToMid, lambdaFactory);

    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyMatches(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyNoMatch(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyNoMatch(factory, tail1, tail6_L);
      verifyNoMatch(factory, tail1, mid_1_E);
      verifyNoMatch(factory, tail1, mid_2_F);
      verifyMatches(factory, tail1, base1_C);
      verifyMatches(factory, tail1, base2_D);
    }

    // -----

    DogTag.Factory<DogTagTestTail> factoryNoSuper = DogTag.create(classFrom(tail1), "kiloShort", "julietBoolean")
        .withReflectUpTo(DogTagTestTail.class)
        .build();
    lambdaFactory = DogTag.createByLambda(DogTagTestTail.class)
        .addSimple(DogTagTestTail::getLimaDouble)
        .addSimple(DogTagTestTail::getMikeFloat)
        .addArray(DogTagTestTail::getNovemberIntArray)
        .addArray(DogTagTestTail::getOperaStringArray)
        .addArray(DogTagTestTail::getPapaLongArray)
        .addArray(DogTagTestTail::getQuebecShortArray)
        .addArray(DogTagTestTail::getRomeoByteArray)
        .addArray(DogTagTestTail::getSierraCharArray)
        .addArray(DogTagTestTail::getTangoBooleanArray)
        .addArray(DogTagTestTail::getUniformFloatArray)
        .addArray(DogTagTestTail::getVictorDoubleArray)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    factories = Arrays.asList(factoryNoSuper, lambdaFactory);

    for (DogTag.Factory<DogTagTestTail> factory : factories) {
      verifyMatches(factory, tail1, tail2_JK);
      verifyNoMatch(factory, tail1, tail3_JKL);
      verifyNoMatch(factory, tail1, tail4_P);
      verifyNoMatch(factory, tail1, tail5_V);
      verifyNoMatch(factory, tail1, tail6_L);
      verifyMatches(factory, tail1, mid_1_E);
      verifyMatches(factory, tail1, mid_2_F);
      verifyMatches(factory, tail1, base1_C);
      verifyMatches(factory, tail1, base2_D);
    }
  }

  @Test
  public void testGoodExcludedFieldName() {
    DogTagTestBase base1 = new DogTagTestBase(5, "bravo", 6, 8L);
    DogTagTestBase base2 = base1.duplicate();
    base2.setCharlieInt(12);
    DogTag.Factory<DogTagTestBase> factory = DogTag.create(classFrom(base1), CHARLIE_INT)
        .build();

    verifyMatches(factory, base1, base2);
    base2.setDeltaLong(88L);
    verifyNoMatch(factory, base1, base2);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testBadExcludedFieldName() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag.create(classFrom(tail), CHARLIE_INT)
        .withReflectUpTo(DogTagTestTail.class)// CHARLIE_INT is a superclass method, but the superclass wasn't included.
        .build();
  }

  @Test(expected=IllegalArgumentException.class)
  public void testBadExcludedFieldName2() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag.create(classFrom(tail), "hotelByte")
        .withReflectUpTo(DogTagTestTail.class)
        .build();
  }

  @Test
  public void testNoStatic() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag<DogTagTestTail> dogTag = DogTag.from(tail);
    int hashCode = dogTag.hashCode();

    // Change the static value and get a new hashCode.
    tail.setStaticInt(tail.getStaticInt()*500);
    int revisedHashCode = dogTag.hashCode();

    // If it's not using the static value, the hashCode won't change when the static value changes.
    assertEquals(hashCode, revisedHashCode);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadFieldName() {
    try {
      DogTagTestTail tail = new DogTagTestTail();
      // Include fields from all three classes
      DogTag.create(classFrom(tail), "kiloShort", "indigoChar", "alphaInt", "missing")
          .withReflectUpTo(Object.class)
          .build();
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("missing"));
      assertTrue(e.getMessage().contains("E7:"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadFieldName2() {
    try {
      DogTagTestTail tail = new DogTagTestTail();
      // Include fields from all three classes
      DogTag.create(classFrom(tail), "kiloShort", "mikeFloat", "julietBoolean", "missing")
          .withReflectUpTo(DogTagTestTail.class) // Differs here from previous test
          .build();
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("missing"));
      assertTrue(e.getMessage().contains("E6:"));
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
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(classFrom(tail1), "novemberIntArray", "operaStringArray")
        .build();
    for (float f1: notNumbers) {
      for (float f2: notNumbers) {
        tail1.setMikeFloat(f1);
        tail2.setMikeFloat(f2);
        verifyMatches(factory, tail1, tail2);
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
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(classFrom(tail1), "novemberIntArray", "operaStringArray")
        .build();
    for (double f1: notNumbers) {
      for (double f2: notNumbers) {
        tail1.setLimaDouble(f1);
        tail2.setLimaDouble(f2);
        verifyMatches(factory, tail1, tail2);
      }
    }
  }

  @Test
  public void testIntArrays() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(classFrom(tail1)).build();

    verifyMatches(factory, tail1, tail2);

    tail1.setNovemberIntArray(new int[] {1, 1, 2, 3, 5});
    verifyNoMatch(factory, tail1, tail2);
  }

  @Test
  public void testNull() {
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = new DogTagTestTail();
    tail2.setFoxtrotPoint(null);

    DogTag.Factory<DogTagTestTail> factory = DogTag.create(classFrom(tail1))
        .withReflectUpTo(DogTagTestBase.class)
        .build();
    verifyNoMatch(factory, tail1, tail2);

    factory.doHashCodeInternal(tail2); // just make sure we can get a hashCode with that null member.
  }

  @Test
  public void testArrays() {
    DogTagTestTail tail = new DogTagTestTail();
    DogTag.Factory<DogTagTestTail> factory = DogTag.create(classFrom(tail)).build();

    // ints
    DogTagTestTail tail1 = new DogTagTestTail();
    DogTagTestTail tail2 = tail1.duplicate();
    verifyMatches(factory, tail1, tail2);
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
    verifyMatches(factory, tail1, tail2);
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

  @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsWithItself", "EqualsBetweenInconvertibleTypes"})
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

  @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsWithItself", "EqualsBetweenInconvertibleTypes"})
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

  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  @Test(expected = IllegalArgumentException.class)
  public void testBadDogTag() {
    // Test for case where dogTag is static. This would not work, because each instance has to have its own DogTag,
    // which holds a copy of the instance. So we throw an exception when the static dogTag is constructed. We test that
    // exception here.

    try {
      new ClassWithBadDogTag();
    } catch (ExceptionInInitializerError e) {
      assertThat(e.getCause().getMessage(), StringContains.containsString("E8:"));
      throw (RuntimeException) e.getCause();
    }
  }

  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  @Test(expected = IllegalArgumentException.class)
  public void testBadFactory() {
    // Test for case where Factory is not static. This would seriously slow down the equals and hashCode methods, since
    // the reflection would happen whenever the class is instantiated instead of once when it's loaded.  So we throw an
    // exception when the dogTag is constructed. We test that exception here.

    new ClassWithBadFactory();
  }

  ////////////////////

  @SuppressWarnings({"unused",
      "WeakerAccess", "PublicConstructorInNonPublicClass"})
  private static class DogTagTestBase {
    private final int alphaInt;
    private final String bravoString;
    private int charlieInt;
    private long deltaLong;
    private static int staticInt = 5;
    private static final DogTag.Factory<?> factory = null; // prevent superfluous test failure

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
    private static final DogTag.Factory<?> factory = null; // prevent superfluous test failure

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
  private static final class DogTagTestTail extends DogTagTestMid {

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

    private static final DogTag.Factory<?> factory = null; // prevent superfluous test failure
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
    private Object[] whiskeyObjectArray = { new Point2D.Float(1.2f, 2.4f), "string", new HashSet<>() };

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
      DogTagTestTail tail = new DogTagTestTail(getAlphaInt(), getBravoString(), getCharlieInt(), getDeltaLong(), 
          getEchoString(), getFoxtrotPoint(), getGolfIntTr(), getHotelByte(), getIndigoChar(), isJulietBoolean(), 
          getKiloShort(), getLimaDouble(), getMikeFloat(), getNovemberIntArray(), getOperaStringArray());
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
  
  @SuppressWarnings("unused")
  private static final class ParadigmTest {
    private final String alphaString;
    private final int bravoInt;
    private final float charlieFloat;
    
    ParadigmTest(String alpha, int bravo, float charlie) {
      alphaString = alpha;
      bravoInt = bravo;
      charlieFloat = charlie;
    }

    private final DogTag<ParadigmTest> dogTag = DogTag.from(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
      return dogTag.equals(obj);
    }

    public String getAlphaString() {
      return alphaString;
    }

    public int getBravoInt() {
      return bravoInt;
    }

    public float getCharlieFloat() {
      return charlieFloat;
    }
  }
  
  private static final class ClassWithBadDogTag {
    private static final ClassWithBadDogTag badInstance = new ClassWithBadDogTag();
    private static final DogTag.DogTagExclusionBuilder<ClassWithBadDogTag> builder = DogTag.create(classFrom(badInstance));
    private static final DogTag.Factory<ClassWithBadDogTag> dogTagFactory = builder.build(); // Should throw IllegalArgumentError
    @SuppressWarnings("unused")
    private static final DogTag<ClassWithBadDogTag> dogTag = dogTagFactory.tag(badInstance); // STATIC!
  }

  private static final class ClassWithBadFactory {
    private final DogTag.Factory<ClassWithBadFactory> factory = DogTag.create(ClassWithBadFactory.class).build();
    @SuppressWarnings("unused")
    private final DogTag<ClassWithBadFactory> dogTag = factory.tag(this);
  }

  @Test
  public void testDeepArrays() {
    int[] iArray1a = { 1, 2, 3 };
    int[] iArray_2 = { 1, 2, 4 };
    int[] iArray1b = { 1, 2, 3 };
    int[] iArray_3 = { 1, 2, 3, 4 };
    int[] iArrayNl = null;
    String[] sArray1a = { "Whiskey", "Tango", "Foxtrot" };
    String[] sArray2_ = { "Whiskey", "Tango", "Hotel" };
    String[] sArray1b = { "Whiskey", "Tango", "Foxtrot" };
    String[] sArray3_ = { "Whiskey", "Tango", "Foxtrot", "Echo" };
    String[] sArrayNl = { "Whiskey", null, "Foxtrot" };
    String[] sArrayN2 = null;

    DogTagTestTail i1a = new DogTagTestTail();
    DogTagTestTail i2_ = new DogTagTestTail();
    DogTagTestTail i1b = new DogTagTestTail();
    DogTagTestTail i3_ = new DogTagTestTail();
    DogTagTestTail iNl = new DogTagTestTail();
    DogTagTestTail a1a = new DogTagTestTail();
    DogTagTestTail a2_ = new DogTagTestTail();
    DogTagTestTail a1b = new DogTagTestTail();
    DogTagTestTail a3_ = new DogTagTestTail();
    DogTagTestTail aNl = new DogTagTestTail();
    DogTagTestTail aN2 = new DogTagTestTail();


    i1a.setWhiskeyObjectArray(of("alpha", iArray1a, "bravo"));
    i2_.setWhiskeyObjectArray(of("alpha", iArray_2, "bravo"));
    i3_.setWhiskeyObjectArray(of("alpha", iArray_3, "bravo"));
    i1b.setWhiskeyObjectArray(of("alpha", iArray1b, "bravo"));
    iNl.setWhiskeyObjectArray(of("alpha", iArrayNl, "bravo"));
    a1a.setWhiskeyObjectArray(of("alpha", sArray1a, "bravo"));
    a2_.setWhiskeyObjectArray(of("alpha", sArray2_, "bravo"));
    a1b.setWhiskeyObjectArray(of("alpha", sArray1b, "bravo"));
    a3_.setWhiskeyObjectArray(of("alpha", sArray3_, "bravo"));
    aNl.setWhiskeyObjectArray(of("alpha", sArrayNl, "bravo"));
    aN2.setWhiskeyObjectArray(of("alpha", sArrayN2, "bravo"));

    DogTag.Factory<DogTagTestTail> deepFactory = DogTag.createByInclusion(DogTagTestTail.class, "whiskeyObjectArray", "alphaInt")
        .build();
    DogTag.Factory<DogTagTestTail> deepExFactory = DogTag.create(DogTagTestTail.class).build();
    DogTag.Factory<DogTagTestTail> lambdaFactory = DogTag.createByLambda(DogTagTestTail.class)
        .addSimple(DogTagTestTail::getAlphaInt)
        .addArray(DogTagTestTail::getWhiskeyObjectArray)
        .build();
    
    List<DogTag.Factory<DogTagTestTail>> factories = Arrays.asList(deepFactory, deepExFactory, lambdaFactory);
    for (DogTag.Factory<DogTagTestTail> factory: factories) {
      verifyNoMatch(factory, i1a, i2_);
      verifyMatches(factory, i1a, i1b);
      verifyNoMatch(factory, i1a, i3_);
      verifyNoMatch(factory, i1a, a1a);
      verifyNoMatch(factory, i1a, iNl);
      verifyNoMatch(factory, i1a, aNl);
      verifyNoMatch(factory, a1a, a2_);
      verifyMatches(factory, a1a, a1b);
      verifyNoMatch(factory, a1a, a3_);
      verifyNoMatch(factory, a1a, aNl);
      verifyNoMatch(factory, a1a, aN2);
    }
  }
  
  @Test
  public void testTwoDArray() {
    int[][] i2ArrayA = {{1, 2, 3}, {2, 3, 4}, {3, 4, 5}};
    int[][] i2ArrayB = {{1, 2, 3}, {2, 4, 9}, {3, 4, 5}};
    int[][] i2ArrayC = {{1, 2, 3}, {2, 3, 4}, {3, 4, 5}};

    TwoDArray a = new TwoDArray(i2ArrayA);
    TwoDArray b = new TwoDArray(i2ArrayB);
    TwoDArray c = new TwoDArray(i2ArrayC);

    DogTag<TwoDArray> dogTag = a.getDogTag();
    DogTag.Factory<TwoDArray> factory = dogTag.getFactory();
    
    verifyNoMatch(factory, a, b);
    verifyMatches(factory, a, c);
    verifyNoMatch(factory, b, c);
    
    DogTag.Factory<TwoDArray> lambdaFactory = DogTag.createByLambda(TwoDArray.class)
        .addArray(TwoDArray::getAlphaIntArray)
        .build();

    verifyNoMatch(lambdaFactory, a, b);
    verifyMatches(lambdaFactory, a, c);
    verifyNoMatch(lambdaFactory, b, c);
  }
  
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  private static final class TwoDArray {
    private final int[][] alphaIntArray;
    private static final DogTag.Factory<?> factory = null; // prevent superfluous test failure

    private final DogTag<TwoDArray> dogTag = DogTag.from(this);
    
    TwoDArray(int[][] alpha) {
      this.alphaIntArray = alpha;
    }

    public int[][] getAlphaIntArray() {
      return alphaIntArray;
    }

    // Normally, you wouldn't do this, but I need it for testing.
    public DogTag<TwoDArray> getDogTag() {
      return dogTag;
    }
  }
  
  private static Object[] of(Object... data) { return data; }
}
