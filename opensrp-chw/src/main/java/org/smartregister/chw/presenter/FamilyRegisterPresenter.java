package org.smartregister.chw.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.interactor.FamilyRegisterInteractor;
import org.smartregister.domain.Client;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.contract.FamilyRegisterContract.InteractorCallBack;
import org.smartregister.family.contract.FamilyRegisterContract.Model;
import org.smartregister.family.contract.FamilyRegisterContract.View;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class FamilyRegisterPresenter extends BaseFamilyRegisterPresenter implements BaseAncRegisterContract.InteractorCallBack {

    public FamilyRegisterPresenter(View view, Model model) {
        super(view, model);
        this.interactor = new FamilyRegisterInteractor();
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {
        try {
            if (getView() != null) {
                getView().showProgressDialog(R.string.saving_dialog_title);
            }

            List<FamilyEventClient> familyEventClientList = model.processRegistration(jsonString);
            if (familyEventClientList == null || familyEventClientList.isEmpty()) {
                return;
            }

            interactor.saveRegistration(familyEventClientList, jsonString, isEditMode, new InteractorCallBack() {
                @Override
                public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {
                    FamilyRegisterPresenter.super.onUniqueIdFetched(triple, s);
                }

                @Override
                public void onNoUniqueId() {
                    FamilyRegisterPresenter.super.onNoUniqueId();
                }

                @Override
                public void onRegistrationSaved(boolean isEditMode, boolean isSaved, List<FamilyEventClient> familyEventClientList) {
                    try {

                        assert isSaved;

                        JSONObject form = new JSONObject(jsonString);
                        String encounterType = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                        String clientBaseEntityId = "";
                        String relationalId = "";
                        for (FamilyEventClient ec : familyEventClientList) {
                            if (ec.getClient().getClientType() != null && ec.getClient().getClientType().equals("Family"))
                                relationalId = ec.getClient().getBaseEntityId();
                            clientBaseEntityId = ec.getClient().getBaseEntityId();
                        }

                        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                        JSONObject relational = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
                        assert relational != null;
                        relational.put("value", relationalId);
                        form.put(DBConstants.KEY.RELATIONAL_ID, relational);
                        form.put(Constants.JSON_FORM_EXTRA.ENTITY_TYPE, clientBaseEntityId);
                        form.put(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE, Constants.EVENT_TYPE.ANC_REGISTRATION);
                        form.put("relational_id", relationalId);
                        new BaseAncRegisterInteractor().saveRegistration(form.toString(), isEditMode, FamilyRegisterPresenter.this, null);

                        addInterventionId(clientBaseEntityId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private View getView() {
        return viewReference != null ? viewReference.get() : null;
    }

    @Override
    public void onRegistrationSaved(String encounterType, boolean isEdit, boolean hasChildren) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    private void addInterventionId(String clientBaseEntityId) {
        Client client = CoreLibrary.getInstance().context().getEventClientRepository().fetchClientByBaseEntityId(clientBaseEntityId);
        client.addIdentifier("intervention_id", UUID.randomUUID().toString());
        JSONObject object = CoreLibrary.getInstance().context().getEventClientRepository().convertToJson(client);
        CoreLibrary.getInstance().context().getEventClientRepository().addorUpdateClient(client.getBaseEntityId(), object);
    }
}