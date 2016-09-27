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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mattcarrier.erector.domain.Property;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.PropertyGroup.Status;

import com.google.common.collect.ImmutableList;

public class PropertyDaoTest extends AbstractDaoTest {
    private PropertyDao dao;
    private PropertyGroupDao pgDao;

    private PropertyGroup pg;
    private PropertyGroup pg2;

    private Property p;
    private Property p2;
    private Property p3;

    @Before
    public void setup() {
        dao = dbi.onDemand(PropertyDao.class);
        pgDao = dbi.onDemand(PropertyGroupDao.class);

        pg = new PropertyGroup();
        pg.setName("name");
        pg.setStatus(Status.ACTIVE);
        pg.setVersion("version");
        pg.setId(pgDao.createPropertyGroup(pg));

        pg2 = new PropertyGroup();
        pg2.setName("name2");
        pg2.setStatus(Status.ACTIVE);
        pg2.setVersion("version");
        pg2.setId(pgDao.createPropertyGroup(pg2));

        p = new Property();
        p.setDescription("description");
        p.setKey("key");
        p.setPropertyGroupId(pg.getId());
        p.setValue("value");
        p.setId(dao.createProperty(p));

        p2 = new Property();
        p2.setDescription("description");
        p2.setKey("key2");
        p2.setPropertyGroupId(pg.getId());
        p2.setValue("value");
        p2.setId(dao.createProperty(p2));

        p3 = new Property();
        p3.setDescription("description");
        p3.setKey("key");
        p3.setPropertyGroupId(pg2.getId());
        p3.setValue("value");
        p3.setId(dao.createProperty(p3));
    }

    @After
    public void tearDown() {
        dao.deleteProperty(p);
        dao.deleteProperty(p2);
        dao.deleteProperty(p3);

        pgDao.deletePropertyGroup(pg);
        pgDao.deletePropertyGroup(pg2);
    }

    @Test
    public void updateProperty() {
        p.setDescription("description2");
        p.setKey("key2");
        p.setPropertyGroupId(pg2.getId());
        p.setValue("value2");
        assertEquals(1, dao.updateProperty(p));
        assertEquals(p, dao.byId(p.getId()));
    }

    @Test
    public void byPropertyGroup() {
        assertEquals(ImmutableList.of(p, p2), dao.byPropertyGroupId(pg.getId()));
    }
}
