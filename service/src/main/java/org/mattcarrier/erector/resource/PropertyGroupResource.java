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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

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
import org.mattcarrier.erector.dao.PropertyGroupDao;
import org.mattcarrier.erector.dao.Sort;
import org.mattcarrier.erector.dao.TagDao;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.Tag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import jersey.repackaged.com.google.common.collect.ImmutableList;

@Api("propertygroups")
@Path("/erector/api/v1/propertygroups")
@Produces(MediaType.APPLICATION_JSON)
public class PropertyGroupResource {
    private final PropertyGroupDao pgDao;
    private final TagDao tagDao;

    public PropertyGroupResource(PropertyGroupDao pgDao, TagDao tagDao) {
        this.pgDao = checkNotNull(pgDao);
        this.tagDao = checkNotNull(tagDao);
    }

    @POST
    @ApiOperation("Creates a PropertyGroup")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", responseHeaders = @ResponseHeader(name = "location", description = "location of created resource")) })
    public Response create(@Valid PropertyGroup pg) throws URISyntaxException {
        if (null != pg.getId()) { throw new WebApplicationException("PropertyGroup already exists.", Status.CONFLICT); }

        return Response.created(new URI("/erector/api/v1/propertygroups/" + pgDao.createPropertyGroup(pg)))
                .type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Updates a PropertyGroup", notes = "PropertyGroup must already be created")
    @ApiResponses({ @ApiResponse(code = 204, message = "Updated Successfully"),
            @ApiResponse(code = 400, message = "PropertyGroup is not persisted"),
            @ApiResponse(code = 404, message = "PropertyGroup not found") })
    public Response update(@PathParam("id") Long id, @Valid PropertyGroup pg) {
        if (null == pg.getId()
                || !id.equals(pg.getId())) { throw new WebApplicationException("PropertyGroup is not persisted.",
                        Status.BAD_REQUEST); }

        if (0 == pgDao.updatePropertyGroup(
                pg)) { throw new WebApplicationException("PropertyGroup Not Found", Status.NOT_FOUND); }

        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Deletes a PropertyGroup")
    @ApiResponses({ @ApiResponse(code = 204, message = "Deletion Successfully"),
            @ApiResponse(code = 404, message = "PropertyGroup not found") })
    public Response delete(@PathParam("id") Long id) {
        pgDao.deletePropertyGroup(id);
        return Response.ok().type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Gets a PropertyGroup by ID")
    @ApiResponse(code = 404, message = "PropertyGroup not found")
    public PropertyGroup byId(@PathParam("id") Long id) {
        final PropertyGroup pg = pgDao.byId(id);
        if (null == pg) { throw new NotFoundException("PropertyGroup not found"); }

        return pg;
    }

    @POST
    @Path("/{id}/tags")
    @ApiOperation("Creates a Tag and associates the Tag to the PropertyGroup")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", responseHeaders = @ResponseHeader(name = "location", description = "location of all tags for propertygroup")),
            @ApiResponse(code = 409, message = "if the tag is already persisted") })
    public Response createTag(@PathParam("id") Long propertyGroupId, @Valid Tag tag) throws URISyntaxException {
        if (null != tag.getId()) { throw new WebApplicationException("Tag already exists.", Status.CONFLICT); }

        tag.setId(tagDao.addTag(tag.getKey(), tag.getValue()));
        tagDao.associateTag(tag.getId(), propertyGroupId);
        return Response.created(new URI("/erector/api/v1/propertygroups/" + propertyGroupId + "/tags"))
                .type(MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("{propertyGroupId}/tags/{tagId}")
    @ApiOperation(value = "Deletes an associated Tag")
    @ApiResponses({ @ApiResponse(code = 204, message = "Deletion Successfully") })
    public Response deleteTag(@PathParam("propertyGroupId") Long propertyGroupId, @PathParam("tagId") Long tagId) {
        tagDao.disassociateTag(tagId);
        tagDao.removeTag(tagId);
        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}/tags")
    @ApiOperation("Retrieves all Tags for the PropertyGroup")
    @ApiResponses({ @ApiResponse(code = 404, message = "PropertyGroup not found") })
    public Collection<Tag> tags(@PathParam("id") Long propertyGroupId) {
        return tagDao.byPropertyGroupId(byId(propertyGroupId).getId());
    }

    @DELETE
    @Path("{propertyGroupId}/tags")
    @ApiOperation(value = "Deletes an associated Tag")
    @ApiResponses({ @ApiResponse(code = 204, message = "Deletion Successfully") })
    public Response removeAllTags(@PathParam("propertyGroupId") Long propertyGroupId) {
        final Set<Long> tagIds = tags(propertyGroupId).parallelStream().map(t -> {
            return t.getId();
        }).collect(Collectors.toSet());
        tagDao.disassociateTags(tagIds);
        tagDao.removeTags(tagIds);
        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @ApiOperation(value = "Search for PropertyGroups", notes = "All fields area available for filtering and sortering as well as start and limit for pagination", response = PropertyGroup.class, responseContainer = "List")
    public PagedResponse<PropertyGroup> filter(@Context UriInfo uriInfo,
            @QueryParam("limit") @DefaultValue("50") @Min(1) Integer limit,
            @QueryParam("start") @DefaultValue("0") Integer start, @QueryParam("sort") List<Sort> sorts) {
        final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        final Map<String, String> bindings = new HashMap<>();
        bindings.put("start", String.valueOf(start));
        bindings.put("limit", String.valueOf(limit));

        if (sorts.isEmpty()) {
            sorts = ImmutableList.of(new Sort("status"), new Sort("id"));
        }

        final Set<Tag> tags = new HashSet<>();
        for (Entry<String, List<String>> e : queryParams.entrySet()) {
            final String key = e.getKey();
            if (e.getValue().isEmpty() || bindings.containsKey(key) || e.getKey().equals("sort") || key.equals("start")
                    || key.equals("limit")) {
                continue;
            }

            if (key.equals("name") || key.equals("version") || key.equals("status")) {
                bindings.put(key, e.getValue().get(0));
                continue;
            }

            final Tag t = new Tag();
            t.setKey(key);
            t.setValue(e.getValue().get(0));
            tags.add(t);
        }

        if (tags.isEmpty()) {
            final int total = pgDao.filterNoTagsCount(bindings);
            return new PagedResponse<>(pgDao.filterNoTags(bindings, sorts), start / limit, limit, total);
        }

        final int total = pgDao.filterWithTagsCount(bindings, tags);
        return new PagedResponse<>(pgDao.filterWithTags(bindings, sorts, tags), start / limit, limit, total);
    }
}
