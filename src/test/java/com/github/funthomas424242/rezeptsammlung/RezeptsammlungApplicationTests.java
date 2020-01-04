package com.github.funthomas424242.rezeptsammlung;

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
import com.github.funthomas424242.rezeptsammlung.rezept.RezeptBuilder;
import com.github.funthomas424242.sbstarter.nitrite.NitriteRepository;
import com.github.funthomas424242.sbstarter.nitrite.NitriteTemplate;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.util.Iterables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.dizitart.no2.objects.filters.ObjectFilters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest
@ActiveProfiles("test")
class RezeptsammlungApplicationTests {

    protected static final Logger LOG = LoggerFactory.getLogger(RezeptsammlungApplicationTests.class);

    public static final String TEXT_DAS_IST_MAL_EINE_ÄNDERUNG = "Das ist mal eine Änderung!!!";

    @Autowired
    protected NitriteTemplate nitriteTemplate;

    protected NitriteRepository<Rezept> repository;

    @BeforeEach
    public void setUp() {
        repository = nitriteTemplate.getRepository(Rezept.class);
        Assumptions.assumeTrue(repository.find().size() == 0, "Vorbedingung size == 0 nicht erfüllt");
    }

    @AfterEach
    public void tearDown() {
        repository.drop();
    }

    @Test
    @DisplayName("Prüfe ob geschriebene Werte wieder ausgelesen werden können.")
    void pruefeSchreibenLesen() {

        final Rezept rezept = new RezeptBuilder().withTitel("Apfelkuchen mit Hefeteig").build();

        // Schreiben
        final WriteResult writeResult = repository.insert(rezept);
        NitriteId nitriteId = Iterables.firstOrDefault(writeResult);
        LOG.debug("### NitriteId: {}", nitriteId);

        // Lesen
        final Cursor<Rezept> rezepts = repository.find(eq("titel", "Apfelkuchen mit Hefeteig"));
        assertEquals(1, rezepts.size());
        final Rezept firstRezept = rezepts.firstOrDefault();
        assertNotSame(rezept, firstRezept);
        assertEquals(rezept, firstRezept);

        final Rezept firstRezeptAgain = rezepts.firstOrDefault();
        assertNotSame(rezept, firstRezeptAgain);
        assertEquals(rezept, firstRezeptAgain);
        // Es wird jedes mal beim Lesen ein neues Objekt erstellt
        assertNotSame(firstRezept, firstRezeptAgain);
        assertEquals(firstRezept, firstRezeptAgain);

        final Cursor<Rezept> rezepts1 = repository.find(eq("id", nitriteId));
        assertEquals(1, rezepts1.size());

    }

    @Test
    @DisplayName("Prüfe ob ein Wert aktualisiert werden kann.")
    void pruefeUpdate() {

        final Rezept rezept = new RezeptBuilder().withTitel("Testen in vertrauten Umgebungen").build();
        final WriteResult result = repository.insert(rezept);
        final NitriteId id = Iterables.firstOrDefault(result);
        LOG.debug("### id: {}", id);


        final Cursor<Rezept> rezepts = repository.find(eq("id", id));
        assertEquals(1, rezepts.size());
        final Rezept zuaenderndesRezept = rezepts.firstOrDefault();
        // simuliere setTitel(TEXT_DAS_IST_MAL_EINE_ÄNDERUNG);
        final Rezept geaendertesRezept
            = new RezeptBuilder(zuaenderndesRezept).withTitel(TEXT_DAS_IST_MAL_EINE_ÄNDERUNG).build();
        final WriteResult writeResult = repository.update(zuaenderndesRezept);
        final NitriteId nitriteId = Iterables.firstOrDefault(writeResult);
        LOG.debug("### updated id: {}", nitriteId);

        final Cursor<Rezept> rezepts1 = repository.find(eq("id", nitriteId));
        assertEquals(1, rezepts1.size());
        assertEquals(TEXT_DAS_IST_MAL_EINE_ÄNDERUNG, rezepts1.firstOrDefault().getTitel());
    }


}
