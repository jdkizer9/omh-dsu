package org.openmhealth.dsu.domain.survey.condition.terminal;

import java.util.Map;

import org.openmhealth.dsu.domain.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.jackson.OhmageNumber;
import org.openmhealth.dsu.domain.survey.SurveyItem;
import org.openmhealth.dsu.domain.survey.condition.Fragment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A {@link Terminal} that represents some number.
 * </p>
 *
 * @author John
 */
public class Numeric extends Terminal {
    /**
     * <p>
     * A builder for {@link org.openmhealth.dsu.domain.survey.condition.terminal.Numeric} objects.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder implements Terminal.Builder<Numeric> {
        /**
         * The number value.
         */
        private final OhmageNumber value;

        /**
         * Creates a new builder with some number.
         *
         * @param value
         *        The number value.
         */
        public Builder(final Number value) {
            this.value = new OhmageNumber(value);
        }

        /*
         * (non-Javadoc)
         * @see org.openmhealth.dsu.domain.survey.condition.Fragment.Builder#merge(org.openmhealth.dsu.domain.survey.condition.Fragment.Builder)
         */
        @Override
        public Fragment.Builder<?> merge(final Fragment.Builder<?> other) {
            if(other instanceof Terminal.Builder<?>) {
                throw
                    new InvalidArgumentException(
                        "More than one terminals in a row are not " +
                            "allowed.");
            }

            return other.merge(this);
        }

        /*
         * (non-Javadoc)
         * @see org.openmhealth.dsu.domain.survey.condition.Fragment.Builder#build()
         */
        @Override
        public Numeric build() throws InvalidArgumentException {
            return new Numeric(value);
        }
    }

    /**
     * The JSON key for the value.
     */
    public static final String JSON_KEY_VALUE = "value";

    /**
     * The numeric value.
     */
    @JsonProperty(JSON_KEY_VALUE)
    private final OhmageNumber value;

    /**
     * Creates a new number node.
     *
     * @param value
     *        The value of the number node.
     *
     * @throws InvalidArgumentException
     *         The value was null or did not begin and end with a quotation
     *         mark.
     */
    @JsonCreator
    public Numeric(@JsonProperty(JSON_KEY_VALUE) final OhmageNumber value)
        throws InvalidArgumentException {

        // Be sure it is not null.
        if(value == null) {
            throw new IllegalStateException("The text is null.");
        }

        // Store the value.
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.condition.Terminal#getValue(java.util.Map)
     */
    @Override
    public OhmageNumber getValue(final Map<String, Object> responses) {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.condition.Fragment#validate(java.util.Map)
     */
    @Override
    public void validate(final Map<String, SurveyItem> surveyItems)
        throws InvalidArgumentException {

        // Do nothing.
    }

    /**
     * @return Returns false if the value of this Numeric represents zero.
     *         Otherwise, true is returned.
     */
    @Override
    public boolean evaluate(final Map<String, Object> responses) {
        return getValue(responses).compareTo(0) == 0;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.condition.terminal.Terminal#lessThanValue(java.util.Map, java.lang.Object)
     */
    @Override
    public boolean lessThanValue(
        final Map<String, Object> responses,
        final Object value) {

        if(value instanceof Number) {
            return this.value.compareTo((Number) value) < 0;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.condition.terminal.Terminal#equalsValue(java.util.Map, java.lang.Object)
     */
    @Override
    public boolean equalsValue(
        final Map<String, Object> responses,
        final Object value) {

        if(value instanceof Number) {
            return this.value.compareTo((Number) value) == 0;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.condition.terminal.Terminal#greaterThanValue(java.util.Map, java.lang.Object)
     */
    @Override
    public boolean greaterThanValue(
        final Map<String, Object> responses,
        final Object value) {

        if(value instanceof Number) {
            return this.value.compareTo((Number) value) > 0;
        }

        return false;
    }
}
