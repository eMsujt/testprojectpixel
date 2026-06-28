package com.skyblock.core.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Coin formatting. Coins are stored as doubles, so balances can carry fractions
 * (e.g. {@code 11.2} or {@code 11121.32}). {@link #format(double)} renders them
 * with grouping separators and up to two decimal places, trimming trailing
 * zeros — the canonical way to show a purse/coin amount across the plugin.
 */
public final class Coins {

    private static final ThreadLocal<DecimalFormat> FULL = ThreadLocal.withInitial(() -> {
        DecimalFormat df = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df;
    });

    private Coins() {
    }

    /** Formats a coin amount with grouping + up to two decimals (e.g. "11.2", "11,121.32"). */
    public static String format(double coins) {
        return FULL.get().format(coins);
    }

    /** Rounds a coin amount to two decimal places (for clean storage of computed values). */
    public static double round(double coins) {
        return Math.round(coins * 100.0) / 100.0;
    }
}
