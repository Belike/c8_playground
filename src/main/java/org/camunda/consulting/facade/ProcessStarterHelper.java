package org.camunda.consulting.facade;

import io.camunda.zeebe.client.ZeebeClient;
import java.util.Map;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import lombok.extern.slf4j.Slf4j;
import org.camunda.consulting.exceptions.CouldNotCreateInstanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Helper is used for reflection of Resilience4j
@Component
@Slf4j
public class ProcessStarterHelper {

  ZeebeClient zeebeClient;

  @Autowired
  public ProcessStarterHelper(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  // Starting Synchronously
  public ProcessInstanceEvent startInstanceOnZeebeSynchronously(String procKey, int id) {
    log.info("Starting new instance with number {}", id);
      ProcessInstanceEvent instanceEvent = zeebeClient
              .newCreateInstanceCommand()
              .bpmnProcessId(procKey)
              .latestVersion()
              .variables(Map.of("id", id))
              .send()
              .join();

      return instanceEvent;
  }

  public void startInstanceOnZeebeAsynchronously(String procKey, int id)
      throws CouldNotCreateInstanceException {
    log.info("Starting new instance with number {}", id);
    zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId(procKey)
        .latestVersion()
        .variables(Map.of("id", id))
        .send()
        .exceptionally(
            t -> {
              log.info("Having an error in starting instance!");
              throw new CouldNotCreateInstanceException("Couldn't Start Instance");
            });
  }

  public void startInstanceWithReturn(String procKey, int id) {
      log.info("Starting new instance with number {}", id);
      ProcessInstanceResult instanceResult = zeebeClient
              .newCreateInstanceCommand()
              .bpmnProcessId(procKey)
              .latestVersion()
              .variables(Map.of("id", id))
              .withResult()
              .send()
              .join();

      log.info("Started process {} with tenant-id {}", instanceResult.getBpmnProcessId(), instanceResult.getTenantId());
  }
}
