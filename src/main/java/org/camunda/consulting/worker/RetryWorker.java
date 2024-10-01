package org.camunda.consulting.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryWorker {

  @JobWorker(type = "retrying")
  public void retryWithFailure(final JobClient client, final ActivatedJob job) {
    log.info("Entering Service Method with Retries {} of type {}", job.getRetries(), job.getType());
    client
        .newFailCommand(job)
        .retries(job.getRetries() - 1)
        .retryBackoff(Duration.ofSeconds(60))
        .send();
  }
}
