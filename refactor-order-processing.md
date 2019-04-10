# Initial
```java
public void processToBeProcessedOrinocoOrders() {
        List<OrinocoOrder> ordersToBeProcessed = null;
        try {
            ordersToBeProcessed = jdbcTemplate.query("SELECT * ORDERS WHERE ...", OrderRowMapper.class);
        } catch (Exception e) {
            return;
        }
        Short numberSuccess = 0;
        Short numberFailure = 0;
        for (OrinocoOrder order : ordersToBeProcessed) {
            OrinocoEmailConfig config = null;
            try {
                config = props.findByOriginSystem(order.getOriginatingSystem());
                if(config != null) {
                    if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
                        if(order.getStructuredData() != null) {
                            List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                            order.getStructuredData().setListItemInfo(itemInfos);
                        }
                        OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
                        orderRequestBom.setOrder(order);
                        OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
                        if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                            oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
                            numberSuccess++;
                        } else {
                            numberFailure++;
                            incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
                        }
                    } else {
                    }
                } else {
                }
            } catch (Exception e) {
                numberFailure++;
                try {
                    incrementRetryCount(order.getId(), config, e.getMessage());
                } catch (Exception incE) {
                }
            }
        }
        try {
            MonitorOrinoco monitor = new MonitorOrinoco();
            monitor.setNumberToSend((long)(numberFailure + numberSuccess));
            monitor.setNumberProcessed((long)numberSuccess);
            monitor.setNumberFailure((long)numberFailure);
            this.oroRepo.writeOrinocoMonitor(monitor); 
        } catch (Exception e) {
        }
    }
```

# 1. Exctract lower abstraction - sql execution

```diff
public void processToBeProcessedOrinocoOrders() {
        List<OrinocoOrder> ordersToBeProcessed = null;
        try {
-           ordersToBeProcessed = jdbcTemplate.query("SELECT * ORDERS WHERE ...", OrderRowMapper.class);
+           ordersToBeProcessed = oroRepo.findOrinocoOrdersByStatus(StatusEnum.TO_BE_PROCESSED);
        } catch (Exception e) {
            return;
        }
        Short numberSuccess = 0;
        Short numberFailure = 0;
...
```

# Extract method - getOrder or return empry. 
```diff
public void processToBeProcessedOrinocoOrders() {
-        List<OrinocoOrder> ordersToBeProcessed = null;
-        try {
-            ordersToBeProcessed = oroRepo.findOrinocoOrdersByStatus(StatusEnum.TO_BE_PROCESSED);
-        } catch (Exception e) {
-            return;
-        }

+       List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();
        Short numberSuccess = 0;
        Short numberFailure = 0;
...

+    private List<OrinocoOrder> getOrdersOrEmpty() {
+        try {
+            return oroRepo.findOrinocoOrdersByStatus(StatusEnum.TO_BE_PROCESSED);
+        } catch (Exception e) {
+            return new ArrayList<>();
+        }
+    };
```


# 3. Exctract method - monitor 

```diff
...
            } catch (Exception e) {
                numberFailure++;
                try {
                    incrementRetryCount(order, config, e.getMessage());
                } catch (Exception incE) {
                }
            }
        }
-        try {
-            MonitorOrinoco monitor = new MonitorOrinoco();
-            monitor.setNumberToSend((long)(numberFailure + numberSuccess));
-            monitor.setNumberProcessed((long)numberSuccess);
-            monitor.setNumberFailure((long)numberFailure);
-            this.oroRepo.writeOrinocoMonitor(monitor); 
-        } catch (Exception e) {
-        }
+        writeMonitor(numberSuccess, numberFailure);
    }

+    private void writeMonitor(Short numberSuccess, Short numberFailure) {
+        try {
+            MonitorOrinoco monitor = new MonitorOrinoco();
+            monitor.setNumberToSend((long)(numberFailure + numberSuccess));
+            monitor.setNumberProcessed((long)numberSuccess);
+            monitor.setNumberFailure((long)numberFailure);
+            this.oroRepo.writeOrinocoMonitor(monitor);
+        } catch (Exception e) {
+        }
+    }
```

