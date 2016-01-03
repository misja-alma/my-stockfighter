package org.misja.stockfighter;

import java.util.Optional;

public class OrderBookUtils {

    public static Optional<Integer> getMidMarket(OrderBook orderBook) {
        // TODO we could probably use getMarketBid and -ask in a nice way here.
        if (orderBook.asks != null && orderBook.asks.length > 0 && orderBook.bids != null && orderBook.bids.length > 0) {
            return Optional.of((orderBook.bids[0].price + orderBook.asks[0].price) / 2);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Integer> getMarketBid(OrderBook orderBook) {
        if (orderBook.bids != null && orderBook.bids.length > 0) {
            return Optional.of(orderBook.bids[0].price);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Integer> getMarketAsk(OrderBook orderBook) {
        if (orderBook.asks != null && orderBook.asks.length > 0) {
            return Optional.of(orderBook.asks[0].price);
        } else {
            return Optional.empty();
        }
    }
}
