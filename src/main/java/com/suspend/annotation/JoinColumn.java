package com.suspend.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {
    String name() default "";
    String referencedColumnName() default "";
    String table() default "";
    String mappedBy() default "";
}
