package org.misja.stockfighter;

public class Quotes {
    public Quote bid;

    public Quote ask;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quotes quotes = (Quotes) o;

        if (bid != null ? !bid.equals(quotes.bid) : quotes.bid != null) return false;
        return ask != null ? ask.equals(quotes.ask) : quotes.ask == null;

    }

    @Override
    public int hashCode() {
        int result = bid != null ? bid.hashCode() : 0;
        result = 31 * result + (ask != null ? ask.hashCode() : 0);
        return result;
    }
}
