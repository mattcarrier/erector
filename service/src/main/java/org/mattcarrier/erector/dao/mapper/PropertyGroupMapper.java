package org.mattcarrier.erector.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.PropertyGroup.Status;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PropertyGroupMapper implements ResultSetMapper<PropertyGroup> {

    @Override
    public PropertyGroup map(int index, ResultSet rs, StatementContext ctxt) throws SQLException {
        final PropertyGroup pg = new PropertyGroup();
        pg.setId(rs.getLong("id"));
        pg.setName(rs.getString("name"));
        pg.setStatus(Status.valueOf(rs.getString("status")));
        pg.setVersion(rs.getString("version"));
        return pg;
    }

}
