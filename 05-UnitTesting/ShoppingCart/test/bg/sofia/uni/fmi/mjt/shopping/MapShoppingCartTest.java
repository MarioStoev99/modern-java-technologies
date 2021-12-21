package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapShoppingCartTest {

    private ShoppingCart shoppingCart;
    private final Item apple = new Apple("123");
    private final Item chocolate = new Chocolate("1234");

    @Mock
    private ProductCatalog productCatalogMock;

    @Before
    public void setUp() {
        shoppingCart = new MapShoppingCart(productCatalogMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNull() {
        shoppingCart.addItem(null);
    }

    @Test
    public void testAddTheSameItemTwoTimes() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);

        assertEquals("We expect one unique item because we add the same item two times", 1, shoppingCart.getUniqueItems().size());
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemWhichDoesNotExist() {
        shoppingCart.removeItem(apple);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNull() {
        shoppingCart.removeItem(null);
    }

    @Test
    public void testAddTheSameItemTwoTimesAndRemoveThemTwoTimes() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);

        shoppingCart.removeItem(apple);
        assertEquals("We add two apples and remove only one,so we must have 1", 1, shoppingCart.getUniqueItems().size());
        shoppingCart.removeItem(apple);
        assertEquals("Then we remove another one from our collection and now they are 0", 0, shoppingCart.getUniqueItems().size());
    }

    @Test
    public void testGetUniqueItems() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);

        assertEquals("We add 3 items but two of them are with the same id,so we have 2 products", 2, shoppingCart.getUniqueItems().size());
    }

    @Test
    public void testGetTotalWithoutElementsInOurShoppingCart() {
        assertEquals("There are no elements in our shopping cart", 0, shoppingCart.getTotal(),0.01);
    }

    @Test
    public void testGetTotalSumWhenWeBuyTwoApplesAndOneChocolate()  {
        double applePrice = 0.70;
        double chocolatePrice = 2.70;

        when(productCatalogMock.getProductInfo("123")).thenReturn(new ProductInfo("apple", "...", applePrice));
        when(productCatalogMock.getProductInfo("1234")).thenReturn(new ProductInfo("chocolate", "...", chocolatePrice));

        shoppingCart.addItem(apple);
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);

        assertEquals("2*0.70 + 2.70 = 4.10", applePrice * 2 + chocolatePrice, shoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetSortedItems() {
        shoppingCart.addItem(apple);
        shoppingCart.addItem(chocolate);
        shoppingCart.addItem(chocolate);
        Collection<Item> actual = shoppingCart.getSortedItems();

        Collection<Item> expected = new ArrayList<>();
        expected.add(chocolate);
        expected.add(apple);

        assertArrayEquals("We have 2 chocolates and one apple", expected.toArray(), actual.toArray());
    }
}
