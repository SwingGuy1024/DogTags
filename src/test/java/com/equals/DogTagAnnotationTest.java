package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static com.equals.DogTag.classFrom;
import static com.equals.TestUtility.verifyMatches;
import static com.equals.TestUtility.verifyNoMatch;

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
    final TestClassOne t123 = new TestClassOne(1, 2, 3);
    final TestClassOne t153 = new TestClassOne(1, 5, 3); // bravo differs from t123
    final TestClassOne t124 = new TestClassOne(1, 2, 4); // charlie differs
    final TestClassOne t623 = new TestClassOne(6, 2, 3); // alpha differs
    final TestClassOne t199 = new TestClassOne(1, 9, 9); // bravo & charlie differ
    final TestClassOne t453 = new TestClassOne(4, 5, 3); // alpha & bravo differ

    final DogTag.Factory<TestClassOne> dogTag1Reflect = DogTag.startWithAll(classFrom(t123)).build(); // Exclude bravo
    final DogTag.Factory<TestClassOne> dogTag1Lambda = DogTag.startEmpty(TestClassOne.class)
        .addSimple(TestClassOne::getAlpha)
        .addSimple(TestClassOne::getCharlie)
        .build();
    List<DogTag.Factory<TestClassOne>> fList = Arrays.asList(dogTag1Reflect, dogTag1Lambda);

    for(final DogTag.Factory<TestClassOne> dogTag1 : fList) {
      verifyMatches(dogTag1, t123, t153); // a, c match
      verifyNoMatch(dogTag1, t123, t124); // a, b
      verifyNoMatch(dogTag1, t123, t623); // b, c
      verifyNoMatch(dogTag1, t123, t199); // a
      verifyNoMatch(dogTag1, t123, t453); // c
      verifyNoMatch(dogTag1, t124, t623); // b
      verifyNoMatch(dogTag1, t124, t453); // nothing
    }

    final DogTag.Factory<TestClassOne> dt2Reflect = DogTag.startWithAll(classFrom(t123))
        .excludeFields("charlie") // exclude bravo, charlie
        .build();
    final DogTag.Factory<TestClassOne> dt2Lambda = DogTag.startEmpty(TestClassOne.class)
        .addSimple(TestClassOne::getAlpha)
        .build();
    fList = Arrays.asList(dt2Reflect, dt2Lambda);

    for (final DogTag.Factory<TestClassOne> dt2 : fList) {
      verifyMatches(dt2, t123, t153); // a, c match
      verifyMatches(dt2, t123, t124); // a, b
      verifyNoMatch(dt2, t123, t623); // b, c
      verifyMatches(dt2, t153, t124); // a
      verifyNoMatch(dt2, t124, t623); // b
      verifyNoMatch(dt2, t153, t623); // c
      verifyNoMatch(dt2, t124, t453); // nothing
    }

    final DogTag.Factory<TestClassOne> dogTagTestExcludeReflect = DogTag.startWithAll(classFrom(t123)) // exclude alpha, bravo
        .withExclusionAnnotation(TestExclude.class)
        .build();
    final DogTag.Factory<TestClassOne> dogTagTestExcludeLambda = DogTag.startEmpty(TestClassOne.class)
        .addSimple(TestClassOne::getCharlie)
        .build();
    fList = Arrays.asList(dogTagTestExcludeReflect, dogTagTestExcludeLambda);
    for (final DogTag.Factory<TestClassOne> dogTagTestExclude : fList) {
      verifyMatches(dogTagTestExclude, t123, t153); // a, c match
      verifyNoMatch(dogTagTestExclude, t123, t124); // a, b
      verifyMatches(dogTagTestExclude, t123, t623); // b, c
      verifyNoMatch(dogTagTestExclude, t123, t199); // a
      verifyNoMatch(dogTagTestExclude, t124, t623); // b
      verifyMatches(dogTagTestExclude, t153, t623); // c
      verifyNoMatch(dogTagTestExclude, t124, t453); // nothing
    }


    final TestClassTwo t123456 = new TestClassTwo(1, 2, 3, 4, 5, 6);
    final TestClassTwo t173456 = new TestClassTwo(1, 7, 3, 4, 5, 6); // b differs
    final TestClassTwo t723456 = new TestClassTwo(7, 2, 3, 4, 5, 6); // a
    final TestClassTwo t127456 = new TestClassTwo(1, 2, 7, 4, 5, 6); // c
    final TestClassTwo t123756 = new TestClassTwo(1, 2, 3, 7, 5, 6); // d
    final TestClassTwo t123476 = new TestClassTwo(1, 2, 3, 4, 7, 6); // e
    final TestClassTwo t183459 = new TestClassTwo(1, 8, 3, 4, 5, 9); // b, f
    final TestClassTwo t123757 = new TestClassTwo(1, 2, 3, 7, 5, 7); // d, f
    final TestClassTwo t183759 = new TestClassTwo(1, 8, 3, 7, 5, 9); // b, d, f
    final TestClassTwo t888458 = new TestClassTwo(8, 8, 8, 4, 5, 8); // a, b, c, f
    final TestClassTwo t993959 = new TestClassTwo(9, 9, 3, 9, 5, 9); // a, b, d, f
    final TestClassTwo t120406 = new TestClassTwo(1, 2, 0, 4, 0, 6); // c, e
    final TestClassTwo t123886 = new TestClassTwo(1, 2, 3, 8, 8, 6); // d, e
    final TestClassTwo t828886 = new TestClassTwo(8, 2, 8, 8, 8, 6); // a, c, d, e
    final TestClassTwo t765432 = new TestClassTwo(7, 6, 5, 4, 3, 2); // a, b, c, d, e, f

    // These two factories use the same fields
    final DogTag.Factory<TestClassTwo> dt3Reflect = DogTag.startWithAll(classFrom(t123456)) // Exclude bravo, foxtrot, Include a, c, d, e
        .build();
    final DogTag.Factory<TestClassTwo> dt3Lambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassTwo::getAlpha)
        .addSimple(TestClassTwo::getCharlie)
        .addSimple(TestClassTwo::getDelta)
        .addSimple(TestClassTwo::getEcho)
        .build();
    List<DogTag.Factory<TestClassTwo>> fList2 = Arrays.asList(dt3Reflect, dt3Lambda);
    for (final DogTag.Factory<TestClassTwo> dt3: fList2) {
      verifyMatches(dt3, t123456, t173456); // b differs
      verifyNoMatch(dt3, t123456, t723456); // a
      verifyNoMatch(dt3, t123456, t127456); // c
      verifyNoMatch(dt3, t123456, t123756); // d
      verifyNoMatch(dt3, t123456, t123476); // e
      verifyNoMatch(dt3, t123456, t183759); // b, d, f
      verifyMatches(dt3, t123456, t183459); // b, f
      verifyNoMatch(dt3, t123456, t123756); // d, f
      verifyNoMatch(dt3, t123456, t888458); // a, b, c, f
      verifyNoMatch(dt3, t123456, t993959); // a, b, d, f
      verifyNoMatch(dt3, t123456, t120406); // c, e
      verifyNoMatch(dt3, t123456, t123886); // d, e
      verifyNoMatch(dt3, t123456, t828886); // a, c, d, e
      verifyNoMatch(dt3, t123456, t765432); // a, b, c, d, e, f
    }

    final DogTag.Factory<TestClassTwo> dogTagNoSuperReflect = DogTag.startWithAll(classFrom(t123456))  // Exclude alpha, bravo, charlie, foxtrot, Include d, e
        .withReflectUpTo(TestClassTwo.class)
        .build();
    final DogTag.Factory<TestClassTwo> dogTagNoSuperLambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassTwo::getDelta)
        .addSimple(TestClassTwo::getEcho)
        .build();
    fList2 = Arrays.asList(dogTagNoSuperReflect, dogTagNoSuperLambda);
    for (final DogTag.Factory<TestClassTwo> dogTagNoSuper: fList2) {
      verifyMatches(dogTagNoSuper, t123456, t173456); // b differs
      verifyMatches(dogTagNoSuper, t123456, t723456); // a
      verifyMatches(dogTagNoSuper, t123456, t127456); // c
      verifyNoMatch(dogTagNoSuper, t123456, t123756); // d
      verifyNoMatch(dogTagNoSuper, t123456, t123476); // e
      verifyNoMatch(dogTagNoSuper, t123456, t183759); // b, d, f
      verifyMatches(dogTagNoSuper, t123456, t183459); // b, f
      verifyNoMatch(dogTagNoSuper, t123456, t123757); // d, f
      verifyMatches(dogTagNoSuper, t123456, t888458); // a, b, c, f
      verifyNoMatch(dogTagNoSuper, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagNoSuper, t123456, t120406); // c, e
      verifyNoMatch(dogTagNoSuper, t123456, t123886); // d, e
      verifyNoMatch(dogTagNoSuper, t123456, t828886); // a, c, d, e
      verifyNoMatch(dogTagNoSuper, t123456, t765432); // a, b, c, d, e, f
    }

    final DogTag.Factory<TestClassTwo> dogTagNoSuperTEReflect = DogTag.startWithAll(classFrom(t123456)) // Exclude alpha, bravo, charlie, delta, foxtrot
        .withReflectUpTo(TestClassTwo.class)
        .withExclusionAnnotation(TestExclude.class)
        .build();
    final DogTag.Factory<TestClassTwo> dogTagNoSuperTELambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassTwo::getEcho)
        .build();
    fList2 = Arrays.asList(dogTagNoSuperTEReflect, dogTagNoSuperTELambda);
    for (final DogTag.Factory<TestClassTwo> dogTagNoSuperTE : fList2) {
      verifyMatches(dogTagNoSuperTE, t123456, t173456); // b differs
      verifyMatches(dogTagNoSuperTE, t123456, t723456); // a
      verifyMatches(dogTagNoSuperTE, t123456, t127456); // c
      verifyMatches(dogTagNoSuperTE, t123456, t123756); // d
      verifyNoMatch(dogTagNoSuperTE, t123456, t123476); // e
      verifyMatches(dogTagNoSuperTE, t123456, t183759); // b, d, f
      verifyMatches(dogTagNoSuperTE, t123456, t183459); // b, f
      verifyMatches(dogTagNoSuperTE, t123456, t123757); // d, f
      verifyMatches(dogTagNoSuperTE, t123456, t888458); // a, b, c, f
      verifyMatches(dogTagNoSuperTE, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagNoSuperTE, t123456, t120406); // c, e
      verifyNoMatch(dogTagNoSuperTE, t123456, t123886); // d, e
      verifyNoMatch(dogTagNoSuperTE, t123456, t828886); // a, c, d, e
      verifyNoMatch(dogTagNoSuperTE, t123456, t765432); // a, b, c, d, e, f
    }

    final DogTag.Factory<TestClassTwo> dogTagTestExclude2Reflect = DogTag.startWithAll(classFrom(t123456)) // Exclude alpha, bravo, delta, foxtrot
        .withExclusionAnnotation(TestExclude.class)
        .build();
    final DogTag.Factory<TestClassTwo> dogTagTestExclude2Lambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassOne::getCharlie)
        .addSimple(TestClassTwo::getEcho)
        .build();

    fList2 = Arrays.asList(dogTagTestExclude2Reflect, dogTagTestExclude2Lambda);
    for (final DogTag.Factory<TestClassTwo> dogTagTestExclude2 : fList2) {
      verifyMatches(dogTagTestExclude2, t123456, t173456); // b, f  differ
      verifyMatches(dogTagTestExclude2, t123456, t723456); // a
      verifyNoMatch(dogTagTestExclude2, t123456, t127456); // c
      verifyMatches(dogTagTestExclude2, t123456, t123756); // d
      verifyNoMatch(dogTagTestExclude2, t123456, t123476); // e
      verifyMatches(dogTagTestExclude2, t123456, t183759); // b, d, f
      verifyMatches(dogTagTestExclude2, t123456, t183459); // b, f
      verifyMatches(dogTagTestExclude2, t123456, t123757); // d, f
      verifyNoMatch(dogTagTestExclude2, t123456, t888458); // a, b, c, f
      verifyMatches(dogTagTestExclude2, t123456, t993959); // a, b, d, f
      verifyNoMatch(dogTagTestExclude2, t123456, t120406); // c, e
      verifyNoMatch(dogTagTestExclude2, t123456, t123886); // d, e
      verifyNoMatch(dogTagTestExclude2, t123456, t828886); // a, c, d, e
      verifyNoMatch(dogTagTestExclude2, t123456, t765432); // a, b, c, d, e, f
    }

    // Test Inclusion Mode.

    final DogTag.Factory<TestClassOne> dogTagIncludeCharlie = DogTag.startWithAll(classFrom(t123)) /* include charlie*/
        .excludeFields("alpha", "bravo")
        .build();
    final DogTag.Factory<TestClassOne> dogTagIncludeLambda = DogTag.startEmpty(TestClassOne.class)
        .addSimple(TestClassOne::getCharlie)
        .build();
    fList = Arrays.asList(dogTagIncludeCharlie, dogTagIncludeLambda);
    for (final DogTag.Factory<TestClassOne> dogTagInclude : fList) {
      verifyMatches(dogTagInclude, t123, t153); // a, c match
      verifyNoMatch(dogTagInclude, t123, t124); // a, b
      verifyMatches(dogTagInclude, t123, t623); // b, c
      verifyNoMatch(dogTagInclude, t153, t124); // a
      verifyNoMatch(dogTagInclude, t124, t623); // b
      verifyMatches(dogTagInclude, t153, t623); // c
      verifyNoMatch(dogTagInclude, t124, t453); // nothing
    }

    final DogTag.Factory<TestClassOne> dogTagIncludeAnnReflect = DogTag.startWithAll(classFrom(t123)) // include alpha, charlie
