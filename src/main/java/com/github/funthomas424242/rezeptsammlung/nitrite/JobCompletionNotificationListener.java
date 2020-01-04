package com.github.funthomas424242.rezeptsammlung.nitrite;

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

import com.github.funthomas424242.sbstarter.nitrite.NitriteRepository;
import org.dizitart.no2.objects.Cursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener<T> extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    protected final NitriteRepository<T> nitriteRepository;

    public JobCompletionNotificationListener() {
        this.nitriteRepository = null;
    }

    public JobCompletionNotificationListener(NitriteRepository<T> nitriteRepository) {
        this.nitriteRepository = nitriteRepository;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (this.nitriteRepository != null && jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            final Cursor<T> cursor = nitriteRepository.find();
            cursor.forEach(
                item -> log.info("Found <" + item + "> in the database.")
            );
        }
    }
}
