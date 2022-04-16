package com.example.groceryproject.Model;

public class ModelUserProducts {
    String OrderId,OrderTime,OrderStatus,OrderCost,OrderBy,OrderTo;

    public ModelUserProducts() {

    }

    public ModelUserProducts(String orderId, String orderTime, String orderStatus, String orderCost, String orderBy, String orderTo) {
        OrderId = orderId;
        OrderTime = orderTime;
        OrderStatus = orderStatus;
        OrderCost = orderCost;
        OrderBy = orderBy;
        OrderTo = orderTo;
    }



    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderCost() {
        return OrderCost;
    }

    public void setOrderCost(String orderCost) {
        OrderCost = orderCost;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getOrderTo() {
        return OrderTo;
    }

    public void setOrderTo(String orderTo) {
        OrderTo = orderTo;
    }
}