//        .withInclusionAnnotation(TestInclude.class)
        .excludeFields("bravo")
        .build();
    final DogTag.Factory<TestClassOne> dogTagIncludeAnnLambda = DogTag.startEmpty(TestClassOne.class)
        .addSimple(TestClassOne::getAlpha)
        .addSimple(TestClassOne::getCharlie)
        .build();
    fList = Arrays.asList(dogTagIncludeAnnReflect, dogTagIncludeAnnLambda);
    for (final DogTag.Factory<TestClassOne> dogTagIncludeAnn : fList) {
      verifyMatches(dogTagIncludeAnn, t123, t153); // a, c match
      verifyNoMatch(dogTagIncludeAnn, t123, t124); // a, b
      verifyNoMatch(dogTagIncludeAnn, t123, t623); // b, c
      verifyNoMatch(dogTagIncludeAnn, t153, t124); // a
      verifyNoMatch(dogTagIncludeAnn, t124, t623); // b
      verifyNoMatch(dogTagIncludeAnn, t153, t623); // c
      verifyNoMatch(dogTagIncludeAnn, t124, t453); // nothing
    }

    final DogTag.Factory<TestClassTwo> dTIsReflect = DogTag.startWithAll(classFrom(t123456)) // include charlie, echo
        .excludeFields("alpha", "bravo", "delta", "foxTrot")
        .build();
    final DogTag.Factory<TestClassTwo> dTIsLambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassTwo::getCharlie)
        .addSimple(TestClassTwo::getEcho)
        .build();

    fList2 = Arrays.asList(dTIsReflect, dTIsLambda);
    for (final DogTag.Factory<TestClassTwo> dTIs : fList2) {
      verifyMatches(dTIs, t123456, t173456); // b differs
      verifyMatches(dTIs, t123456, t723456); // a
      verifyNoMatch(dTIs, t123456, t127456); // c
      verifyMatches(dTIs, t123456, t123756); // d
      verifyNoMatch(dTIs, t123456, t123476); // e
      verifyMatches(dTIs, t123456, t183759); // b, d, f
      verifyMatches(dTIs, t123456, t183459); // b, f
      verifyMatches(dTIs, t123456, t123756); // d, f
      verifyNoMatch(dTIs, t123456, t888458); // a, b, c, f
      verifyMatches(dTIs, t123456, t993959); // a, b, d, f
      verifyNoMatch(dTIs, t123456, t120406); // c, e
      verifyNoMatch(dTIs, t123456, t123886); // d, e
      verifyNoMatch(dTIs, t123456, t828886); // a, c, d, e
      verifyNoMatch(dTIs, t123456, t765432); // a, b, c, d, e, f
    }

    final DogTag.Factory<TestClassTwo> dTI2sReflect = DogTag.startWithAll(classFrom(t123456)) // include alpha, charlie, delta, echo
