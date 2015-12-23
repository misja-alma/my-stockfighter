package org.misja.stockfighter;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class QuotesCalculatorTest {

    private QuotesCalculator calculator = new QuotesCalculator();

    @Test
    public void calculateFlatQuotesShouldCalculateQuotesForEmptyOrderBookAndFlatPosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 0;
        Optional<Integer> lastMidMarket = Optional.of(10);
        Quotes quotes = calculator.calculateQuotes(orderBook, position, lastMidMarket);

        assertNotNull(quotes);
        assertTrue(quotes.bid.price < quotes.ask.price);
        assertTrue(quotes.bid.price < lastMidMarket.get());
        assertTrue(quotes.ask.price > lastMidMarket.get());
        assertTrue(quotes.bid.qty > 0);
        assertTrue(quotes.ask.qty > 0);
        assertTrue(lastMidMarket.get() - quotes.bid.price == quotes.ask.price - lastMidMarket.get());
    }

    @Test
    public void calculateSellQuotesShouldCalculateQuotesForEmptyOrderBookAndPositivePosition() throws Exception {
        OrderBook orderBook = createEmptyOrderBook();
        int position = 900;
        Optional<Integer> lastMidMarket = Optional.of(10);
        Quotes quotes = calculator.calculateQuotes(orderBook, position, lastMidMarket);

        assertNotNull(quotes);
        assertTrue(lastMidMarket.get() - quotes.bid.price > quotes.ask.price - lastMidMarket.get());
    }

    private OrderBook createEmptyOrderBook() {
        return new OrderBook();
    }
}