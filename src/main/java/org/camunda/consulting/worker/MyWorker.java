package org.camunda.consulting.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.camunda.consulting.ProcessVariables;
import org.camunda.consulting.service.MyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Component
public class MyWorker {

  private static final Logger LOG = LoggerFactory.getLogger(MyWorker.class);

  private final MyService myService;

  public MyWorker(MyService myService) {
    this.myService = myService;
  }

  @JobWorker
  public ProcessVariables invokeMyService(@VariablesAsType ProcessVariables variables) {
    LOG.info("Invoking myService with variables: " + variables);

    boolean result = myService.myOperation(variables.getBusinessKey());

    return new ProcessVariables()
        .setResult(result); // new object to avoid sending unchanged variables
  }
}
