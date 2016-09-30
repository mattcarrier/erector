/**
 * The MIT License
 * Copyright © 2016 Matt Carrier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.mattcarrier.erector.resource;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.mattcarrier.erector.api.PagedResponse;
import org.mattcarrier.erector.dao.PropertyGroupDao;
import org.mattcarrier.erector.dao.Sort;
import org.mattcarrier.erector.dao.Sort.Direction;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.Tag;

import jersey.repackaged.com.google.common.collect.ImmutableList;

@Path("/erector/api/v1/propertygroups")
@Produces(MediaType.APPLICATION_JSON)
public class PropertyGroupResource {
    private final PropertyGroupDao pgDao;

    public PropertyGroupResource(PropertyGroupDao pgDao) {
        this.pgDao = checkNotNull(pgDao);
    }

    @POST
    public Response create(@Valid PropertyGroup pg) throws URISyntaxException {
        if (null != pg.getId()) {
            throw new WebApplicationException("PropertyGroup already exists.", Status.CONFLICT);
        }

        return Response.created(new URI("/erector/api/v1/propertygroups/" + pgDao.createPropertyGroup(pg))).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid PropertyGroup pg) {
        if (null == pg.getId() || !id.equals(pg.getId())) {
            throw new WebApplicationException("PropertyGroup is not persisted.", Status.BAD_REQUEST);
        }

        if (0 == pgDao.updatePropertyGroup(pg)) {
            throw new WebApplicationException("PropertyGroup Not Found", Status.NOT_FOUND);
        }

        return Response.noContent().build();
    }

    @GET
    public PagedResponse<PropertyGroup> filter(@Context UriInfo uriInfo) {
        final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        final Map<String, String> bindings = new HashMap<>();
        bindings.put("start", "0");
        bindings.put("limit", String.valueOf(Integer.MAX_VALUE));

        final List<Sort> sorts;
        if (null == queryParams.get("sort")) {
            sorts = ImmutableList.of(new Sort("status"), new Sort("id"));
        } else {
            sorts = queryParams.get("sort").stream().map(s -> {
                if (!s.contains(" ")) {
                    return new Sort(s);
                }

                final String[] split = s.split(" ");
                return new Sort(split[0], Direction.valueOf(split[1].toUpperCase()));
            }).collect(Collectors.toList());
        }

        final Set<Tag> tags = new HashSet<>();
        for (Entry<String, List<String>> e : queryParams.entrySet()) {
            final String key = e.getKey();
            if (e.getValue().isEmpty() || bindings.containsKey(key) || e.getKey().equals("sort")) {
                continue;
            }

            if (key.equals("name") || key.equals("version") || key.equals("status") || key.equals("start")
                    || key.equals("limit")) {
                bindings.put(key, e.getValue().get(0));
            }

            final Tag t = new Tag();
            t.setKey(key);
            t.setValue(e.getValue().get(0));
            tags.add(t);
        }

        final int start = Integer.valueOf(bindings.get("start"));
        final int limit = Integer.valueOf(bindings.get("limit"));
        if (tags.isEmpty()) {
            final int total = pgDao.filterNoTagsCount(bindings);
            return new PagedResponse<>(pgDao.filterNoTags(bindings, sorts), start / limit, limit, total);
        }

        final int total = pgDao.filterWithTagsCount(bindings, tags);
        return new PagedResponse<>(pgDao.filterWithTags(bindings, sorts, tags), start / limit, limit, total);
    }
}
