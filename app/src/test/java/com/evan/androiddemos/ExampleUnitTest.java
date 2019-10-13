package com.evan.androiddemos;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        char[] chars = new char[1];
        String test = new String(chars);
        System.out.print("test = " + test);

//        assertEquals(4, 2 + 2);

        assertEquals(4, 2 + 2);

        String a = null;
//        System.out.println(a.equals("b"));
//        System.out.println("b".equals(null));

//         HashMap<Integer, Boolean> mDefaultCheck = new HashMap<>(1);
//         System.out.println( mDefaultCheck.get(0).booleanValue()); // NullPointerException
//         System.out.println( mDefaultCheck.get(1).booleanValue());
//         System.out.println( mDefaultCheck.get(3).booleanValue());

//        String.format("¥%.2f", price) : String.format("¥%.0f", price)

        System.out.println(String.format("我是字符串"));

        System.out.println(String.format("¥%.2f", 1.87f));
        System.out.println(String.format("¥%.0f", 1f));

        getCheckPos();

        List<String> aa = new ArrayList<>();
        aa.add("1");
        aa.add("2");
        StringBuilder stringBuffer = new StringBuilder(aa.size());
        for (String i : aa) {
//            Arrays.toString(new String[]);
            stringBuffer.append(i).append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        String[] selects = new String[aa.size()];
        selects = aa.toArray(selects);
        System.out.println(aa.toString());
        System.out.println(selects);

        System.out.println(stringBuffer);

//        List<String> aList = new ArrayList<>();
//        aList.add("a");
//        aList.add("b");
//        List<String> bList = new ArrayList<>();
//        bList.add("a");
//        bList.add("b");
        List<Integer> aList = new ArrayList<>();
        aList.add(1);
        aList.add(2);
        List<Integer> bList = new ArrayList<>();
        bList.add(1);
        bList.add(2);
        System.out.println("retain=" + aList.retainAll(bList));
        System.out.println("retain=" + bList.retainAll(aList));

        List<Integer> cList = new ArrayList<>();
        for (Integer item : cList) {
            System.out.println("item=" + item);
        }

//        float allPercent = 0.323f;
//        float allPercent = 0.39f;
        float allPercent = 0;
        System.out.println("allPercent=" + String.format("已阅%.1f%%", allPercent * 100));
        System.out.println("allPercent=" + allPercent * 100);

        /*************IllegalAccessError catch test********************/
        //        try {
//            testThrowError();
//        } catch (Exception e) {  // IllegalAccessError super
//            e.printStackTrace();
//        }
        try {
            testThrowError();
        } catch (Throwable e) {  // IllegalAccessError super is Throwable
            e.printStackTrace();
        }
        /**************IllegalAccessError catch test*******************/

        Person p = new Person("test", 20);
        System.out.println(p);

        List<String> data = new ArrayList<>();
        data.add("a");
        data.add("b");

        String[] args = new String[0];
        String[] evan = data.toArray(args);

        System.out.println("evan.getClass() = " + evan.getClass());
        System.out.println("p.getClass() = " + Person.class);
        System.out.println("p.getClass() = " + p.getClass());

        System.out.println("args = " + args.length);
        for (String item : args) {
            System.out.println("item = " + item);
        }

        final Class<String[]> stringArrayClass = String[].class;
        System.out.println("stringArrayClass = " + stringArrayClass);

        System.out.println("evan = " + evan.length);
        for (String item : evan) {
            System.out.println("i = " + item);
        }

        HashMap<String, String> aMap = new HashMap<String, String>();
        aMap.put("a", "a");
        aMap.put("b", "b");

        HashMap<String, String> bMap = new HashMap<String, String>();
        aMap.put("a", "a");
        aMap.put("b", "b");

        System.out.println("equals = " + (aMap.equals(bMap)));

        System.out.println("aa = " + (500 > 500));

//        testString2Stream("Example on how to convert a String to an InputStream");

        System.out.println("test finish");
    }


    void testThrowError() throws IllegalAccessError {
        System.out.println("testThrowError");
        throw new IllegalAccessError("evan add illegalError");
    }

    public static int getCheckPos() {
        List<Goods> items = new ArrayList<>();
        Goods good = new Goods();
        good.enable = false;
        items.add(good);

        Goods good2 = new Goods();
        good2.enable = false;
        items.add(good2);

        Goods good3 = new Goods();
        good3.reccommnad = true;
        good3.enable = false;
        items.add(good3);

        Goods good4 = new Goods();
        good4.enable = false;
        items.add(good4);

        Goods good5 = new Goods();
        good5.reccommnad = true;
        items.add(good5);

        Goods good6 = new Goods();
        good6.reccommnad = true;
        good6.enable = false;
        items.add(good6);

        int check = 0;
        int recommnad = -1;
        int normal = -1;
        for (int i = 0; i < items.size(); i++) {
            Goods goods = items.get(i);
            if (goods.reccommnad && goods.enable && recommnad == -1) {
                recommnad = i;
                System.out.println("reccommnad enable = " + recommnad);
            }

            if (goods.enable && normal == -1) {
                normal = i;
                System.out.println("normal enable = " + normal);
            }
        }

        if (recommnad >= 0) {
            check = recommnad;
        } else if (normal >= 0) {
            check = normal;
        }

        System.out.println("check = " + check);
        return check;
    }

    public static class Goods {
        public boolean reccommnad;
        public boolean enable;
    }

    public static boolean hasDecimal(float price) {
        int m = (int) price;
        double y = price - m;
        return y > 0;
    }

    private void testString2Stream(String text) {
        if (text == null) return;
        try {
            InputStream is = new ByteArrayInputStream(text.getBytes());

            int byteRead;
            while ((byteRead = is.read()) != -1) {
                System.out.print((char) byteRead);
            }
            System.out.println();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class Person {
        public String name;
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name=8888'" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }


}