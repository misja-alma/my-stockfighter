package org.misja.stockfighter;

import java.io.IOException;
import java.util.Optional;

public class BlockTrader {

    private final Api api = new Api();

    public void blockBuy(int qty, String account, String symbol, String venue) throws IOException {
        // until qty is 0; get asks from orderbook, buy min(qty, ask.qty) fill or kill at ask price
        // Start with taking a midmarket. That will be the target.
        int target = getTargetPriceFromMarket(venue, symbol);

        int qtyLeft = qty;
        while(qtyLeft > 0) {
            OrderBook orderBook = api.getOrderBook(venue, symbol);
            if (orderBook.asks.length > 0) {
                int qtyBuy = Math.min(qtyLeft, orderBook.asks[0].qty);
                int price = orderBook.asks[0].price;

                // Alternatively, instead of skipping we could also put limit orders in the market.
                if (price <= target) {
                    Order order = new Order();
                    order.account = account;
                    order.direction = "buy";
                    order.orderType = "fill-or-kill";
                    order.price = price;
                    order.qty = qtyBuy;
                    order.venue = venue;
                    order.stock = symbol;
                    OrderStatus status = api.placeOrder(order);
                    if (status.ok) {
                        System.out.println("Placed order for price: " + price + " and qty " + qtyBuy + " filled for: " + status.totalFilled);
                        qtyLeft -= status.totalFilled;
                    } else {
                        System.out.println("Order failed!");
                    }
                } else {
                    System.out.println("No good price, waiting ..");
                }
            } else {
                System.out.println("No asks!");
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
        }
    }

    private int getTargetPriceFromMarket(String venue, String symbol) throws IOException {
        Optional<Integer> mid;
        while(!(mid = getMidMarket(venue, symbol)).isPresent()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }
        return mid.get();
    }

    private Optional<Integer> getMidMarket(String venue, String symbol) throws IOException {
        OrderBook orderBook = api.getOrderBook(venue, symbol);
        return OrderBookUtils.getMidMarket(orderBook);
    }
}
