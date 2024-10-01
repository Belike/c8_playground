package org.camunda.consulting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class ExampleService {
    public boolean trueFalseExample(){
        log.info("Calling True Or False");
        return ThreadLocalRandom.current().nextBoolean();
    }
}
