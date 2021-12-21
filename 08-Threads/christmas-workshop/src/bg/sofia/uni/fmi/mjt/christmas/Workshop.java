package bg.sofia.uni.fmi.mjt.christmas;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Workshop {

    private static final int WORKER_ELVES = 4;

    private final Queue<Gift> gifts;
    private final Elf[] elves;
    private final AtomicInteger wishCount;
    private boolean isChristmasTime;

    public Workshop() {
        this.gifts = new ArrayDeque<>();
        this.elves = new Elf[WORKER_ELVES];
        for (int i = 0; i < WORKER_ELVES; ++i) {
            elves[i] = new Elf(i, this);
            elves[i].start();
        }
        this.wishCount = new AtomicInteger(0);
    }

    public void postWish(Gift gift) {
        wishCount.incrementAndGet();

        synchronized (this) {
            gifts.add(gift);
            this.notifyAll();
        }
    }

    public Elf[] getElves() {
        return elves;
    }

    public synchronized Gift nextGift() {
        while (gifts.isEmpty() && !isChristmasTime) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.err.println("A problem occurred while processing: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return gifts.isEmpty() ? null : gifts.remove();
    }

    public AtomicInteger getWishCount() {
        return wishCount;
    }

    public synchronized void setChristmasTime() {
        this.isChristmasTime = true;
        this.notifyAll();
    }

}
