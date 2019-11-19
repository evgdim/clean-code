package com.github.evgdim.restest.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        NewOrder order = new NewOrder(LocalDateTime.now(), new ArrayList<>(), "some client");
        order.addItem("some item");
        ProcessedOrder processedOrder = order.process(LocalDateTime.now(), "courier");
        DeliveredOrder deliveredOrder = processedOrder.deliver(LocalDateTime.now());
        System.out.println(deliveredOrder);
    }
}
