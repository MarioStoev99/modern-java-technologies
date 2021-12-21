package bg.sofia.uni.fmi.mjt.shopping;

import java.util.*;

import bg.sofia.uni.fmi.mjt.shopping.item.AbstractItem;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class ListShoppingCart extends AbstractShoppingCart {

    private final List<Item> items;

    public ListShoppingCart(ProductCatalog catalog) {
        super(catalog);
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new HashSet<>(items);
    }

    @Override
    public void addItem(Item item) throws ItemNotFoundException {
        checkIsNotNull(item, "Item");

        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        checkIsNotNull(item, "Item");
        if (!items.contains(item)) {
            throw new ItemNotFoundException("The provided item doesn't exist!");
        }

        items.remove(item);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = getUniqueItemsInMap();
        Map<Item, Integer> sortedItems = new TreeMap<>(new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return itemToQuantity.get(item2).compareTo(itemToQuantity.get(item1));
            }
        });
        sortedItems.putAll(itemToQuantity);
        return sortedItems.keySet();
    }

    private Map<Item, Integer> getUniqueItemsInMap() {
        Map<Item, Integer> itemToQuantity = new HashMap<>();
        for (Item item : items) {
            itemToQuantity.put(item, itemToQuantity.containsKey(item) ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}

