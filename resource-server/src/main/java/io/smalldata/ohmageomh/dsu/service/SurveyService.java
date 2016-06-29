package io.smalldata.ohmageomh.dsu.service;

import io.smalldata.ohmageomh.surveys.domain.survey.Survey;
import org.openmhealth.dsu.domain.EndUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by changun on 12/17/14.
 */

public interface SurveyService {
    /**
     * Find the surveys that should be visible to the given user.
     * @param user
     * @return A list of surveys should display to this user.
     * @throws SQLException
     */
    Iterable<Survey> findAllSurveysAvailableToUser(EndUser user) throws SQLException;
}
