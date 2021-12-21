package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.Meal;
import bg.sofia.uni.fmi.mjt.restaurant.Order;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;

import java.util.Random;

public abstract class AbstractCustomer extends Thread {

    private static final int MAX_CUSTOMER_ORDER_TIME_MILLIS = 5000;

    private final Restaurant restaurant;

    protected AbstractCustomer(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(MAX_CUSTOMER_ORDER_TIME_MILLIS));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        restaurant.submitOrder(new Order(Meal.chooseFromMenu(), this));
    }

    public abstract boolean hasVipCard();

}