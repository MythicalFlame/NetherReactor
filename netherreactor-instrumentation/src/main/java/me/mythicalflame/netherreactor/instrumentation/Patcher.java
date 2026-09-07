package me.mythicalflame.netherreactor.instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.patches.Patch;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;

public final class Patcher
{
    private static final Instrumentation INSTRUMENTATION = ByteBuddyAgent.install();
    private static final HashMap<String, byte[]> CLASS_CACHE = new HashMap<>();
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static boolean installed = false;

    private Patcher() {}

    static
    {
        CLASS_POOL.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        install();
    }

    private static void install()
    {
        if (installed)
        {
            return;
        }

        installed = true;

        INSTRUMENTATION.addTransformer(
                new ClassFileTransformer()
                {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    {
                        if (classBeingRedefined != null)
                        {
                            return null;
                        }

                        return CLASS_CACHE.get(className);
                    }
                });
    }

    public static CtClass getCtClass(String name) throws IOException, NotFoundException
    {
        switch (name)
        {
            case "void": return CtClass.voidType;
            case "boolean": return CtClass.booleanType;
            case "char": return CtClass.charType;
            case "byte": return CtClass.byteType;
            case "short": return CtClass.shortType;
            case "int": return CtClass.intType;
            case "long": return CtClass.longType;
            case "float": return CtClass.floatType;
            case "double": return CtClass.doubleType;
        }

        if (CLASS_CACHE.containsKey(name.replace('.', '/')))
        {
            return CLASS_POOL.makeClass(new ByteArrayInputStream(CLASS_CACHE.get(name.replace('.', '/'))));
        }
        else
        {
            return CLASS_POOL.get(name);
        }
    }

    //Class.forName loads classes, and that might be bad if someone else wants to transform it
    private static Class<?> getLoadedClass(String name)
    {
        String internalName = name.replace('/', '.');
        for (Class<?> clazz : INSTRUMENTATION.getAllLoadedClasses())
        {
            if (clazz.getName().equals(internalName))
            {
                return clazz;
            }
        }

        return null;
    }

    public static void patch(Patch patch) throws Exception
    {
        CtClass cc = getCtClass(patch.getClassName());

        try
        {
            patch.apply(cc);
            byte[] bytecode = cc.toBytecode();
            CLASS_CACHE.put(patch.getClassName().replace('.', '/'), bytecode);

            Class<?> clazz = getLoadedClass(patch.getClassName());
            if (clazz != null)
            {
                INSTRUMENTATION.redefineClasses(new ClassDefinition(clazz, bytecode));
            }
        }
        finally
        {
            cc.detach();
        }
    }
}
