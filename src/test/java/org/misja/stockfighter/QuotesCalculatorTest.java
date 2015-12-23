package org.misja.stockfighter;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuotesCalculatorTest {

    private QuotesCalculator calculator = new QuotesCalculator();

    @Test
    public void calculateFlatQuotesShouldCalculateQuotesForEmptyOrderBookAndFlatPosition() throws Exception {
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
    public void calculateSellQuotesShouldCalculateQuotesForEmptyOrderBookAndPositivePosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 900;
        int midMarket = 10;
        Quotes quotes = calculator.calculateQuotes(orderBook, position, midMarket);

        assertNotNull(quotes);
        assertTrue(midMarket - quotes.bid.price > quotes.ask.price - midMarket);
    }

    private OrderBook createEmptyOrderBook() {
        return new OrderBook();
    }
}