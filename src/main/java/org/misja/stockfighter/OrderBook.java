package org.misja.stockfighter;

import com.google.api.client.util.Key;

public class OrderBook extends ApiResponse {
    @Key
    public String venue;

    @Key
    public String symbol;

    @Key
    public String ts;

    @Key
    public Quote[] bids;

    @Key
    public Quote[] asks;
}