# Check point
```java
    public void processToBeProcessedOrinocoOrders() {
        List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();

        Short numberSuccess = 0;
        Short numberFailure = 0;
        for (OrinocoOrder order : ordersToBeProcessed) {
            OrinocoEmailConfig config = null;
            try {
                config = props.findByOriginSystem(order.getOriginatingSystem());
                if(config != null) {
                    if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
                        if(order.getStructuredData() != null) {
                            List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                            order.getStructuredData().setListItemInfo(itemInfos);
                        }
                        OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
                        orderRequestBom.setOrder(order);
                        OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
                        if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                            oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
                            numberSuccess++;
                        } else {
                            numberFailure++;
                            incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
                        }
                    } else {
                    }
                } else {
                }
            } catch (Exception e) {
                numberFailure++;
                try {
                    incrementRetryCount(order.getId(), config, e.getMessage());
                } catch (Exception incE) {
                }
            }
        }
        writeMonitor(numberSuccess, numberFailure);
    }
```

# 4. Extract method - for loop body
```java
    public void processToBeProcessedOrinocoOrders() {
        List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();

        Short numberSuccess = 0;
        Short numberFailure = 0;
        for (OrinocoOrder order : ordersToBeProcessed) {
            processOrder(order, numberSuccess, numberFailure);
        }
        writeMonitor(numberSuccess, numberFailure);
    }

    private void processOrder(OrinocoOrder order, Short numberSuccess, Short numberFailure) {
        OrinocoEmailConfig config = null;
        try {
            config = props.findByOriginSystem(order.getOriginatingSystem());
            if(config != null) {
                if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
                    if(order.getStructuredData() != null) {
                        List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                        order.getStructuredData().setListItemInfo(itemInfos);
                    }
                    OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
                    orderRequestBom.setOrder(order);
                    OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
                    if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                        oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
                        numberSuccess++;
                    } else {
                        numberFailure++;
                        incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
                    }
                } else {
                }
            } else {
            }
        } catch (Exception e) {
            numberFailure++;
            try {
                incrementRetryCount(order.getId(), config, e.getMessage());
            } catch (Exception incE) {
            }
        }
    }
```

# 5. Remove external state

```diff
+enum ProcessOrderResult {
+   SUCCESS, ERROR, NOT_PROCESSED
+}

-private void processOrder(OrinocoOrder order, Short numberSuccess, Short numberFailure) {
+private ProcessOrderResult processOrder(OrinocoOrder order) {
        OrinocoEmailConfig config = null;
        try {
            config = props.findByOriginSystem(order.getOriginatingSystem());
            if(config != null) {
                if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
                    if(order.getStructuredData() != null) {
                        List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                        order.getStructuredData().setListItemInfo(itemInfos);
                    }
                    OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
                    orderRequestBom.setOrder(order);
                    OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
                    if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                        oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
-                       numberSuccess++;
+                       return ProcessOrderResult.SUCCESS;
                    } else {
-                        numberFailure++;
                        incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
+                       return ProcessOrderResult.ERROR;
                    }
                } else {
+                    return ProcessOrderResult.NOT_PROCESSED;
                }
            } else {
+                return ProcessOrderResult.NOT_PROCESSED;
            }
        } catch (Exception e) {
-            numberFailure++;
            try {
                incrementRetryCount(order.getId(), config, e.getMessage());
            } catch (Exception incE) {
            }
+            return ProcessOrderResult.ERROR;
        }
    }
```

