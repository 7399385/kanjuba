package me.mile.kjb.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;

import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.constants.RxBusTags;
import me.mile.kjb.service.AudioPlayer;
import me.mile.kjb.utils.MusicUtils;
import me.mile.kjb.storage.preference.Preferences;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.mile.kjb.R.layout.activity_setting);
    }

    @Override
    protected void onServiceBound() {
        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(me.mile.kjb.R.id.ll_fragment_container, settingFragment)
                .commit();
    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mSoundEffect;
        private Preference mFilterSize;
        private Preference mFilterTime;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(me.mile.kjb.R.xml.preference_setting);

            mSoundEffect = findPreference(getString(me.mile.kjb.R.string.setting_key_sound_effect));
            mFilterSize = findPreference(getString(me.mile.kjb.R.string.setting_key_filter_size));
            mFilterTime = findPreference(getString(me.mile.kjb.R.string.setting_key_filter_time));
            mSoundEffect.setOnPreferenceClickListener(this);
            mFilterSize.setOnPreferenceChangeListener(this);
            mFilterTime.setOnPreferenceChangeListener(this);

            mFilterSize.setSummary(getSummary(Preferences.getFilterSize(), me.mile.kjb.R.array.filter_size_entries, me.mile.kjb.R.array.filter_size_entry_values));
            mFilterTime.setSummary(getSummary(Preferences.getFilterTime(), me.mile.kjb.R.array.filter_time_entries, me.mile.kjb.R.array.filter_time_entry_values));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mSoundEffect) {
                startEqualizer();
                return true;
            }
            return false;
        }

        private void startEqualizer() {
            if (MusicUtils.isAudioControlPanelAvailable(getActivity())) {
                Intent intent = new Intent();
                String packageName = getActivity().getPackageName();
                intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, AudioPlayer.get().getAudioSessionId());

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtils.show(me.mile.kjb.R.string.device_not_support);
                }
            } else {
                ToastUtils.show(me.mile.kjb.R.string.device_not_support);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mFilterSize) {
                Preferences.saveFilterSize((String) newValue);
                mFilterSize.setSummary(getSummary(Preferences.getFilterSize(), me.mile.kjb.R.array.filter_size_entries, me.mile.kjb.R.array.filter_size_entry_values));
                RxBus.get().post(RxBusTags.SCAN_MUSIC, new Object());
                return true;
            } else if (preference == mFilterTime) {
                Preferences.saveFilterTime((String) newValue);
                mFilterTime.setSummary(getSummary(Preferences.getFilterTime(), me.mile.kjb.R.array.filter_time_entries, me.mile.kjb.R.array.filter_time_entry_values));
                RxBus.get().post(RxBusTags.SCAN_MUSIC, new Object());
                return true;
            }
            return false;
        }

        private String getSummary(String value, int entries, int entryValues) {
            String[] entryArray = getResources().getStringArray(entries);
            String[] entryValueArray = getResources().getStringArray(entryValues);
            for (int i = 0; i < entryValueArray.length; i++) {
                String v = entryValueArray[i];
                if (TextUtils.equals(v, value)) {
                    return entryArray[i];
                }
            }
            return entryArray[0];
        }
    }
}
