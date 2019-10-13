package evan.com.algo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by shidu on 19/4/6.
 * https://cyc2018.github.io/CS-Notes/#/notes/Java%20%E5%B9%B6%E5%8F%91?id=%E4%BA%94%E3%80%81%E4%BA%92%E6%96%A5%E5%90%8C%E6%AD%A5
 */

public class AwaitSignalExample {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private void before() {
        System.out.println("before out lock");
        lock.lock();
        try {
            System.out.println("before in lock signal1");
            condition.signal();
            System.out.println("before in lock signal2");
        } finally {
            lock.unlock();
            System.out.println("before unlock");
        }
    }

    private void after() {
        System.out.println("after out lock");
        lock.lock();
        try {
            System.out.println("after in lock await1");
            condition.await();
            System.out.println("after in lock await2");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
            System.out.println("after unlock");
        }
    }

    public static void main(String[] args){
        final AwaitSignalExample example  = new AwaitSignalExample();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                example.after();
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                example.before();
            }
        });
        // output:
//        after out lock
//        after in lock await1
//        before out lock
//        before in lock signal1
//        before in lock signal2
//        before unlock
//        after in lock await2
//        after unlock
    }
}
