package pallettown;

import java.util.ArrayList;

/**
 * Created by Owner on 25/01/2017.
 */
public class PTCProxy {

    private final String ip;
    private int usages = 0;
    private static final int MAX_USES = 5;
    private static final long RESET_TIME = PalletTown.resetTime;
    private ArrayList<Long> startTimes = null;
//    private Long latestTime = null;
    public final String auth;


    public PTCProxy(String ip, String auth){
        this.ip = ip;
        this.auth = auth;
    }

//    public void StartUsing(){
//        startTimes = new ArrayList<>();
//        Use();
//    }

    public void Use(){
        long time = System.currentTimeMillis();
        if(startTimes == null){
            startTimes = new ArrayList<>();
        }

//        latestTime = time;

        startTimes.add(time);
    }

    public void ReserveUse(){
        usages++;
    }

    public void UpdateQueue(){
        usages--;
        startTimes.remove(0);
        startTimes.forEach(aLong -> {
            long millis = startTimes.get(0) - System.currentTimeMillis();
            String time = PalletTown.millisToTime(millis);
            System.out.println("    start time: " + time + " ago");
        });
    }

    public boolean NotStarted(){
        return usages == 0;
    }

    public boolean Usable(){
        System.out.println("checking if proxy is usable");

        if(usages < MAX_USES && startTimes == null) return true;
        try {
            long millis = System.currentTimeMillis() - startTimes.get(0);
//        long millis = System.currentTimeMillis() - latestTime;
            String time = PalletTown.millisToTime(millis);
            System.out.println("proxy running time: " + time + ", usages: " + usages);
            return (millis < RESET_TIME && usages < MAX_USES);
        }catch (NullPointerException e){
            System.out.println("NPE");
        }

        return false;
    }

    public String IP(){
        return ip;
    }

    public long WaitTime() {
        return Math.max(RESET_TIME - (System.currentTimeMillis() - startTimes.get(0)),0);
//        return Math.max(RESET_TIME - (System.currentTimeMillis() - latestTime), 0);
    }

}
