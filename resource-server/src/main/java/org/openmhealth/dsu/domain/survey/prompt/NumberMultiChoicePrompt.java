package org.openmhealth.dsu.domain.survey.prompt;

import java.util.List;
import java.util.Set;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.NumberSchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import org.openmhealth.dsu.domain.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.survey.condition.Condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A prompt for the user to make any number of choices from a list of choices
 * where the response will always be a number.
 * </p>
 *
 * @author John Jenkins
 */
public class NumberMultiChoicePrompt extends MultiChoicePrompt<Number> {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE =
        "number_multi_choice_prompt";

    /**
     * Creates a new multi-choice prompt whose choices and responses should be
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
     * @param minChoices
     *        The minimum number of choices the user must select.
     *
     * @param maxChoices
     *        The maximum number of choices the user may select.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public NumberMultiChoicePrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE)
            final Set<? extends Number> defaultResponse,
        @JsonProperty(JSON_KEY_CHOICES)
            final List<? extends Choice<? extends Number>> choiceList,
        @JsonProperty(JSON_KEY_MIN_CHOICES) final Integer minChoices,
        @JsonProperty(JSON_KEY_MAX_CHOICES) final Integer maxChoices)
        throws InvalidArgumentException {

        super(
            surveyItemId,
            condition,
            displayType,
            text,
            displayLabel,
            skippable,
            defaultResponse,
                choiceList,
            minChoices,
            maxChoices);
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() throws IllegalStateException {
        try {
            return
                new ArraySchema(
                    getText(),
                    (skippable() || (getCondition() != null)),
                    getSurveyItemId(),
                    new NumberSchema(null, false, null));
        }
        catch(ConcordiaException e) {
            throw
                new IllegalStateException(
                    "There was a problem building the number multi-choice " +
                        "prompt's schema.",
                    e);
        }
    }
}