package org.misja.stockfighter;

import com.google.api.client.util.Key;

public class Fill {
    @Key
    public int price;

    @Key
    public int qty;

    @Key
    public String ts;
}
