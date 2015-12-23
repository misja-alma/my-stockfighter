package org.misja.stockfighter;

import java.io.IOException;
import java.util.Optional;

public class MarketMaker {
    private static final int DEFAULT_MIDMARKET = 42;
    private static final double DEFAULT_MIDMARKET_DISTANCE_PCT = 3;

    private Api api = new Api();
    private QuotesCalculator quotesCalculator = new QuotesCalculator();
    private int position;
    private Optional<Integer> lastMidMarket = Optional.empty();
    private Optional<OrderStatus> bidStatus = Optional.empty();
    private Optional<OrderStatus> askStatus = Optional.empty();
    private boolean canceled = false;
    
    private final String account;
    private final String symbol;
    private final String venue;

    public MarketMaker(String account, String symbol, String venue) {
        this.account = account;
        this.symbol = symbol;
        this.venue = venue;
    }

    public void makeMarket() throws IOException {
        // put a bid and an ask as limit orders. Keep a certain spread around a midmarket.
        // When a side is hit, re-evaluate your position and move the spread a bit.
        // Better: First see if hitting a bid or ask improves your position. So we need a utility function for position.
        // If not, then place bids and asks which, if hit, improve our position.
        // A util function should:
        // - rate a zero position the highest
        // - increase when the net money position increases. What is net money?
        //   - net money is the cumulative worth of the position (can be negative) plus the money position.
        // E.g We buy one share for 10. Our money position is then 10 (position) - 10 (money) = 0.
        // If we sell the share at 9, our money position decreases while our position becomes zero.
        // So the util function should balance both factors.
        // -> Note that the value of our stocks should also vary based on the last trade and/ or the midmarket!
        // This way it automatically makes sense to put quotes around midmarket, since when these are hit it will
        // imply a money position gain.
        // -> a position of 1000 means a certain loss. So at 999 we want to balance at any cost.
        // -> so position function P is Math.min(1000, position) / -1000. Which goes from 0 to -1.
        // -> money function M is the position value (eval ad midmarket/ last trade?) + money value
        // util function U = aM + bC. Let's take a = 1 and b = 100.
        // what is the worth of a spread?
        // It is S = p1 * U1 + p2 * U2 where U1 and U2 are the utility values after either hit and p1 and p2 the
        // chances they are hit.
        // Let's assume that p is 1 when it crosses the midmarket and 0 when behind another bid.
        // Otherwise is decreases when further away from midmarket but probably not linearly? But
        // for simplicity suppose it is linear in a range [0, midmarket, 2 * midmarket], and let's further suppose that p1 + p2 = 1.
        // Then we can optimize S, let's for now just take a bunch of points around the midmarket and choose between those.

        // loop:
        // - get midmarket and order book
        // - determine spread + quotes based on midmarket, orderBook and position
        // - if quotes differ from old ones:
        //    - cancel old orders
        //    - place new limit orders
        // - wait until order is filled
        // - adjust position
        // - repeat

        // Apart from this loop we would like to double check order statuses now and then, to guard against
        // orders which were filled at the last moment or just against mistakes on the exchange side.
        // This loop just adjusts our position. Maybe it's best to define our position dynamically, from a list of
        // order fills?

        while (!canceled) {
            OrderBook orderBook = api.getOrderBook(venue, symbol);
            // TODO here we could also hit
            lastMidMarket = Optional.of(Tools.getMidMarket(orderBook).orElseGet(() -> virtualMidMarket(orderBook, lastMidMarket)));
            Quotes quotes = quotesCalculator.calculateQuotes(orderBook, getPosition(), lastMidMarket.get());
            placeQuotes(quotes);
            waitForFill();
            // TODO here we could also hit
            cancelCurrentQuotes(); // TODO we could better to it after calculating the new quotes. Maybe it's not needed.
        }
    }

    // if no midmarket present, take the last one and adjust if needed. If there's also no last one, just take some number.
    private int virtualMidMarket(OrderBook orderBook, Optional<Integer> lastMidMarket) {
        int target = lastMidMarket.orElse(DEFAULT_MIDMARKET);
        if (orderBook.asks != null && orderBook.asks.length > 0) {
            // adjust target downwards if needed
            int ask = orderBook.asks[0].price;
            return Math.min(target, (int) Math.round(ask - (DEFAULT_MIDMARKET_DISTANCE_PCT / 100) * ask));
        } else if (orderBook.bids != null && orderBook.bids.length > 0) {
            // adjust target upwards if needed
            int bid = orderBook.bids[0].price;
            return Math.max(target, (int) Math.round(bid + (DEFAULT_MIDMARKET_DISTANCE_PCT / 100) * bid));
        } else {
            return target;
        }
    }

    private void waitForFill() throws IOException {
        while (bidStatus.get().totalFilled < bidStatus.get().originalQty && askStatus.get().totalFilled < askStatus.get().originalQty) {
            bidStatus = Optional.of(api.getOrderStatus(venue, symbol, bidStatus.get().id));
            askStatus = Optional.of(api.getOrderStatus(venue, symbol, askStatus.get().id));
        }
    }

    private void cancelCurrentQuotes() throws IOException {
        if (bidStatus.isPresent() && bidStatus.get().open) {
            bidStatus = Optional.of(api.cancelOrder(venue, symbol, bidStatus.get().id));
        }
        if (askStatus.isPresent() && askStatus.get().open) {
            askStatus = Optional.of(api.cancelOrder(venue, symbol, askStatus.get().id));
        }
        position += bidStatus.isPresent()? bidStatus.get().totalFilled: 0;
        position -= askStatus.isPresent()? askStatus.get().totalFilled: 0;
        bidStatus = Optional.empty();
        askStatus = Optional.empty();
    }

    private void placeQuotes(Quotes quotes) throws IOException {
        Order ask = new Order();
        Order bid = new Order();
        ask.account = account;
        ask.direction = "sell";
        ask.orderType = "limit";
        ask.stock = symbol;
        ask.venue = venue;
        ask.price = quotes.ask.price;
        ask.qty = quotes.ask.qty;

        bid.account = account;
        bid.direction = "buy";
        bid.orderType = "limit";
        bid.stock = symbol;
        bid.venue = venue;
        bid.price = quotes.bid.price;
        bid.qty = quotes.bid.qty;

        System.out.println("Placing new quotes, bid: " + bid.price + ", ask: " + ask.price + ", midMarket: " + lastMidMarket + ", position: " + getPosition());
        askStatus = Optional.of(api.placeOrder(ask));
        bidStatus = Optional.of(api.placeOrder(bid));
    }
    
    public int getPosition() {
        // TODO this could be a moment to double check and adjust our position
        int bidFills = bidStatus.isPresent()? bidStatus.get().totalFilled: 0;
        int askFills = askStatus.isPresent()? askStatus.get().totalFilled: 0;
        return position + bidFills - askFills;
    }
}
