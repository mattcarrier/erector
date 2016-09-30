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

import java.util.List;
import java.util.Map;

import org.mattcarrier.erector.dao.mapper.PropertyMapper;
import org.mattcarrier.erector.domain.Property;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.BindMap;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@UseStringTemplate3StatementLocator
@RegisterMapper(PropertyMapper.class)
public interface PropertyDao {
    @SqlUpdate("INSERT INTO Property(key, value, description, propertyGroupId) VALUES(:key, :value, :description, :propertyGroupId)")
    @GetGeneratedKeys
    public long createProperty(@BindBean Property p);

    @SqlUpdate("UPDATE Property SET key = :key, value = :value, description = :description, propertyGroupId = :propertyGroupId WHERE id = :id")
    public int updateProperty(@BindBean Property p);

    @SqlUpdate("DELETE Property WHERE id = :id")
    public int deleteProperty(@BindBean Property p);

    @SqlQuery("SELECT id, key, value, description, propertyGroupId FROM Property WHERE id = :id")
    public Property byId(@Bind("id") Long id);

//@formatter:off
    @SqlQuery("SELECT " +
                "id, key, value, description, propertyGroupId " +
              "FROM " +
                "Property " +
              "WHERE " +
                "(id = :id OR NULL IS :id) AND " +
                "(key = :key OR NULL IS :key) AND " +
                "(value = :value OR NULL IS :value) AND " +
                "(description = :description OR NULL IS :description) AND " +
                "(propertyGroupId = :propertyGroupId OR NULL IS :propertyGroupId) " +
              "ORDER BY " +
                "<sorts; separator=\",\"> " +
              "OFFSET :start " +
              "LIMIT :limit")
//@formatter:on
    public List<Property> filter(
            @BindMap({ "id", "id", "key", "key", "value", "value", "description", "description", "propertyGroupId",
                    "propertyGroupId", "start", "limit" }) Map<String, String> bindings,
            @Define("sorts") List<Sort> sorts);
}
