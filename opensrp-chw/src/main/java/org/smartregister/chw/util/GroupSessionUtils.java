package org.smartregister.chw.util;

import static org.smartregister.chw.core.utils.CoreReferralUtils.setEntityId;

import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.domain.GroupEventClient;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Author issyzac on 12/06/2023
 */
public class GroupSessionUtils extends CoreJsonFormUtils {

    public static void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String table, String sessionEntityId) throws Exception {
        final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, setEntityId(jsonString, sessionEntityId), table);
        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
    }

    public static GroupEventClient processGroupSessionEvent(AllSharedPreferences allSharedPreferences, String jsonString, String table, String sessionEntityId){

        try{
            GroupEventClient eventClient = null;

            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), sessionEntityId, getString(jsonForm, ENCOUNTER_TYPE), table);
            tagSyncMetadata(allSharedPreferences, baseEvent);

            Client groupClient = createGroupClient(fields, formTag(allSharedPreferences), sessionEntityId);

            eventClient = new GroupEventClient(groupClient, baseEvent);

            return eventClient;

        }catch (Exception e){
            Timber.e(e);
            return null;
        }

    }

    public static Client createGroupClient(JSONArray fields, FormTag formTag, String entityId) {

        String firstName = "";
        String middleName = "";
        String lastName = "Group Session";

        String bd = getFieldValue(fields, FormEntityConstants.Person.birthdate);
        Date birthdate = new Date();

        String gender = "";

        List<Address> addresses = new ArrayList<>(extractAddresses(fields).values());

        Client client = (Client) new Client(entityId)
                .withFirstName(firstName).withMiddleName(middleName).withLastName(lastName)
                .withBirthdate(birthdate, false)
                .withGender(gender)
                .withDateCreated(new Date());

        client.setClientApplicationVersion(formTag.appVersion);
        client.setClientApplicationVersionName(formTag.appVersionName);
        client.setClientDatabaseVersion(formTag.databaseVersion);

        client.withRelationships(new HashMap<String, List<String>>()).withAddresses(addresses)
                .withAttributes(extractAttributes(fields)).withIdentifiers(extractIdentifiers(fields));

        return client;

    }

}
