package com.example.svilen.p8;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

public class StudentTest extends TestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    @SmallTest
    public void testSaySomething(){
        StudentActivity studentActivity = new StudentActivity();
        boolean result = studentActivity.SaySomething("hello World");
        assertEquals(false,result);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
