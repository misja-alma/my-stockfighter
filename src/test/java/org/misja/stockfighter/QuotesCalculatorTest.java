package org.misja.stockfighter;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuotesCalculatorTest {

    private QuotesCalculator calculator = new QuotesCalculator();

    @Test
    public void calculateQuotesShouldCalculateFlatQuotesForEmptyOrderBookAndFlatPosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 0;
        int midMarket = 10;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(quotes.bid.price < quotes.ask.price);
        assertTrue(quotes.bid.price < midMarket);
        assertTrue(quotes.ask.price > midMarket);
        assertTrue(quotes.bid.qty > 0);
        assertTrue(quotes.ask.qty > 0);
        assertTrue(midMarket - quotes.bid.price == quotes.ask.price - midMarket);
    }

    @Test
    public void calculateQuotesShouldCalculateSellQuotesForEmptyOrderBookAndPositivePosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 800;
        int midMarket = 100;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(midMarket - quotes.bid.price > quotes.ask.price - midMarket);
        assertTrue(quotes.ask.price > midMarket);
    }

    @Test
    public void calculateQuotesShouldCalculateStrongSellQuotesForEmptyOrderBookAndStrongPositivePosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 900;
        int midMarket = 10;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(midMarket - quotes.bid.price > quotes.ask.price - midMarket);
        assertTrue(quotes.ask.price <= midMarket);
    }

    @Test
    public void calculateQuotesShouldCalculateBidQuoteLargerThanMarketBid() throws Exception {
        OrderBook orderBook = createOrderBookWithSpread(8, 12);
        int position = 999;
        int midMarket = 10;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(quotes.bid.price >= 8);
        assertTrue(quotes.ask.price > 8);
    }

    @Test
    public void calculateQuotesShouldCalculateAskQuoteSmallerThanMarketAsk() throws Exception {
        OrderBook orderBook = createOrderBookWithSpread(8, 12);
        int position = -999;
        int midMarket = 10;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(quotes.ask.price <= 12);
        assertTrue(quotes.bid.price < 12);
    }

    private OrderBook createOrderBookWithSpread(int bidPrice, int askPrice) {
        Quote bid = new Quote();
        bid.price = bidPrice;
        Quote ask = new Quote();
        ask.price = askPrice;
        OrderBook result = new OrderBook();
        result.bids = new Quote[] { bid };
        result.asks = new Quote[] { ask };
        return result;
    }

    private OrderBook createEmptyOrderBook() {
        return new OrderBook();
    }
}