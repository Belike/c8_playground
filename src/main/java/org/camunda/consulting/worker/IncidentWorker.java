package org.camunda.consulting.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class IncidentWorker {

    @JobWorker(type = "incidentWorker")
    public void retryWithFailure(final JobClient client, final ActivatedJob job) {
        log.info("Entering Service Method with Retries {} of type {}", job.getRetries(), job.getType());
        client
                .newFailCommand(job)
                .retries(0)
                .send();
    }
}
