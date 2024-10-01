package org.camunda.consulting.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class BofaSample {

    ZeebeClient zeebeClient;

    @Autowired
    public BofaSample(ZeebeClient client){
        this.zeebeClient = client;
    }

    @JobWorker(type = "exampleWorker", autoComplete = false )
    public void execute(final JobClient client, final ActivatedJob job){
        log.info("Finishing off example work!");
        client.newCompleteCommand(job).send();
    }

    @JobWorker(type = "participantList", autoComplete = true)
    public Map<String, Object> test(final JobClient client, final ActivatedJob job){
        log.info("Calculating the winner");
        job.getVariable("test");
        ArrayList<String> participants = new ArrayList<>();
        participants.add("Khurram");
        participants.add("Yogesh");
        participants.add("Norman");

        Map<String, Object> returnVariables = new HashMap<>();
        returnVariables.put("example", "test");
        returnVariables.put("participants", participants);

        return returnVariables;
    }
}
