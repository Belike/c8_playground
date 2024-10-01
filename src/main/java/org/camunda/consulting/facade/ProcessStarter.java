package org.camunda.consulting.facade;

import lombok.extern.slf4j.Slf4j;
import org.camunda.consulting.ProcessConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class ProcessStarter {
  ProcessStarterHelper processStarterHelper;

  @Autowired
  public ProcessStarter(ProcessStarterHelper processStarterHelper) {
    this.processStarterHelper = processStarterHelper;
  }

  //@EventListener(ApplicationReadyEvent.class)
  public void startInstances() throws Exception {
    for (int i = 0; i < 11000; i++) {
      processStarterHelper.startInstanceOnZeebeAsynchronously(
          ProcessConstants.BPMN_PROCESS_ID_TESTING_LOAD, i);
    }
    log.info("Finished creating instances");
  }

  //@EventListener(ApplicationReadyEvent.class)
  public void startInstancesSynchronously() throws Exception {
      log.info("Starting to generate Load Synchronously!");
      for(int i = 0; i < 10000; i++){
          processStarterHelper.startInstanceOnZeebeSynchronously(
                  ProcessConstants.BPMN_PROCESS_ID_TESTING_LOAD, i);
      }
  }

  //@EventListener(ApplicationReadyEvent.class)
  public void startInstanceWithReturn() throws Exception {
      processStarterHelper.startInstanceWithReturn("TestingLoad", 9999);
  }
}
