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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Queryable attribute on a PropertyGroup")
public class Tag {
    private Long id;

    @NotNull
    @Pattern(regexp = "[0-9,a-z,A-Z_]{1,64}")
    private String key;

    @Size(max = 128)
    private String value;

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

    @Override
    public final int hashCode() {
        return Objects.hashCode(key, value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }

        final Tag that = (Tag) obj;
        return Objects.equal(null == this.key ? null : this.key.toUpperCase(),
                null == that.key ? null : that.key.toUpperCase()) && Objects.equal(this.value, that.value);
    }

    @Override
    public String toString() {
        final StringBuilder bldr = new StringBuilder(key);
        bldr.append(" = '");
        bldr.append(value);
        bldr.append("'");
        return bldr.toString();
    }
}
