package com.suspend.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    String column() default "";
    String referencedColumn() default "";
    String table() default "";
    String mappedBy() default "";
}
