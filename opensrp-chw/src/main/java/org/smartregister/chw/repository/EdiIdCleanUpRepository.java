package org.smartregister.chw.repository;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

import org.smartregister.repository.BaseRepository;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2023-09-27
 */
public class EdiIdCleanUpRepository extends BaseRepository {

    private static final String BASE_ENTITY_ID = "baseEntityId";

    public static final String CLIENTS_WITH_EDI_IDS_SQL = "WITH clients AS (" +
            "SELECT " +
            "baseEntityId, " +
            "COALESCE(json_extract(json, '$.identifiers.edi_id'), json_extract(json, '$.identifiers.mother_edi_id')) as edi_id " +
            "FROM client " +
            ")" +
            "select * from clients where edi_id is not null";

    public Map<String, String> clientsWithEdiIds() {

        Map<String, String> ediIds = new HashMap<>();

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(CLIENTS_WITH_EDI_IDS_SQL, new String[]{});

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String baseEntityId = cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID));
                @SuppressLint("Range") String ediId = cursor.getString(cursor.getColumnIndex("edi_id"));

                ediIds.put(baseEntityId, ediId);
            }

        } catch (Exception e) {
            Log.e("ERRRRORRRRR", e.getMessage());
            Timber.e(e);
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return ediIds;
    }
}