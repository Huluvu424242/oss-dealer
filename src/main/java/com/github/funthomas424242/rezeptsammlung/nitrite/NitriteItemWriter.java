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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;


public class NitriteItemWriter<T> implements ItemWriter<T>, InitializingBean {
    protected static final Log LOG = LogFactory.getLog(NitriteItemWriter.class);

    protected NitriteRepository<T> repository;

    public NitriteItemWriter(final NitriteRepository<T> repository) {
        this.repository = repository;
        LOG.debug("Nitrite Repository zugewiesen.");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.debug("after properties set called.");
    }

    @Override
    public void write(List<? extends T> list) throws Exception {
        LOG.debug("Beginne mit dem Schreiben der Items ins repo:" + repository);
        list.forEach(o ->
            repository.insert(o)
        );
    }
}
