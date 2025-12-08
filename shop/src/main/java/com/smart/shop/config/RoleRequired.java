package com.smart.shop.config;

import com.smart.shop.enums.UserRole;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RoleRequired {
    UserRole[] value() default {};
}
