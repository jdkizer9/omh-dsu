package io.smalldata.ohmageomh.surveys.domain.survey.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import io.smalldata.ohmageomh.surveys.domain.ISOW3CDateTimeFormat;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.Media;
import io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition;

import java.util.Map;

/**
 * <p>
 * A prompt for the user to enter a date and, optionally, time.
 * </p>
 *
 * @see ISOW3CDateTimeFormat
 *
 * @author John Jenkins
 */
public class TimestampPrompt extends Prompt<String> {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "timestamp_prompt";

    /**
     * Creates a new timestamp prompt.
     *
     * @param surveyItemId
     *        The survey-unique identifier for this prompt.
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
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public TimestampPrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE) final String defaultResponse)
        throws InvalidArgumentException {

        super(
            surveyItemId,
            condition,
            displayType,
            text,
            displayLabel,
            skippable,
            defaultResponse);

        // Validate the display type.
        if(!
            (
                DisplayType.CALENDAR.equals(displayType) ||
                DisplayType.PICKER.equals(displayType))) {

            throw
                new InvalidArgumentException(
                    "The display type '" +
                        displayType.toString() +
                        "' is not valid for the prompt, which must be '" +
                        DisplayType.CALENDAR.toString() +
                        "' or '" +
                        DisplayType.PICKER.toString() +
                        "': " +
                        getSurveyItemId());
        }

        // Verify that it is a valid date/time.
        if(defaultResponse != null) {
            try {
                ISOW3CDateTimeFormat.any().parseDateTime(defaultResponse);
            }
            catch(IllegalArgumentException e) {
                throw
                    new InvalidArgumentException(
                        "The default date time '" +
                            defaultResponse +
                            "' is not a valid date-time: " +
                            getSurveyItemId());
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() {
        return
            new StringSchema(
                getText(),
                (skippable() || (getCondition() != null)),
                getSurveyItemId());
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.prompt.Prompt#validateResponse(java.lang.Object, java.util.Map)
     */
    @Override
    public String validateResponse(
        final String response,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        // Verify that it is a valid date/time.
        try {
            ISOW3CDateTimeFormat.any().parseDateTime(response);
        }
        catch(IllegalArgumentException e) {
            throw
                new InvalidArgumentException(
                    "The default date time '" +
                        response +
                        "' is not a valid date-time: " +
                        getSurveyItemId());
        }

        return response;
    }
}