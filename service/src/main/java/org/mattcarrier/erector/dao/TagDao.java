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

import org.mattcarrier.erector.dao.mapper.TagDomainMapper;
import org.mattcarrier.erector.dao.mapper.TagMapper;
import org.mattcarrier.erector.domain.Tag;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
@RegisterMapper({ TagMapper.class, TagDomainMapper.class })
public interface TagDao {
    @SqlUpdate("ALTER TABLE Tag ADD COLUMN <key> VARCHAR(128); CREATE INDEX indexTag<key> ON Tag(<key>)")
    public void createTagDomain(@Define("key") String key);

    @SqlUpdate("ALTER TABLE Tag DROP COLUMN <key>")
    public void deleteTagDomain(@Define("key") String key);

    @SqlUpdate("INSERT INTO Tag(<key>) VALUES(:value)")
    @GetGeneratedKeys
    public long addTag(@Define("key") String key, @Bind("value") String value);

    @SqlUpdate("INSERT INTO TagPropertyGroupXref(tagId, propertyGroupId) VALUES(:tagId, :propertyGroupId)")
    @GetGeneratedKeys
    public long associateTag(@Bind("tagId") Long tagId, @Bind("propertyGroupId") Long propertyGroupId);

    @SqlUpdate("DELETE FROM TagPropertyGroupXref WHERE tagId = :id")
    public int disassociateTag(@Bind("id") Long id);

    @SqlUpdate("DELETE FROM Tag WHERE id = :id")
    public int removeTag(@Bind("id") Long id);

    @SqlUpdate("DELETE FROM TagPropertyGroupXref WHERE tagId IN (<tagIds>)")
    public int disassociateAllTags(@BindIn("tagIds") Collection<Long> tagIds);

    @SqlUpdate("DELETE FROM Tag WHERE id IN (<tagIds>)")
    public int removeAllTags(@BindIn("tagIds") Collection<Long> tagIds);

    @SqlQuery("SELECT * FROM Tag WHERE doNotDelete = '1' LIMIT 1")
    public TagDomain getTagDomain();

    @SqlQuery("SELECT * FROM Tag WHERE id = :id")
    public Tag byId(@Bind("id") Long id);

//@formatter:off
    @SqlQuery("SELECT " +
                "t.*, " +
              "FROM " +
                "Tag t INNER JOIN " +
                "TagPropertyGroupXref x ON t.Id = x.tagId " +
              "WHERE " +
                "x.propertyGroupId = :propertyGroupId")
//@formatter:on
    public Collection<Tag> byPropertyGroupId(@Bind("propertyGroupId") Long propertyGroupId);
}
