package com.example.camundakotlin

import com.example.camundakotlin.beeregates.DrinkBeer
import com.example.camundakotlin.beeregates.OrderBeer
import com.example.camundakotlin.beeregates.Vomit
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.verify
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AtTheBarTest(
    private val runtimeService: RuntimeService,
    @MockkBean private val orderBeer: OrderBeer,
    @MockkBean private val drinkBeer: DrinkBeer,
    @MockkBean private val vomit: Vomit,
) : DescribeSpec({
    every { drinkBeer.execute(any()) } answers { println("I´m drinking a beer") }
    every { vomit.execute(any()) } answers { println("looks like reverse beer") }
    every { orderBeer.execute(any()) } answers { println("one beer plz") }
    val processInstance = runtimeService.startProcessInstanceByKey("AtTheBar", mapOf("drunk" to true))

    describe("when I´m at the bar") {
        it("make sure I can order some beer") {
            assertThat(processInstance).isStarted().hasPassed("OrderBeerTask")
        }
        describe("I order some beer") {
            it("and proof myself I said 'plz'") {
                verify { orderBeer.execute(any()) }
            }
            it("and then I am waiting for the beer") {
                assertThat(processInstance).isWaitingAt("WaitForBeer")
            }
            describe("and when the Barkeeper says 'Here is your beer'") {
                runtimeService
                    .createMessageCorrelation("MessageForBeer")
                    .processInstanceId(processInstance.id)
                    .correlate()

                it("I drink the beer") {
                    verify { drinkBeer.execute(any()) }
                }
                it("I feel drunk") {
                    assertThat(processInstance).hasPassed("ExclusiveGatewayDrunk")
                }
                it("I vomit") {
                    verify(exactly = 1) { vomit.execute(any()) }
                }
                it("and exact in this order") {
                    assertThat(processInstance).hasPassedInOrder(
                        *arrayOf("DrinkBeerTask", "ExclusiveGatewayDrunk", "VomitTask")
                    )
                }
            }
        }
    }
})