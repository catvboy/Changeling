package mchorse.metamorph.util;

import java.lang.reflect.Method;

import net.minecraftforge.common.MinecraftForge;

public class InvokeUtil {
    
    public static final int MC_MAJOR_VERSION = Integer.parseInt(MinecraftForge.MC_VERSION.split("\\.")[1]);
    
    /**
     * Ascends up a class chain until it finds the specified method, regardless
     * of access modifier. Assumes finalClazz is the original declarer of the specified method.
     */
    public static Method getPrivateMethod(Class clazz, Class finalClazz, String methodName, Class<?>... paramVarArgs)
            throws NoSuchMethodException, SecurityException
    {
        Method privateMethod = null;
        
        for (Class testClazz = clazz;
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

}
