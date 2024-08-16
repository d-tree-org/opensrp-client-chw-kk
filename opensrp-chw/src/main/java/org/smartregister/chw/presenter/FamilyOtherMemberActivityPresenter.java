package org.smartregister.chw.presenter;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.interactor.FamilyInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;

import timber.log.Timber;

public class FamilyOtherMemberActivityPresenter extends CoreFamilyOtherMemberActivityPresenter {

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected CoreFamilyProfileInteractor getFamilyProfileInteractor() {
        if (profileInteractor == null) {
            this.profileInteractor = new FamilyProfileInteractor();
        }
        return (CoreFamilyProfileInteractor) profileInteractor;
    }

    @Override
    protected FamilyProfileContract.Model getFamilyProfileModel(String familyName) {
        if (profileModel == null) {
            this.profileModel = new FamilyProfileModel(familyName);
        }
        return profileModel;
    }

    @Override
    protected void setProfileInteractor() {
        if (familyInteractor == null) {
            familyInteractor = new FamilyInteractor();
        }
    }

    @Override
    public void updateFamilyMember(Context context, String jsonString, boolean isIndependent) {

        if(StringUtils.isNotBlank(org.smartregister.util.JsonFormUtils.getFieldValue(jsonString, "unique_identifier_update"))) {
            try {
                jsonString = updateOpenSRPId(jsonString);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        super.updateFamilyMember(context, jsonString, isIndependent);
    }

    private String updateOpenSRPId(String jsonString) throws JSONException {
        JSONObject form = new JSONObject(jsonString);
        UniqueId uniqueId = FamilyLibrary.getInstance().getUniqueIdRepository().getNextUniqueId();
        String newID = (uniqueId != null) ? uniqueId.getOpenmrsId().replace("-", "") : "";
        form.put("current_opensrp_id", newID);
        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);

        if (fields != null) {
            JSONObject uniqueIdObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.UNIQUE_ID);
            if (uniqueIdObject != null) {
                uniqueIdObject.put(org.smartregister.chw.anc.util.JsonFormUtils.VALUE, newID);
                FamilyLibrary.getInstance().getUniqueIdRepository().close(newID);
            }
        }
        return form.toString();
    }
}
