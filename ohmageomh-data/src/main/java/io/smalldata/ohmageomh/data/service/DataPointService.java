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

package io.smalldata.ohmageomh.data.service;

import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.data.domain.DataPointSearchCriteria;
import io.smalldata.ohmageomh.data.domain.LastDataPointDate;
import org.openmhealth.schema.domain.omh.DataPointHeader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


/**
 * A service that manages data points.
 *
 * @author Emerson Farrugia
 * @author Jared Sieling
 */
public interface DataPointService {

    boolean exists(String id);

    Optional<DataPoint> findOne(String id);

    Iterable<DataPoint> findBySearchCriteria(DataPointSearchCriteria searchCriteria, @Nullable Integer offset,
                                             @Nullable Integer limit);

    DataPoint save(DataPoint dataPoint);

    Iterable<DataPoint> save(Iterable<DataPoint> dataPoints);

    void delete(String id);

    Long deleteByIdAndUserId(String id, String userId);

    void setUserId(DataPointHeader header, String endUserId);

    List<LastDataPointDate> findLastDataPointDate(List<String> userIds, DataPointSearchCriteria searchCriteria, String dateField);
}
