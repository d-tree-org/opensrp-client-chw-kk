package org.smartregister.chw.provider;

import static org.smartregister.chw.core.utils.Utils.getDuration;
import static org.smartregister.chw.util.Utils.getClientName;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.nerdstone.neatformcore.views.widgets.CheckBoxNFormView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.holders.SelectChildGSViewHolder;
import org.smartregister.chw.model.SelectedChildGS;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Kassim Sheghembe on 2023-06-19
 */
public class SelectChildForGroupSessionFragmentProvider extends ChildRegisterProvider {
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private Context context;
    private View.OnClickListener onClickListener;
    private final HashMap<String, SelectedChildGS> childList = new HashMap<>();

    public SelectChildForGroupSessionFragmentProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, (SelectChildGSViewHolder) viewHolder);
            populateIdentifierColumn(pc, viewHolder);
        }
    }

    public void addChildToChildSelectedList(String childBaseEntityId, SelectedChildGS selectedChildGS) {
        childList.put(childBaseEntityId, selectedChildGS);
    }

    public void removeChildFromChildSelectedList(String childBaseEntityId) {
        childList.remove(childBaseEntityId);
    }

    public HashMap<String, SelectedChildGS> getSelectedChildList() {
        return childList;
    }

    public void updateChildSelectionStatus(String baseEntityId, boolean cameWithCareGiver, List<String> accompanyingRelatives, String groupPlaced) {
        if (childList.containsKey(baseEntityId)) {
            Objects.requireNonNull(childList.get(baseEntityId)).setCameWithPrimaryCareGiver(cameWithCareGiver);
            Objects.requireNonNull(childList.get(baseEntityId)).setGroupPlaced(groupPlaced);
            Objects.requireNonNull(childList.get(baseEntityId)).setAccompanyingRelatives(accompanyingRelatives);
        }
    }

    protected void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, SelectChildGSViewHolder viewHolder) {
        String parentFirstName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
        String parentLastName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
        String parentMiddleName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);
        String childBaseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);

        String parentName = context.getResources().getString(R.string.care_giver_initials) + ": " + getClientName(parentFirstName, parentMiddleName, parentLastName);
        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String childName = getClientName(firstName, middleName, lastName);

        viewHolder.checkBoxNFormView.setChecked(childList.containsKey(childBaseEntityId));
        fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));

        String dobString = getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillChildNameAndAge(viewHolder, childName, dobString);
        setAddressAndGender(pc, viewHolder);

        addButtonClickListeners(client, viewHolder);

    }

    private void fillChildNameAndAge(RegisterViewHolder viewHolder, String childName, String dobString) {
        String age = context.getResources().getString(R.string.age) + ": " + WordUtils.capitalize(Utils.getTranslatedDate(dobString, context));
        if (!ChwApplication.getApplicationFlavor().prioritizeChildNameOnChildRegister()) {
            fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " + WordUtils.capitalize(Utils.getTranslatedDate(dobString, context)));
        } else {
            fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName));
            fillValue(viewHolder.textViewChildAge, age);
        }
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(getChildRegisterLayout(), parent, false);
        return new SelectChildGSViewHolder(view);
    }

    private int getChildRegisterLayout() {
        return R.layout.adapter_select_child_group_register_list_row;
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        return super.createFooterHolder(parent);
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        super.getFooterView(viewHolder, currentPageCount, totalPageCount, hasNext, hasPrevious);
    }

    public void addButtonClickListeners(SmartRegisterClient client, SelectChildGSViewHolder viewHolder) {
        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(patient, client);

        View checkedView = viewHolder.checkBoxNFormView;
        attachDosageOnclickListener(checkedView, client);
    }

    @Override
    protected void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
    }
}
