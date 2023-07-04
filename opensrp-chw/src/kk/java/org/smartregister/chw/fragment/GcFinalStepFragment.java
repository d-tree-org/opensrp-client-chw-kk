package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * Author issyzac on 04/07/2023
 */
public class GcFinalStepFragment extends BaseGroupSessionRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = rootView;

        return rootView;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
    }

    @Override
    protected int getLayout() {
        return super.getLayout();
    }

    @Override
    public String getSessionDetails() {
        return null;
    }
}
