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
package org.mattcarrier.erector.persistence.jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mattcarrier.erector.ErectorConfiguration;
import org.mattcarrier.erector.dao.PropertyDao;
import org.mattcarrier.erector.dao.PropertyGroupDao;
import org.mattcarrier.erector.dao.TagDao;
import org.mattcarrier.erector.persistence.PersistenceFactory;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.annotation.JsonTypeName;

import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.flyway.FlywayConfiguration;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;

@JsonTypeName("jdbi")
public class JDBIPersistenceFactory implements PersistenceFactory, DatabaseConfiguration<ErectorConfiguration>,
        FlywayConfiguration<ErectorConfiguration> {
    @NotNull
    private String name = "jdbi";

    @Valid
    @NotNull
    private ErectorDataSourceFactory database = new H2InMemoryDataSourceFactory();

    @Valid
    @NotNull
    private FlywayFactory flywayFactory = new FlywayFactory();

    private DBI jdbi;

    @Override
    public void initialize(Environment env) {
        final DBIFactory factory = new DBIFactory();
        this.jdbi = factory.build(env, database, name);
    }

    @Override
    public PropertyGroupDao propertyGroupDao() {
        return jdbi.onDemand(PropertyGroupDao.class);
    }

    @Override
    public TagDao tagDao() {
        return jdbi.onDemand(TagDao.class);
    }

    @Override
    public PropertyDao propertyDao() {
        return jdbi.onDemand(PropertyDao.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(ErectorConfiguration configuration) {
        return database;
    }

    @Override
    public FlywayFactory getFlywayFactory(ErectorConfiguration configuration) {
        return flywayFactory;
    }
}
