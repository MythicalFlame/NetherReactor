package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;

import java.io.IOException;

public class MethodInjectionPatch extends Patch
{
    private final InjectionType INJECTION_TYPE;
    private final String METHOD_NAME;
    private final String[] METHOD_PARAMS;
    private final String INJECTION_CONTENT;

    public MethodInjectionPatch(String className, InjectionType injectionType, String methodName, String[] methodParams, String injectionContent)
    {
        super(className);
        this.INJECTION_TYPE = injectionType;
        this.METHOD_NAME = methodName;
        this.METHOD_PARAMS = methodParams;
        this.INJECTION_CONTENT = injectionContent;
    }

    @Override
    public void apply(CtClass cc) throws NotFoundException, IOException, CannotCompileException
    {
        CtClass[] params = new CtClass[METHOD_PARAMS.length];
        for (int i = 0; i < METHOD_PARAMS.length; ++i)
        {
            params[i] = Patcher.getCtClass(METHOD_PARAMS[i]);
        }
        CtMethod method = cc.getDeclaredMethod(METHOD_NAME, params);
        if (INJECTION_TYPE == InjectionType.BEFORE)
        {
            method.insertBefore(INJECTION_CONTENT);
        }
        else if (INJECTION_TYPE == InjectionType.AFTER)
        {
            method.insertAfter(INJECTION_CONTENT);
        }
    }

    public enum InjectionType
    {
        BEFORE,
        AFTER
    }
}