```diff
- public void processToBeProcessedOrinocoOrders() {
-    List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();
-
-    Short numberSuccess = 0;
-    Short numberFailure = 0;
-    for (OrinocoOrder order : ordersToBeProcessed) {
-        processOrder(order, numberSuccess, numberFailure);
-    }
-    writeMonitor(numberSuccess, numberFailure);
-}

+public void processToBeProcessedOrinocoOrders() {
+    List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();
+
+    List<ProcessOrderResult> processOrderResults =
+            ordersToBeProcessed.stream()
+            .map(this::processOrder)
+            .collect(Collectors.toList());
+
+    Long numberFailure = processOrderResults.stream().filter(r -> r == ProcessOrderResult.ERROR).count();
+    Long numberSuccess = processOrderResults.stream().filter(r -> r == ProcessOrderResult.SUCCESS).count();
+    writeMonitor(numberSuccess.shortValue(), numberFailure.shortValue());
+}
```

# Check point
```java
public void processToBeProcessedOrinocoOrders() {
    List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();

    List<ProcessOrderResult> processOrderResults =
            ordersToBeProcessed.stream()
            .map(this::processOrder)
            .collect(Collectors.toList());

    Long numberFailure = processOrderResults.stream().filter(r -> r == ProcessOrderResult.ERROR).count();
    Long numberSuccess = processOrderResults.stream().filter(r -> r == ProcessOrderResult.SUCCESS).count();
    writeMonitor(numberSuccess.shortValue(), numberFailure.shortValue());
}

enum ProcessOrderResult {
    SUCCESS, ERROR, NOT_PROCESSED
}

private ProcessOrderResult processOrder(OrinocoOrder order) {
    OrinocoEmailConfig config = null;
    try {
        config = props.findByOriginSystem(order.getOriginatingSystem());
        if(config != null) {
            if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
                if(order.getStructuredData() != null) {
                    List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                    order.getStructuredData().setListItemInfo(itemInfos);
                }
                OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
                orderRequestBom.setOrder(order);
                OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
                if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                    oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
                    return ProcessOrderResult.SUCCESS;
                } else {
                    incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
                    return ProcessOrderResult.ERROR;
                }
            } else {
                return ProcessOrderResult.NOT_PROCESSED;
            }
        } else {
            return ProcessOrderResult.NOT_PROCESSED;
        }
    } catch (Exception e) {
        try {
            incrementRetryCount(order.getId(), config, e.getMessage());
        } catch (Exception incE) {
        }
        return ProcessOrderResult.ERROR;
    }
}
```

# 6. Reduce indentation - extract method

```java
private ProcessOrderResult processOrder(OrinocoOrder order) {
        OrinocoEmailConfig config = null;
        try {
            config = props.findByOriginSystem(order.getOriginatingSystem());
            if(config != null) {
                return processOrderWithConfig(order, config); // Logic extracted
            } else {
                return ProcessOrderResult.NOT_PROCESSED;
            }
        } catch (Exception e) {
            try {
                incrementRetryCount(order.getId(), config, e.getMessage());
            } catch (Exception incE) {
            }
            return ProcessOrderResult.ERROR;
        }
    }

private ProcessOrderResult processOrderWithConfig(OrinocoOrder order, OrinocoEmailConfig config) {
        if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
            if(order.getStructuredData() != null) {
                List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
                order.getStructuredData().setListItemInfo(itemInfos);
            }
            OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
            orderRequestBom.setOrder(order);
            OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
            if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
                oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
                return ProcessOrderResult.SUCCESS;
            } else {
                incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
                return ProcessOrderResult.ERROR;
            }
        } else {
            return ProcessOrderResult.NOT_PROCESSED;
        }
    }
```

# 7. Reduce indentation - extract method again

