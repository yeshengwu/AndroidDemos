package com.evan.androiddemos.codekk.proxy;

public class Test {
    public static void main(String[] args){
        ClassA classA = new ClassA();
        ClassB classB = new ClassB(classA);
        classB.operateMethod1();
        classB.operateMethod2();
    }
}
