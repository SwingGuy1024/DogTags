package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;

import static com.equals.TestUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/24/19
 * <p>Time: 12:39 AM
 *
 * @author Miguel Mu\u00f1oz
 * @noinspection HardCodedStringLiteral
 */
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

    DogTag<TestClassOne> dogTag1 = DogTag.from(TestClassOne.class); // Exclude bravo
    verifyMatch__(dogTag1, t123, t153); // a, c match
    verifyNoMatch(dogTag1, t123, t124); // a, b
    verifyNoMatch(dogTag1, t123, t623); // b, c
    verifyNoMatch(dogTag1, t123, t199); // a
    verifyNoMatch(dogTag1, t123, t453); // c

    DogTag<TestClassOne> dt2 = DogTag.create(TestClassOne.class, "charlie") // exclude bravo, charlie
        .build();

    verifyMatch__(dt2, t123, t153); // a, c match
    verifyMatch__(dt2, t123, t124); // a, b
    verifyNoMatch(dt2, t123, t623); // b, c
    verifyMatch__(dt2, t153, t124); // a
    verifyNoMatch(dt2, t124, t623); // b
    verifyNoMatch(dt2, t153, t623); // c

    DogTag<TestClassOne> dogTagTestExclude = DogTag.create(TestClassOne.class, TestExclude.class) // exclude alpha, bravo
        .build();
    verifyMatch__(dogTagTestExclude, t123, t153); // a, c match
    verifyNoMatch(dogTagTestExclude, t123, t124); // a, b
    verifyMatch__(dogTagTestExclude, t123, t623); // b, c
    verifyNoMatch(dogTagTestExclude, t123, t199); // a
    verifyNoMatch(dogTagTestExclude, t124, t623); // b
    verifyMatch__(dogTagTestExclude, t153, t623); // c


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

    DogTag<TestClassTwo> dt3 = DogTag.create(TestClassTwo.class) // Exclude bravo, foxtrot, Include a, c, d, e
        .withReflectUpTo(TestClassOne.class)
        .build();
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

    DogTag<TestClassTwo> dogTagNoSuper = DogTag.from(TestClassTwo.class); // Exclude alpha, bravo, charlie, foxtrot, Include d, e
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

    DogTag<TestClassTwo> dogTagNoSuperTE = DogTag.create(TestClassTwo.class, TestExclude.class) // Exclude alpha, brave, charlie, delta, foxtrot
        .build();
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

    DogTag<TestClassTwo> dogTagTestExclude2 = DogTag.create(TestClassTwo.class, TestExclude.class) // Exclude alpha, bravo, delta, foxtrot
        .withReflectUpTo(TestClassOne.class)
        .build();

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

    // Test Inclusion Mode.
    
    DogTag<TestClassOne> dogTagInclude = DogTag.createByInclusion(TestClassOne.class) // include charlie
        .build();
    verifyMatch__(dogTagInclude, t123, t153); // a, c match
    verifyNoMatch(dogTagInclude, t123, t124); // a, b
    verifyMatch__(dogTagInclude, t123, t623); // b, c
    verifyNoMatch(dogTagInclude, t153, t124); // a
    verifyNoMatch(dogTagInclude, t124, t623); // b
    verifyMatch__(dogTagInclude, t153, t623); // c

    DogTag<TestClassOne> dogTagIncludeAnn = DogTag.createByInclusion(TestClassOne.class, TestInclude.class) // include alpha, charlie
        .build();
    verifyMatch__(dogTagIncludeAnn, t123, t153); // a, c match
    verifyNoMatch(dogTagIncludeAnn, t123, t124); // a, b
    verifyNoMatch(dogTagIncludeAnn, t123, t623); // b, c
    verifyNoMatch(dogTagIncludeAnn, t153, t124); // a
    verifyNoMatch(dogTagIncludeAnn, t124, t623); // b
    verifyNoMatch(dogTagIncludeAnn, t153, t623); // c

    DogTag<TestClassTwo> dTI = DogTag.createByInclusion(TestClassTwo.class) // Include echo
        .build();
    verifyMatch__(dTI, t123456, t173456); // b, f differ
    verifyMatch__(dTI, t123456, t723456); // a
    verifyMatch__(dTI, t123456, t127456); // c
    verifyMatch__(dTI, t123456, t123756); // d
    verifyNoMatch(dTI, t123456, t123476); // e
    verifyMatch__(dTI, t123456, t183759); // b, d, f
    verifyMatch__(dTI, t123456, t183459); // b, f
    verifyMatch__(dTI, t123456, t123756); // d, f
    verifyMatch__(dTI, t123456, t888458); // a, b, c, f
    verifyMatch__(dTI, t123456, t993959); // a, b, d, f
    verifyNoMatch(dTI, t123456, t120406); // c, e
    verifyNoMatch(dTI, t123456, t123886); // d, e
    verifyNoMatch(dTI, t123456, t828886); // a, c, d, e

    DogTag<TestClassTwo> dTIs = DogTag.createByInclusion(TestClassTwo.class) // include charlie, echo
        .withReflectUpTo(TestClassOne.class)
        .build();
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

    DogTag<TestClassTwo> dTI2 = DogTag.createByInclusion(TestClassTwo.class, TestInclude.class) // include delta, echo
        .build();
    verifyMatch__(dTI2, t123456, t173456); // b, f differ
    verifyMatch__(dTI2, t123456, t723456); // a
    verifyMatch__(dTI2, t123456, t127456); // c
    verifyNoMatch(dTI2, t123456, t123756); // d
    verifyNoMatch(dTI2, t123456, t123476); // e
    verifyNoMatch(dTI2, t123456, t183759); // b, d, f
    verifyMatch__(dTI2, t123456, t183459); // b, f
    verifyNoMatch(dTI2, t123456, t123756); // d, f
    verifyMatch__(dTI2, t123456, t888458); // a, b, c, f
    verifyNoMatch(dTI2, t123456, t993959); // a, b, d, f
    verifyNoMatch(dTI2, t123456, t120406); // c, e
    verifyNoMatch(dTI2, t123456, t123886); // d, e
    verifyNoMatch(dTI2, t123456, t828886); // a, c, d, e

    DogTag<TestClassTwo> dTI2s = DogTag.createByInclusion(TestClassTwo.class, TestInclude.class) // include alpha, charlie, delta, echo
        .withReflectUpTo(TestClassOne.class)
        .build();
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

    private static final DogTag<TestClassOne> dogTag = DogTag.from(TestClassOne.class);

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(this, that);
    }
  }

  /**
   * @noinspection unused
   */
  @SuppressWarnings("PackageVisibleField")
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

    private static final DogTag<TestClassTwo> dogTag = DogTag.create(TestClassTwo.class)
        .withFinalFieldsOnly(true) // This should implicitly set withCachedHash to true 
        .build();

    private final CachedHash cachedHash = dogTag.makeCachedHash();

    @Override
    public int hashCode() {
      return dogTag.doHashCode(this, cachedHash);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
      return dogTag.doEqualsTest(this, that);
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

}
