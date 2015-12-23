package org.misja.stockfighter;

import com.google.api.client.util.Key;

public class Quote {
    @Key
    public int price;

    @Key
    public int qty;

    @Key
    public boolean isBuy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;

        if (price != quote.price) return false;
        if (qty != quote.qty) return false;
        return isBuy == quote.isBuy;
    }

    @Override
    public int hashCode() {
        int result = price;
        result = 31 * result + qty;
        result = 31 * result + (isBuy ? 1 : 0);
        return result;
    }
}
