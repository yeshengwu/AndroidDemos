package evan.com.algo;

/**
 * Created by shidu on 19/4/6.
 */

public class Client {

    public static void main(String[] args){
        Student a = new Student("zhangsan");
        System.out.println("a = "+a);
        Student b = a;
        b.name = "lisi";
        System.out.println("b = "+b);
        System.out.println("b a = "+a);
        // output
//        a = zhangsan
//        b = lisi
//        b a = lisi



    }
}
