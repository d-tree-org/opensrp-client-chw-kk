package org.smartregister.chw.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.R;
import org.smartregister.chw.interactor.FamilyRegisterInteractor;
import org.smartregister.chw.model.FamilyRegisterModel;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.contract.FamilyRegisterContract.Interactor;
import org.smartregister.family.contract.FamilyRegisterContract.InteractorCallBack;
import org.smartregister.family.contract.FamilyRegisterContract.Model;
import org.smartregister.family.contract.FamilyRegisterContract.Presenter;
import org.smartregister.family.contract.FamilyRegisterContract.View;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class FamilyRegisterPresenter extends BaseFamilyRegisterPresenter {

    public FamilyRegisterPresenter(View view, Model model){
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

            interactor.saveRegistration(familyEventClientList, jsonString, isEditMode, this);
        } catch (Exception var4) {
            Timber.e(var4);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode, boolean isSaved, List<FamilyEventClient> familyEventClientList) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    private View getView(){
        return viewReference != null ? (View) viewReference.get() : null;
    }
}