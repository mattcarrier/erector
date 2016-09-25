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
package org.mattcarrier.erector.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "A singular property")
public class Property {
    private Long id;

    @NotNull
    @Size(min = 1, max = 128)
    private String key;

    @Size(max = 1024)
    private String value;

    @Size(max = 512)
    private String description;

    @NotNull
    private Long propertyGroupId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPropertyGroupId() {
        return propertyGroupId;
    }

    public void setPropertyGroupId(Long propertyGroupId) {
        this.propertyGroupId = propertyGroupId;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(key, value, description, propertyGroupId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }

        final Property that = (Property) obj;
        return Objects.equal(this.description, that.description) && Objects.equal(this.key, that.key)
                && Objects.equal(this.propertyGroupId, that.propertyGroupId) && Objects.equal(this.value, that.value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("description", description).add("id", id).add("key", key)
                .add("propertyGroupId", propertyGroupId).add("value", value).toString();
    }
}
