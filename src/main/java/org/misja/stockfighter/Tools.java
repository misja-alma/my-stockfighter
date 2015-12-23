package org.misja.stockfighter;

import java.util.Optional;

public class Tools {

    public static Optional<Integer> getMidMarket(OrderBook orderBook) {
        if (orderBook.asks != null && orderBook.asks.length > 0 && orderBook.bids != null && orderBook.bids.length > 0) {
            return Optional.of((orderBook.bids[0].price + orderBook.asks[0].price) / 2);
        } else {
            return Optional.empty();
        }
    }
}
