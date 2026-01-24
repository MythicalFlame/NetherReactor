package me.mythicalflame.netherreactor.api;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This class represents a NetherReactor API version.
 */
public final class Version
{
    /**
     * The major version number.
     */
    private final int major;
    /**
     * The minor version number.
     */
    private final int minor;
    /**
     * The patch version number.
     */
    private final int patch;
    /**
     * The release metadata.
     */
    @Nonnull
    private final String releaseData;

    /**
     * Constructs a Version object with the release metadata "release".
     *
     * @param major The major version number.
     * @param minor The minor version number.
     * @param patch The patch version number.
     */
    public Version(int major, int minor, int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.releaseData = "release";
    }

    /**
     * Constructs a Version object with a custom release metadata.
     *
     * @param major The major version number.
     * @param minor The minor version number.
     * @param patch The patch version number.
     * @param releaseData The release metadata.
     */
    public Version(int major, int minor, int patch, @Nonnull String releaseData)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.releaseData = releaseData;
    }

    /**
     * Gets the major number of the version.
     *
     * @return The major version number.
     */
    public int getMajor()
    {
        return major;
    }

    /**
     * Gets the minor number of the version.
     *
     * @return The minor version number.
     */
    public int getMinor()
    {
        return minor;
    }

    /**
     * Gets the patch number of the version.
     *
     * @return The patch version number.
     */
    public int getPatch()
    {
        return patch;
    }

    /**
     * Gets the release metadata of the version.
     *
     * @return The release metadata.
     */
    public @Nonnull String getReleaseData()
    {
        return releaseData;
    }

    /**
     * Overrides Object#toString.
     *
     * @return A string in the format of "vmajor.minor.patch-releaseData".
     */
    @Override
    public String toString()
    {
        return "v" + major + "." + minor + "." + patch + "-" + releaseData;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Version otherVersion))
        {
            return false;
        }

        return major == otherVersion.major && minor == otherVersion.minor && patch == otherVersion.patch && releaseData.equals(otherVersion.releaseData);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new Object[]{major, minor, patch, releaseData});
    }
}
