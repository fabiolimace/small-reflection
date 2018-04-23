package com.github.small.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reflection {

    protected Class<?> clazz;
    protected String clazzName;
    protected String clazzFullName;
    protected List<Class<?>> inheritanceChain;
    protected List<Field> fields;
    protected List<Method> methods;
    protected List<Method> getters;
    protected List<Method> setters;

    public Reflection(Class<?> clazz) {
        this.clazz = clazz;
        init();
    }

    private void init() {
        clazzName = clazz.getSimpleName();
        clazzFullName = clazz.getName();
        inheritanceChain = getInheritanceChain(clazz);
        fields = getFields(clazz);
        methods = getMethods(clazz);
        getters = getGetters(clazz);
        setters = getSetters(clazz);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getClazzFullName() {
        return clazzFullName;
    }

    public void setClazzFullName(String clazzFullName) {
        this.clazzFullName = clazzFullName;
    }

    public List<Class<?>> getInheritanceChain() {
        return inheritanceChain;
    }

    public void setInheritanceChain(List<Class<?>> inheritanceChain) {
        this.inheritanceChain = inheritanceChain;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public List<Method> getGetters() {
        return getters;
    }

    public void setGetters(List<Method> getters) {
        this.getters = getters;
    }

    public List<Method> getSetters() {
        return setters;
    }

    public void setSetters(List<Method> setters) {
        this.setters = setters;
    }

    public Method getGetter(Field field) {
        for(Method method: this.setters) {
            if(isGetter(method, field)) {
                return method;
            }
        }
        return null;
    }

    public Method getSetter(Field field) {
        for(Method method: this.setters) {
            if(isSetter(method, field)) {
                return method;
            }
        }
        return null;
    }

    protected static String getGetterName(String fieldName) {
        return "get" + toUpperCamelCase(fieldName);
    }

    protected static String getSetterName(String fieldName) {
        return "set" + toUpperCamelCase(fieldName);
    }

    protected static String getEncapsulatedFieldName(String methodName) {

        if(methodName.startsWith("get") || methodName.startsWith("set")){
            return toLowerCamelCase(methodName.substring(3));
        }

        return toLowerCamelCase(methodName);
    }

    protected static boolean isGetter(Method method){
        if(method==null) return false;
        if(!(method.getName().startsWith("get"))) return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        if(!Modifier.isPublic(method.getModifiers())) return false;
        if(!hasField(method.getDeclaringClass(), getEncapsulatedFieldName(method.getName()))) return false;
        return true;
    }

    protected static boolean isGetter(Method method, Field field) {
        return isGetter(method) && hasMethod(method.getDeclaringClass(), getGetterName(field.getName()));
    }

    protected static boolean isSetter(Method method){
        if(method==null) return false;
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length != 1) return false;
        if(!Modifier.isPublic(method.getModifiers())) return false;
        if(!hasField(method.getDeclaringClass(), getEncapsulatedFieldName(method.getName()))) return false;
        return true;
    }

    protected static boolean isSetter(Method method, Field field) {
        return isSetter(method) && hasMethod(method.getDeclaringClass(), getSetterName(field.getName()));
    }

    protected static boolean hasField(Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName) != null;
    }

    protected static boolean hasMethod(Class<?> clazz, String methodName) {
        return getMethod(clazz, methodName) != null;
    }

    protected static Field getField(Class<?> clazz, String fieldName) {
        for (Field f: getFields(clazz)) {
            if(f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    protected static Method getMethod(Class<?> clazz, String methodName) {
        for (Method m: getMethods(clazz)) {
            if(m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    protected static List<Field> getFields(Class<?> clazz) {
        List<Class<?>> clazzChain = getInheritanceChain(clazz);
        List<Field> fields = new ArrayList<>();

        for(Class<?> c : clazzChain) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        return fields;
    }

    protected static List<Method> getMethods(Class<?> clazz) {
        List<Class<?>> clazzChain = getInheritanceChain(clazz);
        List<Method> methods = new ArrayList<>();

        for(Class<?> c : clazzChain) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
        }

        return methods;
    }

    protected static List<Method> getGetters(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for(Method m: getMethods(clazz)) {
            if(isGetter(m)) {
                methods.add(m);
            }
        }
        return methods;
    }

    protected static List<Method> getSetters(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for(Method m: getMethods(clazz)) {
            if(isSetter(m)) {
                methods.add(m);
            }
        }
        return methods;
    }

    protected static List<Class<?>> getInheritanceChain(Class<?> clazz) {
        List<Class<?>> chain = new ArrayList<>();
        chain.add(clazz);

        Class<?> parent = clazz.getSuperclass();
        while (parent != null) {
            chain.add(parent);
            parent = parent.getSuperclass();
        }

        return chain;
    }

    protected static List<Annotation> getAnnotations(AnnotatedElement annotatedElement) {
        List<Annotation> annotations = new ArrayList<>();

        for (Annotation a : annotatedElement.getAnnotations()) {
            annotations.add(a);
        }
        return annotations;
    }

    protected static Annotation getAnnotation(AnnotatedElement annotatedElement, Class<?> annotationClazz) {
        if (annotatedElement == null) return null;
        for(Annotation a : getAnnotations(annotatedElement)) {
            if(a.annotationType() == annotationClazz) {
                return a;
            }
        }
        return null;
    }

    protected static boolean hasAnnotation(AnnotatedElement annotatedElement, Class<?> annotationClazz) {
        return getAnnotation(annotatedElement, annotationClazz) != null;
    }
    
	
    protected static String toLowerCamelCase(String string) {
        char chars[]= string.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        string = new String(chars);
        return string;
    }

    protected static String toUpperCamelCase(String string) {
        char chars[]= string.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        string = new String(chars);
        return string;
    }

    protected static String toSnakeCase(String string) {

        StringBuffer buffer = new StringBuffer();
        int i = 0;
        for (char c : string.toCharArray()) {
            if (i > 0 && Character.isUpperCase(c)) {
                buffer.append('_');
            }
            buffer.append(Character.toLowerCase(c));
            i++;
        }
        return buffer.toString();
    }

    public static String getDatabaseObjectIdentifier(String javaObjectIdentifier) {
        return toSnakeCase(javaObjectIdentifier);
    }
}
