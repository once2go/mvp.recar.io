package io.recar.once2go.easynavi.framents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.recar.once2go.easynavi.R;

/**
 * Created by once2go on 11.04.16.
 */
public class ObdDataFragment extends Fragment  {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.obd_data_fragment_layout, container, false);

        return view;
    }
}
