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
import org.h2.tools.Server;
import org.mattcarrier.erector.persistence.PersistenceFactory;
import org.mattcarrier.erector.resource.PropertyGroupResource;
import org.mattcarrier.erector.resource.PropertyResource;
import org.mattcarrier.erector.resource.TagResource;

import com.google.common.base.Optional;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayConfiguration;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

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
        bootstrap.addBundle(new SwaggerBundle<ErectorConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ErectorConfiguration configuration) {
                return configuration.getSwagger().buildSwaggerBundleConfiguration();
            }
        });
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
        System.out.println(dsFactory.get().getUrl());
        Server.createWebServer("-web").start();
        if (flywayFactory.isPresent() && dsFactory.isPresent()) {
            final DataSource ds = dsFactory.get().build(env.metrics(), "flyway");
            final Flyway flyway = flywayFactory.get().build(ds);
            flyway.migrate();
            ds.getConnection().close();
        }

        configuration.getPersistence().initialize(env);
        env.jersey().register(new PropertyGroupResource(configuration.getPersistence().propertyGroupDao()));
        env.jersey().register(new PropertyResource(configuration.getPersistence().propertyDao()));
        env.jersey().register(new TagResource(configuration.getPersistence().tagDao()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Optional<DataSourceFactory> dsFactory(ErectorConfiguration configuration) {
        final PersistenceFactory persistence = configuration.getPersistence();
        if (!(persistence instanceof DatabaseConfiguration)) {
            return Optional.absent();
        }

        final PooledDataSourceFactory dsFactory = ((DatabaseConfiguration) persistence)
                .getDataSourceFactory(configuration);
        if (dsFactory instanceof DataSourceFactory) {
            return Optional.of((DataSourceFactory) dsFactory);
        }

        return Optional
                .of((DataSourceFactory) ((DatabaseConfiguration) persistence).getDataSourceFactory(configuration));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Optional<FlywayFactory> flywayFactory(ErectorConfiguration configuration) {
        final PersistenceFactory persistence = configuration.getPersistence();
        if (!(persistence instanceof FlywayConfiguration)) {
            return Optional.absent();
        }

        return Optional.of((FlywayFactory) ((FlywayConfiguration) persistence).getFlywayFactory(configuration));
    }
}
