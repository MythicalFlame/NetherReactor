package me.mythicalflame.netherreactor.events;

import me.mythicalflame.netherreactor.content.Mod;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * An event called when a mod is registered.
 */
public final class ModRegisterEvent extends Event
{
    /**
     * The list of handlers for the event.
     */
    @Nonnull
    private static final HandlerList HANDLER_LIST = new HandlerList();
    /**
     * The mod being registered.
     */
    @Nonnull
    private final Mod mod;

    /**
     * Creates a ModRegisterEvent. This is not to be used by third parties, and is only intended for use within the NetherReactorModLoader itself.
     *
     * @param mod The mod being registered.
     */
    public ModRegisterEvent(@Nonnull Mod mod)
    {
        this.mod = mod;
    }

    /**
     * Gets the mod being registered.
     *
     * @return The mod associated with this event.
     */
    @Nonnull
    public Mod getMod()
    {
        return mod;
    }

    /**
     * Gets the list of handlers for the event.
     *
     * @return The HandlerList of the event.
     */
    public static @Nonnull HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

    /**
     * Gets the list of handlers for the event.
     *
     * @return The HandlerList of the event.
     */
    @Override
    public @Nonnull HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }
}
