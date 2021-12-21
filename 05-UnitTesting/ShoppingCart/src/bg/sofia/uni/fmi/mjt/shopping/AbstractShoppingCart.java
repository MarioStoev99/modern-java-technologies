package bg.sofia.uni.fmi.mjt.shopping;

public abstract class AbstractShoppingCart implements ShoppingCart {

    protected ProductCatalog catalog;

    protected AbstractShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    protected void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }
}
