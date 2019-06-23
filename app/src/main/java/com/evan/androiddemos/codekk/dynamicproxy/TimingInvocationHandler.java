package com.evan.androiddemos.codekk.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TimingInvocationHandler implements InvocationHandler {

    private Object target;

    public TimingInvocationHandler(Object target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //do some processing before the method invocation
        long start = System.currentTimeMillis();
        System.out.println(method.getName() + " method before: record time");

        //invoke the method
        Object result = method.invoke(target, args);

        //do some processing after the method invocation
        System.out.println(method.getName() + " cost time is:" + (System.currentTimeMillis() - start));
        System.out.println(method.getName() + " result:" + result);
        return result;
    }
}
