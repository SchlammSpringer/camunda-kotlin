package com.example.camundakotlin

import org.camunda.bpm.engine.delegate.DelegateExecution
import java.awt.Color
import java.io.Serializable

/** Abstract super-class for all shopping cart deposit variants. */
sealed class ShoppingCartDeposit(open val diameter: Double, open val thickness: Double) : Serializable

/** A chip representing a [ShoppingCartDeposit]. */
data class ShoppingCartChip(val color: Color, override val diameter: Double, override val thickness: Double) :
    ShoppingCartDeposit(diameter, thickness)

/** A coin which may be used as [ShoppingCartDeposit]. */
data class Coin(val value: Int, val unit: String, override val diameter: Double, override val thickness: Double) :
    ShoppingCartDeposit(diameter, thickness)

/** All the means of payment supported by the shopping process. */
enum class MeansOfPayment { CASH, DEBIT_CARD, CREDIT_CARD }

/** A list set as variable with a given [name] in this [DelegateExecution] instance
 * or the [emptyList] if not present. */
internal fun <T> DelegateExecution.getMandatoryList(name: String): List<T> {
    return if (hasVariable(name)) getVariable(name) as List<T> else emptyList()
}

/** Set a variable with a given [name] only if it is not present in this [DelegateExecution] instance. */
internal fun DelegateExecution.setVariableIfMissing(name: String, newValueLambda: () -> Any) {
    if (!hasVariable(name)) setVariable(name, newValueLambda())
}