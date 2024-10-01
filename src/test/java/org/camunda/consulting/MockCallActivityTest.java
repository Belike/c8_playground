package org.camunda.consulting;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.DeploymentAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import lombok.extern.slf4j.Slf4j;
import org.camunda.consulting.facade.ProcessStarterHelper;
import org.camunda.consulting.service.ExampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProcessApplication.class)
@ZeebeSpringTest
@Slf4j
public class MockCallActivityTest {

    private final ZeebeTestEngine engine;
    private final ZeebeClient client;
    private RecordStream recordStream;

    @MockBean
    private ExampleService exampleService;

    @Autowired
    private ProcessStarterHelper processStarterHelper;

    @Autowired
    public MockCallActivityTest(ZeebeTestEngine engine, ZeebeClient zeebeClient, TestUtils testUtils){
        this.engine = engine;
        this.client = zeebeClient;
    }

    @BeforeEach
    public void setup(){
        DeploymentEvent deploymentEvent = client.newDeployResourceCommand()
                .addResourceFromClasspath("models/randomizeHappiness.bpmn")
                .addResourceFromClasspath("models/fake-call-activity.bpmn")
                .send()
                .join();
        DeploymentAssert assertions
                = BpmnAssert.assertThat(deploymentEvent).containsProcessesByBpmnProcessId("happinessRandomizer");
        DeploymentAssert assertions2
                = BpmnAssert.assertThat(deploymentEvent).containsProcessesByBpmnProcessId("fake-call-activity");
    }

    // Showing that no bpmn file has to be maintained, and everything can be written in code
    @BeforeEach
    public void genericDeployment() {
        BpmnModelInstance bpmnModelInstance = Bpmn.createExecutableProcess("test").startEvent("Start").done();
        client.newDeployResourceCommand()
                .addProcessModel(bpmnModelInstance, "test" + ".bpmn")
                .send()
                .join();
    }

    @Test
    public void testHappyPath() throws Exception {
        when(exampleService.trueFalseExample()).thenReturn(true);

        ProcessInstanceEvent processInstanceEvent = processStarterHelper.startInstanceOnZeebeSynchronously(ProcessConstants.PROCESS_RANDOMIZE_HAPPINESS_ID, 1);
        assertThat(processInstanceEvent).isStarted();

        waitForProcessInstanceCompleted(processInstanceEvent);

        Optional<InspectedProcessInstance> firstProcessInstance = InspectionUtility.findProcessInstances()
                .withParentProcessInstanceKey(processInstanceEvent.getProcessInstanceKey())
                .withBpmnProcessId("fake-call-activity")
                .findFirstProcessInstance();
        ProcessInstanceAssert assertions = BpmnAssert.assertThat(firstProcessInstance.get());
        log.info("ParentProcessInstanceKey: {}", processInstanceEvent.getProcessInstanceKey());
        log.info("ChildProcessInstanceKey: {}", firstProcessInstance.get().getProcessInstanceKey());


        assertThat(processInstanceEvent)
                .isCompleted()
                .hasPassedElement("fake-call-activity")
                .hasPassedElement("InvokeBooleanRandomizer_ServiceTask")
                .hasPassedElement("InvokeHappiness_ServiceTask")
                .hasVariableWithValue("Happy", true);

        Mockito.verify(exampleService, times(1)).trueFalseExample();
    }
}
