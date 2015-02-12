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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import javax.validation.Valid;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A data point.
 *
 * @author Emerson Farrugia
 */
public class DataPoint {
    @Id
    private String id; // duplicated from header to make MongoDB happy
    private String userId;
    private DataPointHeader header;
    private Map<?, ?> body;


    /**
     * @param header the header of this data point
     * @param body the body of this data point
     */
    @JsonCreator
    public DataPoint(@JsonProperty("header") DataPointHeader header, @JsonProperty("body") Map<?, ?> body) {

        checkNotNull(header);
        checkNotNull(body);

        this.id = header.getId();
        this.header = header;
        this.body = body;
    }

    /**
     * @deprecated should only be used by frameworks for persistence or serialisation
     */
    @Deprecated
    DataPoint() {
    }

    /**
     * @return the identifier of the data point
     */
    @JsonIgnore
    public String getId() {
        return id;
    }

    // TODO since it's not clear if this needs to be serialised, it's suppressed for now
    @JsonIgnore
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Valid
    public DataPointHeader getHeader() {
        return header;
    }

    public Map<?, ?> getBody() {
        return body;
    }
}
