package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;
import static org.smartregister.chw.malaria.util.DBConstants.KEY.GEST_AGE;
import static org.smartregister.chw.util.NotificationsUtil.handleNotificationRowClick;
import static org.smartregister.chw.util.NotificationsUtil.handleReceivedNotifications;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.AncMemberProfileContract;
import org.smartregister.chw.core.activity.CoreAncMemberProfileActivity;
import org.smartregister.chw.core.adapter.NotificationListAdapter;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.ChwNotificationUtil;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.custom_view.AncFloatingMenu;
import org.smartregister.chw.dao.ScheduleDao;
import org.smartregister.chw.dataloader.AncMemberDataLoader;
import org.smartregister.chw.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.interactor.AncMemberProfileInteractor;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.AncMemberProfilePresenter;
import org.smartregister.chw.presenter.FamilyRegisterPresenter;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.chw.util.KKCoreConstants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Client;
import org.smartregister.domain.Task;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AncMemberProfileActivity extends CoreAncMemberProfileActivity implements AncMemberProfileContract.View {

    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
    private NotificationListAdapter notificationListAdapter = new NotificationListAdapter();

    private TextView registrationStatus;

    private AlertStatus alertStatus;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, AncMemberProfileActivity.class);
        passToolbarTitle(activity, intent);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    private void checkPhoneNumberProvided() {
        ((AncFloatingMenu) baseAncFloatingMenu).redraw(!StringUtils.isBlank(memberObject.getPhoneNumber())
                || !StringUtils.isBlank(getFamilyHeadPhoneNumber()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationAndReferralRecyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationListAdapter.canOpen = true;
        ChwNotificationUtil.retrieveNotifications(ChwApplication.getApplicationFlavor().hasReferrals(),
                memberObject.getBaseEntityId(), this);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        if (((ChwApplication) ChwApplication.getInstance()).hasReferrals()) {
            addAncReferralTypes();
        }
    }

    @Override
    public void initializeFloatingMenu() {
        baseAncFloatingMenu = new AncFloatingMenu(this, getAncWomanName(),
                memberObject.getPhoneNumber(), getFamilyHeadName(), getFamilyHeadPhoneNumber(), getProfileType());

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.anc_fab:
                    checkPhoneNumberProvided();
                    ((AncFloatingMenu) baseAncFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((AncFloatingMenu) baseAncFloatingMenu).launchCallWidget();
                    ((AncFloatingMenu) baseAncFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    ((AncMemberProfilePresenter) ancMemberProfilePresenter()).referToFacility();
                    ((AncFloatingMenu) baseAncFloatingMenu).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((AncFloatingMenu) baseAncFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
        baseAncFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(baseAncFloatingMenu, linearLayoutParams);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        ancMemberProfilePresenter().fetchTasks();
    }

    private void addAncReferralTypes() {
        referralTypeModels.add(new ReferralTypeModel(getString(R.string.anc_danger_signs),
                BuildConfig.USE_UNIFIED_REFERRAL_APPROACH ? org.smartregister.chw.util.Constants.JSON_FORM.getAncUnifiedReferralForm() : org.smartregister.chw.util.Constants.JSON_FORM.getAncReferralForm(), CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS));

        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.hiv_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getHivReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_HIV));

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.tb_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getTbReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_TB));

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.gbv_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
        }

        if (MalariaDao.isRegisteredForMalaria(baseEntityID)) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.client_malaria_follow_up), null, null));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_remove_member) {
            CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

            final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(memberObject.getBaseEntityId());
            final CommonPersonObjectClient client =
                    new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
            client.setColumnmaps(commonPersonObject.getColumnmaps());

            IndividualProfileRemoveActivity.startIndividualProfileActivity(AncMemberProfileActivity.this, client, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyHead(), memberObject.getPrimaryCareGiver(), AncRegisterActivity.class.getCanonicalName());
            return true;
        } else if (itemId == R.id.action_pregnancy_out_come) {
            CoreConstants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
            PncRegisterActivity.startPncRegistrationActivity(AncMemberProfileActivity.this, memberObject.getBaseEntityId(), null, KKCoreConstants.ANC_PREGNANCY_OUTCOME.getPregnancyOutcome(), AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId(), memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName(), memberObject.getLastMenstrualPeriod());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.anc_danger_signs_outcome).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }

    @Override // to chw
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                    FamilyEventClient familyEventClient =
                            new FamilyProfileModel(memberObject.getFamilyName()).processUpdateMemberRegistration(jsonString, memberObject.getBaseEntityId());
                    new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, ancMemberProfilePresenter());
                } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.UPDATE_ANC_REGISTRATION)) {
                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    updateLmpdFromEdd(fields);
                    String gestAge = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, GEST_AGE).getString(CoreJsonFormUtils.VALUE);
                    this.setMemberGA(gestAge);
                    new BaseAncRegisterInteractor().saveRegistration(form.toString(), false, null, null);
                } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.ANC_REFERRAL)) {
                    ancMemberProfilePresenter().createReferralEvent(Utils.getAllSharedPreferences(), jsonString);
                    showToast(this.getString(R.string.referral_submitted));
                }

            } catch (Exception e) {
                Timber.e(e, "AncMemberProfileActivity -- > onActivityResult");
            }
        } else if (requestCode == Constants.REQUEST_CODE_HOME_VISIT) {
            refreshViewOnHomeVisitResult();
        } else if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED) {
            ChwScheduleTaskExecutor.getInstance().execute(memberObject.getBaseEntityId(), CoreConstants.EventType.ANC_HOME_VISIT, new Date());
            finish();
        }
    }

    private void updateLmpdFromEdd(JSONArray fields) throws JSONException {
        JSONObject lmp = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.LAST_MENSTRUAL_PERIOD);
        boolean hasLmp = StringUtils.isNotBlank(lmp.optString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));

        if (!hasLmp) {
            JSONObject eddJson = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.EDD);
            DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd-MM-yyyy");

            LocalDate lmpDate = dateTimeFormat.parseLocalDate(eddJson.optString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE)).plusDays(-280);
            lmp.put(org.smartregister.chw.anc.util.JsonFormUtils.VALUE, dateTimeFormat.print(lmpDate));
        }
    }

    @Override
    public void startFormForEdit(Integer title_resource, String formName) {
        try {
            JSONObject form = null;
            boolean isPrimaryCareGiver = memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId());
            String titleString = title_resource != null ? getResources().getString(title_resource) : null;

            if (formName.equals(CoreConstants.JSON_FORM.getAncRegistration())) {

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new AncMemberDataLoader(titleString));
                form = binder.getPrePopulatedForm(formName);
                if (form != null) {
                    form.put(JsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.UPDATE_ANC_REGISTRATION);
                    updateWeeksValue(form);
                }


            } else if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {

                String eventName = org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName, memberObject.getChwMemberId()));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());
            }

            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void updateWeeksValue(JSONObject form) throws JSONException {
        JSONArray fieldsJson = org.smartregister.util.JsonFormUtils.fields(form);
        for (int i = 0; i < fieldsJson.length(); i++) {
            if (fieldsJson.getJSONObject(i).getString("key").matches("weeks")) {
                fieldsJson.getJSONObject(i).put("value", getResources().getString(R.string.gest_age_weeks));
                break;
            }
        }
    }

    @Override
    protected void registerPresenter() {
        presenter = new AncMemberProfilePresenter(this, new AncMemberProfileInteractor(this), memberObject);
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return ChwApplication.getApplicationFlavor().usesPregnancyRiskProfileLayout();
    }

    public void openMedicalHistory() {
        AncMedicalHistoryActivity.startMe(this, memberObject);
    }

    @Override
    public void openUpcomingService() {
        AncUpcomingServicesActivity.startMe(this, memberObject);
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, memberObject.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, memberObject.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, memberObject.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, memberObject.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, hasDueServices);
        startActivity(intent);
    }

    @Override
    public void setVisitNotDoneThisMonth() {
        super.setVisitNotDoneThisMonth();
        setFamilyStatus(alertStatus);
    }

    @Override
    public void updateVisitNotDone(long value) {
        super.updateVisitNotDone(value);
        setFamilyStatus(alertStatus);
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        super.setFamilyStatus(status);
        alertStatus=status;
        TextView tvFamilyStatus=findViewById(R.id.textview_family_has);
        Integer dueServiceCount = ScheduleDao.getDueServicesCount(memberObject.getFamilyBaseEntityId());
        if(dueServiceCount != null){
            if (dueServiceCount == 0) {
                tvFamilyStatus.setText(NCUtils.fromHtml(getString(R.string.family_has_nothing_due)));
            }
        }
    }

    @Override
    public void setFamilyLocation() {
        if (ChwApplication.getApplicationFlavor().flvSetFamilyLocation()) {
            view_family_location_row.setVisibility(View.VISIBLE);
            rlFamilyLocation.setVisibility(View.VISIBLE);
        }
    }

    protected String getMemberGPS() {
        return memberObject.getGps();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.textview_record_visit || id == R.id.textview_record_reccuring_visit) {
            AncHomeVisitActivity.startMe(this, memberObject.getBaseEntityId(), false);
        } else if (id == R.id.textview_edit) {
            AncHomeVisitActivity.startMe(this, memberObject.getBaseEntityId(), true);
        } else if (id == R.id.referral_row) {
            Task task = (Task) view.getTag();
            ReferralFollowupActivity.startReferralFollowupActivity(this, task.getIdentifier(), memberObject.getBaseEntityId());
        }

        handleNotificationRowClick(this, view, notificationListAdapter, memberObject.getBaseEntityId());
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        boolean isReferralFollowDue = false;

        for (Task task : taskList) {
            int days = Math.abs(Days.daysBetween(task.getAuthoredOn(), DateTime.now()).getDays());
            if ((days >= 1 && task.getPriority() == Task.TaskPriority.ROUTINE) || days >= 3) {
                isReferralFollowDue = true;
                break;
            }
        }

        RelativeLayout layoutReferralRow = findViewById(R.id.referral_row);
        if (isReferralFollowDue) {
            layoutReferralRow.setOnClickListener(this);
            layoutReferralRow.setVisibility(View.VISIBLE);
            findViewById(R.id.view_referral_row).setVisibility(View.VISIBLE);
            layoutReferralRow.setTag(taskList.iterator().next());
        } else {
            layoutReferralRow.setVisibility(View.GONE);
            findViewById(R.id.view_referral_row).setVisibility(View.GONE);
        }
    }

    @Override
    public void onMemberDetailsReloaded(MemberObject memberObject) {
        this.memberObject = memberObject;
        super.onMemberDetailsReloaded(memberObject);
        this.ancMemberProfilePresenter().fetchTasks();
    }

    private void refreshViewOnHomeVisitResult() {
        Observable<Visit> observable = Observable.create(visitObservableEmitter -> {
            Visit lastVisit = getVisit(CoreConstants.EventType.ANC_HOME_VISIT);
            visitObservableEmitter.onNext(lastVisit);
            visitObservableEmitter.onComplete();
        });

        final Disposable[] disposable = new Disposable[1];
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Visit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable[0] = d;
                    }

                    @Override
                    public void onNext(Visit visit) {
                        displayView();
                        setLastVisit(visit.getDate());
                        onMemberDetailsReloaded(memberObject);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        disposable[0].dispose();
                        disposable[0] = null;
                    }
                });
    }

    @Override
    public void startFormActivity(JSONObject formJson) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, formJson, Utils.metadata().familyMemberFormActivity),
                JsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public void openFamilyLocation() {
        //TODO: Removed in KK until kujaku is resolved
        //Intent intent = new Intent(this, AncMemberMapActivity.class);
        //this.startActivity(intent);
    }


    @Override
    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    @Override
    public void onReceivedNotifications(List<Pair<String, String>> notifications) {
        handleReceivedNotifications(this, notifications, notificationListAdapter);
    }

    public interface Flavor {
        Boolean hasFamilyLocationRow();

        Boolean hasEmergencyTransport();
    }

}
