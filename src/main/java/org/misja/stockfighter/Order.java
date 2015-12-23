package org.misja.stockfighter;

import com.google.api.client.util.Key;

public class Order {

    @Key
    public String account;

    @Key
    public String venue;

    @Key
    public String stock;

    @Key
    public int qty;

    @Key
    public Integer price;

    @Key
    public String direction;

    @Key
    public String orderType;
}
