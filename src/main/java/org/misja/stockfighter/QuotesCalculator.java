package org.misja.stockfighter;

import java.util.Optional;

public class QuotesCalculator {
    private static final int SPREAD_QTY = 50;

    public Quotes calculateQuotes(OrderBook orderBook, int position, int midMarket) {
        int minBid = Math.min(midMarket - 2, (int) (midMarket * 0.95));
        int maxAsk = Math.max(midMarket + 2, (int) (midMarket * 1.05));
        Optional<Double> bestValue = Optional.empty();
        int bestBid = 0;
        int bestAsk = 0;

        // TODO we could also evaluate quotes crossing the midmarket. Those could be good when we're reaching the risk limit.
        //      we of course always want ask > bid, also ask should be > orderBook bid.
        // Evaluate all combo's [minBid .. midMarket> * <midMarket .. maxAsk]
        for (int bid = minBid; bid < midMarket; bid++) {
            for (int ask = maxAsk; ask > midMarket; ask--) {
                double valuation = valueSpread(bid, ask, orderBook, position, midMarket);
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
        result.bid.qty = SPREAD_QTY;
        result.ask.qty = SPREAD_QTY;

        return result;
    }

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
    private double valueSpread(int bid, int ask, OrderBook orderBook, int position, int midMarket) {
        double pBidHit = calculatePHitForBid(bid, midMarket, orderBook);
        double pAskHit = calculatePHitForAsk(ask, midMarket, orderBook);

        // normalize
        double sum = pAskHit + pBidHit;
        pBidHit = pBidHit / (sum);
        pAskHit = pAskHit / (sum);

        // TODO normalize them so that there sum is 1? Strictly speaking that wouldn't be correct, but ..
        //      -> We would like pAsk > pBid if ask is closer to midmarket than bid.
        //      But, in the absence of other orders, is shouldn't matter much for pAsk or PBid which are equally far from midMarket,
        //      how far both of them are from the midmarket. So normalize after all?!

        // Current money value not relevant for the difference between spreads
        double valueAfterBidHit =  positionRisk(position + SPREAD_QTY) * 10000 + SPREAD_QTY * (midMarket - bid);
        double valueAfterAskHit =  positionRisk(position - SPREAD_QTY) * 10000 + SPREAD_QTY * (ask - midMarket);

        return pBidHit * valueAfterBidHit + pAskHit * valueAfterAskHit;
    }

    // Should be between 0 and 1, more positive is better.
    // TODO how to value the position risk? We want a pos. increase to be more costful, the more our position is positive.
    //      So it's not a linear function!
    //      Near the end (1000) an increase of 1 should be so costly that it undoes any possibly (half) spread.
    // The last 100 from 900 to 1000 should cost more than 100 * 10 or so.
    private double positionRisk(int position) {
        return 1000 - 1000.0 / (1000 - (Math.pow(position, 3) / Math.pow(1000, 2)));
    }

    private double calculatePHitForBid(int bid, int midMarket, OrderBook orderBook) {
        // if bid is below or at the first orderBook bid then put the chance to 0
        // otherwise return bid / midMarket
        if (orderBook.bids != null && orderBook.bids.length > 0 && orderBook.bids[0].price >= bid) return 0;
        return ((double) bid) / midMarket;
    }

    private double calculatePHitForAsk(int ask, int midMarket, OrderBook orderBook) {
        // if bid is over or at the first orderBook bid then put the chance to 0
        // otherwise return 1 - ((ask - midMarket) / midMarket)
        if (orderBook.asks != null && orderBook.asks.length > 0 && orderBook.asks[0].price <= ask) return 0;
        return 1 - (((double) (Math.max(0, ask - midMarket))) / midMarket);
    }
}
