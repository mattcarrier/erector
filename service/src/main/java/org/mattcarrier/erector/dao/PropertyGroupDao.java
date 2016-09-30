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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mattcarrier.erector.dao.mapper.PropertyGroupMapper;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.Tag;
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
@RegisterMapper(PropertyGroupMapper.class)
public interface PropertyGroupDao {
    @SqlUpdate("INSERT INTO PropertyGroup(name, status, version) VALUES(:name, :status, :version)")
    @GetGeneratedKeys
    public long createPropertyGroup(@BindBean PropertyGroup pg);

    @SqlUpdate("UPDATE PropertyGroup SET name = :name, status = :status, version = :version WHERE id = :id")
    public int updatePropertyGroup(@BindBean PropertyGroup pg);

    @SqlUpdate("DELETE PropertyGroup WHERE id = :id")
    public int deletePropertyGroup(@Bind("id") Long id);

    @SqlQuery("SELECT id, name, status, version FROM PropertyGroup WHERE id = :id")
    public PropertyGroup byId(@Bind("id") Long id);

//@formatter:off
    @SqlQuery("SELECT " +
                "p.id, p.name, p.status, p.version " +
              "FROM " +
                "PropertyGroup p " +
              "WHERE " +
                "(p.id = :id OR NULL IS :id) AND " +
                "(p.name = :name OR NULL IS :name) AND " +
                "(p.version = :version OR NULL IS :version) AND " +
                "(p.status = :status OR NULL IS :status) " +
              "ORDER BY " +
                "p.<sorts; separator=\", p.\"> " +
              "OFFSET :start " +
              "LIMIT :limit")
//@formatter:on
    public List<PropertyGroup> filterNoTags(@BindMap({ "id", "id", "name", "name", "version", "version", "status",
            "status", "start", "limit" }) Map<String, String> bindings, @Define("sorts") List<Sort> sorts);

//@formatter:off
    @SqlQuery("SELECT " +
                "COUNT(1) " +
              "FROM " +
                "PropertyGroup p " +
              "WHERE " +
                "(p.id = :id OR NULL IS :id) AND " +
                "(p.name = :name OR NULL IS :name) AND " +
                "(p.version = :version OR NULL IS :version) AND " +
                "(p.status = :status OR NULL IS :status)")
//@formatter:on
    public int filterNoTagsCount(@BindMap({ "id", "id", "name", "name", "version", "version", "status",
            "status" }) Map<String, String> bindings);

//@formatter:off
    @SqlQuery("SELECT " +
                "p.id, p.name, p.status, p.version " +
              "FROM " +
                "PropertyGroup p INNER JOIN " +
                "TagPropertyGroupXref x ON p.id = x.propertyGroupId INNER JOIN " +
                "Tag t ON x.tagId = t.Id " +
              "WHERE " +
                "(p.id = :id OR NULL IS :id) AND " +
                "(p.name = :name OR NULL IS :name) AND " +
                "(p.version = :version OR NULL IS :version) AND " +
                "(p.status = :status OR NULL IS :status) AND " +
                "t.<tags; separator=\" AND t.\"> " +
              "ORDER BY " +
                "p.<sorts; separator=\", p.\"> " +
              "OFFSET :start " +
              "LIMIT :limit")
//@formatter:on
    public List<PropertyGroup> filterWithTags(
            @BindMap({ "id", "id", "name", "name", "version", "version", "status", "status", "start",
                    "limit" }) Map<String, String> bindings,
            @Define("sorts") List<Sort> sorts, @Define("tags") Collection<Tag> tags);

//@formatter:off
    @SqlQuery("SELECT " +
                "COUNT(1) " +
              "FROM " +
                "PropertyGroup p INNER JOIN " +
                "TagPropertyGroupXref x ON p.id = x.propertyGroupId INNER JOIN " +
                "Tag t ON x.tagId = t.Id " +
              "WHERE " +
                "(p.id = :id OR NULL IS :id) AND " +
                "(p.name = :name OR NULL IS :name) AND " +
                "(p.version = :version OR NULL IS :version) AND " +
                "(p.status = :status OR NULL IS :status) AND " +
                "t.<tags; separator=\" AND t.\">")
//@formatter:on
    public int filterWithTagsCount(@BindMap({ "id", "id", "name", "name", "version", "version", "status", "status",
            "start", "limit" }) Map<String, String> bindings, @Define("tags") Collection<Tag> tags);
}
