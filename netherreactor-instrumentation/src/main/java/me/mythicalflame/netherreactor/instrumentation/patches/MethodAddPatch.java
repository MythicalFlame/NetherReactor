package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;

import java.io.IOException;

public class MethodAddPatch extends Patch
{
    private final int[] MODIFIERS;
    private final String RETURN_TYPE;
    private final String METHOD_NAME;
    private final String[] METHOD_PARAMS;
    private final String[] METHOD_EXCEPTIONS;
    private final String METHOD_BODY;

    public MethodAddPatch(String className, int[] modifiers, String returnType, String methodName, String[] methodParams, String[] methodExceptions, String methodBody)
    {
        super(className);
        this.MODIFIERS = modifiers;
        this.RETURN_TYPE = returnType;
        this.METHOD_NAME = methodName;
        this.METHOD_PARAMS = methodParams;
        this.METHOD_EXCEPTIONS = methodExceptions;
        this.METHOD_BODY = methodBody;
    }

    @Override
    public void apply(CtClass cc) throws NotFoundException, IOException, CannotCompileException
    {
        CtClass returnType = Patcher.getCtClass(RETURN_TYPE);
        CtClass[] params = new CtClass[METHOD_PARAMS.length];
        for (int i = 0; i < METHOD_PARAMS.length; ++i)
        {
            params[i] = Patcher.getCtClass(METHOD_PARAMS[i]);
        }
        CtClass[] exceptions = new CtClass[METHOD_EXCEPTIONS.length];
        for (int i = 0; i < METHOD_EXCEPTIONS.length; ++i)
        {
            exceptions[i] = Patcher.getCtClass(METHOD_EXCEPTIONS[i]);
        }
        int modifier = 0;
        for (int mod : MODIFIERS)
        {
            modifier |= mod;
        }
        CtMethod method = CtNewMethod.make(modifier, returnType, METHOD_NAME, params, exceptions, METHOD_BODY, cc);
        cc.addMethod(method);
    }
}
