package com.github.funthomas424242.rezeptsammlung.crawler;

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
public class CrawlerConfiguration {

    protected static final Logger LOG = LoggerFactory.getLogger(CrawlerConfiguration.class);

    @Value("${rezept.batch.crawler.inputfile:}")
    protected String batchInputFile;

    protected NitriteTemplate nitriteTemplate;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public CrawlerConfiguration(NitriteTemplate nitriteTemplate) {
        this.nitriteTemplate = nitriteTemplate;
    }


    protected NitriteRepository<SiteUrl> getSiteUrlRepository() {
        final NitriteRepository<SiteUrl> repository = nitriteTemplate.getRepository(SiteUrl.class);
        // Da Indizes permanent in der Datenbank gespeichert werden,
        // dürfen diese nur 1x angelegt werden.
        // können aber async erstellt werden, da nur mit geringen Datenmengen gerechnet wird.
        if (!repository.hasIndex("id")) {
            repository.createIndex("id", indexOptions(IndexType.Unique, true));
        }
        if (!repository.hasIndex("url")) {
            repository.createIndex("url", indexOptions(IndexType.Unique, true));
        }
//        if (!repository.hasIndex("lastReadtime")) {
//            repository.createIndex("lastReadtime", indexOptions(IndexType.NonUnique, true));
//        }
        return repository;
    }

//    @Bean
    public JsonItemReader<SiteUrl> reader() {

        final ObjectMapper objectMapper = new ObjectMapper();
        // configure the objectMapper as required
        final JacksonJsonObjectReader<SiteUrl> jsonObjectReader =
            new JacksonJsonObjectReader<>(SiteUrl.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<SiteUrl>()
            .jsonObjectReader(jsonObjectReader)
            .resource(new FileSystemResource(batchInputFile))
            .name("siteJsonItemReader")
            .build();
    }

//    @Bean
    public SiteUrlItemProcessor processor() {
        return new SiteUrlItemProcessor();
    }


//    @Bean
    public SiteUrlItemWriter writer() {
        final NitriteRepository<SiteUrl> siteRepo = getSiteUrlRepository();
        LOG.debug("nitrite repository for writer is: {}", siteRepo);
        return new SiteUrlItemWriter(siteRepo);
    }


//    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<SiteUrl, SiteUrl>chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @Bean
    public Job importSiteJob() {
        return jobBuilderFactory.get("importSiteJob")
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener<SiteUrl>(getSiteUrlRepository()))
            .flow(step1())
            .end()
            .build();
    }


}
