package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static com.equals.TestUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/24/19
 * <p>Time: 12:39 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DogTagAnnotationTest {
  // TODO: test that useTransients is ignored in Inclusion mode.
  // TODO: test that @DogTagExclude can override withTransients(true)
  // TODO: Test createByInclusion(X, String...)

  @Test
  public void testExclusionAnnotations() {
    TestClassOne t123 = new TestClassOne(1, 2, 3);
    TestClassOne t153 = new TestClassOne(1, 5, 3); // bravo differs from t123
    TestClassOne t124 = new TestClassOne(1, 2, 4); // charlie differs
    TestClassOne t623 = new TestClassOne(6, 2, 3); // alpha differs
    TestClassOne t199 = new TestClassOne(1, 9, 9); // bravo & charlie differ
    TestClassOne t453 = new TestClassOne(4, 5, 3); // alpha & bravo differ

    DogTag.Factory<TestClassOne> dogTag1Reflect = DogTag.create(t123).buildFactory(); // Exclude bravo
    DogTag.Factory<TestClassOne> dogTag1Lambda = DogTag.createByLambda(TestClassOne.class)
        .add(TestClassOne::getAlpha)
        .add(TestClassOne::getCharlie)
        .buildFactory();
    List<DogTag.Factory<TestClassOne>> fList = Arrays.asList(dogTag1Reflect, dogTag1Lambda);

    for(DogTag.Factory<TestClassOne> dogTag1 : fList) {
      verifyMatch__(dogTag1, t123, t153); // a, c match
      verifyNoMatch(dogTag1, t123, t124); // a, b
      verifyNoMatch(dogTag1, t123, t623); // b, c
      verifyNoMatch(dogTag1, t123, t199); // a
      verifyNoMatch(dogTag1, t123, t453); // c
      verifyNoMatch(dogTag1, t124, t623); // b
      verifyNoMatch(dogTag1, t124, t453); // nothing
    }

    DogTag.Factory<TestClassOne> dt2Reflect = DogTag.create(t123, "charlie") // exclude bravo, charlie
        .buildFactory();
    DogTag.Factory<TestClassOne> dt2Lambda = DogTag.createByLambda(TestClassOne.class)
        .add(TestClassOne::getAlpha)
        .buildFactory();
    fList = Arrays.asList(dt2Reflect, dt2Lambda);

    for (DogTag.Factory<TestClassOne> dt2 : fList) {
      verifyMatch__(dt2, t123, t153); // a, c match
      verifyMatch__(dt2, t123, t124); // a, b
      verifyNoMatch(dt2, t123, t623); // b, c
      verifyMatch__(dt2, t153, t124); // a
      verifyNoMatch(dt2, t124, t623); // b
      verifyNoMatch(dt2, t153, t623); // c
      verifyNoMatch(dt2, t124, t453); // nothing
    }

    DogTag.Factory<TestClassOne> dogTagTestExcludeReflect = DogTag.create(t123) // exclude alpha, bravo
        .withExclusionAnnotation(TestExclude.class)
        .buildFactory();
    DogTag.Factory<TestClassOne> dogTagTestExcludeLambda = DogTag.createByLambda(TestClassOne.class)
        .add(TestClassOne::getCharlie)
        .buildFactory();
    fList = Arrays.asList(dogTagTestExcludeReflect, dogTagTestExcludeLambda);
    for (DogTag.Factory<TestClassOne> dogTagTestExclude : fList) {
      verifyMatch__(dogTagTestExclude, t123, t153); // a, c match
      verifyNoMatch(dogTagTestExclude, t123, t124); // a, b
      verifyMatch__(dogTagTestExclude, t123, t623); // b, c
      verifyNoMatch(dogTagTestExclude, t123, t199); // a
      verifyNoMatch(dogTagTestExclude, t124, t623); // b
      verifyMatch__(dogTagTestExclude, t153, t623); // c
    }


    TestClassTwo t123456 = new TestClassTwo(1, 2, 3, 4, 5, 6);
    TestClassTwo t173456 = new TestClassTwo(1, 7, 3, 4, 5, 9); // b differs
    TestClassTwo t723456 = new TestClassTwo(7, 2, 3, 4, 5, 6); // a
    TestClassTwo t127456 = new TestClassTwo(1, 2, 7, 4, 5, 6); // c
    TestClassTwo t123756 = new TestClassTwo(1, 2, 3, 7, 5, 6); // d
    TestClassTwo t123476 = new TestClassTwo(1, 2, 3, 4, 7, 6); // e
    TestClassTwo t183459 = new TestClassTwo(1, 8, 3, 4, 5, 9); // b, f
    TestClassTwo t123757 = new TestClassTwo(1, 2, 3, 7, 5, 7); // d, f
    TestClassTwo t183759 = new TestClassTwo(1, 8, 3, 7, 5, 9); // b, d, f
    TestClassTwo t888458 = new TestClassTwo(8, 8, 8, 4, 5, 8); // a, b, c, f
    TestClassTwo t993959 = new TestClassTwo(9, 9, 3, 9, 5, 9); // a, b, d, f
    TestClassTwo t120406 = new TestClassTwo(1, 2, 0, 4, 0, 6); // c, e
    TestClassTwo t123886 = new TestClassTwo(1, 2, 3, 8, 8, 6); // d, e
    TestClassTwo t828886 = new TestClassTwo(8, 2, 8, 8, 8, 6); // a, c, d, e

    // These two factories use the same fields
    DogTag.Factory<TestClassTwo> dt3Reflect = DogTag.create(t123456) // Exclude bravo, foxtrot, Include a, c, d, e
        .buildFactory();
    DogTag.Factory<TestClassTwo> dt3Lambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassTwo::getAlpha)
        .add(TestClassTwo::getCharlie)
        .add(TestClassTwo::getDelta)
        .add(TestClassTwo::getEcho)
        .buildFactory();
    List<DogTag.Factory<TestClassTwo>> fList2 = Arrays.asList(dt3Reflect, dt3Lambda);
    for (DogTag.Factory<TestClassTwo> dt3: fList2) {
      verifyMatch__(dt3, t123456, t173456); // b differs
      verifyNoMatch(dt3, t123456, t723456); // a
      verifyNoMatch(dt3, t123456, t127456); // c
      verifyNoMatch(dt3, t123456, t123756); // d
      verifyNoMatch(dt3, t123456, t123476); // e
      verifyNoMatch(dt3, t123456, t183759); // b, d, f
      verifyMatch__(dt3, t123456, t183459); // b, f
      verifyNoMatch(dt3, t123456, t123756); // d, f
      verifyNoMatch(dt3, t123456, t888458); // a, b, c, f
      verifyNoMatch(dt3, t123456, t993959); // a, b, d, f
      verifyNoMatch(dt3, t123456, t120406); // c, e
      verifyNoMatch(dt3, t123456, t123886); // d, e
      verifyNoMatch(dt3, t123456, t828886); // a, c, d, e
    }

    DogTag.Factory<TestClassTwo> dogTagNoSuperReflect = DogTag.create(t123456)  // Exclude alpha, bravo, charlie, foxtrot, Include d, e
        .withReflectUpTo(TestClassTwo.class)
        .buildFactory();
    DogTag.Factory<TestClassTwo> dogTagNoSuperLambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassTwo::getDelta)
        .add(TestClassTwo::getEcho)
        .buildFactory();
    fList2 = Arrays.asList(dogTagNoSuperReflect, dogTagNoSuperLambda);
    for (DogTag.Factory<TestClassTwo> dogTagNoSuper: fList2) {
      verifyMatch__(dogTagNoSuper, t123456, t173456); // b
      verifyMatch__(dogTagNoSuper, t123456, t723456); // a
      verifyMatch__(dogTagNoSuper, t123456, t127456); // c
      verifyNoMatch(dogTagNoSuper, t123456, t123756); // d
      verifyNoMatch(dogTagNoSuper, t123456, t123476); // e
      verifyNoMatch(dogTagNoSuper, t123456, t183759); // b, d, f
      verifyMatch__(dogTagNoSuper, t123456, t183459); // b, f
      verifyNoMatch(dogTagNoSuper, t123456, t123757); // d, f
      verifyMatch__(dogTagNoSuper, t123456, t888458); // a, b, c, f
      verifyNoMatch(dogTagNoSuper, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagNoSuper, t123456, t120406); // c, e
      verifyNoMatch(dogTagNoSuper, t123456, t123886); // d, e
      verifyNoMatch(dogTagNoSuper, t123456, t828886); // a, c, d, e
    }

    DogTag.Factory<TestClassTwo> dogTagNoSuperTEReflect = DogTag.create(t123456) // Exclude alpha, bravo, charlie, delta, foxtrot
        .withReflectUpTo(TestClassTwo.class)
        .withExclusionAnnotation(TestExclude.class)
        .buildFactory();
    DogTag.Factory<TestClassTwo> dogTagNoSuperTELambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassTwo::getEcho)
        .buildFactory();
    fList2 = Arrays.asList(dogTagNoSuperTEReflect, dogTagNoSuperTELambda);
    for (DogTag.Factory<TestClassTwo> dogTagNoSuperTE : fList2) {
      verifyMatch__(dogTagNoSuperTE, t123456, t173456); // b
      verifyMatch__(dogTagNoSuperTE, t123456, t723456); // a
      verifyMatch__(dogTagNoSuperTE, t123456, t127456); // c
      verifyMatch__(dogTagNoSuperTE, t123456, t123756); // d
      verifyNoMatch(dogTagNoSuperTE, t123456, t123476); // e
      verifyMatch__(dogTagNoSuperTE, t123456, t183759); // b, d, f
      verifyMatch__(dogTagNoSuperTE, t123456, t183459); // b, f
      verifyMatch__(dogTagNoSuperTE, t123456, t123757); // d, f
      verifyMatch__(dogTagNoSuperTE, t123456, t888458); // a, b, c, f
      verifyMatch__(dogTagNoSuperTE, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagNoSuperTE, t123456, t120406); // c, e
      verifyNoMatch(dogTagNoSuperTE, t123456, t123886); // d, e
      verifyNoMatch(dogTagNoSuperTE, t123456, t828886); // a, c, d, e
    }

    DogTag.Factory<TestClassTwo> dogTagTestExclude2Reflect = DogTag.create(t123456) // Exclude alpha, bravo, delta, foxtrot
        .withExclusionAnnotation(TestExclude.class)
        .buildFactory();
    DogTag.Factory<TestClassTwo> dogTagTestExclude2Lambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassOne::getCharlie)
        .add(TestClassTwo::getEcho)
        .buildFactory();

    fList2 = Arrays.asList(dogTagTestExclude2Reflect, dogTagTestExclude2Lambda);
    for (DogTag.Factory<TestClassTwo> dogTagTestExclude2 : fList2) {
      verifyMatch__(dogTagTestExclude2, t123456, t173456); // b, f 
      verifyMatch__(dogTagTestExclude2, t123456, t723456); // a
      verifyNoMatch(dogTagTestExclude2, t123456, t127456); // c
      verifyMatch__(dogTagTestExclude2, t123456, t123756); // d
      verifyNoMatch(dogTagTestExclude2, t123456, t123476); // e
      verifyMatch__(dogTagTestExclude2, t123456, t183759); // b, d, f
      verifyMatch__(dogTagTestExclude2, t123456, t183459); // b, f
      verifyMatch__(dogTagTestExclude2, t123456, t123757); // d, f
      verifyNoMatch(dogTagTestExclude2, t123456, t888458); // a, b, c, f
      verifyMatch__(dogTagTestExclude2, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagTestExclude2, t123456, t120406); // c, e
      verifyNoMatch(dogTagTestExclude2, t123456, t123886); // d, e
      verifyNoMatch(dogTagTestExclude2, t123456, t828886); // a, c, d, e
    }

    // Test Inclusion Mode.

    DogTag.Factory<TestClassOne> dogTagIncludeReflect = DogTag.createByInclusion(t123) // include charlie
        .buildFactory();
    DogTag.Factory<TestClassOne> dogTagIncludeLambda = DogTag.createByLambda(TestClassOne.class)
        .add(TestClassOne::getCharlie)
        .buildFactory();
    fList = Arrays.asList(dogTagIncludeReflect, dogTagIncludeLambda);
    for (DogTag.Factory<TestClassOne> dogTagInclude : fList) {
      verifyMatch__(dogTagInclude, t123, t153); // a, c match
      verifyNoMatch(dogTagInclude, t123, t124); // a, b
      verifyMatch__(dogTagInclude, t123, t623); // b, c
      verifyNoMatch(dogTagInclude, t153, t124); // a
      verifyNoMatch(dogTagInclude, t124, t623); // b
      verifyMatch__(dogTagInclude, t153, t623); // c
    }

    DogTag.Factory<TestClassOne> dogTagIncludeAnnReflect = DogTag.createByInclusion(t123) // include alpha, charlie
        .withInclusionAnnotation(TestInclude.class)
        .buildFactory();
    DogTag.Factory<TestClassOne> dogTagIncludeAnnLambda = DogTag.createByLambda(TestClassOne.class)
        .add(TestClassOne::getAlpha)
        .add(TestClassOne::getCharlie)
        .buildFactory();
    fList = Arrays.asList(dogTagIncludeAnnReflect, dogTagIncludeAnnLambda);
    for (DogTag.Factory<TestClassOne> dogTagIncludeAnn : fList) {
      verifyMatch__(dogTagIncludeAnn, t123, t153); // a, c match
      verifyNoMatch(dogTagIncludeAnn, t123, t124); // a, b
      verifyNoMatch(dogTagIncludeAnn, t123, t623); // b, c
      verifyNoMatch(dogTagIncludeAnn, t153, t124); // a
      verifyNoMatch(dogTagIncludeAnn, t124, t623); // b
      verifyNoMatch(dogTagIncludeAnn, t153, t623); // c
    }

    DogTag.Factory<TestClassTwo> dTIsReflect = DogTag.createByInclusion(t123456) // include charlie, echo
        .buildFactory();
    DogTag.Factory<TestClassTwo> dTIsLambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassTwo::getCharlie)
        .add(TestClassTwo::getEcho)
        .buildFactory();
    
    fList2 = Arrays.asList(dTIsReflect, dTIsLambda);
    for (DogTag.Factory<TestClassTwo> dTIs : fList2) {
      verifyMatch__(dTIs, t123456, t173456); // b, f differ
      verifyMatch__(dTIs, t123456, t723456); // a
      verifyNoMatch(dTIs, t123456, t127456); // c
      verifyMatch__(dTIs, t123456, t123756); // d
      verifyNoMatch(dTIs, t123456, t123476); // e
      verifyMatch__(dTIs, t123456, t183759); // b, d, f
      verifyMatch__(dTIs, t123456, t183459); // b, f
      verifyMatch__(dTIs, t123456, t123756); // d, f
      verifyNoMatch(dTIs, t123456, t888458); // a, b, c, f
      verifyMatch__(dTIs, t123456, t993959); // a, b, d, f
      verifyNoMatch(dTIs, t123456, t120406); // c, e
      verifyNoMatch(dTIs, t123456, t123886); // d, e
      verifyNoMatch(dTIs, t123456, t828886); // a, c, d, e
    }


    DogTag.Factory<TestClassTwo> dTI2sReflect = DogTag.createByInclusion(t123456) // include alpha, charlie, delta, echo
        .withInclusionAnnotation(TestInclude.class)
        .buildFactory();
    DogTag.Factory<TestClassTwo> dTI2sLambda = DogTag.createByLambda(TestClassTwo.class)
        .add(TestClassTwo::getAlpha)
        .add(TestClassTwo::getCharlie)
        .add(TestClassTwo::getDelta)
        .add(TestClassTwo::getEcho)
        .buildFactory();
    fList2 = Arrays.asList(dTI2sReflect, dTI2sLambda);
    
    for (DogTag.Factory<TestClassTwo> dTI2s: fList2) {
      verifyMatch__(dTI2s, t123456, t173456); // b, f differ
      verifyNoMatch(dTI2s, t123456, t723456); // a
      verifyNoMatch(dTI2s, t123456, t127456); // c
      verifyNoMatch(dTI2s, t123456, t123756); // d
      verifyNoMatch(dTI2s, t123456, t123476); // e
      verifyNoMatch(dTI2s, t123456, t183759); // b, d, f
      verifyMatch__(dTI2s, t123456, t183459); // b, f
      verifyNoMatch(dTI2s, t123456, t123756); // d, f
      verifyNoMatch(dTI2s, t123456, t888458); // a, b, c, f
      verifyNoMatch(dTI2s, t123456, t993959); // a, b, d, f
      verifyNoMatch(dTI2s, t123456, t120406); // c, e
      verifyNoMatch(dTI2s, t123456, t123886); // d, e
      verifyNoMatch(dTI2s, t123456, t828886); // a, c, d, e
    }
  }

  @SuppressWarnings("PackageVisibleField")
  private static class TestClassOne {
    @TestInclude
    @TestExclude // this is safe because I can't use exclusion mode and inclusion mode at the same time
    int alpha;
    @DogTagExclude
    int bravo;
    @DogTagInclude
    int charlie;

    TestClassOne(int alpha, int bravo, int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private final DogTag<TestClassOne> dogTag = DogTag.from(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.equals(that);
    }

    public int getAlpha() {
      return alpha;
    }

    public int getBravo() {
      return bravo;
    }

    public int getCharlie() {
      return charlie;
    }
  }

  @SuppressWarnings("unused")
  private static class TestClassTwo extends TestClassOne {
    @TestExclude
    @TestInclude
    private final int delta;
    @DogTagInclude
    private final int echo;
    @DogTagExclude
    private int foxTrot;

    TestClassTwo(int alpha, int bravo, int charlie, int delta, int echo, int foxTrot) {
      super(alpha, bravo, charlie);
      this.delta = delta;
      this.echo = echo;
      this.foxTrot = foxTrot;
    }

    public int getDelta() {
      return delta;
    }

    public int getEcho() {
      return echo;
    }

    public int getFoxTrot() {
      return foxTrot;
    }

    public void setFoxTrot(final int foxTrot) {
      this.foxTrot = foxTrot;
    }

    private final DogTag<TestClassTwo> dogTag = DogTag.create(this)
        .withFinalFieldsOnly(true) // This should implicitly set withCachedHash to true 
        .buildFactory()
        .tag(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.equals(that);
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface TestExclude {
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface TestInclude {
  }

  
  static class OrderedClass {
    @DogTagInclude(order = 30)
    private int third;
    
    @DogTagInclude
    private int fourth;
    
    @DogTagInclude(order = 10)
    private int first;
    
    @DogTagInclude(order = DogTag.DEFAULT_ORDER_VALUE + 10)
    private int last;
    
    @DogTagInclude(order = 20)
    private int second;
    
//    private static final DogTag.Factory<OrderedClass> factory = DogTag.createByInclusion()
  }
}
