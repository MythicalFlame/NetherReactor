package me.mythicalflame.netherreactor.api;

import java.util.Arrays;

/**
 * This class represents a NetherReactor API version.
 *
 * @param major The major version number.
 * @param minor The minor version number.
 * @param patch The patch version number.
 */
public record Version(int major, int minor, int patch)
{
    /**
     * Gets the version's representation as a string.
     *
     * @return A string in the format of "major.minor.patch".
     */
    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Version(int majorOther, int minorOther, int patchOther)))
        {
            return false;
        }

        return major == majorOther && minor == minorOther && patch == patchOther;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new Object[]{major, minor, patch});
    }
}
