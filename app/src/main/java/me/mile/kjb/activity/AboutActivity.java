package me.mile.kjb.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import me.mile.kjb.BuildConfig;
import me.mile.kjb.R;
import me.mile.kjb.storage.preference.Preferences;

public class AboutActivity extends BaseActivity {
    private static boolean iskanjuba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        iskanjuba = bundle.getBoolean("iskanjuba");
        setContentView(R.layout.activity_about);
        getFragmentManager().beginTransaction().replace(me.mile.kjb.R.id.ll_fragment_container, new AboutFragment()).commit();
    }

    public static class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference mVersion;
        private Preference mShare;
//        private Preference mStar;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(!iskanjuba) addPreferencesFromResource(me.mile.kjb.R.xml.preference_about_pony);
            else addPreferencesFromResource(R.xml.preference_about_kanjuba);
            mVersion = findPreference("version");
            mShare = findPreference("share");
//            mStar = findPreference("star");

            mVersion.setSummary("v " + BuildConfig.VERSION_NAME);
            setListener();
        }

        private void setListener() {
            mShare.setOnPreferenceClickListener(this);

        }
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mShare) {
                share();
                return true;
            }
            return false;
        }

        private void share() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app, getString(R.string.app_name))+ Preferences.getShareUrl());
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }

        private void openUrl(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}
