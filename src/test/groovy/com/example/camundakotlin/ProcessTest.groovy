package com.example.camundakotlin

import com.example.camundakotlin.beeregates.DrinkBeer
import com.example.camundakotlin.beeregates.OrderBeer
import com.example.camundakotlin.beeregates.Vomit
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension
import org.junit.jupiter.api.extension.RegisterExtension
import spock.lang.Specification

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*
import static org.camunda.bpm.engine.test.mock.Mocks.register

@Deployment(resources = 'simple.bpmn')
class ProcessTest extends Specification {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder(
            new ProcessCoverageInMemProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL)
    ).build()

    def "a week day at the bar"() {
        given: "some setup stuff"
        RuntimeService runtimeService = runtimeService()
        ProcessInstance processInstance

        boolean drunk = true

        OrderBeer orderBeer = Mock()
        DrinkBeer drinkBeer = Mock()
        Vomit vomit = Mock()

        register "orderBeer", orderBeer
        register "vomit", vomit
        register "drinkBeer", drinkBeer

        when: "I am at the bar"
        processInstance = runtimeService
                .startProcessInstanceByKey('AtTheBar', ["drunk": drunk])

        then: "make sure that I can order some beer"
        assertThat processInstance isStarted() isWaitingAt 'OrderBeerTask'

        when: "I order some beer"
//    Async Before because in Unit Tests there is no job executor
        execute job()
//    Async After
        execute job()

        then: "I sad one beer plz"
        1 * orderBeer.execute(_) >> println('one beer plz')

        and: "that I am waiting for the beer"
        assertThat processInstance isWaitingAt 'WaitForBeer'

        when: "when the Barkeeper says 'Here is your beer'"
        runtimeService
                .createMessageCorrelation('MessageForBeer')
                .processInstanceId(processInstance.id)
                .correlate()

        then: "I drink the beer, feel drunk and vomit"
        assertThat processInstance hasPassedInOrder 'DrinkBeerTask', 'ExclusiveGatewayDrunk', 'VomitTask'
        1 * drinkBeer.execute(_) >> println('I´m drinking a beer')
        1 * vomit.execute(_) >> println('looks like reverse beer')

        and: "After that I am waiting for the doctor"
        assertThat processInstance isWaitingAt 'WaitForTheDoctor'

        when: "When the doctor says 'I am Ok'"
        runtimeService
                .createMessageCorrelation('MessageWaitForTheDoctor')
                .processInstanceId(processInstance.id)
                .correlate()

        then: "My week day at bar is over"
        assertThat processInstance isEnded()

    }

    def "a no alcohol day at the bar"() {
        given: "some setup stuff"
        RuntimeService runtimeService = runtimeService()
        ProcessInstance processInstance

        OrderBeer orderBeer = Mock()
        DrinkBeer drinkBeer = Mock()
        Vomit vomit = Mock()

        register "orderBeer", orderBeer
        register "vomit", vomit
        register "drinkBeer", drinkBeer

        when: "I am at the bar"
        processInstance = runtimeService.startProcessInstanceByKey('AtTheBar')

        then: "make sure that I can order some beer"
        assertThat processInstance isStarted() isWaitingAt 'OrderBeerTask'

        when: "I order some beer"
//    Async Before
        execute job()
//    Async After
        execute job()

        then: "I sad one beer plz"
        1 * orderBeer.execute(_) >> println('one beer plz')

        and: "that I am waiting for the beer"
        assertThat processInstance isWaitingAt 'WaitForBeer'

        when: "when the Barkeeper says 'Here is your beer'"
        runtimeService
                .createMessageCorrelation('MessageForBeer')
                .processInstanceId(processInstance.id)
                .correlate()

        then: "I drink the beer, it tastes terrible"
        assertThat processInstance hasPassed 'DrinkBeerTask'
        1 * drinkBeer.execute(_) >> { throw new BpmnError("WITHOUTALCOHOL") }
        0 * vomit.execute(_)
        and: "I start a brawl"
        assertThat processInstance hasPassed 'BrawlTask'

        and: "After the brawl I am waiting for the police"
        assertThat processInstance isWaitingAt 'WaitForPoliceTask'

        when: "When the police says 'Ohh come on folks'"
        runtimeService
                .createMessageCorrelation('MessageWaitForPolice')
                .processInstanceId(processInstance.id)
                .correlate()

        then: "My no alcohol day at bar is over"
        assertThat processInstance isEnded()

    }

}