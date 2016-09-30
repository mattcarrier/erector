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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.federecio.dropwizard.swagger.SwaggerViewConfiguration;

public class ErectorSwaggerBundleConfiguration {
    /**
     * This is the only property that is required for Swagger to work correctly.
     * <p/>
     * It is a comma separated list of the all the packages that contain the
     * {@link io.swagger.annotations.Api} annotated resources
     */
    @NotEmpty
    private String resourcePackage = "org.mattcarrier.erector.resource";

    private String title;
    private String version;
    private String description;
    private String termsOfServiceUrl;
    private String contact;
    private String contactEmail;
    private String contactUrl;
    private String license;
    private String licenseUrl;
    private SwaggerViewConfiguration swaggerViewConfiguration = new SwaggerViewConfiguration();
    private Boolean prettyPrint = true;
    private String host;
    private String[] schemes = new String[] { "http" };
    private Boolean enabled = true;

    /**
     * For most of the scenarios this property is not needed.
     * <p/>
     * This is not a property for Swagger but for bundle to set up Swagger UI
     * correctly. It only needs to be used of the root path or the context path
     * is set programmatically and therefore cannot be derived correctly. The
     * problem arises in that if you set the root path or context path in the
     * run() method in your Application subclass the bundle has already been
     * initialized by that time and so does not know you set the path
     * programmatically.
     */
    @JsonProperty
    private String uriPrefix;

    @JsonProperty
    public String getResourcePackage() {
        return resourcePackage;
    }

    @JsonProperty
    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    @JsonProperty
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    @JsonProperty
    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    @JsonProperty
    public String getContact() {
        return contact;
    }

    @JsonProperty
    public void setContact(String contact) {
        this.contact = contact;
    }

    @JsonProperty
    public String getContactEmail() {
        return contactEmail;
    }

    @JsonProperty
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @JsonProperty
    public String getContactUrl() {
        return contactUrl;
    }

    @JsonProperty
    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    @JsonProperty
    public String getLicense() {
        return license;
    }

    @JsonProperty
    public void setLicense(String license) {
        this.license = license;
    }

    @JsonProperty
    public String getLicenseUrl() {
        return licenseUrl;
    }

    @JsonProperty
    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    @JsonProperty
    public String getUriPrefix() {
        return uriPrefix;
    }

    @JsonProperty
    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @JsonProperty
    public SwaggerViewConfiguration getSwaggerViewConfiguration() {
        return swaggerViewConfiguration;
    }

    @JsonProperty
    public void setSwaggerViewConfiguration(final SwaggerViewConfiguration swaggerViewConfiguration) {
        this.swaggerViewConfiguration = swaggerViewConfiguration;
    }

    @JsonProperty
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    @JsonProperty
    public void setIsPrettyPrint(final boolean isPrettyPrint) {
        this.prettyPrint = isPrettyPrint;
    }

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public String[] getSchemes() {
        return schemes;
    }

    @JsonProperty
    public void setSchemes(String[] schemes) {
        this.schemes = schemes;
    }

    @JsonProperty
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty
    public void setIsEnabled(final boolean isEnabled) {
        this.enabled = isEnabled;
    }

    public SwaggerBundleConfiguration buildSwaggerBundleConfiguration() {
        final SwaggerBundleConfiguration bundle = new SwaggerBundleConfiguration();
        bundle.setContact(this.contact);
        bundle.setContactEmail(this.contactEmail);
        bundle.setContactUrl(this.contactUrl);
        bundle.setDescription(this.description);
        bundle.setHost(this.host);
        bundle.setIsEnabled(this.enabled);
        bundle.setIsPrettyPrint(this.prettyPrint);
        bundle.setLicense(this.license);
        bundle.setLicenseUrl(this.licenseUrl);
        bundle.setResourcePackage(this.resourcePackage);
        bundle.setSchemes(this.schemes);
        bundle.setSwaggerViewConfiguration(this.swaggerViewConfiguration);
        bundle.setTermsOfServiceUrl(this.termsOfServiceUrl);
        bundle.setTitle(this.title);
        bundle.setUriPrefix(this.uriPrefix);
        bundle.setVersion(this.version);
        return bundle;
    }
}
