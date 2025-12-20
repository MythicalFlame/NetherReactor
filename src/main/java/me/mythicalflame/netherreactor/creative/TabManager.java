package me.mythicalflame.netherreactor.creative;

import me.mythicalflame.netherreactor.events.ModRegisterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public final class TabManager implements Listener
{
    private static final ArrayList<CreativeTab> tabs = new ArrayList<>();
    /**
     * Gets a sorted list of all creative tabs.
     *
     * @return The ArrayList of all CreativeTabs, sorted first by priority and then by alphabetical order (namespace and then ID).
     */
    @Nonnull
    public static ArrayList<CreativeTab> getCreativeTabs()
    {
        return tabs;
    }

    @EventHandler
    public void onRegister(ModRegisterEvent event)
    {
        tabs.addAll(event.getMod().getCreativeTabs());
        //Sort first by priority, then namespace, then ID.
        tabs.sort((a, b) -> {
            if (a.getPriority() < b.getPriority())
            {
                return -1;
            }
            else if (a.getPriority() > b.getPriority())
            {
                return 1;
            }

            int compare = a.getKey().namespace().compareTo(b.getKey().namespace());
            if (compare == 0)
            {
                int compare2 = a.getKey().value().compareTo(b.getKey().value());
                return Integer.compare(compare2, 0);
            }
            else if (compare < 0)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        });
    }
}
