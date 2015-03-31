package org.openmhealth.dsu.domain.ohmage.survey.prompt;

import java.util.List;

import name.jenkins.paul.john.concordia.schema.NumberSchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import org.openmhealth.dsu.domain.ohmage.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.ohmage.survey.condition.Condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A prompt for the user to make one choice among a list of choices where the
 * response will always be a number.
 * </p>
 *
 * @author John Jenkins
 */
public class NumberSingleChoicePrompt extends SingleChoicePrompt<Number> {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE =
        "number_single_choice_prompt";

    /**
     * Creates a new single-choice prompt whose choices and response should be
     * a number.
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
     * @param choices
     *        The list of choices.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public NumberSingleChoicePrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE) final Number defaultResponse,
        @JsonProperty(JSON_KEY_CHOICES)
            final List<? extends Choice<? extends Number>> choiceList)
        throws InvalidArgumentException {

        super(
            surveyItemId,
            condition,
            displayType,
            text,
            displayLabel,
            skippable,
            defaultResponse,
                choiceList);

        if(!
            (
                DisplayType.LIST.equals(displayType) ||
                DisplayType.SLIDER.equals(displayType))) {

            throw
                new InvalidArgumentException(
                    "The display type '" +
                        displayType.toString() +
                        "' is not valid for the prompt, which must be '" +
                        DisplayType.LIST.toString() +
                        "' or '" +
                        DisplayType.SLIDER.toString() +
                        "': " +
                        getSurveyItemId());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.ohmage.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() {
        return
            new NumberSchema(
                getText(),
                (skippable() || (getCondition() != null)),
                getSurveyItemId());
    }
}