package org.smartregister.chw.dao;

import org.smartregister.chw.util.KkConstants;
import org.smartregister.dao.AbstractDao;

import java.util.List;

/**
 * Author issyzac on 05/04/2023
 */
public class ImmunizationDao extends AbstractDao {

    public static String receivedBCG(String baseEntityId) {
        String sql = "SELECT received_bcg FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_bcg");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedOPV0(String baseEntityId) {
        String sql = "SELECT received_bopv0 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_bopv0");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedOPV1(String baseEntityId) {
        String sql = "SELECT received_bopv1 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_bopv1");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedHepbHib1(String baseEntityId) {
        String sql = "SELECT received_dtp_hepb_hib1 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_dtp_hepb_hib1");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedPcvi1(String baseEntityId) {
        String sql = "SELECT received_pcvi1 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_pcvi1");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedRota1(String baseEntityId) {
        String sql = "SELECT received_rota1 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_rota1");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedBopv2(String baseEntityId) {
        String sql = "SELECT received_bopv2 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_bopv2");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedHepbHib2(String baseEntityId) {
        String sql = "SELECT received_dtp_hepb_hib2 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_dtp_hepb_hib2");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedPcvi2(String baseEntityId) {
        String sql = "SELECT received_pcvi2 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_pcvi2");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedRota2(String baseEntityId) {
        String sql = "SELECT received_rota2 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_rota2");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedRota3(String baseEntityId) {
        String sql = "SELECT received_rota3 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_rota3");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedBopv3(String baseEntityId) {
        String sql = "SELECT received_bopv3 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_bopv3");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedHepbHib3(String baseEntityId) {
        String sql = "SELECT received_dtp_hepb_hib3 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_dtp_hepb_hib3");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedPcv3(String baseEntityId) {
        String sql = "SELECT received_pcv3 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_pcv3");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedSuruaRubella1(String baseEntityId) {
        String sql = "SELECT received_surua_rubella1 FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_surua_rubella1");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

    public static String receivedIpv(String baseEntityId) {
        String sql = "SELECT received_ipv FROM " + KkConstants.TABLES.CHILD_IMMUNIZATIONS + " WHERE base_entity_id = '" + baseEntityId + "'";
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "received_ipv");
        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);
        return null;
    }

}
