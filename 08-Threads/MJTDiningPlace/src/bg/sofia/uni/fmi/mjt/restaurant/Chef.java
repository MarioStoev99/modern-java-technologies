package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {

    private final int id;
    private final Restaurant restaurant;
    private int cookedMealsCount;

    public Chef(int id, Restaurant restaurant) {
        this.id = id;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        Order order;
        while ((order = restaurant.nextOrder()) != null) {
            try {
                Thread.sleep(order.meal().getCookingTime());
                cookedMealsCount++;
            } catch (InterruptedException e) {
                System.err.print("Problem occurred while cooking :" + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Chef with id " + id + " cooked " + cookedMealsCount + " meals!");
    }

    public int getTotalCookedMeals() {
        return cookedMealsCount;
    }

}
