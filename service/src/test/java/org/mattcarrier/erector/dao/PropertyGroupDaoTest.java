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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.flywaydb.core.Flyway;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mattcarrier.erector.domain.PropertyGroup;
import org.mattcarrier.erector.domain.PropertyGroup.Status;
import org.mockito.ArgumentCaptor;
import org.skife.jdbi.v2.DBI;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;

public class PropertyGroupDaoTest {
    private static final DataSourceFactory h2Config = new DataSourceFactory();

    private static final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
    private static final LifecycleEnvironment lifecycleEnvironment = mock(LifecycleEnvironment.class);
    private static final Environment environment = mock(Environment.class);
    private static final DBIFactory factory = new DBIFactory();
    private static final List<Managed> managed = new ArrayList<>();
    private static final MetricRegistry metricRegistry = new MetricRegistry();
    private static DBI dbi;

    @BeforeClass
    public static void setUp() throws Exception {
        BootstrapLogging.bootstrap();
        h2Config.setUrl("jdbc:h2:mem:JDBITest-" + System.currentTimeMillis());
        h2Config.setUser("sa");
        h2Config.setDriverClass("org.h2.Driver");
        h2Config.setValidationQuery("SELECT 1");

        when(environment.healthChecks()).thenReturn(healthChecks);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.metrics()).thenReturn(metricRegistry);
        when(environment.getHealthCheckExecutorService()).thenReturn(Executors.newSingleThreadExecutor());

        dbi = factory.build(environment, h2Config, "hsql");
        final ArgumentCaptor<Managed> managedCaptor = ArgumentCaptor.forClass(Managed.class);
        verify(lifecycleEnvironment).manage(managedCaptor.capture());
        managed.addAll(managedCaptor.getAllValues());
        for (Managed obj : managed) {
            obj.start();
        }

        final Flyway flyway = new Flyway();
        flyway.setDataSource(h2Config.build(metricRegistry, "flyway"));
        flyway.migrate();
    }

    @Test
    public void createPropertyGroup() {
        final PropertyGroup pg = new PropertyGroup();
        pg.setName("name");
        pg.setStatus(Status.ACTIVE);
        pg.setVersion("version");
        final PropertyGroupDao dao = dbi.onDemand(PropertyGroupDao.class);
        pg.setId(dao.createPropertyGroup(pg));

        assertEquals(pg, dao.byId(pg.getId()));
    }
}
