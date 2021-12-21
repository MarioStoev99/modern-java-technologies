package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MJTDiningPlaceTest {

    private Restaurant restaurant;

    @Before
    public void setUp() {
        restaurant = new MJTDiningPlace(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubmitOrderNull() {
        restaurant.submitOrder(null);
    }

    @Test
    public void testSubmitOrderWith10Customers() throws InterruptedException {
        int customerCount = 10;

        AbstractCustomer[] customers = new AbstractCustomer[customerCount];
        for (int i = 0; i < customerCount; i++) {
            if (i % 2 == 0) {
                customers[i] = new VipCustomer(restaurant);
            } else {
                customers[i] = new Customer(restaurant);
            }
            customers[i].start();
        }

        for (int i = 0; i < customerCount; i++) {
            customers[i].join();
        }

        assertEquals(customerCount, restaurant.getOrdersCount());
    }

    @Test
    public void testSubmitOrder() throws InterruptedException {
        AbstractCustomer customer = new VipCustomer(restaurant);
        AbstractCustomer customer1 = new Customer(restaurant);
        AbstractCustomer customer2 = new Customer(restaurant);

        customer.start();
        customer1.start();
        customer2.start();

        customer.join();
        customer1.join();
        customer2.join();

        assertEquals(3, restaurant.getOrdersCount());
    }

    @Test
    public void testGetChefs() {
        assertEquals(3, restaurant.getChefs().length);
    }
}
