package org.b3log.latke.util;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ReflectHelper {
	
	public static String[] getMethodVariableNames(Class<?> clazz, String targetMethodName) {  
	 
	    ClassPool pool = ClassPool.getDefault();  
	    pool.insertClassPath(new ClassClassPath(clazz));  
	    CtClass cc;  
	    CtMethod cm = null;  
	    try {  
	        cc = pool.get(clazz.getName());  
	        cm = cc.getDeclaredMethod(targetMethodName);
	    } catch (NotFoundException e) {  
	        e.printStackTrace();  
	    }  
	  
	    MethodInfo methodInfo = cm.getMethodInfo();  
	    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
	    String[] variableNames = new String[0];  
	    try {  
	        variableNames = new String[cm.getParameterTypes().length];  
	    } catch (NotFoundException e) {  
	        e.printStackTrace();  
	    }  
	    int staticIndex = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
	    for (int i = 0; i < variableNames.length; i++)  
	        variableNames[i] = attr.variableName(i + staticIndex);  
	    return variableNames;  
	}  

}
