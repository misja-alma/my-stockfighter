package org.misja.stockfighter;

import java.io.IOException;

public class Challenge3 {
    private static final String account = "SAL23982420";
    private static final String symbol = "PLCM";
    private static final String venue = "DXHEX";

    public static void main(String[] args) throws IOException {
        new MarketMaker(account, symbol, venue).makeMarket();
    }
}
