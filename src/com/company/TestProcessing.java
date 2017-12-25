package com.company;

import com.company.Annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestProcessing {

    public static void main(String[] args) throws IllegalAccessException,
            InstantiationException,
            InvocationTargetException, NoSuchFieldException {

        Class c = TestClass.class;
        TestClass tc = (TestClass) c.newInstance();

        Map<TestResults, List<String>> res = new HashMap<>();
        res.put(TestResults.PASSED, new ArrayList<>());
        res.put(TestResults.FAILED, new ArrayList<>());
        res.put(TestResults.SKIPPED, new ArrayList<>());

        Method[] methods = c.getDeclaredMethods();

        Method before = null;
        Method setUp = null;
        Method after = null;
        Method destroy = null;
        for (Method m : methods) {
            if (m.isAnnotationPresent(SetUp.class)) {
                setUp = m;
            }
            if (m.isAnnotationPresent(Before.class)) {
                before = m;
            }
            if (m.isAnnotationPresent(After.class)) {
                after = m;
            }
            if (m.isAnnotationPresent(Destroy.class)) {
                destroy = m;
            }
        }


        if (setUp != null) {
            setUp.invoke(tc);
        }
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class) && m.getAnnotation(Test.class).isEnabled()) {
                if (before != null) before.invoke(tc);
                try {
                    m.invoke(tc);
                    res.get(res.get(TestResults.PASSED)).add(m.getName());
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (m.isAnnotationPresent(Expected.class) && m.getAnnotation(Expected.class).c().equals(cause.getClass())) {
                        res.get(res.get(TestResults.PASSED)).add(m.getName());
                    } else {
                        res.get(res.get(TestResults.FAILED)).add(m.getName());
                    }
                }
                if (after != null) after.invoke(tc);
            } else if (m.isAnnotationPresent(Test.class) && !m.getAnnotation(Test.class).isEnabled()) {
                if (before != null) before.invoke(tc);
                res.get(res.get(TestResults.SKIPPED)).add(m.getName());
                if (after != null) after.invoke(tc);
            }
        }

        if (destroy != null) destroy.invoke(tc);
        System.out.println("\nTest results:");
        res.entrySet().forEach(System.out::println);


    }
}
