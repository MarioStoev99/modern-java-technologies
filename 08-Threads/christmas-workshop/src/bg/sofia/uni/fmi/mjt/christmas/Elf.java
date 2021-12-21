package bg.sofia.uni.fmi.mjt.christmas;

public class Elf extends Thread {

    private final int id;
    private final Workshop workshop;
    private int totalGiftsCrafted;

    public Elf(int id, Workshop workshop) {
        this.id = id;
        this.workshop = workshop;
    }

    @Override
    public void run() {
        craftGift();
    }

    public void craftGift() {
        Gift gift;
        while ((gift = workshop.nextGift()) != null) {
            try {
                Thread.sleep(gift.getCraftTime());
                totalGiftsCrafted++;
            } catch (InterruptedException e) {
                System.err.println("A problem occurred while processing" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public int getTotalGiftsCrafted() {
        return totalGiftsCrafted;
    }
}
