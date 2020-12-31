package com.evan.module;

/**https://jakewharton.com/androids-java-8-support **/
public class Java8 {
    interface Logger {
        void log(String s);
    }

    public static void main(String... args) {
        sayHi(s -> System.out.println(s));
    }

    private static void sayHi(Logger logger) {
        logger.log("Hello!");
    }
}
