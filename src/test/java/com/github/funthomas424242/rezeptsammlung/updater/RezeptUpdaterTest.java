//package com.github.funthomas424242.rezeptsammlung.updater;
//
///*-
// * #%L
// * rezeptsammlung
// * %%
// * Copyright (C) 2019 PIUG
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Lesser Public License for more details.
// *
// * You should have received a copy of the GNU General Lesser Public
// * License along with this program.  If not, see
// * <http://www.gnu.org/licenses/lgpl-3.0.html>.
// * #L%
// */
//
//import com.github.funthomas424242.rezeptsammlung.rezept.Rezept;
//import com.github.funthomas424242.sbstarter.nitrite.NitriteRepository;
//import com.github.funthomas424242.sbstarter.nitrite.NitriteTemplate;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ActiveProfiles("updater")
//class RezeptUpdaterTest {
//
//    @Autowired
//    NitriteTemplate nitriteTemplate;
//
//    protected NitriteRepository<Rezept> repository;
//
//    @BeforeEach
//    public void setUp() {
//        repository = nitriteTemplate.getRepository(Rezept.class);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        repository.drop();
//    }
//
//
//    @Test
//    public void pruefeImportierteItems() {
//        assertEquals(2, repository.size());
//    }
//
//
//}
