package org.smartregister.chw.dao;

import android.database.Cursor;

import org.smartregister.dao.AbstractDao;

import java.util.List;

/**
 * Author issyzac on 11/06/2023
 */
public class GroupCurriculumDao extends AbstractDao {

    public static Integer getNumberGroupCurriculumSessions() {
        String sql = "select count(*) from ec_group_session where strftime('%Y-%m', session_date) = strftime('%Y-%m', 'now')";
        DataMap<Integer> dataMap = c -> c.getType(0) == Cursor.FIELD_TYPE_NULL ? null : c.getInt(0);
        List<Integer> values = readData(sql, dataMap);
        if (values == null || values.size() == 0 || values.get(0) == null)
            return 0;
        return values.get(0);
    }

}
