package org.camunda.consulting;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@ZeebeSpringTest
public class TestUtils {

    private ZeebeClient client;
    private ZeebeTestEngine engine;

    public TestUtils(ZeebeClient client, ZeebeTestEngine engine){
        this.client = client;
        this.engine = engine;
    }

    public void completeServiceTasks(final String jobType, final int count)
            throws InterruptedException, TimeoutException {

        final var activateJobsResponse =
                client.newActivateJobsCommand().jobType(jobType).maxJobsToActivate(count).send().join();

        final int activatedJobCount = activateJobsResponse.getJobs().size();
        if (activatedJobCount < count) {
            Assertions.fail(
                    "Unable to activate %d jobs, because only %d were activated."
                            .formatted(count, activatedJobCount));
        }

        for (int i = 0; i < count; i++) {
            final var job = activateJobsResponse.getJobs().get(i);

            client.newCompleteCommand(job.getKey()).send().join();
        }

        waitForIdleState(Duration.ofSeconds(1));
    }

    private void waitForIdleState(final Duration duration)
            throws InterruptedException, TimeoutException {
        engine.waitForIdleState(duration);
    }
}
