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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funthomas424242.rades.annotations.builder.RadesAddBuilder;
import com.github.funthomas424242.rades.annotations.builder.RadesNoBuilder;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.Objects;

@RadesAddBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteUrl implements Serializable {

    @Id
    @RadesNoBuilder
    protected NitriteId id;

    protected String url;

    protected SiteType type;

    public SiteType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "SiteUrl{" +
            "id=" + id +
            ", url='" + url + '\'' +
            ", type=" + type +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteUrl)) return false;
        SiteUrl siteUrl = (SiteUrl) o;
        return Objects.equals(id, siteUrl.id) &&
            Objects.equals(url, siteUrl.url) &&
            type == siteUrl.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, type);
    }


}
