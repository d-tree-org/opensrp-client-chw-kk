package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.GroupSessionRegisterActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Objects;
import java.util.UUID;

/**
 * Author issyzac on 04/07/2023
 */
public class GcRegistrationStageFragment extends BaseGroupSessionRegisterFragment {

    TextView tvSessionSummaryNumber;
    TextView tvSessionTookPlaceTitle;
    RelativeLayout rlSessionSummaryNumberOverlay;
    private MaterialButton nextButton;
    private MaterialButton submitNotDoneButton;

    //New sessions implementation
    private TextInputEditText etSessionDate;
    private AppCompatSpinner spTypeOfPlace;
    private AppCompatSpinner spDidSessionTakePlace;
    private LinearLayoutCompat llNoSessionContainer;
    private LinearLayoutCompat llSessionRegistrationContainer;
    private AppCompatSpinner spNoSessionSpinner;
    private AppCompatSpinner divideChildrenInGroupsSpinner;
    private TextInputLayout etGps;
    private TextInputLayout etDuration;
    private TextInputEditText etOtherReasonText;
    private TextInputLayout etOtherReasonLayout;

    private String selectedDateString = "";
    private DateTime selectedDateTime = null;

    private static FormUtils formUtils;

    private static final String[] places = { "Session place","Dispensary", "Village Office", "Participant house", "Outreach vaccination point", "Primary School", "Under the tree" };
    private static final String[] sessionTookPlaces = { "-", "Yes", "No" };
    private static final String[] noSessionReasons = { "CHW(s) incapacitated (sickness, etc.)", "All caregivers unavailable", "All caregivers refused", "All children incapacitated (sickness, etc.)", "Other (Specify)" };

    private static final String[] divideChildrenInGroupsKeys = {"Did you divide children into different age groups?", "Yes", "No"};

    ArrayAdapter<String> placesAdapter;
    ArrayAdapter<String> sessionTookPlaceAdapter;
    ArrayAdapter<String> noSessionReasonAdapter;

    ArrayAdapter<String> divideChildrenInGroupsAdapter;
    private SessionModelUpdatedListener nextStepListener;
    private GroupSessionModel sessionModel;

    public GcRegistrationStageFragment(SessionModelUpdatedListener listener){
        this.nextStepListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placesAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, places);
        sessionTookPlaceAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, sessionTookPlaces);
        noSessionReasonAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, noSessionReasons);
        divideChildrenInGroupsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, divideChildrenInGroupsKeys);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = rootView;

        etSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSessionDate();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter().fetchSessionDetails();
                //Update session model
                if (sessionModel != null){
                    nextStepListener.onSessionModelUpdated(sessionModel);
                    ((BaseRegisterActivity) requireActivity()).switchToFragment(1);
                }
            }
        });

        submitNotDoneButton.setOnClickListener(v -> {
            presenter().fetchSessionDetails();
            //Update session model
            if (sessionModel != null){
                nextStepListener.onSessionModelUpdated(sessionModel);
                presenter().saveGroupSession(sessionModel);
                //requireActivity().finish();
            }
        });

        return view;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_register;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        tvSessionSummaryNumber = view.findViewById(R.id.tv_sessions_summary_number);
        rlSessionSummaryNumberOverlay = view.findViewById(R.id.rl_sessions_summary_number);

        setupVisitSummaryThisMonth();

        etOtherReasonText = view.findViewById(R.id.et_other_reason_text);
        etOtherReasonLayout = view.findViewById(R.id.et_other_reason);

        spDidSessionTakePlace = view.findViewById(R.id.sp_session_took_place);
        llNoSessionContainer = view.findViewById(R.id.ll_no_session_container);
        llSessionRegistrationContainer = view.findViewById(R.id.ll_start_session_reg);
        spNoSessionSpinner = view.findViewById(R.id.sp_no_session_reason);

        etSessionDate = view.findViewById(R.id.editTextSessionDate);
        spTypeOfPlace = view.findViewById(R.id.spinnerTypeOfPlace);
        etGps = view.findViewById(R.id.editTextGps);
        etDuration = view.findViewById(R.id.editTextDuration);
        divideChildrenInGroupsSpinner = view.findViewById(R.id.divide_children_in_groups);

        nextButton = view.findViewById(R.id.buttonNext);
        submitNotDoneButton = view.findViewById(R.id.button_submit_not_done);
        tvSessionTookPlaceTitle = view.findViewById(R.id.tv_session_took_place_title);

        setupSpinner();

    }

    private void setupVisitSummaryThisMonth() {
        presenter().refreshSessionSummaryView();
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
    }

    @Override
    public void getSessionDetails(){

        sessionModel = GroupSessionRegisterActivity.getSessionModel();

        if (sessionModel == null)
            sessionModel = new GroupSessionModel();

        //Update the sessionDetails Object
        //Generate a randomUUID for the form
        String id = UUID.randomUUID().toString();
        sessionModel.setSessionId(id);

        long sessionDateValue = DateUtil.getMillis(selectedDateTime);
        sessionModel.setSessionDate(sessionDateValue);

        String sessionPlaceString = spTypeOfPlace.getSelectedItem().toString();
        sessionModel.setSessionPlace(sessionPlaceString);

        boolean dividedInGroups = divideChildrenInGroupsSpinner.getSelectedItem().toString().equalsIgnoreCase("Yes");
        sessionModel.setChildrenDividedInGroups(dividedInGroups);

    }

    private void selectSessionDate(){
        DialogFragment newFragment = new DatePickerFragment(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateTime now = new DateTime();
                selectedDateTime = new DateTime(i, i1, i2, now.getHourOfDay(), now.getMinuteOfHour());
                selectedDateString = i2+"/"+i1+"/"+i;
                etSessionDate.setText(selectedDateString);
            }
        });
        Activity activity = (BaseRegisterActivity) getActivity();
        newFragment.show(activity.getFragmentManager(), "datePicker");
    }

    private void setupSpinner(){
        sessionTookPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDidSessionTakePlace.setAdapter(sessionTookPlaceAdapter);
        spDidSessionTakePlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    //Nothing selected
                    llNoSessionContainer.setVisibility(View.GONE);
                    llSessionRegistrationContainer.setVisibility(View.GONE);
                }
                else if (i == 1){
                    //Yes
                    llNoSessionContainer.setVisibility(View.GONE);
                    llSessionRegistrationContainer.setVisibility(View.VISIBLE);
                }else if (i == 2){
                    //No
                    llNoSessionContainer.setVisibility(View.VISIBLE);
                    llSessionRegistrationContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        noSessionReasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNoSessionSpinner.setAdapter(noSessionReasonAdapter);
        spNoSessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4){
                    etOtherReasonLayout.setVisibility(View.VISIBLE);
                }else{
                    etOtherReasonLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfPlace.setAdapter(placesAdapter);
        spTypeOfPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        divideChildrenInGroupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divideChildrenInGroupsSpinner.setAdapter(divideChildrenInGroupsAdapter);

        divideChildrenInGroupsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private static FormUtils getFormUtils() throws Exception {
        if (formUtils == null){
            formUtils = FormUtils.getInstance(ChwApplication.getInstance().getApplicationContext());
        }
        return formUtils;
    }

}
