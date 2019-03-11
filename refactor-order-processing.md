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