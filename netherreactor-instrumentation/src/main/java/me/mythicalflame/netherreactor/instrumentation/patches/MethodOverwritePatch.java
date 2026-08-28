package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;

import java.io.IOException;

public class MethodOverwritePatch extends Patch
{
    private final String METHOD_NAME;
    private final String[] METHOD_PARAMS;
    private final String METHOD_BODY;

    public MethodOverwritePatch(String className, String methodName, String[] methodParams, String methodBody)
    {
        super(className);
        this.METHOD_NAME = methodName;
        this.METHOD_PARAMS = methodParams;
        this.METHOD_BODY = methodBody;
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
        method.setBody(METHOD_BODY);
    }
}
