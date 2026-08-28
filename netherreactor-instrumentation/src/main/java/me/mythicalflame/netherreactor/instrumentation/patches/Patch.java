package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CtClass;

public abstract class Patch
{
    private final String CLASS_NAME;

    protected Patch(String className)
    {
        this.CLASS_NAME = className;
    }

    public abstract void apply(CtClass cc) throws Exception;

    public String getClassName()
    {
        return CLASS_NAME;
    }
}
