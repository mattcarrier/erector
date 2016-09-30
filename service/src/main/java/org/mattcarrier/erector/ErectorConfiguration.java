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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mattcarrier.erector.persistence.PersistenceFactory;
import org.mattcarrier.erector.persistence.jdbi.JDBIPersistenceFactory;

import io.dropwizard.Configuration;

public class ErectorConfiguration extends Configuration {
    @Valid
    @NotNull
    private PersistenceFactory persistence = new JDBIPersistenceFactory();

    @Valid
    @NotNull
    private ErectorSwaggerBundleConfiguration swagger = new ErectorSwaggerBundleConfiguration();

    public PersistenceFactory getPersistence() {
        return persistence;
    }

    public void setPersistence(PersistenceFactory persistence) {
        this.persistence = persistence;
    }

    public ErectorSwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

    public void setSwagger(ErectorSwaggerBundleConfiguration swagger) {
        this.swagger = swagger;
    }
}
