package com.github.funthomas424242.rezeptsammlung.rezept;

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
import com.github.funthomas424242.sbstarter.nitrite.NitriteTemplate;
import org.dizitart.no2.IndexType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.dizitart.no2.IndexOptions.indexOptions;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

@RestController
@RequestMapping(path = "/rezepte")
public class Rezeptsammlung {

    @Autowired
    protected NitriteTemplate nitriteTemplate;


    @GetMapping(path = "/all", produces = "application/json")
    public ResponseEntity<List<Rezept>> getAll() {
        final NitriteRepository<Rezept> repository = getRepository();
        final List<Rezept> rezepte = repository.find().toList();
        repository.close();
        return new ResponseEntity<List<Rezept>>(rezepte, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Rezept> getRezept(@PathVariable final long id) {
        final NitriteRepository<Rezept> repository = getRepository();
        final Rezept rezept = repository.find(eq("id", id)).firstOrDefault();
        repository.close();
        return new ResponseEntity<Rezept>(rezept, HttpStatus.OK);
    }

    @PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Rezept> addRezept(@RequestBody final Rezept rezept) {
        final NitriteRepository<Rezept> repository = getRepository();
        repository.insert(rezept);
        repository.close();
        return new ResponseEntity<Rezept>(rezept, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Rezept> deleteRezept(@PathVariable final long id) {
        final NitriteRepository<Rezept> repository = getRepository();
        final int geloeschteAnzahl = repository.remove(eq("id", id)).getAffectedCount();
        repository.close();
        if (geloeschteAnzahl == 0) {
            return new ResponseEntity<Rezept>(HttpStatus.NOT_FOUND);
        } else if (geloeschteAnzahl == 1) {
            return new ResponseEntity<Rezept>(HttpStatus.OK);
        } else if (geloeschteAnzahl > 1) {
            return new ResponseEntity<Rezept>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<Rezept>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

}
