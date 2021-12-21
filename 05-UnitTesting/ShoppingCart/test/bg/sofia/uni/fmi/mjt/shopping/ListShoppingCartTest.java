package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListShoppingCartTest {

    private final Item apple = new Apple("123");
    private final Item chocolate = new Chocolate("1234");

    private ShoppingCart shoppingCart;

    @Mock
    private ProductCatalog productCatalogMock;

    // setUp and setUpClass for before and beforeClass
    // tearDown,tearDownClass for after and afterClass
    @Before
    public void setUp() {
        shoppingCart = new ListShoppingCart(productCatalogMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNull() {
        shoppingCart.addItem(null);
    }

    @Test
    public void testAddItemTwoEqualApplesAndOneChocolate() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        shoppingCart.addItem(apple);

        assertEquals("It must have two elements in our collection", 2, shoppingCart.getUniqueItems().size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNull() {
        shoppingCart.removeItem(null);
    }

    @Test
    public void testRemoveItemSuccess() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        shoppingCart.removeItem(apple);

        assertEquals("We must have only an element in our collection after we had removed the apple from the cart", 1, shoppingCart.getUniqueItems().size());
    }

    @Test
    public void testGetUniqueItems() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        shoppingCart.addItem(apple);

        assertEquals("We expect only two items in our shopping cart", 2, shoppingCart.getUniqueItems().size());
    }

    @Test
    public void testGetTotalSumWhichMustBePaid() {
        double applePrice = 0.70;
        double chocolatePrice = 2.70;

        when(productCatalogMock.getProductInfo("123")).thenReturn(new ProductInfo("apple", "...", applePrice));
        when(productCatalogMock.getProductInfo("1234")).thenReturn(new ProductInfo("chocolate", "...", chocolatePrice));

        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        double expectedTotalPrice = applePrice + chocolatePrice;

        assertEquals("We expect 0.70 + 2.00 to be equal to 2.70", expectedTotalPrice, shoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetSortedItems() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        shoppingCart.addItem(chocolate);
        Collection<Item> actual = new ArrayList<>(shoppingCart.getSortedItems());

        Collection<Item> expected = new ArrayList<Item>();
        expected.add(chocolate);
        expected.add(apple);

        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}
