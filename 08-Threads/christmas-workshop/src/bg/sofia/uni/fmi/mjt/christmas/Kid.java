package bg.sofia.uni.fmi.mjt.christmas;

import java.util.Random;

public class Kid extends Thread {

    private static final int MAX_TIME_FOR_KID_TO_ORDER_A_PRESENT = 5000;

    private final Workshop workshop;

    public Kid(Workshop workshop) {
        this.workshop = workshop;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(MAX_TIME_FOR_KID_TO_ORDER_A_PRESENT));
        } catch (InterruptedException e) {
            System.err.println("A problem occurred while choosing a gift: " + e.getMessage());
            e.printStackTrace();
        }
        workshop.postWish(Gift.getGift());
    }

}
