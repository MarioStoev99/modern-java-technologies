package bg.sofia.uni.fmi.mjt.restaurant.comparators;

import bg.sofia.uni.fmi.mjt.restaurant.Order;

import java.util.Comparator;

public class VipOrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order first, Order second) {
        boolean firstHasVipCard = first.customer().hasVipCard();
        boolean secondHasVipCard = second.customer().hasVipCard();

        if (firstHasVipCard && !secondHasVipCard) {
            return -1;
        } else if (!firstHasVipCard && secondHasVipCard) {
            return 1;
        } else {
            return Integer.compare(second.meal().getCookingTime(), first.meal().getCookingTime());
        }
    }

}
