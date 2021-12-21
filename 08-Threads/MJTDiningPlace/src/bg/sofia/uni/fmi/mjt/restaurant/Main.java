package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Restaurant restaurant = new MJTDiningPlace(3);

        AbstractCustomer customer = new VipCustomer(restaurant);
        AbstractCustomer customer1 = new Customer(restaurant);
        AbstractCustomer customer2 = new Customer(restaurant);
        AbstractCustomer customer3 = new VipCustomer(restaurant);

        customer.start();
        customer1.start();
        customer2.start();
        customer3.start();

        try {
            customer.join();
            customer1.join();
            customer2.join();
            customer3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restaurant.close();
        Thread.sleep(2000);
        System.out.println(restaurant.getOrdersCount());
    }
}
