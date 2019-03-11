package com.endava.movies.batches;


import com.endava.movies.models.Movie;
import com.endava.movies.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import java.util.Collections;
import java.util.LinkedList;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    private final MovieRepository movieRepository;
    private double will_be_injected;

    @Bean
    @StepScope
    public RepositoryItemReader<Movie> reader(@Value("#{jobParameters['voteAverage']}") double voteAverage) {
        val reader = new RepositoryItemReader<Movie>();
        reader.setRepository(movieRepository);
        reader.setMethodName("findByVoteAverageGreaterThanEqual");
        reader.setArguments(Collections.singletonList(voteAverage));
        reader.setSort(Collections.singletonMap("internalId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Movie> writer() {
        val currentTime = Long.toString(System.currentTimeMillis());
        val outputResource = new FileSystemResource("output/" + currentTime + ".csv");
        return buildWriter(outputResource);
    }

    private FlatFileItemWriter buildWriter(FileSystemResource outputResource) {
        val writer = new FlatFileItemWriter<Movie>();
        val myDelimiter = new DelimitedLineAggregator<Movie>();
        val fields = new BeanWrapperFieldExtractor<Movie>();
        val fieldsMovie = getAllFieldsClass(new Movie());
        writer.setResource(outputResource);
        writer.setHeaderCallback(writerInter -> writerInter.write(String.join(",", fieldsMovie)));
        fields.setNames(fieldsMovie);
        myDelimiter.setDelimiter(";");
        myDelimiter.setFieldExtractor(fields);
        writer.setLineAggregator(myDelimiter);
        return writer;
    }

    private String[] getAllFieldsClass(Movie movie) {
        val fields = new LinkedList<String>();
        for (val item : movie.getClass().getDeclaredFields()) {
            fields.add(item.getName());
        }
        return fields.toArray(new String[0]);
    }

    @Bean
    public Step databaseToCsvFileStep() {
        return stepBuilderFactory.get("databaseToCsvFileStep")
                .<Movie, Movie> chunk(10)
                .reader(reader(will_be_injected))
                .writer(writer())
                .build();
    }

    @Bean
    public Job databaseToCsvFileJob() {
        return jobBuilderFactory.get("databaseToCsvFileJob")
                .incrementer(new RunIdIncrementer())
                .flow(databaseToCsvFileStep())
                .end()
                .build();
    }
}