package org.camunda.consulting.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

public class SendMailWorker {

    @JobWorker(type = "sendMail", autoComplete = true)
    public void sendMail(JobClient client, ActivatedJob job){

    }
}
