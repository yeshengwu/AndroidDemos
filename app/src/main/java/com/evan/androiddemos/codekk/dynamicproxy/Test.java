package com.evan.androiddemos.codekk.dynamicproxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {

        InvocationHandler timingInvocationHandler = new TimingInvocationHandler(new OperateImpl());

        Class<?>[] interfaces = {Operate.class};

        Operate proxy = (Operate) Proxy.newProxyInstance(Operate.class.getClassLoader(), interfaces, timingInvocationHandler);
        int result = proxy.operateMethod1(110,"i am string arg");
        System.out.println("Test method1 return = "+result);
        proxy.operateMethod2();
        proxy.operateMethod3();

    }
}
