package org.camunda.consulting.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestStagingWorker {

    @Value("${pr.value:}")
    private String prValue;

    @JobWorker(type = "${pr.value}")
    public void testHeaderBehavior(final JobClient client, final ActivatedJob job){
        log.info("I am working on this piece of code - haha!");
    }
}
