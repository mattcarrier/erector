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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

public class Sort {
    public enum Direction {
        ASC, DESC
    }

    private final String field;
    private final Direction direction;

    public Sort(String sort) {
        if (!sort.contains(" ")) {
            this.field = sort;
            this.direction = Direction.ASC;
        } else {
            final String[] split = sort.split(" ");
            this.field = split[0];
            this.direction = Direction.valueOf(split[1].toUpperCase());
        }
    }

    public Sort(String field, Direction direction) {
        this.field = checkNotNull(field);
        this.direction = checkNotNull(direction);
    }

    public String getField() {
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sort)) {
            return false;
        }

        final Sort that = (Sort) obj;
        return Objects.equals(this.field, that.field) && Objects.equals(this.direction, that.direction);
    }

    @Override
    public String toString() {
        return new StringBuilder(field).append(" ").append(direction).toString();
    }
}
