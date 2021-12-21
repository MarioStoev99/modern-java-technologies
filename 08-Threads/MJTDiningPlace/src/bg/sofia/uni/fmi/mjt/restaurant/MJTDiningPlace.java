package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.comparators.VipOrderComparator;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MJTDiningPlace implements Restaurant {

    private final Queue<Order> orders;
    private final Chef[] chefs;
    private final AtomicInteger orderCount;
    private boolean isClosed;

    public MJTDiningPlace(int chefsCount) {
        this.orders = new PriorityQueue<>(new VipOrderComparator());

        this.chefs = new Chef[chefsCount];
        for (int i = 0; i < chefsCount; ++i) {
            this.chefs[i] = new Chef(i, this);
            this.chefs[i].setName("chef" + i);
            this.chefs[i].start();
        }
        this.orderCount = new AtomicInteger(0);
    }

    @Override
    public void submitOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null!");
        }
        synchronized (this) {
            orders.add(order);
            this.notifyAll();
        }
        orderCount.incrementAndGet();
    }

    @Override
    public synchronized Order nextOrder() {
        while (orders.isEmpty() && !isClosed) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.err.println("A problem occurred while processing: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return orders.isEmpty() ? null : orders.poll();
    }

    @Override
    public int getOrdersCount() {
        return orderCount.get();
    }

    @Override
    public Chef[] getChefs() {
        return chefs;
    }

    @Override
    public synchronized void close() {
        isClosed = true;
        this.notifyAll();
    }

}
