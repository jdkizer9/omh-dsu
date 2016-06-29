package io.smalldata.ohmageomh.surveys.domain.survey.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.SurveyItem;

import java.util.Map;

/**
 * <p>
 * A {@link Fragment} that reverses the result of a sub-fragment.
 * </p>
 *
 * @author John Jenkins
 */
public class Not extends Fragment {
    /**
     * <p>
     * A builder for {@link io.smalldata.ohmageomh.surveys.domain.survey.condition.Not} objects.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder implements Fragment.Builder<Not> {
        /**
         * The sub-fragment, whose result will be inverted.
         */
        private Fragment.Builder<?> fragment = null;

        /*
         * (non-Javadoc)
         * @see io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition.Fragment.Builder#merge(io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition.Fragment.Builder)
         */
        @Override
        public Fragment.Builder<?> merge(final Fragment.Builder<?> other) {
            if(fragment == null) {
                fragment = other;
                return this;
            }
            else {
                return other.merge(this);
            }
        }

        /*
         * (non-Javadoc)
         * @see io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition.Fragment.Builder#build()
         */
        @Override
        public Not build() throws InvalidArgumentException {
            return new Not(fragment.build());
        }
    }

    /**
     * The string value of an {@link io.smalldata.ohmageomh.surveys.domain.survey.condition.Not} within a condition sentence.
     */
    public static final String VALUE = "!";

    /**
     * The JSON key for the fragment.
     */
    public static final String JSON_KEY_NOT = "not";

    /**
     * The fragment that this not inverts.
     */
    @JsonProperty(JSON_KEY_NOT)
    private final Fragment fragment;

    /**
     * Creates a new Not backed by some fragment.
     *
     * @param fragment
     *        The fragment that backs this not.
     *
     * @throws InvalidArgumentException
     *         The underlying fragment was null.
     */
    @JsonCreator
    public Not(
        @JsonProperty(JSON_KEY_NOT) final Fragment fragment)
        throws InvalidArgumentException {

        if(fragment == null) {
            throw
                new InvalidArgumentException(
                    "The fragment is null.");
        }

        this.fragment = fragment;
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.condition.Fragment#validate(java.util.Map)
     */
    @Override
    public void validate(final Map<String, SurveyItem> surveyItems)
        throws InvalidArgumentException {

        fragment.validate(surveyItems);
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.condition.Fragment#evaluate(java.util.Map)
     */
    @Override
    public boolean evaluate(final Map<String, Object> responses) {
        return ! fragment.evaluate(responses);
    }

    @Override
    public String toString() {
        return "NOT "+fragment.toString();
    }
}
