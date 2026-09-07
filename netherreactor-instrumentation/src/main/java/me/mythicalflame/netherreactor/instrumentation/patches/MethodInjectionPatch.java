package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Represents a patch that injects into a method.
 */
@NullMarked
public class MethodInjectionPatch extends Patch
{
    /**
     * The method of injection.
     */
    private final InjectionType INJECTION_TYPE;
    /**
     * The name of the method.
     */
    private final String METHOD_NAME;
    /**
     * The names of the classes of the parameters of the method.
     */
    private final String[] METHOD_PARAMS;
    /**
     * The injection content in Javassist format.
     */
    private final String INJECTION_CONTENT;

    /**
     * Constructs a patch that injects into a method.
     *
     * @param className The full name of the class to patch.
     * @param injectionType The method of injection.
     * @param methodName The name of the method.
     * @param methodParams An array of the full names of the classes of the parameters of the method.
     * @param injectionContent The content to inject in Javassist format.
     */
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

    /**
     * Represents a method of injecting content.
     */
    public enum InjectionType
    {
        /**
         * Injects before the method's body.
         */
        BEFORE,
        /**
         * Injects after the method's body.
         */
        AFTER
    }
}
