package org.camunda.consulting.worker;

import com.github.javafaker.Faker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import java.time.Duration;
import org.camunda.consulting.dto.TestDto;

// @Component
public class GcTestWorker {
  @JobWorker(type = "exampleWorker", autoComplete = false)
  public void executeWork(final JobClient client, final ActivatedJob job) {
    int id = (int) job.getVariable("id");
    Faker faker = Faker.instance();
    TestDto testDto =
        new TestDto(faker.name().firstName(), faker.name().lastName(), faker.animal().name());
    if (id <= 50) {
      client.newCompleteCommand(job.getKey()).variables(testDto).send();
    } else {
      client.newFailCommand(job.getKey()).retries(1).retryBackoff(Duration.ofHours(1)).send();
    }
  }
}
