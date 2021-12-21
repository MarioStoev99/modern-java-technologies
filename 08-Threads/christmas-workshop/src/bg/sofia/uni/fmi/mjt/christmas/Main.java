package bg.sofia.uni.fmi.mjt.christmas;

public class Main {

    private static final int NUMBER_OF_WISHESH = 10;

    public static void main(String[] args) throws InterruptedException {
        Workshop workshop = new Workshop();

        Kid[] kids = new Kid[NUMBER_OF_WISHESH];
        for (int i = 0; i < NUMBER_OF_WISHESH; i++) {
            kids[i] = new Kid(workshop);
            kids[i].start();
        }
        for (int i = 0; i < NUMBER_OF_WISHESH; i++) {
            kids[i].join();
        }
        System.out.println(workshop.getWishCount());
        Thread.sleep(1000);

        workshop.setChristmasTime();

        int craftedGifts = 0;
        for(Elf elf : workshop.getElves()) {
            craftedGifts += elf.getTotalGiftsCrafted();
        }
        System.out.println("Developed gifts : " + craftedGifts);

    }
}
