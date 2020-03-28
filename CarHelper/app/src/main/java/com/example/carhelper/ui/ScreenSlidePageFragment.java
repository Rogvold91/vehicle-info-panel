package com.example.carhelper.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.carhelper.R;
import com.example.carhelper.service.CarData;
import com.example.carhelper.service.IDataCallback;
import com.example.carhelper.util.Constants;
import com.example.carhelper.util.FabricConverter;
import com.example.carhelper.util.TypeControl;

import com.example.carhelper.util.Settings;

import static com.example.carhelper.util.Unit.*;

public class ScreenSlidePageFragment extends Fragment implements DataObserver{

    private final Handler mHanlder;
    private SpeedometerGauge mVehiclePanel;
    private IDataCallback mCallback;
    private TypeControl mType;

    public ScreenSlidePageFragment(TypeControl type) {
        this.mType = type;
        this.mHanlder = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (mVehiclePanel != null)
                    mVehiclePanel.updateValue(msg.what);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        mVehiclePanel = v.findViewById(R.id.info_view);
        init_values_style();
        init_color_style();
        return v;
    }

    private void init_values_style() {
        // configure value range and ticks
        int value;
        value = mType.equals(TypeControl.Speedometer) ? Settings.getInstance(getContext()).getmMaxSpeed()
                                                      : Settings.getInstance(getContext()).getmMaxRPM();
        mVehiclePanel.setmMaxValue(value);

        value = mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.major_ticks_speed)
                : getContext().getResources().getInteger(R.integer.major_ticks_rpm);
        mVehiclePanel.setmMajorTickStep(value);

        value = mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.minor_ticks_speed)
                : getContext().getResources().getInteger(R.integer.minor_ticks_rpm);
        mVehiclePanel.setMinorTicks(value);

        // Configure value range colors
        int value_start, value_end;
        value_start =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.start_low_speed)
                : getContext().getResources().getInteger(R.integer.start_low_rpm);
        value_end =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.end_low_speed)
                : getContext().getResources().getInteger(R.integer.end_low_rpm);

        mVehiclePanel.addColoredRange(value_start, value_end, Color.GREEN);

        value_start =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.start_mid_speed)
                : getContext().getResources().getInteger(R.integer.start_mid_rpm);
        value_end =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.end_mid_speed)
                : getContext().getResources().getInteger(R.integer.end_mid_rpm);
        mVehiclePanel.addColoredRange(value_start, value_end, Color.YELLOW);

        value_start =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.start_up_speed)
                : getContext().getResources().getInteger(R.integer.start_up_rpm);
        value_end =  mType.equals(TypeControl.Speedometer) ? getContext().getResources().getInteger(R.integer.end_up_speed)
                : getContext().getResources().getInteger(R.integer.end_up_rpm);
        mVehiclePanel.addColoredRange(value_start, value_end, Color.RED);
        mVehiclePanel.setLabelConverter(FabricConverter.buildConverter(SI, SI));

    }

    private void init_color_style() {
        if (mType.equals(TypeControl.Speedometer)) {
            boolean is_empire_system = getContext().getResources().getBoolean(R.bool.is_empire);
            mVehiclePanel.setUnitsText(is_empire_system ? Constants.MPH : Constants.KMH);
        }
        mVehiclePanel.setmLabelTextSize(27);
    }

    @Override
    public void onCarDataUpdated(@NonNull CarData data) {
        if (mType.equals(TypeControl.Speedometer)) {
            mHanlder.sendEmptyMessage((int) data.getSpeed());
        } else {
            mHanlder.sendEmptyMessage((int) data.getPrm());
        }
    }
}
