package net.insomniakitten.pylon.ref;

import net.insomniakitten.pylon.annotation.rift.Listener;
import net.insomniakitten.pylon.annotation.rift.Mod;

import javax.annotation.Nonnull;
import java.util.Locale;

// TODO Move into Rift's API to reduce potential code duplication

/**
 * Constants that represent that physical sides of the game runtime
 * Utilized for loading conditions of {@link Mod} and {@link Listener}
 * @author InsomniaKitten
 * @since 0.1.0
 */
public enum Side {
    /**
     * Represents both physical sides
     */
    BOTH,

    /**
     * Represents the physical client side
     */
    CLIENT,

    /**
     * Represents the physical server side
     */
    SERVER;

    /**
     * Determines if this Side represents both physical sides
     * @return True if {@link Side#BOTH} equals this Side
     * @since 0.1.0
     */
    public boolean isBoth() {
        return Side.BOTH == this;
    }

    /**
     * Determines if this Side represents the physical client side
     * @return True if {@link Side#CLIENT} equals this Side
     * @since 0.1.0
     */
    public boolean isClient() {
        return Side.CLIENT == this;
    }

    /**
     * Determines if this Side represents the physical server side
     * @return True if {@link Side#SERVER} equals this Side
     * @since 0.1.0
     */
    public boolean isServer() {
        return Side.SERVER == this;
    }

    /**
     * Determines if this Side is equivalent to the given {@code side}
     * @return True if {@link Side#BOTH} or {@code side} equals this Side
     * @since 0.1.0
     */
    public boolean isEquivalentTo(final Side side) {
        return Side.BOTH == this || side == this;
    }

    /**
     * A user friendly name for this Side, formed by the lower-cased
     * value of the constant's field name.
     * @since 0.1.0
     */
    @Nonnull
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
