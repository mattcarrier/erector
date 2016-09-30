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
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.Tag;
import org.mattcarrier.erector.domain.PropertyGroup.Status;

import com.google.common.collect.ImmutableList;

public class TagDaoTest extends AbstractDaoTest {
    private TagDao dao;
    private PropertyGroupDao pgDao;

    private Tag t;
    private Tag t2;

    private PropertyGroup pg;

    @Before
    public void setup() {
        dao = dbi.onDemand(TagDao.class);
        pgDao = dbi.onDemand(PropertyGroupDao.class);

        t = new Tag();
        t.setKey("testTag");
        t.setValue("value");
        dao.createTagDomain(t.getKey());

        t2 = new Tag();
        t2.setKey("testTag");
        t2.setValue("value2");

        pg = new PropertyGroup();
        pg.setName("name");
        pg.setStatus(Status.ACTIVE);
        pg.setVersion("version");
        pg.setId(pgDao.createPropertyGroup(pg));

        t.setId(dao.addTag(t.getKey(), t.getValue()));
        dao.associateTag(t.getId(), pg.getId());

        t2.setId(dao.addTag(t2.getKey(), t2.getValue()));
        dao.associateTag(t2.getId(), pg.getId());
    }

    @After
    public void tearDown() {
        final ImmutableList<Long> tagIds = ImmutableList.of(t.getId(), t2.getId());
        dao.disassociateAllTags(tagIds);
        dao.removeAllTags(tagIds);
        dao.deleteTagDomain(t.getKey());
        pgDao.deletePropertyGroup(pg.getId());
    }

    @Test
    public void byId() {
        assertEquals(t, dao.byId(t.getId()));
    }

    @Test
    public void disassociateAndRemoveTag() {
        dao.disassociateTag(t2.getId());
        dao.removeTag(t2.getId());
        assertNull(dao.byId(t2.getId()));
    }
}
