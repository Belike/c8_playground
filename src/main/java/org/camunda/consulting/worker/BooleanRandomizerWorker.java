package org.camunda.consulting.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.camunda.consulting.service.ExampleService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class BooleanRandomizerWorker {

    private final ExampleService exampleService;

    public BooleanRandomizerWorker(ExampleService exampleService){
        this.exampleService = exampleService;
    }

    @JobWorker(type = "randomboolean", autoComplete = true)
    public Map<String, Object> execute(){
        log.info("Invoke Random Happiness");
        Map<String, Object> returnVariables = new HashMap<>();
        returnVariables.put("Happy", exampleService.trueFalseExample());

        return returnVariables;
    }
}
