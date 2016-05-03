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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


/**
 * A schema identifier. It consists of a namespace, a name, and a version. A schema identifier unambiguously identifies
 * a single, immutable schema. The namespace is used to avoid naming collisions in schemas written by different groups
 * or organisations.
 *
 * @author Emerson Farrugia
 */
public class SchemaId implements Comparable<SchemaId> {

    public static final Pattern NAMESPACE_PATTERN = compile("[a-zA-Z0-9.-]+");
    public static final Pattern NAME_PATTERN = compile("[a-zA-Z0-9-]+");

    private String namespace;
    private String name;
    private SchemaVersion version;

    public SchemaId(String namespace, String name, String version) {

        this(namespace, name, new SchemaVersion(version));
    }

    public SchemaId(String namespace, String name, SchemaVersion version) {

        Preconditions.checkNotNull(namespace);
        Preconditions.checkArgument(isValidNamespace(namespace));
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(isValidName(name));
        Preconditions.checkNotNull(version);

        this.namespace = namespace;
        this.name = name;
        this.version = version;
    }

    /**
     * @deprecated should only be used by frameworks for persistence or serialisation
     */
    @Deprecated
    SchemaId() {
    }

    public String getNamespace() {
        return namespace;
    }

    public static boolean isValidNamespace(String namespace) {
        return namespace == null || NAMESPACE_PATTERN.matcher(namespace).matches();
    }

    public String getName() {
        return name;
    }

    public static boolean isValidName(String name) {
        return name == null ||  NAME_PATTERN.matcher(name).matches();
    }

    public SchemaVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {

        return Joiner.on(":").join(namespace, name, version);
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        SchemaId schemaId = (SchemaId) object;

        return name.equals(schemaId.name) && namespace.equals(schemaId.namespace) && version.equals(schemaId.version);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(SchemaId that) {

        if (getNamespace().compareTo(that.getNamespace()) != 0) {
            return getNamespace().compareTo(that.getNamespace());
        }

        if (getName().compareTo(that.getName()) != 0) {
            return getName().compareTo(that.getName());
        }

        return getVersion().compareTo(that.getVersion());
    }
}
