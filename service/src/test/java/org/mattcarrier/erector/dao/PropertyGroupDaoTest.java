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
package org.mattcarrier.erector.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.Tag;
import org.mattcarrier.erector.domain.PropertyGroup.Status;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PropertyGroupDaoTest extends AbstractDaoTest {
    private PropertyGroupDao dao;
    private TagDao tagDao;

    private PropertyGroup pg;
    private PropertyGroup pgInactive;
    private PropertyGroup pgVersion2;
    private PropertyGroup pgName2;

    private Tag t;
    private Tag t2;

    @Before
    public void setup() {
        dao = dbi.onDemand(PropertyGroupDao.class);
        tagDao = dbi.onDemand(TagDao.class);

        pg = new PropertyGroup();
        pg.setName("name");
        pg.setStatus(Status.ACTIVE);
        pg.setVersion("version");
        pg.setId(dao.createPropertyGroup(pg));

        pgInactive = new PropertyGroup();
        pgInactive.setName("name");
        pgInactive.setStatus(Status.INACTIVE);
        pgInactive.setVersion("version");
        pgInactive.setId(dao.createPropertyGroup(pgInactive));

        pgVersion2 = new PropertyGroup();
        pgVersion2.setName("name");
        pgVersion2.setStatus(Status.ACTIVE);
        pgVersion2.setVersion("version2");
        pgVersion2.setId(dao.createPropertyGroup(pgVersion2));

        pgName2 = new PropertyGroup();
        pgName2.setName("name2");
        pgName2.setStatus(Status.ACTIVE);
        pgName2.setVersion("version");
        pgName2.setId(dao.createPropertyGroup(pgName2));

        t = new Tag();
        t.setKey("testTag");
        t.setValue("value");
        tagDao.createTagDomain(t.getKey());

        t2 = new Tag();
        t2.setKey("testTag");
        t2.setValue("value");

        t.setId(tagDao.addTag(t.getKey(), t.getValue()));
        tagDao.associateTag(t.getId(), pg.getId());

        t2.setId(tagDao.addTag(t2.getKey(), t2.getValue()));
        tagDao.associateTag(t2.getId(), pgInactive.getId());
    }

    @After
    public void tearDown() {
        final ImmutableList<Long> tagIds = ImmutableList.of(t.getId(), t2.getId());
        tagDao.disassociateAllTags(tagIds);
        tagDao.removeAllTags(tagIds);
        tagDao.deleteTagDomain(t.getKey());

        dao.deletePropertyGroup(pg);
        dao.deletePropertyGroup(pgInactive);
        dao.deletePropertyGroup(pgVersion2);
        dao.deletePropertyGroup(pgName2);
    }

    @Test
    public void update() {
        pg.setName("differentname");
        pg.setStatus(Status.INACTIVE);
        pg.setVersion("version2");
        assertEquals(1, dao.updatePropertyGroup(pg));
        assertEquals(pg, dao.byId(pg.getId()));
    }

    @Test
    public void byId() {
        assertEquals(pg, dao.byId(pg.getId()));
    }

    @Test
    public void byName() {
        final List<PropertyGroup> groups = dao.filterNoTags(ImmutableMap.of("name", pg.getName()),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(3, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void byNameAndVersion() {
        final List<PropertyGroup> groups = dao.filterNoTags(
                ImmutableMap.of("name", pg.getName(), "version", pg.getVersion()),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(2, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void byNameAndStatus() {
        final List<PropertyGroup> groups = dao.filterNoTags(
                ImmutableMap.of("name", pg.getName(), "status", pg.getStatus().toString()),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(2, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void byNameVersionAndStatus() {
        final List<PropertyGroup> groups = dao.filterNoTags(
                ImmutableMap.of("name", pg.getName(), "status", pg.getStatus().toString(), "version", pg.getVersion()),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(1, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void byNameVersionAndTags() {
        final List<PropertyGroup> groups = dao.filterWithTags(
                (ImmutableMap.of("name", pg.getName(), "version", pg.getVersion())),
                ImmutableList.of(new Sort("status"), new Sort("id")), ImmutableList.of(t));
        assertEquals(2, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void pagingAll() {
        final List<PropertyGroup> groups = dao.filterNoTags(
                (ImmutableMap.of("name", pg.getName(), "start", "0", "limit", String.valueOf(Integer.MAX_VALUE))),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(3, groups.size());
        assertEquals(pg, groups.iterator().next());
    }

    @Test
    public void paging() {
        final List<PropertyGroup> groups = dao.filterNoTags(
                (ImmutableMap.of("name", pg.getName(), "start", "1", "limit", "1")),
                ImmutableList.of(new Sort("status"), new Sort("id")));
        assertEquals(1, groups.size());
        assertEquals(pgVersion2, groups.iterator().next());
    }
}
