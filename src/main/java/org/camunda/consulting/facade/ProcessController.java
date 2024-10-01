package org.camunda.consulting.facade;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.camunda.consulting.ProcessConstants;
import org.camunda.consulting.ProcessVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/process")
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);
    private final ZeebeClient zeebe;

    private final JobWorkerManager jobWorkerManager;

    @Autowired
    public ProcessController(ZeebeClient client, JobWorkerManager jobWorkerManager) {
        this.jobWorkerManager = jobWorkerManager;
        this.zeebe = client;
    }

    @PostMapping("/start")
    public void startProcessInstance(@RequestBody ProcessVariables variables) {

        LOG.info(
                "Starting process `"
                        + ProcessConstants.BPMN_PROCESS_ID_CAMUNDA_PROCESS
                        + "` with variables: "
                        + variables);

        zeebe
                .newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstants.BPMN_PROCESS_ID_CAMUNDA_PROCESS)
                .latestVersion()
                .variables(variables)
                .send();
    }

    @PostMapping("/message/{messageName}/{correlationKey}")
    public void publishMessage(
            @PathVariable String messageName,
            @PathVariable String correlationKey,
            @RequestBody ProcessVariables variables) {

        LOG.info(
                "Publishing message `{}` with correlation key `{}` and variables: {}",
                messageName,
                correlationKey,
                variables);

        zeebe
                .newPublishMessageCommand()
                .messageName(messageName)
                .correlationKey(correlationKey)
                .variables(variables)
                .send();
    }

    @PostMapping("synchronousMessage/{messageName}/{correlationKey}")
    public Mono<String> publishSynchronousMessage(
            @PathVariable String messageName,
            @PathVariable String correlationKey,
            @RequestBody Map<String, Object> variables
    ) {
        LOG.info(
                "Publishing message `{}` with correlation key `{}` and variables: {}",
                messageName,
                correlationKey,
                variables);

        String applicationId = (String) variables.get("applicationId");
        zeebe
            .newPublishMessageCommand()
            .messageName(messageName)
            .correlationKey(applicationId)
            .variables(variables)
            .send();

        return Mono.create(
                sink -> {
                    String jobType = "responseFor_" + applicationId;
                    ZeebeWorkerValue jobWorkerConfig = new ZeebeWorkerValue();
                    jobWorkerConfig.setType(jobType);
                    jobWorkerConfig.setName(jobType);
                    JobWorker jobWorker = jobWorkerManager.openWorker(zeebe, jobWorkerConfig,
                            (client, job) -> {
                                /*Additional possibility to query for job related information
                                like job.processInstanceKey or similar.
                                * */
                                String response = (String) job.getVariablesAsMap().toString();
                                LOG.info("Finishing message with response: {}", response);
                                client.newCompleteCommand(job).send();
                                sink.success(response);
                            });
                    LOG.info("Response was received, closing worker for good!");
                    sink.onDispose(() -> jobWorkerManager.closeWorker(jobWorker));
                });
        // Timeout would also require clean-up? Do not utilize like this!
        // .timeout(Duration.ofMillis(150))
        //        .thenReturn("Timeout hit");

    }
}
