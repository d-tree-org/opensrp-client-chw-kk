package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.activity.ReferralDetailsViewActivity;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class KKReferralDetailsViewActivity extends ReferralDetailsViewActivity implements View.OnClickListener {

    private CustomFontTextView clientName;
    private CustomFontTextView careGiverName;
    private CustomFontTextView careGiverPhone;
    private CustomFontTextView clientReferralProblem;
    private CustomFontTextView referralDate;
    private CustomFontTextView referralFacility;
    private CustomFontTextView recordFollowup;
    private CustomFontTextView preReferralManagement;
    private CustomFontTextView referralType;
    private MemberObject memberObject;
    private CommonPersonObjectClient pClient;

    public static void startReferralDetailsViewActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, KKReferralDetailsViewActivity.class);
        intent.putExtra("memberObject", memberObject);
        intent.putExtra(CoreConstants.INTENT_KEY.CLIENT, client);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        this.setContentView(R.layout.referral_details_activity);
        this.inflateToolbar();
        this.memberObject = (MemberObject)this.getIntent().getSerializableExtra("memberObject");
        this.pClient = (CommonPersonObjectClient) this.getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CLIENT);
        this.setUpViews();
    }

    private void inflateToolbar() {
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.back_referrals_toolbar);
        CustomFontTextView toolBarTextView = (CustomFontTextView)toolbar.findViewById(R.id.toolbar_title);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable upArrow = this.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(this.getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0.0F);
        }

        toolbar.setNavigationOnClickListener((v) -> {
            this.finish();
        });
        toolBarTextView.setText(R.string.back_to_referrals);
        toolBarTextView.setOnClickListener((v) -> {
            this.finish();
        });
        this.appBarLayout = (AppBarLayout)this.findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= 21) {
            this.appBarLayout.setOutlineProvider((ViewOutlineProvider)null);
        }

    }

    public void setUpViews() {
        this.referralFacility = this.findViewById(R.id.referral_facility);
        this.preReferralManagement = this.findViewById(R.id.pre_referral_management);
        this.clientName = this.findViewById(R.id.client_name);
        this.careGiverName = this.findViewById(R.id.care_giver_name);
        this.careGiverPhone = this.findViewById(R.id.care_giver_phone);
        this.clientReferralProblem = this.findViewById(R.id.client_referral_problem);
        this.referralDate = this.findViewById(R.id.referral_date);
        this.referralFacility = this.findViewById(R.id.referral_facility);
        this.preReferralManagement = this.findViewById(R.id.pre_referral_management);
        this.referralType = this.findViewById(R.id.referral_type);
        this.recordFollowup = this.findViewById(R.id.record_followup_visit);
        this.recordFollowup.setOnClickListener(this);
        this.getReferralDetails();
    }

    private void getReferralDetails() {
        if (this.memberObject != null) {
            this.updateProblemDisplay();
            String clientAge = String.valueOf((new Period(new DateTime(this.memberObject.getAge()), new DateTime())).getYears());
            this.clientName.setText(String.format(Locale.getDefault(), "%s %s %s, %s", this.memberObject.getFirstName(), this.memberObject.getMiddleName(), this.memberObject.getLastName(), clientAge));
            DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            BigDecimal dateTimestamp = new BigDecimal(this.memberObject.getChwReferralDate());
            Calendar referralDateCalendar = Calendar.getInstance();
            referralDateCalendar.setTimeInMillis(dateTimestamp.longValue());
            this.referralDate.setText(dateFormatter.format(referralDateCalendar.getTime()));
            this.referralFacility.setVisibility(View.GONE);
            if (pClient != null) {
                String focus = Utils.getValue(pClient.getColumnmaps(), CoreConstants.DB_CONSTANTS.FOCUS, true);
                String priority = Utils.getValue(pClient.getColumnmaps(), CoreConstants.DB_CONSTANTS.PRIORITY, true);
                this.referralType.setText(getCategory(this, focus, priority));
            }
            if (this.memberObject.getPrimaryCareGiver() == null) {
                this.careGiverName.setVisibility(View.GONE);
            } else {
                this.careGiverName.setText(String.format("CG : %s", this.memberObject.getPrimaryCareGiver()));
            }

            this.careGiverPhone.setText(!this.getFamilyMemberContacts().isEmpty() && this.getFamilyMemberContacts() != null ? this.getFamilyMemberContacts() : this.getString(org.smartregister.chw.referral.R.string.phone_not_provided));
            this.updateProblemDisplay();
        }

    }

    private void updateProblemDisplay() {
        try {
            String problemString = this.memberObject.getProblem().trim();
            if (problemString.charAt(0) == '[') {
                problemString = problemString.substring(1);
            }

            if (problemString.charAt(problemString.length() - 1) == ']') {
                problemString = problemString.substring(0, problemString.length() - 1);
            }

            this.clientReferralProblem.setText(problemString);
            if (!StringUtils.isEmpty(this.memberObject.getProblemOther())) {
                this.clientReferralProblem.append(", " + this.memberObject.getProblemOther());
            }
        } catch (Exception var2) {
            Timber.e(var2);
            this.clientReferralProblem.setText(this.getString(org.smartregister.chw.referral.R.string.empty_value));
        }

    }

    private String getFamilyMemberContacts() {
        String phoneNumber = "";
        String familyPhoneNumber = this.memberObject.getPhoneNumber();
        String familyPhoneNumberOther = this.memberObject.getOtherPhoneNumber();
        if (StringUtils.isNoneEmpty(new CharSequence[]{familyPhoneNumber})) {
            phoneNumber = familyPhoneNumber;
        } else if (StringUtils.isEmpty(familyPhoneNumber) && StringUtils.isNoneEmpty(new CharSequence[]{familyPhoneNumberOther})) {
            phoneNumber = familyPhoneNumberOther;
        } else if (StringUtils.isNoneEmpty(new CharSequence[]{familyPhoneNumber}) && StringUtils.isNoneEmpty(new CharSequence[]{familyPhoneNumberOther})) {
            phoneNumber = familyPhoneNumber + ", " + familyPhoneNumberOther;
        } else if (StringUtils.isNoneEmpty(new CharSequence[]{this.memberObject.getFamilyHeadPhoneNumber()})) {
            phoneNumber = this.memberObject.getFamilyHeadPhoneNumber();
        }

        return phoneNumber;
    }

    private static String getCategory(Context context, String problem, String referralTypeArg) {
        int referralType = Integer.parseInt(referralTypeArg);
        if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.SICK_CHILD)) {
            if(referralType == 1) {
                return  context.getString(R.string.child_hf_referral_text);
            }
            return context.getString(R.string.child_addo_linkage_text);
        } else if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS)){
            if(referralType == 1) {
                return context.getString(R.string.anc_hf_referral_text);
            }
            return context.getString(R.string.anc_addo_linkage_text);
        } else if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.ADOLESCENT_DANGER_SIGNS)) {
            if (referralType == 1) {
                return context.getString(R.string.adolescent_hf_referral_text);
            }
            return context.getString(R.string.adolescent_addo_linkage_text);
        } else {
            if(referralType == 1) {
                return context.getString(R.string.pnc_hf_referral_text);
            }
            return context.getString(R.string.pnc_addo_linkage_text);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.record_followup_visit) {
            if (pClient != null) {
                openReferralFollowUp(pClient);
            }
        }
    }

    private void openReferralFollowUp(CommonPersonObjectClient pClient) {
        ReferralFollowupActivity.startReferralFollowupActivity(this, pClient.getCaseId(), Utils.getValue(pClient.getColumnmaps(), CoreConstants.DB_CONSTANTS.FOR, false));
    }
}
