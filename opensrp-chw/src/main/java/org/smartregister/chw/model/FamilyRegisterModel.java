package org.smartregister.chw.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class FamilyRegisterModel extends BaseFamilyRegisterModel {

    @Override
    public List<FamilyEventClient> processRegistration(String jsonString) {
        List<FamilyEventClient> familyEventClientList = new ArrayList();

        FamilyEventClient familyEventClient = JsonFormUtils.processFamilyUpdateForm(Utils.context().allSharedPreferences(), jsonString);
        if (familyEventClient == null) {
            return familyEventClientList;
        } else {
            FamilyEventClient headEventClient = JsonFormUtils.processFamilyHeadRegistrationForm(Utils.context().allSharedPreferences(), jsonString, familyEventClient.getClient().getBaseEntityId());
            if (headEventClient == null) {
                return familyEventClientList;
            } else {
                if (headEventClient.getClient() != null && familyEventClient.getClient() != null) {
                    String headUniqueId = headEventClient.getClient().getIdentifier(Utils.metadata().uniqueIdentifierKey);
                    if (StringUtils.isNotBlank(headUniqueId)) {
                        String familyUniqueId = headUniqueId + "_family";
                        familyEventClient.getClient().addIdentifier(Utils.metadata().uniqueIdentifierKey, familyUniqueId);
                    }
                }

                Client familyClient = familyEventClient.getClient();
                familyClient.addRelationship(Utils.metadata().familyRegister.familyHeadRelationKey, headEventClient.getClient().getBaseEntityId());
                familyClient.addRelationship(Utils.metadata().familyRegister.familyCareGiverRelationKey, headEventClient.getClient().getBaseEntityId());
                familyEventClientList.add(familyEventClient);
                familyEventClientList.add(headEventClient);
                return familyEventClientList;
            }
        }
    }
}
