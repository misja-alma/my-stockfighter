package org.misja.stockfighter;

public class Heuristics {

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
    public static double valueSpread(int bid, int ask, OrderBook orderBook, int position, int midMarket, int quoteQuantity) {
        double pBidHit = calculatePHitForBid(bid, midMarket, orderBook);
        double pAskHit = calculatePHitForAsk(ask, midMarket, orderBook);

        // normalize skewedly.
        double squaredSum = pAskHit * pAskHit + pBidHit * pBidHit;
        pBidHit = pBidHit * pBidHit / squaredSum;
        pAskHit = pAskHit * pAskHit / (squaredSum);


        // TODO normalize them so that there sum is 1? Strictly speaking that wouldn't be correct, but ..
        //      -> We would like pAsk > pBid if ask is closer to midmarket than bid.
        //      But, in the absence of other orders, is shouldn't matter much for pAsk or PBid which are equally far from midMarket,
        //      how far both of them are from the midmarket. So normalize after all?!
        // -> TODO this doesn't take into account how fast we want to make money! We might want to have a less profitable quote if it is hit more fast.

        // Current money value not relevant for the difference between spreads
        double valueAfterBidHit =  positionRisk(position + quoteQuantity, quoteQuantity) * -10000 + quoteQuantity * (midMarket - bid);
        double valueAfterAskHit =  positionRisk(position - quoteQuantity, quoteQuantity) * -10000 + quoteQuantity * (ask - midMarket);

        return pBidHit * valueAfterBidHit + pAskHit * valueAfterAskHit;
    }

    // Should be between 0 and 1, more positive is more risk.
    // TODO how to value the position risk? We want a pos. increase to be more costful, the more our position is positive.
    //      So it's not a linear function!
    //      Near the end (1000) an increase of 1 should be so costly that it undoes any possibly (half) spread.
    // The last 100 from 900 to 1000 should cost more than 100 * 10 or so.
    // -> Actually the positionRisk is just the chance to hit the limit. This depends also on the quoting size.
    // So a pos within one quote from the limit has 50% chance to go broke. Two quotes away 0.25%, etc.
    // So, the risk is just 0.5 ^ quotesAway. Where quotesAway should be rounded upwards.
    public static double positionRisk(int position, int quoteSize) {
        double quotesAway = Math.ceil((1000.0 - Math.abs(position)) / quoteSize);
        return Math.pow(0.5, quotesAway);
    }

    // TODO we should take the size of the orderbook into account when calculating pHit for bids at the marketBid (or lower?). Same for asks. It is not always 0.
    public static double calculatePHitForBid(int bid, int midMarket, OrderBook orderBook) {
        // if bid is below or at the first orderBook bid then put the chance to 0
        // otherwise return bid / midMarket
        if (orderBook.bids != null && orderBook.bids.length > 0 && orderBook.bids[0].price >= bid) return 0;
        int zeroHit = (int) Math.min(midMarket - 1, midMarket * 0.7);
        int zeroHitDist = Math.max(0, bid - zeroHit);
        return Math.min(1, ((double) zeroHitDist) / (midMarket - zeroHit));
    }

    public static double calculatePHitForAsk(int ask, int midMarket, OrderBook orderBook) {
        // if bid is over or at the first orderBook bid then put the chance to 0
        // otherwise return 1 - ((ask - midMarket) / midMarket)
        if (orderBook.asks != null && orderBook.asks.length > 0 && orderBook.asks[0].price <= ask) return 0;
        int zeroHit = (int) Math.max(midMarket + 1, midMarket * 1.3);
        int zeroHitDist = Math.max(0, zeroHit - ask);
        return Math.min(1, ((double) zeroHitDist) / (zeroHit - midMarket));
    }
}
