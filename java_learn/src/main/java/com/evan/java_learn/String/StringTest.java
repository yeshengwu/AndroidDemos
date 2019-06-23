package com.evan.java_learn.String;


/**
 *  参考 https://github.com/yeshengwu/javatest
 */

public class StringTest {

    public static void main(String[] args) {
        String a = "ab";
        System.out.println(a.equals(null)); // false

        String test = null;
        System.out.println(test.equals("a")); // NPE
    }
}
