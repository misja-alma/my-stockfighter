package org.misja.stockfighter;

import java.io.IOException;

public class Challenge3 {
    private static final String account = "FKB82818849";
    private static final String symbol = "IITO";
    private static final String venue = "JEHEX";

    public static void main(String[] args) throws IOException {
        new MarketMaker(account, symbol, venue).makeMarket();
    }
}
