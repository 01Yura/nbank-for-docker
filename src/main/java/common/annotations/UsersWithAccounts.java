package common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UsersWithAccounts {
    int users() default 1;
    int accountsPerUser() default 1;
    int auth() default 1; // which user to auth as (1-based)
    boolean uiAuth() default true; // if false, do not open Selenide for auth
}


