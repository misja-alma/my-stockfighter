package org.misja.stockfighter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiTest {
    private Api api = new Api();

    @Test
    public void healthCheckShouldReturnOk() throws Exception {
        assertTrue(api.healthCheck());
    }

    @Test
    public void healthCheckForVenueShouldReturnOk() throws Exception {
        assertTrue(api.healthCheckForVenue("TESTEX"));
    }

    @Test
    public void getOrderBookShouldReturnOrderBook() throws Exception {
        OrderBook orderBook = api.getOrderBook("TESTEX", "FOOBAR");
        assertTrue(orderBook.ok);
    }

    @Test
    public void shouldPlaceOrder() throws Exception {
        Order order = new Order();
        order.account = "EXB123456";
        order.venue = "TESTEX";
        order.stock = "FOOBAR";
        order.direction = "buy";
        order.orderType = "limit";
        order.price = 10;
        order.qty = 10;
        OrderStatus orderStatus = api.placeOrder(order);
        assertTrue(orderStatus.ok);
    }
}