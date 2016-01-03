package org.misja.stockfighter;

import java.util.Optional;

import static org.misja.stockfighter.Heuristics.valueSpread;
import static org.misja.stockfighter.OrderBookUtils.getMarketAsk;
import static org.misja.stockfighter.OrderBookUtils.getMarketBid;

public class QuotesCalculator {
    private static final int QUOTE_QTY = 75;

    public Quotes calculateQuotes(OrderBook orderBook, int position, int midMarket) {
        int minBid = Math.min(midMarket - 2, (int) (midMarket * 0.95));
        int maxAsk = Math.max(midMarket + 2, (int) (midMarket * 1.05));
        Optional<Integer> firstBookBid = getMarketBid(orderBook);
        int minAsk = firstBookBid.isPresent() ? firstBookBid.get() + 1: minBid;
        Optional<Integer> firstBookAsk = getMarketAsk(orderBook);
        int maxBid = firstBookAsk.isPresent() ? firstBookAsk.get() - 1: maxAsk;
        Optional<Double> bestValue = Optional.empty();
        int bestBid = 0;
        int bestAsk = 0;

        for (int bid = minBid; bid < maxBid; bid++) {
            for (int ask = Math.max(minAsk, bid + 1); ask <= maxAsk; ask++) {
                double valuation = valueSpread(bid, ask, orderBook, position, midMarket, QUOTE_QTY);
                if (!bestValue.isPresent() || valuation > bestValue.get()) {
                    bestValue = Optional.of(valuation);
                    bestBid = bid;
                    bestAsk = ask;
                }
            }
        }

        // Then just return the best quotes.
        Quotes result = new Quotes();
        result.bid = new Quote();
        result.ask = new Quote();
        result.bid.price = bestBid;
        result.bid.isBuy = true;
        result.ask.isBuy = false;
        result.ask.price = bestAsk;
        result.bid.qty = QUOTE_QTY;
        result.ask.qty = QUOTE_QTY;

        return result;
    }
}
