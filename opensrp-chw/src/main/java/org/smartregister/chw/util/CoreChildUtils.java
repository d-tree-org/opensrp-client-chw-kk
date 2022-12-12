package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;

public abstract class CoreChildUtils {
    public static String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        return mainSelectRegisterWithoutGroupby(tableName, familyTableName, familyMemberTableName, tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + mainCondition + "'");
    }

    public static String mainSelectRegisterWithoutGroupby(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBuilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBuilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + tableName + ".base_entity_id COLLATE NOCASE ");

        return queryBuilder.mainCondition(mainCondition);
    }

    public static String[] mainColumns(String tableName, String familyTable, String familyMemberTable) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(tableName + "." + DBConstants.KEY.RELATIONAL_ID + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_FIRST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_LAST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + org.smartregister.chw.core.utils.ChildDBConstants.PHONE_NUMBER + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER);
        columnList.add(familyMemberTable + "." + org.smartregister.chw.core.utils.ChildDBConstants.OTHER_PHONE_NUMBER + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER_OTHER);
        columnList.add(familyTable + "." + DBConstants.KEY.VILLAGE_TOWN + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_HOME_ADDRESS);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(tableName + "." + DBConstants.KEY.GENDER);
        columnList.add(tableName + "." + DBConstants.KEY.DOB);
        columnList.add(tableName + "." + org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.LAST_HOME_VISIT);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.VISIT_NOT_DONE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.CHILD_BF_HR);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NUMBER);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DATE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DESCRIPTION);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.DATE_CREATED);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_ACTION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.VACCINE_CARD);
        columnList.add(tableName + "." + ChildDBConstants.KEY.MOTHER_ENTITY_ID);
        return columnList.toArray(new String[columnList.size()]);
    }
}
