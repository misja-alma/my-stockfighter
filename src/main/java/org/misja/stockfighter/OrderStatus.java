package org.misja.stockfighter;

import com.google.api.client.util.Key;

public class OrderStatus extends ApiResponse {
    @Key
    public String symbol;

    @Key
    public String venue;

    @Key
    public String direction;

    @Key
    public int originalQty;

    @Key
    public int qty;

    @Key
    public String orderType;

    @Key
    public int id;

    @Key
    public String account;

    @Key
    public String ts;

    @Key
    public Fill[] fills;

    @Key
    public int totalFilled;

    @Key
    public boolean open;
}
