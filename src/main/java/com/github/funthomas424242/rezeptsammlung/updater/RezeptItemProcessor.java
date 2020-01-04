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

import com.github.funthomas424242.rezeptsammlung.rezept.Rezept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class RezeptItemProcessor implements ItemProcessor<Rezept, Rezept> {

    protected static final Logger LOG = LoggerFactory.getLogger(RezeptItemProcessor.class);

    @Override
    public Rezept process(final Rezept rezept) throws Exception {
        final Rezept rezeptNew = rezept;
        LOG.info("Converting ({}) into ({})", rezept, rezeptNew);
        return rezeptNew;
    }

}
