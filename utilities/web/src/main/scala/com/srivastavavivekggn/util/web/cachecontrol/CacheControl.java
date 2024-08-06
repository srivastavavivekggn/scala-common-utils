package com.srivastavavivekggn.scala.util.web.cachecontrol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl {

    /**
     * The cache-control policies to apply to the response.
     *
     * @see CachePolicy
     */
    String[] policy() default { "no-cache, no-store, max-age=0, must-revalidate" };

    /**
     *  The maximum amount of time, in seconds, that this content will be considered fresh.
     */
    int maxAge() default 0;

    /**
     * The maximum amount of time, in seconds, that this content will be considered fresh
     * only for shared caches (e.g., proxy) caches.
     */
    int sharedMaxAge() default -1;
}
