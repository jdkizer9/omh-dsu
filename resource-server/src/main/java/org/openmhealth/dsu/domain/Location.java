package org.openmhealth.dsu.domain;

import org.openmhealth.dsu.domain.exception.InvalidArgumentException;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A description of a location with an accuracy and time-stamp.
 * </p>
 *
 * @author John Jenkins
 */
public class Location {
    /**
     * The JSON key for the latitude.
     */
    public static final String JSON_KEY_LATITUDE = "latitude";
    /**
     * The JSON key for the longitude.
     */
    public static final String JSON_KEY_LONGITUDE = "longitude";
    /**
     * The JSON key for the accuracy.
     */
    public static final String JSON_KEY_ACCURACY = "accuracy";
    /**
     * The JSON key for the time the location reading was made.
     */
    public static final String JSON_KEY_TIME = "time";

    /**
     * The latitude value.
     */
    @JsonProperty(JSON_KEY_LATITUDE)
    private final double latitude;
    /**
     * The longitude value.
     */
    @JsonProperty(JSON_KEY_LONGITUDE)
    private final double longitude;
    /**
     * The accuracy of the point in meters.
     */
    @JsonProperty(JSON_KEY_ACCURACY)
    private final double accuracy;
    /**
     * The number of milliseconds since the Unix epoch when this reading was
     * made.
     */
    @JsonProperty(JSON_KEY_TIME)
    private final long time;

    /**
     * Creates a new Location object.
     *
     * @param latitude
     *        The latitude value.
     *
     * @param longitude
     *        The longitude value.
     *
     * @param accuracy
     *        The accuracy of the point in meters.
     *
     * @param time
     *        The time the location reading was made.
     */
    public Location(
        @JsonProperty(JSON_KEY_LATITUDE) final double latitude,
        @JsonProperty(JSON_KEY_LONGITUDE) final double longitude,
        @JsonProperty(JSON_KEY_ACCURACY) final double accuracy,
        @JsonProperty(JSON_KEY_TIME) final long time)
        throws InvalidArgumentException {

        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
    }
}