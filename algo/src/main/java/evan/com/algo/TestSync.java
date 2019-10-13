package evan.com.algo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shidu on 19/4/6.
 * https://blog.csdn.net/uniquewonderq/article/details/49847771
 * 当一个线程进入一个对象的一个synchronized()方法后，其他线程是否可进入此对象的其他方法？
 */

public class TestSync {
    public synchronized void synchronizedMethod(){
        System.out.println("begin calling sychronizedMethod...");
        try{
            Thread.sleep(10000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("finish calling  sychronizedMethod...");
    }


    public synchronized static void generalMethod(){
        System.out.println("call generalMethod...");
    }

    public static void main(String[] args){
        final TestSync example  = new TestSync();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                example.synchronizedMethod();
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                example.generalMethod();
            }
        });
    }

}
