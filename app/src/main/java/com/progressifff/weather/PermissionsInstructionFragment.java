package com.progressifff.weather;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import java.util.Objects;

import static com.progressifff.weather.MainActivity.LOCATION_PERMISSION_REQUEST_CODE;

public final class PermissionsInstructionFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_instruction_fragment_layout,null);
        view.findViewById(R.id.goToSettingsBtn).setOnClickListener((new OnClickListener() {
            public final void onClick(@NonNull View v) {
            Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + v.getContext().getPackageName()));
            Objects.requireNonNull(getActivity()).startActivityForResult(appSettingsIntent, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }));
        return view;
    }
}
