package com.company;


import com.company.Annotations.*;

import java.io.IOException;

public class TestClass {
    private String str;

    @SetUp
    public void setUp() {
        System.out.println("SetUp");
    }

    @Before
    public void before() {
        System.out.println("Before");
    }

    @After
    public void after() {
        System.out.println("After");
    }

    @Destroy
    public void destroy() {
        System.out.println("Destroy");
    }


    @Test
    @Expected(c = RuntimeException.class)
    public void test1() throws IOException {
        str = "1";
        if (str.equals("1")) {
            System.out.println("test1");
            throw new RuntimeException("Йа ексепшын");
        }
        throw new IOException();
    }

    @Test
    @Expected(c = RuntimeException.class)
    public void test2() throws IOException {
        str = "2";
        if (str.equals("2")) {
            System.out.println("test2");
            throw new RuntimeException("Йа ексепшын");
        }
        throw new IOException();
    }

    @Test(isEnabled = false)
    @Expected(c = RuntimeException.class)
    public void test3() throws IOException {
        str = "3";
        if (str.equals("3")) {
            System.out.println("test3");
            throw new RuntimeException("Йа ексепшын");
        }
        throw new IOException();


    }

    @Test
    @Expected(c = IOException.class)
    public void test4() throws IOException {
        str = "4";
        if (str.equals("4")) {
            System.out.println("test4");
            throw new RuntimeException("Йа ексепшын");
        }
        throw new IOException();
    }


}