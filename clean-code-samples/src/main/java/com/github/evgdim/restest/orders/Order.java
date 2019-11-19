package com.github.evgdim.restest.orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public abstract class Order {
    protected final LocalDateTime orderDate;
    protected final List<String> items;
    protected final String client;

    public Order(LocalDateTime orderDate, List<String> items, String client) {
        Objects.requireNonNull(orderDate);
        Objects.requireNonNull(items);
        Objects.requireNonNull(client);
        this.orderDate = orderDate;
        this.items = items;
        this.client = client;
    }
}

class NewOrder extends Order {
    public NewOrder(LocalDateTime orderDate, List<String> items, String client) {
        super(orderDate, items, client);
    }

    public void addItem(String item) {
        // all kind of business validation can be added here
        // logic related to adding of item can be added here - e.g. if the item already exists - increase the count
        this.items.add(item);
    }

    public void removeItem(String item) {
        this.items.remove(item);
    }

    public ProcessedOrder process(LocalDateTime sentToCourierAt, String courierName) {
        if(this.items.isEmpty()) { // no need to a null check here, because items is checked in the constructor and cannot be changed
            throw new NoOrderItemsException("You have to add items, Bro!");
        }
        return new ProcessedOrder(this.orderDate, this.items, this.client, sentToCourierAt, courierName);
    }

    public CanceledOrder cancel(LocalDateTime canceledOn, String cancelReason) {
        return new CanceledOrder(this.orderDate, this.items, this.client, canceledOn, cancelReason);
    }
}

class ProcessedOrder extends NewOrder {
    protected final LocalDateTime sentToCourierAt; // should be extracted as a class
    protected final String courierName;

    public ProcessedOrder(LocalDateTime orderDate, List<String> items, String client, LocalDateTime sentToCourierAt, String courierName) {
        super(orderDate, items, client);
        Objects.requireNonNull(courierName);
        Objects.requireNonNull(sentToCourierAt);
        this.sentToCourierAt = sentToCourierAt;
        this.courierName = courierName;
    }

    @Override
    public void addItem(String item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeItem(String item) {
        throw new UnsupportedOperationException();
    }

    public DeliveredOrder deliver(LocalDateTime deliveredOn) {
        return new DeliveredOrder(this.orderDate, this.items, this.client, this.sentToCourierAt, this.courierName, deliveredOn);
    }
}

class CanceledOrder extends NewOrder {
    private final LocalDateTime canceledOn;
    private final String cancelReason;

    public CanceledOrder(LocalDateTime orderDate, List<String> items, String client, LocalDateTime canceledOn, String cancelReason) {
        super(orderDate, items, client);
        Objects.requireNonNull(canceledOn);
        Objects.requireNonNull(cancelReason);
        this.canceledOn = canceledOn;
        this.cancelReason = cancelReason;
    }

    @Override
    public void addItem(String item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeItem(String item) {
        throw new UnsupportedOperationException();
    }
}

class DeliveredOrder extends ProcessedOrder {
    private final LocalDateTime deliveredOn;

    public DeliveredOrder(LocalDateTime orderDate, List<String> items, String client, LocalDateTime sentToCourierAt, String courierName, LocalDateTime deliveredOn) {
        super(orderDate, items, client, sentToCourierAt, courierName);
        Objects.requireNonNull(deliveredOn);
        this.deliveredOn = deliveredOn;
    }

    @Override
    public String toString() {
        return "DeliveredOrder{" +
                "orderDate=" + orderDate +
                ", items=" + items +
                ", client='" + client + '\'' +
                ", sentToCourierAt=" + sentToCourierAt +
                ", courierName='" + courierName + '\'' +
                ", deliveredOn=" + deliveredOn +
                '}';
    }
}

class NoOrderItemsException extends RuntimeException {
    public NoOrderItemsException(String message) {
        super(message);
    }
}