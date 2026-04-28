package me.mythicalflame.netherreactor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FileUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class NetherReactorUtilities
{
    private NetherReactorUtilities() {}

    //Source: PaperMC docs
    public static Component minimessage(String msg)
    {
        return MiniMessage.miniMessage().deserialize(msg);
    }

    public static final class RomanNumeral
    {
        private final int NUMBER;
        private final String LETTER;
        private static final RomanNumeral[] romanNumerals = {new RomanNumeral(100, "C"),
                                                             new RomanNumeral(90, "XC"),
                                                             new RomanNumeral(50, "L"),
                                                             new RomanNumeral(40, "XL"),
                                                             new RomanNumeral(10, "X"),
                                                             new RomanNumeral(9, "IX"),
                                                             new RomanNumeral(5, "V"),
                                                             new RomanNumeral(4, "IV"),
                                                             new RomanNumeral(1, "I")};

        private RomanNumeral(int num, String letter)
        {
            this.NUMBER = num;
            this.LETTER = letter;
        }

        public static String getRomanNumber(int number)
        {
            StringBuilder result = new StringBuilder();
            for (RomanNumeral romanNumeral : romanNumerals)
            {
                while (number >= romanNumeral.NUMBER)
                {
                    result.append(romanNumeral.LETTER);
                    number -= romanNumeral.NUMBER;
                }
            }
            return result.toString();
        }
    }

    /**
     * A utility class for reading config files during bootstrap.
     */
    public static class ConfigurationManager
    {
        /**
         * The root node.
         */
        private final CommentedConfigurationNode root;

        /**
         * Creates a ConfigurationManager from a file path.
         *
         * @param path The path to read the config from. E.g. Path.of("plugins", name, "config.yml").
         * @throws IOException If the configuration loader cannot build.
         */
        public ConfigurationManager(@Nonnull Path path) throws IOException
        {
            this.root = YamlConfigurationLoader.builder().path(path).build().load();
        }

        /**
         * Creates a ConfigurationManager from a file path. If the config file does not exist, copies it from a resource at a given path.
         *
         * @param readPath The path to read the config from. E.g. Path.of("plugins", name, "config.yml").
         * @param pluginClass A class from your plugin that is in a module with the resource you want to copy from.
         * @param resourcePath The resource path to copy from if the config file does not exist. E.g. "/config.yml"
         * @throws IOException If the configuration loader cannot build, or the default file cannot be copied.
         */
        public ConfigurationManager(@Nonnull Path readPath, @Nonnull Class<?> pluginClass, @Nonnull String resourcePath) throws IOException
        {
            File configFile = readPath.toFile();
            if (!configFile.exists())
            {
                FileUtils.copyInputStreamToFile(pluginClass.getResourceAsStream("/config.yml"), configFile);
            }
            this.root = YamlConfigurationLoader.builder().path(readPath).build().load();
        }

        /**
         * Returns the root node.
         *
         * @return The root node.
         */
        public CommentedConfigurationNode getRoot()
        {
            return this.root;
        }

        /**
         * Gets the node at the given path.
         *
         * @param path The path.
         * @return The node at the given path.
         */
        public CommentedConfigurationNode getNode(Object... path)
        {
            return root.node(path);
        }

        /**
         * Gets the int at the given path.
         *
         * @param path The path.
         * @return The int at the given path.
         */
        public int getInt(Object... path)
        {
            return root.node(path).getInt();
        }

        /**
         * Gets the int at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The int at the given path.
         */
        public int getIntOrDefault(int defaultValue, Object... path)
        {
            return root.node(path).getInt(defaultValue);
        }

        /**
         * Gets the boolean at the given path.
         *
         * @param path The path.
         * @return The boolean at the given path.
         */
        public boolean getBoolean(Object... path)
        {
            return root.node(path).getBoolean();
        }

        /**
         * Gets the boolean at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The boolean at the given path.
         */
        public boolean getBooleanOrDefault(boolean defaultValue, Object... path)
        {
            return root.node(path).getBoolean(defaultValue);
        }

        /**
         * Gets the double at the given path.
         *
         * @param path The path.
         * @return The double at the given path.
         */
        public double getDouble(Object... path)
        {
            return root.node(path).getDouble();
        }

        /**
         * Gets the double at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The double at the given path.
         */
        public double getDoubleOrDefault(double defaultValue, Object... path)
        {
            return root.node(path).getDouble(defaultValue);
        }

        /**
         * Gets the float at the given path.
         *
         * @param path The path.
         * @return The float at the given path.
         */
        public float getFloat(Object... path)
        {
            return root.node(path).getFloat();
        }

        /**
         * Gets the float at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The float at the given path.
         */
        public float getFloatOrDefault(float defaultValue, Object... path)
        {
            return root.node(path).getFloat(defaultValue);
        }

        /**
         * Gets the long at the given path.
         *
         * @param path The path.
         * @return The long at the given path.
         */
        public long getLong(Object... path)
        {
            return root.node(path).getLong();
        }

        /**
         * Gets the long at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The long at the given path.
         */
        public long getLongOrDefault(long defaultValue, Object... path)
        {
            return root.node(path).getLong(defaultValue);
        }

        /**
         * Gets the String at the given path.
         *
         * @param path The path.
         * @return The String at the given path.
         */
        public String getString(Object... path)
        {
            return root.node(path).getString();
        }

        /**
         * Gets the String at the given path.
         *
         * @param defaultValue The default value to return if the value is not found.
         * @param path The path.
         * @return The String at the given path.
         */
        public String getStringOrDefault(String defaultValue, Object... path)
        {
            return root.node(path).getString(defaultValue);
        }
    }
}
