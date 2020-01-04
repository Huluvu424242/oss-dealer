package com.github.funthomas424242.rezeptsammlung.updater;

/*-
 * #%L
 * rezeptsammlung
 * %%
 * Copyright (C) 2019 PIUG
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.funthomas424242.rezeptsammlung.nitrite.JobCompletionNotificationListener;
import com.github.funthomas424242.rezeptsammlung.nitrite.NitriteItemWriter;
import com.github.funthomas424242.rezeptsammlung.rezept.Rezept;
import com.github.funthomas424242.sbstarter.nitrite.NitriteRepository;
import com.github.funthomas424242.sbstarter.nitrite.NitriteTemplate;
import org.dizitart.no2.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import static org.dizitart.no2.IndexOptions.indexOptions;

@Configuration
@EnableBatchProcessing
//public class CrawlerConfiguration extends DefaultBatchConfigurer {
//@Override
//public void setDataSource(DataSource dataSource) {
// no data source used, so empty statement
//    }
public class RezeptUpdaterConfiguration {

    @Value("${rezept.batch.updater.inputfile:}")
    protected String batchInputFile;

    protected static final Logger LOG = LoggerFactory.getLogger(RezeptUpdaterConfiguration.class);

    protected NitriteTemplate nitriteTemplate;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public RezeptUpdaterConfiguration(NitriteTemplate nitriteTemplate) {
        this.nitriteTemplate = nitriteTemplate;
    }

    protected NitriteRepository<Rezept> getRepository() {
        final NitriteRepository<Rezept> repository = nitriteTemplate.getRepository(Rezept.class);
        // Da Indizes permanent in der Datenbank gespeichert werden,
        // dürfen diese nur 1x angelegt werden.
        // können aber async erstellt werden, da nur mit geringen Datenmengen gerechnet wird.
        if (!repository.hasIndex("id")) {
            repository.createIndex("id", indexOptions(IndexType.Unique, true));
        }
        if (!repository.hasIndex("titel")) {
            repository.createIndex("titel", indexOptions(IndexType.Fulltext, true));
        }
        if (!repository.hasIndex("tag")) {
            repository.createIndex("tag", indexOptions(IndexType.NonUnique, true));
        }
        return repository;
    }

    //    @Bean
    public JsonItemReader<Rezept> readerRezept() {

        final ObjectMapper objectMapper = new ObjectMapper();
        // configure the objectMapper as required
        final JacksonJsonObjectReader<Rezept> jsonObjectReader =
            new JacksonJsonObjectReader<>(Rezept.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Rezept>()
            .jsonObjectReader(jsonObjectReader)
            .resource(new FileSystemResource(batchInputFile))
            .name("rezeptJsonItemReader")
            .build();
    }

    public RezeptItemProcessor processorRezept() {
        return new RezeptItemProcessor();
    }

    public NitriteItemWriter<Rezept> writerRezept() {
        final NitriteRepository<Rezept> rezeptRepo = getRepository();
        LOG.debug("nitrite repository for writer is: {}", rezeptRepo);
        return new NitriteItemWriter<Rezept>(rezeptRepo);
    }

    public Step step() {
        return stepBuilderFactory.get("step")
            .<Rezept, Rezept>chunk(10)
            .reader(readerRezept())
            .processor(processorRezept())
            .writer(writerRezept())
            .build();
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importRezeptJob")
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener<>(getRepository()))
            .flow(step())
            .end()
            .build();
    }

//    @Scheduled(cron = "*/5 * * * * *")
//    public void perform() throws Exception {
//
//        System.out.println("Job Started at :" + new Date());
//
//        JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
//            .toJobParameters();
//
//        JobExecution execution = jobLauncher.run(readFiles(), param);
//
//        System.out.println("Job finished with status :" + execution.getStatus());
//    }


}
