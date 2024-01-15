package com.suspend.annotation;

import java.lang.annotation.*;

/**
 * Annotation for excluding classes from scanning inside Suspend.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Exclude {
}
