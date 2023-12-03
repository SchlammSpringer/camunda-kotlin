package com.example.camundakotlin

/** Process constants. */
object Process {
    const val NAME = "Shopping"

    /** Activity IDs of [Process]. */
    enum class ActivityIds (val variable: String){
         CREATE_SHOPPING_LIST ("CreateShoppingList"),
         PREPARE_MEANS_OF_PAYMENT ("PrepareMeansOfPayment"),
         PREPARE_CART_DEPOSIT ("PrepareShoppingCartDeposit"),
         PREPARE_SHOPPING_COMPLETED ("PrepareShoppingCompleted"),
         TAKE_CART ("TakeShoppingCart"),
         CHOOSE_GOODS ("ChooseGoods#multiInstanceBody"),
         PAY_GOODS ("PayGoods"),
         CREATE_NEW_SHOPPING_LIST ("CreateNewShoppingList"),
         SHOPPING_COMPLETED ("PerformShoppingCompleted"),
         COMPLETED ("ShoppingCompleted"),
         PREPARATION_FAILED ("ShoppingPreparationFailure"),
         SHOPPING_FAILED ("ShoppingFailed"),
    }

    /** Variable names of the [Process]. */
    enum class Variables(val variable: String) {
        /** List of names of all goods which are to be bought. */
         SHOPPING_LIST ( "shoppingList"),
        /** Whether a shopping cart is needed in order to buy the goods of the shopping list (according to the buyer).
         * Do not confuse with [CART_MANDATORY]. */
         CART_NEEDED ( "shoppingCartNeeded"),
        /** All payment options accepted by the buyer. */
         PAYMENT_OPTIONS ( "meansOfPaymentOptions"),
        /** Optional (prepared) deposit used for the shopping cart if needed or mandatory. */
         CART_DEPOSIT ( "shoppingCartDeposit"),
        /** Whether a shopping cart is mandatory when entering the store. */
         CART_MANDATORY ( "shoppingCartMandatory"),
        /** Whether a shopping cart was taken when entering the store. */
         CART_TAKEN ( "shoppingCartTaken"),
        /** Currently carried goods. */
         GOODS ( "goods"),
        /** Actually chosen */
         MEANS_OF_PAYMENT ( "meansOfPayment"),
        /** Bill received for the goods bought. */
         BILL ( "bill"),
        /** Whether all goods on the shopping list were bought after the store was left. */
         ALL_GOODS_BOUGHT ( "allGoodsBought"),
    }
}