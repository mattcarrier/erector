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

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
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
            public DataSourceFactory getDataSourceFactory(ErectorConfiguration configuration) {
                return configuration.getDatabase();
            }

            @Override
            public FlywayFactory getFlywayFactory(ErectorConfiguration configuration) {
                return configuration.getFlywayFactory();
            }
        });
    }

    @Override
    public void run(ErectorConfiguration configuration, Environment environment) throws Exception {
        // TODO Auto-generated method stub

    }

}
