package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exception.CapacityExceededException;

import java.time.LocalDateTime;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Parcel parcel1 = new Parcel(5, LocalDateTime.of(2021, 10, 8, 1, 1, 1));
        Parcel parcel2 = new Parcel(6, LocalDateTime.of(2021, 10, 9, 1, 1, 1));
        Parcel parcel3 = new Parcel(7, LocalDateTime.of(2021, 10, 12, 1, 1, 1));
        Parcel parcel4 = new Parcel(8, LocalDateTime.of(2021, 10, 12, 1, 1, 1));
        DeliveryServiceWarehouse<Integer, Parcel> wareHouse = new MJTExpressWarehouse<>(3, 10);
        try {
            wareHouse.submitParcel(1, parcel1, parcel1.submissionDate());
            wareHouse.submitParcel(2, parcel2, parcel2.submissionDate());
            wareHouse.submitParcel(3, parcel3, parcel3.submissionDate());
            wareHouse.submitParcel(4, parcel4, parcel4.submissionDate());
            Map<Integer, Parcel> parcels1 = wareHouse.getWarehouseItems();
            System.out.println(parcels1);
            System.out.println(wareHouse.getWarehouseSpaceLeft());
            Map<Integer, Parcel> parcels = wareHouse.deliverParcelsSubmittedAfter(LocalDateTime.of(2021, 10, 10, 1, 1, 1));
            Map<Integer, Parcel> parcels2 = wareHouse.getWarehouseItems();
            System.out.println(parcels2);
            System.out.println(parcels);
        } catch (CapacityExceededException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
