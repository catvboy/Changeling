package mchorse.metamorph.util;

import java.lang.reflect.Method;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import mchorse.metamorph.Metamorph;

public class InvokeUtil
{
    protected static final int MAX_ERRORS_PER_CLASS = 5;
    protected static ThreadLocal<WeakHashMap<Class<?>, Integer>> classErrorCounts = new ThreadLocal<>();
    
    protected static WeakHashMap<Class<?>, Integer> getClassBlacklist()
    {
        WeakHashMap<Class<?>, Integer> blacklist = classErrorCounts.get();
        if (blacklist == null) {
            blacklist = new WeakHashMap<>();
            classErrorCounts.set(blacklist);
        }
        return blacklist;
    }
    
    protected static boolean isClassBlacklisted(Class<?> clazz)
    {
        WeakHashMap<Class<?>, Integer> blacklist = getClassBlacklist();
        Integer count = blacklist.get(clazz);
        if (count == null)
        {
            count = 0;
        }
        return count >= MAX_ERRORS_PER_CLASS;
    }
    
    protected static void incrementClassErrors(Class<?> clazz)
    {
        WeakHashMap<Class<?>, Integer> blacklist = getClassBlacklist();
        Integer count = blacklist.get(clazz);
        if (count == null)
        {
            count = 1;
        }
        else
        {
            ++count;
        }
        blacklist.put(clazz, count);
        
        if (count == MAX_ERRORS_PER_CLASS)
        {
            Metamorph.LOGGER.error("Too many errors for class " + clazz.getName() + ". " +
                    "Class will be blacklisted from reflection on this thread.");
        }
    }

    /**
     * Ascends up a class chain until it finds the specified method, regardless
     * of access modifier. Assumes finalClazz is the original declarer of the specified method.
     * 
     * If a class emits too many NoClassDefFoundErrors, then give up and return null.
     */
    public static @Nullable Method getPrivateMethod(Class<?> clazz, Class<?> finalClazz, String methodName, Class<?>... paramVarArgs)
            throws NoSuchMethodException, SecurityException
    {
        if (isClassBlacklisted(clazz))
        {
            return null;
        }

        try
        {
            Method privateMethod = null;
            
            for (Class<?> testClazz = clazz;
                    testClazz != finalClazz && privateMethod == null;
                    testClazz = testClazz.getSuperclass())
            {
                for (Method method : testClazz.getDeclaredMethods())
                {
                    if (!method.getName().equals(methodName))
                    {
                        continue;
                    }
                    
                    Class<?>[] parameters = method.getParameterTypes();
                    if (!(parameters.length == paramVarArgs.length))
                    {
                        continue;
                    }
                    boolean matchingMethod = true;
                    for (int i = 0; i < parameters.length; i++)
                    {
                        if (!(parameters[i] == paramVarArgs[i]))
                        {
                            matchingMethod = false;
                            break;
                        }
                    }
                    
                    if (matchingMethod)
                    {
                        privateMethod = method;
                        break;
                    }
                }
            }
            
            if (privateMethod == null)
            {
                privateMethod = finalClazz.getDeclaredMethod(methodName, paramVarArgs);
            }
            
            privateMethod.setAccessible(true);
            return privateMethod;
        }
        catch (NoClassDefFoundError e)
        {
            Metamorph.LOGGER.error("Failed to do dynamic reflection on class " + clazz.getName() + ". " +
                    "This is most likely caused by a classloading issue in the class. " +
                    "For example, it may be referencing a class from a mod that isn't loaded, " +
                    "or referencing client-only code on a dedicated server.");
            e.printStackTrace();
            incrementClassErrors(clazz);

            return null;
        }
    }

}
