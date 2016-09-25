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
package org.mattcarrier.erector;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.google.common.base.Optional;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayConfiguration;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ErectorApplication extends Application<ErectorConfiguration> {
    public static void main(String[] args) throws Exception {
        new ErectorApplication().run(args);
    }

    @Override
    public String getName() {
        return "erector";
    }

    @Override
    public void initialize(Bootstrap<ErectorConfiguration> bootstrap) {
        bootstrap.addBundle(new FlywayBundle<ErectorConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ErectorConfiguration configuration) {
                final Optional<DataSourceFactory> dsFactory = dsFactory(configuration);
                return dsFactory.isPresent() ? dsFactory.get() : null;
            }

            @Override
            public FlywayFactory getFlywayFactory(ErectorConfiguration configuration) {
                final Optional<FlywayFactory> flywayFactory = flywayFactory(configuration);
                return flywayFactory.isPresent() ? flywayFactory.get() : null;
            }
        });
    }

    @Override
    public void run(ErectorConfiguration configuration, Environment env) throws Exception {
        final Optional<FlywayFactory> flywayFactory = flywayFactory(configuration);
        final Optional<DataSourceFactory> dsFactory = dsFactory(configuration);
        if (flywayFactory.isPresent() && dsFactory.isPresent()) {
            final DataSource ds = dsFactory.get().build(env.metrics(), "flyway");
            final Flyway flyway = flywayFactory.get().build(ds);
            flyway.migrate();
            ds.getConnection().close();
        }

        configuration.getPersistence().initialize(env);
        configuration.getPersistence().propertyGroupDao();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Optional<DataSourceFactory> dsFactory(ErectorConfiguration configuration) {
        if (!(configuration instanceof DatabaseConfiguration)) {
            return Optional.absent();
        }

        return Optional
                .of((DataSourceFactory) ((DatabaseConfiguration) configuration).getDataSourceFactory(configuration));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Optional<FlywayFactory> flywayFactory(ErectorConfiguration configuration) {
        if (!(configuration instanceof FlywayConfiguration)) {
            return Optional.absent();
        }

        return Optional.of((FlywayFactory) ((FlywayConfiguration) configuration).getFlywayFactory(configuration));
    }
}
