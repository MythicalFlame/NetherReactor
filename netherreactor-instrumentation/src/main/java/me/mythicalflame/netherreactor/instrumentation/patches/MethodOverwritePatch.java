package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Represents a patch that overwrites a method.
 */
@NullMarked
public class MethodOverwritePatch extends Patch
{
    /**
     * The name of the method to overwrite.
     */
    private final String METHOD_NAME;
    /**
     * The names of the classes of the parameters of the method.
     */
    private final String[] METHOD_PARAMS;
    /**
     * The new method body in Javassist format.
     */
    private final String METHOD_BODY;

    /**
     * Constructs a patch that overwrites a method.
     *
     * @param className The full name of the class to patch.
     * @param methodName The name of the method to patch.
     * @param methodParams An array of the full names of the classes of the parameters of the method.
     * @param methodBody The new method body in Javassist format.
     */
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
