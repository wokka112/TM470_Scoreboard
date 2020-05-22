package com.floatingpanda.scoreboard.db;

import com.floatingpanda.scoreboard.data.BgCategory;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class holding test values
 */
public class TestData {

    static final BgCategory BG_CATEGORY_1 = new BgCategory(1, "Strategy");
    static final BgCategory BG_CATEGORY_2 = new BgCategory(2, "Luck");
    static final BgCategory BG_CATEGORY_3 = new BgCategory(3, "Ameritrash");

    static final List<BgCategory> BG_CATEGORIES = Arrays.asList(BG_CATEGORY_1, BG_CATEGORY_2, BG_CATEGORY_3);
}
