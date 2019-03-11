package com.endava.movies.batches;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BatchLauncher {

    @Qualifier("databaseToCsvFileJob")
    private final Job job;
    private final JobLauncher jobLauncher;

    public void launchDatabaseToCsvFileJob(final double voteAverage){

        try {
            jobLauncher.run(job, newExecution(voteAverage));
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    private JobParameters newExecution(final double voteAverage) {
        val parameters = new HashMap<String, JobParameter>();
        val parameter = new JobParameter(voteAverage);
        val currentDate = new JobParameter(new Date());

        parameters.put("currentTime", currentDate);
        parameters.put("voteAverage", parameter);
        return new JobParameters(parameters);
    }

}
