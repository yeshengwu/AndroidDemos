package com.evan.androiddemos.codekk;

public class JavaTest {
    public static void main(String[] args){

        EntranceItem entranceItem = new EntranceItem();
        entranceItem.setName("name");
        entranceItem.setTarget(BookDetail.class);

        System.out.println(entranceItem.getName());
        System.out.println(entranceItem.getTarget().getName());

        if (entranceItem.getTarget().getName().equals(BookDetail.class.getName())) {
            System.out.println("equals");
        } else {
            System.out.println("equals not");
        }


        Class<?> genericIntClass = entranceItem.getTarget();
//        genericIntClass = Integer.class;
        genericIntClass = double.class;

    }
}
