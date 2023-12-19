package net.boster.escape.from.tarkov.data.database;

public class DatabaseRunnable {

    private static boolean isDisabled = true;
    private String returns;

    public static void disable() {
        isDisabled = true;
    }

    public static void enable() {
        isDisabled = false;
    }

    public String getReturns() {
        return returns;
    }

    public void setReturns(String s) {
        returns = s;
    }

    public void run(Runnable run) {
        if(!isDisabled) {
            new Thread(run).start();
        } else {
            run.run();
        }
    }
}
