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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.GroupSessionRegisterActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.util.DateUtil;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.UUID;

/**
 * Author issyzac on 04/07/2023
 */
public class GcRegistrationStageFragment extends BaseGroupSessionRegisterFragment {

    private MaterialButton nextButton;

    //New sessions implementation
    private TextInputEditText etSessionDate;
    private AppCompatSpinner spTypeOfPlace;
    private TextInputLayout etGps;
    private TextInputLayout etDuration;

    private String selectedDateString = "";
    private DateTime selectedDateTime = null;

    private static FormUtils formUtils;

    private static final String[] places = {"Session place","Hospital", "Health Center", "School"};

    private SessionModelUpdatedListener nextStepListener;
    private GroupSessionModel sessionModel;

    public GcRegistrationStageFragment(SessionModelUpdatedListener listener){
        this.nextStepListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return view;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_register;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        etSessionDate = view.findViewById(R.id.editTextSessionDate);
        spTypeOfPlace = view.findViewById(R.id.spinnerTypeOfPlace);
        etGps = view.findViewById(R.id.editTextGps);
        etDuration = view.findViewById(R.id.editTextDuration);

        nextButton = view.findViewById(R.id.buttonNext);

        setupSpinner();

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, places);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfPlace.setAdapter(adapter);
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
    }

    private static FormUtils getFormUtils() throws Exception {
        if (formUtils == null){
            formUtils = FormUtils.getInstance(ChwApplication.getInstance().getApplicationContext());
        }
        return formUtils;
    }

}
