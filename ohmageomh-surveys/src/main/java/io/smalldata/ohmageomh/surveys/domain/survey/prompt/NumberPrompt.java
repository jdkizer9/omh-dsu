package io.smalldata.ohmageomh.surveys.domain.survey.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import name.jenkins.paul.john.concordia.schema.NumberSchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.Media;
import io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition;

import java.util.Map;

/**
 * <p>
 * A prompt for the user to enter a numeric value.
 * </p>
 *
 * @author John Jenkins
 */
public class NumberPrompt extends Prompt<Number> {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "number_prompt";

    /**
     * The JSON key for the minimum value.
     */
    public static final String JSON_KEY_MIN = "min";
    /**
     * The JSON key for the maximum value.
     */
    public static final String JSON_KEY_MAX = "max";
    /**
     * The JSON key for whether or not the response must be a whole number.
     */
    public static final String JSON_KEY_WHOLE_NUMBERS_ONLY =
        "whole_numbers_only";

    /**
     * The minimum allowed value for a response.
     */
    @JsonProperty(JSON_KEY_MIN)
    private final Number min;
    /**
     * The maximum allowed value for a response.
     */
    @JsonProperty(JSON_KEY_MAX)
    private final Number max;
    /**
     * Whether or not only whole numbers are allowed.
     */
    @JsonProperty(JSON_KEY_WHOLE_NUMBERS_ONLY)
    private final boolean wholeNumbersOnly;

    /**
     * Creates a new number prompt.
     *
     * @param surveyItemId
     *        The condition on whether or not to show this prompt.
     *
     * @param condition
     *        The condition on whether or not to show this prompt.
     *
     * @param displayType
     *        The display type to use to visualize the prompt.
     *
     * @param text
     *        The text to display to the user.
     *
     * @param displayLabel
     *        The text to use as a short name in visualizations.
     *
     * @param skippable
     *        Whether or not this prompt may be skipped.
     *
     * @param defaultResponse
     *        The default response for this prompt or null if a default is not
     *        allowed.
     *
     * @param min
     *        The minimum allowed value for a response or null if there is no
     *        minimum value.
     *
     * @param max
     *        The maximum allowed value for a response or null if there is no
     *        maximum value.
     *
     * @param wholeNumbersOnly
     *        Whether or not the response must be whole number as opposed to a
     *        decimal. If null, the default is false.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public NumberPrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE) final Number defaultResponse,
        @JsonProperty(JSON_KEY_MIN) final Number min,
        @JsonProperty(JSON_KEY_MAX) final Number max,
        @JsonProperty(JSON_KEY_WHOLE_NUMBERS_ONLY)
            final boolean wholeNumbersOnly)
        throws InvalidArgumentException {

        super(
            surveyItemId,
            condition,
            displayType,
            text,
            displayLabel,
            skippable,
            defaultResponse);

        if(!
            (
                DisplayType.LIST.equals(displayType) ||
                DisplayType.PICKER.equals(displayType) ||
                DisplayType.SLIDER.equals(displayType) ||
                DisplayType.TEXTBOX.equals(displayType))) {

            throw
                new InvalidArgumentException(
                    "The display type '" +
                        displayType.toString() +
                        "' is not valid for the prompt, which must be '" +
                        DisplayType.LIST.toString() +
                        "', '" +
                        DisplayType.PICKER.toString() +
                        "', '" +
                        DisplayType.SLIDER.toString() +
                        "' or '" +
                        DisplayType.TEXTBOX.toString() +
                        "': " +
                        getSurveyItemId());
        }

        this.min = min;
        this.max = max;
        this.wholeNumbersOnly = wholeNumbersOnly;
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() {
        return
            new NumberSchema(
                getText(),
                (skippable() || (getCondition() != null)),
                getSurveyItemId());
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.prompt.Prompt#validateResponse(java.lang.Object, java.util.Map)
     */
    @Override
    public Number validateResponse(
        final Number response,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        // If a 'min' exists, check that the response conforms.
        if((min != null) && (response.doubleValue() < min.doubleValue())) {
            throw
                new InvalidArgumentException(
                    "The response was less than the allowed minimum: " +
                        getSurveyItemId());
        }

        // If a 'max' exists, check that the response conforms.
        if((max != null) && (response.doubleValue() > max.doubleValue())) {
            throw
                new InvalidArgumentException(
                    "The response was greater than the allowed maximum: " +
                        getSurveyItemId());
        }

        // If decimals are not allowed, check that the response conforms.
        if(wholeNumbersOnly && (! isWholeNumber(response))) {
            throw
                new InvalidArgumentException(
                    "The response must be a whole number: " +
                        getSurveyItemId());
        }

        return response;
    }

    /**
     * Returns whether or not a given value is a whole number.
     *
     * @param value
     *        The value to check.
     *
     * @return True if the value is a whole number; false, otherwise.
     */
    protected static boolean isWholeNumber(final Number value) {
        return value.longValue() == value.doubleValue();
    }
}