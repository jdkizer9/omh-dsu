package io.smalldata.ohmageomh.dsu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.dsu.domain.Study;
import org.openmhealth.dsu.domain.EndUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jared Sieling.
 */
@Service
public class StudyServiceImpl implements StudyService {

    private static final Logger log = LoggerFactory.getLogger(StudyServiceImpl.class);

    @Resource(name="admindashboardDataSource")
    DataSource dataSource;
    @Autowired
    ObjectMapper objectMapper;

    final static String queryUserIdByUsername =
            "SELECT id FROM users WHERE username = ? LIMIT 1;";
    final static String queryStudyByName =
            "SELECT id, name from studies WHERE name = ? LIMIT 1";
    final static String queryStudiesByUsername =
            "SELECT studies.id AS id, studies.name AS name " +
                    "FROM studies " +
                    "   JOIN study_participants ON study_participants.study_id = studies.id " +
                    "   JOIN users ON users.id = study_participants.user_id " +
                    "WHERE users.username = ?; ";
    final static String queryParticipantMembershipByUsernameAndStudyId =
            "SELECT COUNT(*) " +
                    "FROM   users " +
                    "       INNER JOIN study_participants " +
                    "               ON users.id = study_participants.user_id " +
                    "WHERE  users.username = ? " +
                    "       AND study_id = ?; ";
    final static String insertNewStudyParticipant =
            "INSERT INTO study_participants (study_id, user_id, created_at, updated_at) VALUES (?, ?, ?, ?);";
    final static String insertNewUser =
            "INSERT INTO users (username, gmail, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?);";


    @Override
    public Optional<Study> getStudyByName(String studyName) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String[] args = {studyName};
        try{
            return Optional.of(select.queryForObject(queryStudyByName, args, new RowToStudy()));
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Study> getStudiesByUsername(String username) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String[] args = {username};
        try {
            return select.query(queryStudiesByUsername, new RowToStudy(), args);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Study>();
        }
    }

    @Override
    public boolean isUserEnrolled(EndUser user, Study study) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        Object[] args = {user.getUsername(), study.getId()};
        return select.queryForObject(queryParticipantMembershipByUsernameAndStudyId, args, Integer.class) > 0;

    }

    @Override
    public Optional<String> getParticipantId(EndUser user, Study study) {
        if(isUserEnrolled(user, study)){
            try {
                return Optional.of(String.format("%04d", getUserId(user)));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public void enrollUser(EndUser user, Study study) {
        log.info("User:"+user + " Study:"+study);
        // only attempt to create a new "Study-Participant" entry if it does not exist.
        if(!isUserEnrolled(user, study)) {
            log.info("User:"+user + "does not in study" + study);
            JdbcTemplate select = new JdbcTemplate(dataSource);
            Long userId;
            // check if the user exists in the dashboard
            try {
                userId = getUserId(user);
            }catch (Exception e) {
                userId = createNewUser(user);

            }
            // insert a new "Study-Participant" entry.
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Object[] args = new Object[]{study.getId(), userId, timestamp, timestamp};
            log.info("Create a new study-participant entry for " + user + " " + study);
            int numInsert = select.update(insertNewStudyParticipant, args);
            if(numInsert != 1){
                throw new RuntimeException("Failed to create a new study-participant entry");
            }
        }
    }

    private class RowToStudy implements RowMapper<Study> {
        @Override
        public Study mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Study(rs.getLong("id"), rs.getString("name"));        }
    }

    private Long getUserId(EndUser user) throws Exception {
        Object[] args = {user.getUsername()};
        JdbcTemplate select = new JdbcTemplate(dataSource);
        try{
            return select.queryForObject(queryUserIdByUsername, args, Long.class);
        }catch (EmptyResultDataAccessException e) {
            throw new Exception();
        }
    }

    private Long createNewUser(EndUser user){
        log.info("User:"+user + "does not exist. Create one.");
        // create the user if it does not exist
        Timestamp timestamp = new Timestamp(new Date().getTime());
        JdbcTemplate select = new JdbcTemplate(dataSource);
        select.update(insertNewUser,
                user.getUsername(),
                user.getEmailAddress().isPresent() ?
                        user.getEmailAddress().get().getAddress() : null,
                timestamp,
                timestamp);
        // query the user id again
        try {
            return getUserId(user);
        }catch (Exception e) {
            String msg = "Can't not create admin dashboard user:" + user;
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }
}
