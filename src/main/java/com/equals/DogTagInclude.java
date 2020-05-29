package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate fields to mark them for inclusion from use by DogTags, when using inclusion mode. This also lets you specify an order for
 * each field. Fields will be tested in the order specified. The values used for order need not be sequential. You may want to use
 * values like 10, 20, 30, and so on, to make it easier to change the order without changing every number. The Order defaults to 1000. This
 * way, if you use order values of 10, 20, 30, and so on, any unspecified orders will be grouped together at the end. So if you only need
 * to specify the field to test first, just set its order to something lower than the default.
 * @see DogTag.DogTagInclusionBuilder
 * @see DogTag#createByInclusion(Object, String...)
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/22/19
 * <p>Time: 9:53 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DogTagInclude {
  int DEFAULT_ORDER_VALUE =1000;
  int order() default DEFAULT_ORDER_VALUE;
}
