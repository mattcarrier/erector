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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.mattcarrier.erector.api.PagedResponse;
import org.mattcarrier.erector.dao.PropertyDao;
import org.mattcarrier.erector.dao.Sort;
import org.mattcarrier.erector.domain.Property;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import jersey.repackaged.com.google.common.collect.ImmutableList;

@Api("properties")
@Path("/erector/api/v1/properties")
@Produces(MediaType.APPLICATION_JSON)
public class PropertyResource {
    private final PropertyDao propDao;

    public PropertyResource(PropertyDao propDao) {
        this.propDao = checkNotNull(propDao);
    }

    @POST
    @ApiOperation("Creates a Property")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", responseHeaders = @ResponseHeader(name = "location", description = "location of created resource") ) })
    public Response create(@Valid Property p) throws URISyntaxException {
        if (null != p.getId()) {
            throw new WebApplicationException("Property already exists.", Status.CONFLICT);
        }

        return Response.created(new URI("/erector/api/v1/properties/" + propDao.createProperty(p)))
                .type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Updates a Property", notes = "Property must already be created")
    @ApiResponses({ @ApiResponse(code = 204, message = "Updated Successfully"),
            @ApiResponse(code = 400, message = "Property is not persisted"),
            @ApiResponse(code = 404, message = "Property not found") })
    public Response update(@PathParam("id") Long id, @Valid Property p) throws URISyntaxException {
        if (null == p.getId() || !id.equals(p.getId())) {
            throw new WebApplicationException("Property is not persisted.", Status.BAD_REQUEST);
        }

        if (0 == propDao.updateProperty(p)) {
            throw new WebApplicationException("Property Not Found", Status.NOT_FOUND);
        }

        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Deletes a Property")
    @ApiResponses({ @ApiResponse(code = 204, message = "Deletion Successfully"),
            @ApiResponse(code = 404, message = "Property not found") })
    public Response delete(@PathParam("id") Long id) {
        if (0 == propDao.deleteProperty(id)) {
            throw new WebApplicationException("Property Not Found", Status.NOT_FOUND);
        }

        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Gets a Property by ID")
    @ApiResponse(code = 404, message = "Property not found")
    public Property byId(@PathParam("id") Long id) {
        final Property p = propDao.byId(id);
        if (null == p) {
            throw new NotFoundException("Property not found");
        }

        return p;
    }

    @GET
    @ApiOperation(value = "Search for Properties", notes = "All fields area available for filtering and sortering as well as start and limit for pagination", response = Property.class, responseContainer = "List")
    public PagedResponse<Property> filter(@Context UriInfo uriInfo,
            @QueryParam("limit") @DefaultValue("50") @Min(1) Integer limit,
            @QueryParam("start") @DefaultValue("0") Integer start, @QueryParam("sort") List<Sort> sorts) {
        final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        final Map<String, String> bindings = new HashMap<>();
        bindings.put("start", String.valueOf(start));
        bindings.put("limit", String.valueOf(limit));

        if (sorts.isEmpty()) {
            sorts = ImmutableList.of(new Sort("id"));
        }

        for (Entry<String, List<String>> e : queryParams.entrySet()) {
            final String key = e.getKey();
            if (e.getValue().isEmpty() || bindings.containsKey(key) || e.getKey().equals("sort") || key.equals("start")
                    || key.equals("limit")) {
                continue;
            }

            bindings.put(key, e.getValue().get(0));
        }

        final int total = propDao.filterCount(bindings);
        return new PagedResponse<>(propDao.filter(bindings, sorts), start / limit, limit, total);
    }
}
