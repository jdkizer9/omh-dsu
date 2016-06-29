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

package org.openmhealth.dsu.domain;

import org.openmhealth.schema.domain.SchemaId;
import org.openmhealth.schema.domain.SchemaVersion;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.openmhealth.dsu.domain.DataPointModality.SELF_REPORTED;


/**
 * A builder that simplifies the construction of data points.
 *
 * @author Emerson Farrugia
 */
public class DataPointBuilder {

    private String id = UUID.randomUUID().toString();
    private String userId;
    private String schemaNamespace;
    private String schemaName;
    private Integer schemaVersionMajor;
    private Integer schemaVersionMinor;
    private String schemaVersionQualifier;
    private OffsetDateTime creationDateTime = OffsetDateTime.now();
    private Map<?, ?> body = new HashMap<>();

    public DataPointBuilder() {

    }

    public DataPointBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public DataPointBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public DataPointBuilder setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
        return this;
    }

    public DataPointBuilder setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public DataPointBuilder setSchemaVersionMajor(Integer schemaVersionMajor) {
        this.schemaVersionMajor = schemaVersionMajor;
        return this;
    }

    public DataPointBuilder setSchemaVersionMinor(Integer schemaVersionMinor) {
        this.schemaVersionMinor = schemaVersionMinor;
        return this;
    }

    public DataPointBuilder setSchemaVersionQualifier(String schemaVersionQualifier) {
        this.schemaVersionQualifier = schemaVersionQualifier;
        return this;
    }

    public DataPointBuilder setSchemaVersion(SchemaVersion schemaVersion) {

        if (schemaVersion == null) {
            setSchemaVersionMajor(null);
            setSchemaVersionMinor(null);
            setSchemaVersionQualifier(null);
        }
        else {
            setSchemaVersionMajor(schemaVersion.getMajor());
            setSchemaVersionMinor(schemaVersion.getMinor());
            setSchemaVersionQualifier(schemaVersion.getQualifier().orElse(null));
        }

        return this;
    }

    public DataPointBuilder setCreationDateTime(OffsetDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }

    public DataPointBuilder setBody(Map<?, ?> body) {
        this.body = body;
        return this;
    }

    public DataPoint build() {

        SchemaVersion schemaVersion = new SchemaVersion(schemaVersionMajor, schemaVersionMinor, schemaVersionQualifier);
        SchemaId schemaId = new SchemaId(schemaNamespace, schemaName, schemaVersion);

        DataPointAcquisitionProvenance acquisitionProvenance = new DataPointAcquisitionProvenance("source");
        acquisitionProvenance.setModality(SELF_REPORTED);
        acquisitionProvenance.setSourceCreationDateTime(creationDateTime.minusMinutes(1));

        DataPointHeader header = new DataPointHeader(id, schemaId, creationDateTime);
        header.setAcquisitionProvenance(acquisitionProvenance);

        DataPoint dataPoint = new DataPoint(header, body);
        dataPoint.setUserId(userId);

        return dataPoint;
    }
}
