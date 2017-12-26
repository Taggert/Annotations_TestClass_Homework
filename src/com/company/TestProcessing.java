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

        //Extracting methods
        Method[] methods = c.getDeclaredMethods();
        // Setting up preparable methods
        Method before = null;
        Method setUp = null;
        Method after = null;
        Method destroy = null;
        for (Method m : methods) {
            if (m.isAnnotationPresent(SetUp.class)) {
                if (!(setUp == null)) {
                    throw new RuntimeException("SetUp used more than one time");
                }
                setUp = m;
            }
            if (m.isAnnotationPresent(Before.class)) {
                if (!(before == null)) {
                    throw new RuntimeException("Before used more than one time");
                }
                before = m;
            }
            if (m.isAnnotationPresent(After.class)) {
                if (!(after == null)) {
                    throw new RuntimeException("After used more than one time");
                }
                after = m;
            }
            if (m.isAnnotationPresent(Destroy.class)) {
                if (!(destroy == null)) {
                    throw new RuntimeException("Destroy used more than one time");
                }
                destroy = m;
            }
        }


        if (setUp != null) {
            setUp.invoke(tc);
        }
        for (Method m : methods) {
            if (!m.isAnnotationPresent(Test.class)) {
                continue;
            }
            if (!m.getAnnotation(Test.class).isEnabled()) {
                res.get(TestResults.SKIPPED).add(m.getName());
                continue;
            }

            //Peforming test
            TestResults result = TestResults.PASSED;
            if (before != null) {
                before.invoke(tc);
            }
            try {
                m.invoke(tc);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (!m.isAnnotationPresent(Expected.class) ||  !m.getAnnotation(Expected.class).c().equals(cause.getClass())) {
                    result = TestResults.FAILED;
                }
            }
            res.get(result).add(m.getName());
            if (after != null) {
                after.invoke(tc);
            }

        }

        if (destroy != null) destroy.invoke(tc);
        System.out.println("\nTest results:");
        res.entrySet().forEach(System.out::println);


    }
}
