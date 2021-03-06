package com.android.settings.vanir.navbar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.settings.BaseSetting;
import com.android.settings.BaseSetting.OnSettingChangedListener;
import com.android.settings.R;
import com.android.settings.SingleChoiceSetting;

import com.vanir.util.DeviceUtils;

import com.android.settings.util.HardwareKeyNavbarHelper;

public class NavbarSettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    private static final String NAVIGATION_BAR_WIDTH = "navigation_bar_width";

    private SeekBar mNavigationBarHeight;
    private SeekBar mNavigationBarHeightLandscape;
    private SeekBar mNavigationBarWidth;
    private TextView mBarHeightValue;
    private TextView mBarHeightLandscapeValue;
    private TextView mBarWidthValue;

    private Switch mEnabledSwitch;

    private static int HValue;
    private static int LValue;
    private static int WValue;
    private static int mDefaultHeight;
    private static int mDefaultHeightLandscape;
    private static int mDefaultWidth;

    int MIN_HEIGHT_PERCENT;
    int MIN_WIDTH_PERCENT;

    private Handler mHandler;

    public NavbarSettingsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (HardwareKeyNavbarHelper.shouldShowNavbarToggle()) {
            mEnabledSwitch = new Switch(getActivity());
            mHandler = new Handler();
            final int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
            mEnabledSwitch.setPaddingRelative(0, 0, padding, 0);
            mEnabledSwitch.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (HardwareKeyNavbarHelper.shouldShowNavbarToggle()) {
            getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM);
            getActivity().getActionBar().setCustomView(mEnabledSwitch, new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.END));
            mEnabledSwitch.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.ENABLE_NAVIGATION_BAR, 0) == 1));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (HardwareKeyNavbarHelper.shouldShowNavbarToggle()) {
            getActivity().getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
            getActivity().getActionBar().setCustomView(null);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navbar_settings, container, false);

        mNavigationBarHeight = (SeekBar) v.findViewById(R.id.navigation_bar_height); // make seekbar object
        mBarHeightValue = (TextView) v.findViewById(R.id.navigation_bar_height_value);

        mNavigationBarHeightLandscape = (SeekBar) v.findViewById(R.id.navigation_bar_height_landscape); // make seekbar object
        mBarHeightLandscapeValue = (TextView) v.findViewById(R.id.navigation_bar_height_landscape_value);

        mNavigationBarWidth = (SeekBar) v.findViewById(R.id.navigation_bar_width); // make seekbar object
        mBarWidthValue = (TextView) v.findViewById(R.id.navigation_bar_width_value);

        final Resources res = getActivity().getResources();
        mDefaultHeight = res.getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height);
        mDefaultHeightLandscape = res.getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height_landscape);
        mDefaultWidth = res.getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_width);

        final int MAX_HEIGHT_PERCENT = res.getInteger(R.integer.navigation_bar_height_max_percent);
        final int MAX_WIDTH_PERCENT = res.getInteger(R.integer.navigation_bar_width_max_percent);
        MIN_HEIGHT_PERCENT = res.getInteger(R.integer.navigation_bar_height_min_percent);
        MIN_WIDTH_PERCENT = res.getInteger(R.integer.navigation_bar_width_min_percent);
        final double MAX_WIDTH_SCALAR = MAX_WIDTH_PERCENT/100.0;
        final double MIN_WIDTH_SCALAR = MIN_WIDTH_PERCENT/100.0;
        final double MAX_HEIGHT_SCALAR = MAX_HEIGHT_PERCENT/100.0;
        final double MIN_HEIGHT_SCALAR = MIN_HEIGHT_PERCENT/100.0;

        mNavigationBarHeight.setMax(MAX_HEIGHT_PERCENT - MIN_HEIGHT_PERCENT);
        mNavigationBarHeightLandscape.setMax(MAX_HEIGHT_PERCENT - MIN_HEIGHT_PERCENT);
        mNavigationBarWidth.setMax(MAX_WIDTH_PERCENT - MIN_WIDTH_PERCENT);
        
        final ContentResolver cr = getActivity().getContentResolver();
        HValue = Settings.System.getInt(cr, Settings.System.NAVIGATION_BAR_HEIGHT, mDefaultHeight);
        LValue = Settings.System.getInt(cr, Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, mDefaultHeightLandscape);
        WValue = Settings.System.getInt(cr, Settings.System.NAVIGATION_BAR_WIDTH, mDefaultWidth);

        SharedPreferences prefs = getActivity().getSharedPreferences("last_slider_values", Context.MODE_PRIVATE);
        final int currentHeightPercent = prefs.getInt("heightPercent",
                (int)(100.0 * ( HValue - MIN_HEIGHT_SCALAR * mDefaultHeight) /
                ( MAX_HEIGHT_SCALAR * mDefaultHeight - MIN_HEIGHT_SCALAR * mDefaultHeight )));
        final int currentHeightLandscapePercent = prefs.getInt("heightLandscapePercent",
                (int)(100.0 * ( LValue - MIN_HEIGHT_SCALAR * mDefaultHeightLandscape) /
                ( MAX_HEIGHT_SCALAR * mDefaultHeightLandscape - MIN_HEIGHT_SCALAR * mDefaultHeightLandscape )));
        final int currentWidthPercent = prefs.getInt("widthPercent",
                (int)(100.0 * ( WValue - MIN_WIDTH_SCALAR * mDefaultWidth) /
                ( MAX_WIDTH_SCALAR * mDefaultWidth - MIN_WIDTH_SCALAR * mDefaultWidth )));

        mNavigationBarHeight.setOnSeekBarChangeListener(this);
        mNavigationBarHeight.setProgress(currentHeightPercent);
        mBarHeightValue.setText(String.valueOf(currentHeightPercent + MIN_HEIGHT_PERCENT)+"%");

        mNavigationBarHeightLandscape.setOnSeekBarChangeListener(this);
        mNavigationBarHeightLandscape.setProgress(currentHeightLandscapePercent);
        mBarHeightLandscapeValue.setText(String.valueOf(currentHeightLandscapePercent + MIN_HEIGHT_PERCENT)+"%");

        mNavigationBarWidth.setOnSeekBarChangeListener(this);
        mNavigationBarWidth.setProgress(currentWidthPercent);
        mBarWidthValue.setText(String.valueOf(currentWidthPercent + MIN_WIDTH_PERCENT)+"%");
 
        if (DeviceUtils.isPhone(getActivity())) {
            v.findViewById(R.id.navigation_bar_height_landscape_text).setVisibility(View.GONE);
            mBarHeightLandscapeValue.setVisibility(View.GONE);
            mNavigationBarHeightLandscape.setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.navigation_bar_width_text).setVisibility(View.GONE);
            mBarWidthValue.setVisibility(View.GONE);
            mNavigationBarWidth.setVisibility(View.GONE);
        }
        
        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mEnabledSwitch) {
            mEnabledSwitch.setEnabled(false);
            HardwareKeyNavbarHelper.writeEnableNavbarOption(getActivity(), mEnabledSwitch.isChecked());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEnabledSwitch.setEnabled(true);
                }
            }, 1000);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekbar, int rawprogress, boolean fromUser) {
        double proportion = 1.0;
        if (fromUser) {
            if (seekbar == mNavigationBarWidth) {
                final int progress = rawprogress + MIN_WIDTH_PERCENT;
                proportion = ((double)progress/100.0);
                mBarWidthValue.setText(String.valueOf(progress)+"%");
                WValue = (int)(proportion*mDefaultWidth);
                Settings.System.putInt(getActivity().getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH, WValue);
            } else if (seekbar == mNavigationBarHeight) {
                final int progress = rawprogress + MIN_HEIGHT_PERCENT;
                proportion = ((double)progress/100.0);
                mBarHeightValue.setText(String.valueOf(progress)+"%");
                HValue = (int)(proportion*mDefaultHeight);
                Settings.System.putInt(getActivity().getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT, HValue);
            } else if (seekbar == mNavigationBarHeightLandscape) {
                final int progress = rawprogress + MIN_HEIGHT_PERCENT;
                proportion = ((double)progress/100.0);
                mBarHeightLandscapeValue.setText(String.valueOf(progress)+"%");
                LValue = (int)(proportion*mDefaultHeightLandscape);
                Settings.System.putInt(getActivity().getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, LValue);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences prefs = getActivity().getSharedPreferences("last_slider_values", Context.MODE_PRIVATE);
        prefs.edit().putInt("heightPercent", mNavigationBarHeight.getProgress())
                    .putInt("heightLandscapePercent", mNavigationBarHeightLandscape.getProgress())
                    .putInt("widthPercent", mNavigationBarWidth.getProgress()).commit();
    }
}
