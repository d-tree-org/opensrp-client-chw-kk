package org.smartregister.chw.presenter;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.toList;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.core.presenter.CoreFamilyProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.interactor.FamilyChangeContractInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import timber.log.Timber;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter implements BaseAncRegisterContract.InteractorCallBack {

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View view, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(view, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        interactor = new FamilyProfileInteractor();
        verifyHasPhone();
    }

    @Override
    protected CoreChildRegisterModel getChildRegisterModel() {
        return new ChildRegisterModel();
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            this.interactor.getNextUniqueId(triple, this);
        } else {
            JSONObject form = this.model.getFormAsJson(formName, entityId, currentLocationId);
            this.getView().startFormActivity(form);
        }
    }

    @Override
    public void saveAncMember(String jsonString, boolean isEditMode) {
        try{
            getView().showProgressDialog(R.string.saving_dialog_title);

            JSONObject form = new JSONObject(jsonString);
            String encounterType = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

            FamilyEventClient familyEventClient = model.processMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null){
                return;
            }
            interactor.saveRegistration(familyEventClient, jsonString, false, new FamilyProfileContract.InteractorCallBack() {
                @Override
                public void startFormForEdit(CommonPersonObjectClient commonPersonObjectClient) {
                    FamilyProfilePresenter.super.startFormForEdit(commonPersonObjectClient);
                }

                @Override
                public void refreshProfileTopSection(CommonPersonObjectClient commonPersonObjectClient) {
                    FamilyProfilePresenter.super.refreshProfileTopSection(commonPersonObjectClient);
                }

                @Override
                public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {
                    FamilyProfilePresenter.super.onUniqueIdFetched(triple, s);
                }

                @Override
                public void onNoUniqueId() {
                    FamilyProfilePresenter.super.onNoUniqueId();
                }

                @Override
                public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {
                    //Registration has been saved, start ANC Registration here
                    try{
                        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                        JSONObject relational = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
                        assert relational != null;
                        relational.put("value", familyBaseEntityId);
                        form.put(DBConstants.KEY.RELATIONAL_ID, relational);
                        form.put(Constants.JSON_FORM_EXTRA.ENTITY_TYPE, familyEventClient.getClient().getBaseEntityId());
                        form.put("relational_id", familyBaseEntityId);
                        new BaseAncRegisterInteractor().saveRegistration(form.toString(), false, FamilyProfilePresenter.this, null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID) {

        boolean res = false;
        try {
            FamilyMember member = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, entityID);
            if (member != null && member.getPrimaryCareGiver()) {
                LocationPickerView lpv = new LocationPickerView(context);
                lpv.init();
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                new FamilyChangeContractInteractor().updateFamilyRelations(context, member, lastLocationId);
                res = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }

    @Override
    public String saveChwFamilyMember(Context context, String jsonString) {
        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = model.processMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return null;
            }
            FamilyMember familyMember = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, familyBaseEntityId);
            Event eventMember = familyEventClient.getEvent();
            eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.EverSchool.CODE, "",
                    toList(CoreJsonFormUtils.getEverSchoolOptions(context).get(familyMember.getEverSchool())), toList(familyMember.getEverSchool()), null, CoreConstants.JsonAssets.FAMILY_MEMBER.EVER_SCHOOL));

            eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.SchoolLevel.CODE, "",
                    toList(CoreJsonFormUtils.getSchoolLevels(context).get(familyMember.getSchoolLevel())), toList(familyMember.getSchoolLevel()), null, CoreConstants.JsonAssets.FAMILY_MEMBER.SCHOOL_LEVEL));


            interactor.saveRegistration(familyEventClient, jsonString, false, this);
            return familyEventClient.getClient().getBaseEntityId();
        } catch (Exception e) {
            getView().hideProgressDialog();
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onRegistrationSaved(String encounterType, boolean isEdit, boolean hasChildren) {
        getView().refreshMemberList(FetchStatus.fetched);
        getView().hideProgressDialog();
    }
}
