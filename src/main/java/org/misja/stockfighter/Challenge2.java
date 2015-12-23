package org.misja.stockfighter;

import java.io.IOException;

public class Challenge2 {
    private static final String account = "FKB82818849";
    private static final String symbol = "IITO";
    private static final String venue = "JEHEX";
    private static final int qty = 100000;

    public static void main(String[] args) throws IOException {
        new BlockTrader().blockBuy(qty, account, symbol, venue);
        System.out.println("Finished!");
    }
}