```java
private ProcessOrderResult processOrderWithConfig(OrinocoOrder order, OrinocoEmailConfig config) {
    if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
        return processOrderWhenMaxCountNotReached(order, config);
    } else {
        return ProcessOrderResult.NOT_PROCESSED;
    }
}

private ProcessOrderResult processOrderWhenMaxCountNotReached(OrinocoOrder order, OrinocoEmailConfig config) {
    if(order.getStructuredData() != null) {
        List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
        order.getStructuredData().setListItemInfo(itemInfos);
    }
    OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
    orderRequestBom.setOrder(order);
    OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
    if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
        oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
        return ProcessOrderResult.SUCCESS;
    } else {
        incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
        return ProcessOrderResult.ERROR;
    }
}
```

# Final

```java
public void processToBeProcessedOrinocoOrders() {
    List<OrinocoOrder> ordersToBeProcessed = getOrdersOrEmpty();

    List<ProcessOrderResult> processOrderResults =
            ordersToBeProcessed.stream()
            .map(this::processOrder)
            .collect(Collectors.toList());

    Long numberFailure = processOrderResults.stream().filter(r -> r == ProcessOrderResult.ERROR).count();
    Long numberSuccess = processOrderResults.stream().filter(r -> r == ProcessOrderResult.SUCCESS).count();
    writeMonitor(numberSuccess.shortValue(), numberFailure.shortValue());
}

enum ProcessOrderResult {
    SUCCESS, ERROR, NOT_PROCESSED
}

private ProcessOrderResult processOrder(OrinocoOrder order) {
    OrinocoEmailConfig config = null;
    try {
        config = props.findByOriginSystem(order.getOriginatingSystem());
        if(config != null) {
            return processOrderWithConfig(order, config);
        } else {
            return ProcessOrderResult.NOT_PROCESSED;
        }
    } catch (Exception e) {
        try {
            incrementRetryCount(order.getId(), config, e.getMessage());
        } catch (Exception incE) {
        }
        return ProcessOrderResult.ERROR;
    }
}

private ProcessOrderResult processOrderWithConfig(OrinocoOrder order, OrinocoEmailConfig config) {
    if(order.getRetryCount() == null || order.getRetryCount() <= config.getMaxCount()) {
        return processOrderWhenMaxCountNotReached(order, config);
    } else {
        return ProcessOrderResult.NOT_PROCESSED;
    }
}

private ProcessOrderResult processOrderWhenMaxCountNotReached(OrinocoOrder order, OrinocoEmailConfig config) {
    if(order.getStructuredData() != null) {
        List<ItemInfo> itemInfos = oroRepo.findItemInfosForOrder(order.getStructuredData().getTeckId());
        order.getStructuredData().setListItemInfo(itemInfos);
    }
    OrinocoOrderRequest orderRequestBom = new OrinocoOrderRequest();
    orderRequestBom.setOrder(order);
    OrinocoOrderResponse sendOrderResp = oroOrderService.sendOrder(orderRequestBom);
    if(sendOrderResp.getAdvice().getStatus() == AdviceStatusEnum.SUCCESS) {
        oroRepo.setStatus(order.getId(), StatusEnum.PROCESSED);
        return ProcessOrderResult.SUCCESS;
    } else {
        incrementRetryCount(order.getId(), config, sendOrderResp.getAdvice().getMessageText());
        return ProcessOrderResult.ERROR;
    }
}

private void writeMonitor(Short numberSuccess, Short numberFailure) {
    try {
        MonitorOrinoco monitor = new MonitorOrinoco();
        monitor.setNumberToSend((long)(numberFailure + numberSuccess));
        monitor.setNumberProcessed((long)numberSuccess);
        monitor.setNumberFailure((long)numberFailure);
        this.oroRepo.writeOrinocoMonitor(monitor);
    } catch (Exception e) {
    }
}

private void incrementRetryCount(Long orderId, OrinocoEmailConfig config, String messageText) {
}

private List<OrinocoOrder> getOrdersOrEmpty() {
    try {
        return oroRepo.findOrinocoOrdersByStatus(StatusEnum.TO_BE_PROCESSED);
    } catch (Exception e) {
        return new ArrayList<>();
    }
};
```