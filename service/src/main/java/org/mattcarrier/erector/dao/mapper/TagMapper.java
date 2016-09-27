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
package org.mattcarrier.erector.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.mattcarrier.erector.domain.Tag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TagMapper implements ResultSetMapper<Tag> {
    @Override
    public Tag map(int index, ResultSet rs, StatementContext ctxt) throws SQLException {
        final Tag t = new Tag();
        final ResultSetMetaData md = rs.getMetaData();

        t.setId(rs.getLong("id"));
        for (int i = 1; i <= md.getColumnCount(); i++) {
            final String val = rs.getString(i);
            final String label = md.getColumnLabel(1);
            if (null != val && !("doNotDelete".equalsIgnoreCase(md.getColumnName(i)))
                    && !("id".equalsIgnoreCase(md.getColumnName(i)))) {
                t.setKey(md.getColumnName(i));
                t.setValue(rs.getString(i));

                return t;
            }
        }

        throw new RuntimeException("Tag[" + t.getId() + "] has no value");
    }
}