//        .withInclusionAnnotation(TestInclude.class)
        .excludeFields("bravo", "foxTrot")
        .build();
    final DogTag.Factory<TestClassTwo> dTI2sLambda = DogTag.startEmpty(TestClassTwo.class)
        .addSimple(TestClassTwo::getAlpha)
        .addSimple(TestClassTwo::getCharlie)
        .addSimple(TestClassTwo::getDelta)
        .addSimple(TestClassTwo::getEcho)
        .build();
    fList2 = Arrays.asList(dTI2sReflect, dTI2sLambda);

    for (final DogTag.Factory<TestClassTwo> dTI2s: fList2) {
      verifyMatches(dTI2s, t123456, t173456); // b, f differ
      verifyNoMatch(dTI2s, t123456, t723456); // a
      verifyNoMatch(dTI2s, t123456, t127456); // c
      verifyNoMatch(dTI2s, t123456, t123756); // d
      verifyNoMatch(dTI2s, t123456, t123476); // e
      verifyNoMatch(dTI2s, t123456, t183759); // b, d, f
      verifyMatches(dTI2s, t123456, t183459); // b, f
      verifyNoMatch(dTI2s, t123456, t123756); // d, f
      verifyNoMatch(dTI2s, t123456, t888458); // a, b, c, f
      verifyNoMatch(dTI2s, t123456, t993959); // a, b, d, f
      verifyNoMatch(dTI2s, t123456, t120406); // c, e
      verifyNoMatch(dTI2s, t123456, t123886); // d, e
      verifyNoMatch(dTI2s, t123456, t828886); // a, c, d, e
      verifyNoMatch(dTI2s, t123456, t765432); // a, b, c, d, e, f
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

    TestClassOne(final int alpha, final int bravo, final int charlie) {
      this.alpha = alpha;
      this.bravo = bravo;
      this.charlie = charlie;
    }

    private static final DogTag.Factory<TestClassOne> factory = DogTag.startWithAll(TestClassOne.class)
        .excludeFields("alpha", "bravo").build();
    private final DogTag<TestClassOne> dogTag = factory.tag(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      System.out.println("TestClassOne.doEquals()");
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

  private static class TestClassTwo extends TestClassOne {
    @TestExclude
    @TestInclude
    private final int delta; // this is safe because I can't use exclusion mode and inclusion mode at the same time
    @DogTagInclude
    private final int echo;
    @DogTagExclude
    private int foxTrot;
    private static final DogTag.Factory<?> unused = null; // prevent superfluous test failure

    TestClassTwo(final int alpha, final int bravo, final int charlie, final int delta, final int echo, final int foxTrot) {
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

    private final DogTag<TestClassTwo> dogTag = DogTag.startWithAll(classFrom(this))
        .withCachedHash(true)
        .build()
        .tag(this);

    @Override
    public int hashCode() {
      return dogTag.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object that) {
      System.out.println("TestClassTwo.toEquals()");
      return dogTag.equals(that);
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface TestExclude { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface TestInclude { }
}
