package test.pallettown;

import org.junit.Test;
import pallettown.PTCProxy;

/**
 * Created by Owner on 28/01/2017.
 */
public class ProxyTest implements Runnable {

    private static final String[] ips = new String[] {
            "23.239.219.67:21260",
            "104.224.39.45:21279" ,
            "138.128.66.121:21260" ,
            "38.109.22.101:21324" ,
            "45.57.195.248:21235"
    };

    private static final int THREADS = 1;
    private static final int WORK_ITEMS = 30;


    private static final PTCProxy[] proxies = new PTCProxy[ips.length];

    private int accNum = 0;

    @Test
    public void TestProxies(){

        for (int i = 0; i < ips.length; i++) {
            proxies[i] = new PTCProxy(ips[i], "IP");
        }

        ProxyTest pxyTest = new ProxyTest();
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(pxyTest,"Worker " + i);
        }

        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
        }

        System.out.println(Thread.currentThread().getName()+ " is twiddling its thumbs");
        try {
            for (int i = 0; i < THREADS; i++)
                threads[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    synchronized
    private int incAccNum() {
        return accNum++;
    }

    @Override
    public void run() {
        int mytaskcount = 0;

        int accNum;
        while ((accNum = incAccNum()) < WORK_ITEMS) {
            System.out.println(Thread.currentThread().getName()+" making account "+ accNum);
            System.out.println(Thread.currentThread().getName()+" making account with proxy: " + getProxy());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "done making account " + accNum);
            mytaskcount++;
        }

        System.out.println(Thread.currentThread().getName()+" did "+mytaskcount+ " tasks");
    }

    synchronized
    private static String getProxy() {
        System.out.println("getting proxy for " + Thread.currentThread().getName());

        PTCProxy shortestWait = null;

        for (int i = 0; i < proxies.length; i++) {
            PTCProxy proxy = proxies[i];

            System.out.println("    trying proxy " + i + ": " + proxy.IP());
            if(shortestWait == null){
                shortestWait = proxy;
            }

            if(proxy.NotStarted()){
                System.out.println("    proxy unstarted, using..");
//                proxy.StartUsing();
                return proxy.IP();
            }

            if(proxy.Usable()){
                proxy.Use();
                System.out.println("    proxy usable, using...");
                return proxy.IP();
            }else{
                System.out.println("    proxy unusable");
                if(proxy.WaitTime() == 0){
                    System.out.println("    proxy ready to be reset, updating queue and using...");
                    proxy.UpdateQueue();
                    proxy.Use();
                    return proxy.IP();
                }
                if(proxy.WaitTime() < shortestWait.WaitTime()){
                    System.out.println("    proxy new shortest delay");
                    shortestWait = proxy;
                }
            }
        }

        System.out.println("    no available proxies, waiting for next available proxy...");
        try {
            assert shortestWait != null;
            System.out.println("    shortest wait time: " + shortestWait.WaitTime());
            Thread.sleep(shortestWait.WaitTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shortestWait.UpdateQueue();
        shortestWait.Use();
        return shortestWait.IP();
    }
}
