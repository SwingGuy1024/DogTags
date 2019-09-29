package com.equals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate fields to mark them for inclusion from use by DogTags, when using inclusion mode.
 * @see com.equals.DogTag.DogTagInclusionBuilder
 * @see DogTag#createByInclusion(Class, String...) 
 * @see DogTag#createByInclusion(Class, Class) 
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/22/19
 * <p>Time: 9:53 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DogTagInclude {
}
