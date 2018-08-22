package net.insomniakitten.pylon.annotation.rift;

import net.insomniakitten.pylon.ref.Side;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares this source as a Mod, for the annotation processor to parse into
 * a JSON object and export to `riftmod.json`. Source can only ever contain
 * a single Mod annotation. If more than one are present at compile time,
 * an exception will be thrown by the annotation processor. This annotation
 * can either be applied to a class, or a package-info file.
 * @author InsomniaKitten
 * @since 0.1.0
 */
@Documented
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {
    /**
     * The unique identifier of this Mod
     * @since 0.1.0
     */
    String id();

    /**
     * The friendly name of this Mod
     * @since 0.1.0
     */
    String name() default "";

    /**
     * The semantic version of this Mod
     * @since 0.1.0
     */
    String version();

    /**
     * The physical side this Mod should be loaded on
     * By default, it will be loaded on both physical sides
     * @since 0.1.0
     * @deprecated Currently not implemented by Rift
     */
    @Deprecated
    Side side() default Side.BOTH;

    /**
     * The authors of this Mod
     * @since 0.1.0
     */
    String[] authors() default {};
}
