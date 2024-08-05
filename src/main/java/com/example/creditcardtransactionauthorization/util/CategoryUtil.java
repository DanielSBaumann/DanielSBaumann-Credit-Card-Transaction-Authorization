package com.example.creditcardtransactionauthorization.util;

public class CategoryUtil {

    public static String determineCategory(String mcc, String merchant) {
        String category = determineCategoryByMerchant(merchant);
        if (category == null) {
            category = determineCategoryByMcc(mcc);
        }
        return category != null ? category : Constants.CATEGORY_CASH;
    }

    private static String determineCategoryByMerchant(String merchant) {
        if (merchant.startsWith("UBER EATS")) {
            return Constants.CATEGORY_MEAL;
        } else if (merchant.startsWith("UBER TRIP")) {
            return Constants.CATEGORY_CASH;
        }
        return null;
    }

    private static String determineCategoryByMcc(String mcc) {
        switch (mcc) {
            case Constants.MCC_FOOD_1:
            case Constants.MCC_FOOD_2:
                return Constants.CATEGORY_FOOD;
            case Constants.MCC_MEAL_1:
            case Constants.MCC_MEAL_2:
                return Constants.CATEGORY_MEAL;
            default:
                return null;
        }
    }
}
