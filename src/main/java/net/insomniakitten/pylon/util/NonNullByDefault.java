package net.insomniakitten.pylon.util;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation can be applied to a package, class or field to indicate that
 * all declared elements are non-null by default, unless annotated otherwise.
 *
 * @author InsomniaKitten
 * @see ParametersAreNonnullByDefault
 */
@Documented
@Nonnull
@TypeQualifierDefault(
  {
    ElementType.FIELD,
    ElementType.LOCAL_VARIABLE,
    ElementType.METHOD,
    ElementType.PARAMETER
  }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNullByDefault {}
