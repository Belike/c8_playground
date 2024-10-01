package org.camunda.consulting.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserTaskWorker {

    @JobWorker(type = "execListener", autoComplete = true)
    public void execListener(final JobClient client, final ActivatedJob job){
        log.info("Running into execListener");
        log.info("This job is about to be executed {} with key {}", job.getType(), job.getKey());
    }

    @JobWorker(type = "io.camunda.zeebe:userTask", timeout = 60000, autoComplete = false)
    public void userTask(final JobClient client, final ActivatedJob job){
        log.info("Exec UserTask Behaviour");
        log.info("Executing job with key {}", job.getKey());
    }
}
