package org.misja.stockfighter;

import java.io.IOException;

public class Challenge3 {
    private static final String account = "WAS73193552";
    private static final String symbol = "KCYE";
    private static final String venue = "UXNPEX";

    public static void main(String[] args) throws IOException {
        new MarketMaker(account, symbol, venue).makeMarket();
    }
}
