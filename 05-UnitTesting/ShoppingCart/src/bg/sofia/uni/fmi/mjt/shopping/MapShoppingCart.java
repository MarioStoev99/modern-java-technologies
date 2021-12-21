package bg.sofia.uni.fmi.mjt.shopping;

import java.util.*;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class MapShoppingCart extends AbstractShoppingCart {

    private final Map<Item, Integer> items;

    public MapShoppingCart(ProductCatalog catalog) {
        super(catalog);
        this.items = new HashMap<>();
    }

    public Collection<Item> getUniqueItems() {
        return items.keySet();
    }

    @Override
    public void addItem(Item item) {
        checkIsNotNull(item, "Item");

        int updatedCount = items.containsKey(item) ? items.get(item) + 1 : 1;
        items.put(item, updatedCount);
    }


    @Override
    public void removeItem(Item item) {
        checkIsNotNull(item, "Item");
        if (!items.containsKey(item)) {
            throw new ItemNotFoundException("Provided item does not exist!");
        }

        if(items.get(item) == 1) {
            items.remove(item);
        } else {
            items.put(item, items.get(item) - 1);
        }
    }


    @Override
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ProductInfo info = catalog.getProductInfo(entry.getKey().getId());
            total += info.price() * entry.getValue();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        List<Item> sortedItems = new ArrayList<>(items.keySet());
        Collections.sort(sortedItems, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return Integer.compare(items.get(item2),items.get(item1));
            }
        });
        return sortedItems;
    }

}
