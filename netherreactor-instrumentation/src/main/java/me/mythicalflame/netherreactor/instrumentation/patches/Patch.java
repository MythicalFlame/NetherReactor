package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CtClass;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a modification to a class.
 */
@NullMarked
public abstract class Patch
{
    /**
     * The name of the class to patch.
     */
    private final String CLASS_NAME;

    /**
     * Constructs a patch.
     *
     * @param className The class to patch.
     */
    protected Patch(String className)
    {
        this.CLASS_NAME = className;
    }

    /**
     * Applies a patch.
     *
     * @param cc The Javassist class to apply this patch to.
     * @throws Exception If an error is encountered while patching.
     */
    public abstract void apply(CtClass cc) throws Exception;

    /**
     * Gets the class that the patch targets.
     *
     * @return The class that this patch targets.
     */
    public String getClassName()
    {
        return CLASS_NAME;
    }
}
