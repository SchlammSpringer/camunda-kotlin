package com.example.camundakotlin.delegates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CreateShoppingListTask : JavaDelegate {
    override fun execute(execution: DelegateExecution?) {
        TODO("Not yet implemented")

       /* execution.setVariableIfMissing(Process.Variables.SHOPPING_LIST) {
            AllGoods.randomNames(shoppingListSize).also { logger.info("Created shopping list: $it") }
        }
        execution.setVariableIfMissing(Process.Variables.CART_NEEDED) {
            val cartNeeded = Random.nextBoolean((shoppingListSize - 2.0) / shoppingListSize)
            cartNeeded.also { logger.info("Shopping cart needed: $it") }
        }*/
    }
}