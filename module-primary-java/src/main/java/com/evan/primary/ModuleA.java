package com.evan.primary;

import java.util.ArrayList;
import java.util.List;

public class ModuleA {
    public static void main(String[] args) {
        List<String> testList = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            testList.add("i="+i);
        }

        for (String item: testList){
            System.out.println(item);
        }

        List<Bean> beanList = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            beanList.add(new Bean("i="+i));
        }

        for (Bean item: beanList){
            System.out.println(item);
        }
    }

    public static class Bean {
        private String f1;
        public Bean(String s) {
            f1 = s;
        }
    }
}
