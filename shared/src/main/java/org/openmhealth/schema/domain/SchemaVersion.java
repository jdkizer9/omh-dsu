/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.schema.domain;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * A sequence-based schema version, consisting of a major number, minor number, and an optional qualifier.
 *
 * @author Emerson Farrugia
 */
public class SchemaVersion implements Comparable<SchemaVersion> {

    public static final String QUALIFIER_PATTERN_STRING = "[a-zA-Z0-9]+";
    public static final Pattern QUALIFIER_PATTERN = Pattern.compile(QUALIFIER_PATTERN_STRING);
    public static final String VERSION_PATTERN_STRING = "(\\d+)\\.(\\d+)(?:\\.(" + QUALIFIER_PATTERN_STRING + "))?";
    public static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_PATTERN_STRING);

    private int major;
    private int minor;
    private String qualifier;

    public SchemaVersion(String version) {

        Preconditions.checkNotNull(version);
        Matcher matcher = VERSION_PATTERN.matcher(version);
        Preconditions.checkArgument(matcher.matches());

        this.major = Integer.valueOf(matcher.group(1));
        this.minor = Integer.valueOf(matcher.group(2));
        this.qualifier = matcher.group(3);
    }

    public SchemaVersion(int major, int minor) {
        this(major, minor, null);
    }

    public SchemaVersion(int major, int minor, String qualifier) {

        Preconditions.checkArgument(major >= 0);
        Preconditions.checkArgument(minor >= 0);
        Preconditions.checkArgument(qualifier == null || QUALIFIER_PATTERN.matcher(qualifier).matches());

        this.major = major;
        this.minor = minor;
        this.qualifier = qualifier;
    }

    /**
     * @deprecated should only be used by frameworks for persistence or serialisation
     */
    @Deprecated
    SchemaVersion() {
    }

    public static boolean isValidVersion(String version) {
        return version == null || VERSION_PATTERN.matcher(version).matches();
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public Optional<String> getQualifier() {
        return Optional.ofNullable(qualifier);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(this.major);
        builder.append(".");
        builder.append(this.minor);

        if (qualifier != null) {
            builder.append(".");
            builder.append(qualifier);
        }

        return builder.toString();
    }

    @Override
    public int compareTo(SchemaVersion that) {

        if (getMajor() < that.getMajor()) {
            return -1;
        }
        if (getMajor() > that.getMajor()) {
            return 1;
        }

        if (getMinor() < that.getMinor()) {
            return -1;
        }
        if (getMinor() > that.getMinor()) {
            return 1;
        }

        if (getQualifier().isPresent() && !that.getQualifier().isPresent()) {
            return -1;
        }
        if (!getQualifier().isPresent() && that.getQualifier().isPresent()) {
            return 1;
        }
        if (!getQualifier().isPresent()) {
            return 0;
        }
        return getQualifier().get().compareTo(that.getQualifier().get());
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        SchemaVersion that = (SchemaVersion) object;

        if (major != that.major) {
            return false;
        }
        if (minor != that.minor) {
            return false;
        }
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }
}
