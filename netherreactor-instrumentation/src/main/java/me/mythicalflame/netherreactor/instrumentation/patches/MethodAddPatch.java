package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Represents a patch that adds a method.
 */
@NullMarked
public class MethodAddPatch extends Patch
{
    /**
     * The modifiers of the method.
     */
    private final int[] MODIFIERS;
    /**
     * The name of the class of the return type of the method.
     */
    private final String RETURN_TYPE;
    /**
     * The name of the method.
     */
    private final String METHOD_NAME;
    /**
     * The names of the classes of the parameters of the method.
     */
    private final String[] METHOD_PARAMS;
    /**
     * The names of the classes of the exceptions thrown by the method.
     */
    private final String[] METHOD_EXCEPTIONS;
    /**
     * The method body in Javassist format.
     */
    private final String METHOD_BODY;

    /**
     * Constructs a patch that adds a method.
     *
     * @param className The full name of the class to patch.
     * @param modifiers The modifiers of the method, as an array consisting of values from java.lang.reflect.Modifier.
     * @param returnType The full name of the class of the return type of the method.
     * @param methodName The name of the method.
     * @param methodParams An array of the full names of the classes of the parameters of the method.
     * @param methodExceptions An array of the full names of the classes of the exceptions thrown by the method.
     * @param methodBody The method body in Javassist format.
     */
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
