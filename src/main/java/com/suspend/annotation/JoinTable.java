package com.suspend.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinTable {

    String name() default "";

    JoinColumn[] joinColumns() default {};

    JoinColumn[] inverseJoinColumns() default {};

}
