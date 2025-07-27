package me.mythicalflame.netherreactor;

import me.mythicalflame.netherreactor.commands.CommandNetherReactor;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.Version;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public final class NetherReactorModLoader extends JavaPlugin
{
    private static final Version[] compatibleVersions = {new Version(0, 8, 0, "beta")};
    private static NetherReactorModLoader plugin;
    private static Logger logger;
    private static final ArrayList<Mod> registeredMods = new ArrayList<>();
    private static NamespacedKey contentKey;

    @Override
    public void onEnable()
    {
        plugin = this;
        logger = getLogger();
        contentKey = new NamespacedKey(this, "content");

        //Set up configuration
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Enable commands
        getCommand("netherreactor").setExecutor(new CommandNetherReactor());

        logger.info("Finished starting up!");
    }

    /**
     * Gets the list of registered mods.
     *
     * @return An ArrayList of all registered Mods.
     */
    public static ArrayList<Mod> getRegisteredMods()
    {
        return registeredMods;
    }

    /**
     * Gets the content key of the mod loader.
     *
     * @return The NamespacedKey that this plugin uses to mark whether an object is custom.
     */
    public static NamespacedKey getContentKey()
    {
        return contentKey;
    }

    /**
     * Registers a mod with the mod loader.
     *
     * @param mod The mod that should be registered. The mod MUST: <ol><li>Be built against a compatible API version.</li><li>Use a unique namespace.</ol>
     *
     * @return Whether the mod registration was successful or not.
     */
    public static boolean registerMod(@Nonnull Mod mod)
    {
        if (Arrays.stream(compatibleVersions).noneMatch(version -> version.equals(mod.getAPIVersion())))
        {
            if (plugin.getConfig().getBoolean("shutDownServerOnError.modVersionError"))
            {
                logger.severe("Critical error found while registering mod " + mod + "!");
                logger.severe("Could not register mod " + mod + " due to requiring an incompatible version! (" + mod.getAPIVersion() + ")");
                logger.severe("The server is now shutting down as a precautionary measure in case this mod was integral to your server experience.");
                logger.severe("To disable the server shutting down, set shutDownServerOnError.modVersionError to false in the NetherReactor configuration.");
                plugin.getServer().shutdown();
            }
            else
            {
                logger.warning("Could not register mod " + mod + " due to requiring an incompatible version! (" + mod.getAPIVersion() + ")");
            }
            return false;
        }

        if (registeredMods.stream().anyMatch(reg -> reg.getNamespace().equals(mod.getNamespace())))
        {
            if (plugin.getConfig().getBoolean("shutDownServerOnError.modNamespaceCollision"))
            {
                logger.severe("Critical error found while registering mod " + mod + "!");
                logger.severe("Could not register mod " + mod + " due to namespace " + mod.getNamespace() + " being already used!");
                logger.severe("The server is now shutting down as a precautionary measure in case this mod was integral to your server experience.");
                logger.severe("To disable the server shutting down, set shutDownServerOnError.modNamespaceCollision to false in the NetherReactor configuration.");
                plugin.getServer().shutdown();
            }
            else
            {
                logger.warning("Could not register mod " + mod + " due to namespace " + mod.getNamespace() + " being already used!");
            }

            return false;
        }

        ModRegister.register(mod, plugin);
        registeredMods.add(mod);
        logger.info("Successfully registered mod " + mod);

        return true;
    }

    /**
     * Gets the supported API versions.
     *
     * @return The API versions that this plugin build is compatible with.
     */
    public static Version[] getCompatibleVersions()
    {
        return compatibleVersions;
    }
}
