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

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mattcarrier.erector.dao.TagDao;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("tags")
@Path("/erector/api/v1/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {
    private final TagDao tagDao;

    public TagResource(TagDao tagDao) {
        this.tagDao = checkNotNull(tagDao);
    }

    @POST
    @Path("/domain/{tagName}")
    @ApiOperation(value = "Add a new entry into the tag domain")
    @ApiResponses({ @ApiResponse(code = 409, message = "if the entry is already in the tag domain") })
    public List<String> addTagDomain(@PathParam("tagName") String tagName) {
        final List<String> domain = tagDomain();
        for (String t : domain) {
            if (t.toLowerCase().equals(tagName.toLowerCase())) {
                throw new WebApplicationException(tagName + " is already part of the domain", Status.CONFLICT);
            }
        }

        tagDao.createTagDomain(tagName);
        return tagDomain();
    }

    @GET
    @Path("/domain")
    @ApiOperation(value = "Lists the tag domain")
    public List<String> tagDomain() {
        return tagDao.getTagDomain().getDomain();
    }

    @DELETE
    @Path("/domain/{tagName}")
    @ApiOperation(value = "Remove an entry from the tag domain")
    public Response removeTagDomain(@PathParam("tagName") String tagName) {
        tagDao.deleteTagDomain(tagName);
        return Response.ok().type(MediaType.APPLICATION_JSON).build();
    }
}
