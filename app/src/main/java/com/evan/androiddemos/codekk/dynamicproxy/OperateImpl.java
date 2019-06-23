package com.evan.androiddemos.codekk.dynamicproxy;

public class OperateImpl implements Operate {

    @Override
    public int operateMethod1(int arg1, String arg2) {
        System.out.println("OperateImpl op1. arg1 = "+arg1+" arg2 = "+arg2);
        return arg1;
    }

    @Override
    public void operateMethod2() {
        System.out.println("OperateImpl op2");
    }

    @Override
    public void operateMethod3() {
        System.out.println("OperateImpl op3");
    }
}
