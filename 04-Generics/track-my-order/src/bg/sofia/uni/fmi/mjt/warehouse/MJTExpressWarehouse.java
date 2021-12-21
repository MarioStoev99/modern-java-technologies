package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exception.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exception.ParcelNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {

    private final int capacity;
    private final int retentionPeriod;

    private final Map<L, Parcel<P>> wareHouse;

    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        this.wareHouse = new HashMap<>(capacity);
    }

    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {
        checkIsNotNull(label, "Label");
        checkIsNotNull(parcel, "Parcel");

        long difference = Duration.between(submissionDate, LocalDateTime.now()).toNanos();
        if (difference < 0) {
            throw new IllegalArgumentException("The provided date is date in the future!");
        }

        if (capacity <= wareHouse.size()) {
            // handleCapacityReached
            if (canRemoveItems()) {
                removeItems();
            } else {
                throw new CapacityExceededException("The ware house is full");
            }
        }

        wareHouse.put(label, new Parcel(parcel, submissionDate));
    }

    @Override
    public P getParcel(L label) {
        checkIsNotNull(label, "Label");

        if (!wareHouse.containsKey(label)) {
            return null;
        }

        return wareHouse.get(label).parcel();
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        checkIsNotNull(label, "Label");

        if (!wareHouse.containsKey(label)) {
            throw new ParcelNotFoundException("This parcel does not exist in the system");
        }

        return wareHouse.remove(label).parcel();
    }

    @Override
    public double getWarehouseSpaceLeft() {
        if (capacity == 0) {
            return 0;
        }

        double loadFactor = (double) wareHouse.size() / capacity;
        return Math.round(loadFactor * 100) / 100.00;
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        Map<L, P> wareHouseItemsWithoutDates = new HashMap<>();
        for (Map.Entry<L, Parcel<P>> entry : wareHouse.entrySet()) {
            wareHouseItemsWithoutDates.put(entry.getKey(), entry.getValue().parcel());
        }
        return wareHouseItemsWithoutDates;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        checkIsNotNull(before, "Date");

        Map<L, P> items = getItemsAccordingToDate(before, difference -> difference > 0);
        removeItems(items);
        return items;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        checkIsNotNull(after, "Date");

        Map<L, P> items = getItemsAccordingToDate(after, difference -> difference < 0);
        removeItems(items);
        return items;
    }

    private boolean existExpiredItems() {
        Map<L, P> expiredItems = getItemsAccordingToDate(LocalDateTime.now(), difference -> difference > retentionPeriod);
        boolean haveExpiredItems = expiredItems.size() != 0;
        if (haveExpiredItems) {
            removeItems(expiredItems);
        }

        return haveExpiredItems;
    }

    private void removeItems(Map<L, P> items) {
        for (Map.Entry<L, P> item : items.entrySet()) {
            wareHouse.remove(item.getKey());
        }
    }

    private Map<L, P> getItemsAccordingToDate(LocalDateTime date, Predicate<Integer> predicate) {
        Map<L, P> items = new HashMap<>();

        for (Map.Entry<L, Parcel<P>> item : wareHouse.entrySet()) {
            int difference = (int) Duration.between(item.getValue().submissionDate(), date).toDays();

            if (predicate.test(difference)) {
                L label = item.getKey();
                P parcel = item.getValue().parcel();

                items.put(label, parcel);
            }
        }
        return items;
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }

}