package com.progressifff.weather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public final class PermissionsRationaleFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_rationale_fragment_layout, null);
        view.findViewById(R.id.requestPermissionsBtn).setOnClickListener(new OnClickListener() {
            public final void onClick(View it) {
                MainActivity.requestPermissions(getActivity(), MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
        return view;
    }
}
