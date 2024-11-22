package com.example.sunwaysportslink.ui.setting.changelanguage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.ui.home.HomeActivity;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    private RadioGroup languageRadioGroup;
    private Button btnSaveLanguage;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, ChangeLanguageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        btnSaveLanguage = findViewById(R.id.btnSaveLanguage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Language");
        }

        // Load saved language preference or default to English
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String savedLanguage = preferences.getString("languageCode", "en");
        setDefaultLanguageSelection(savedLanguage);

        btnSaveLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = languageRadioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String languageCode = getLanguageCode(selectedRadioButton.getText().toString());
                    saveLanguagePreference(languageCode);
                    changeAppLanguage(languageCode);
                }
            }
        });
    }

    private void setDefaultLanguageSelection(String languageCode) {
        switch (languageCode) {
            case "md":
                ((RadioButton) findViewById(R.id.rbMandarin)).setChecked(true);
                break;
            case "ms":
                ((RadioButton) findViewById(R.id.rbBahasaMelayu)).setChecked(true);
                break;
            default:
                ((RadioButton) findViewById(R.id.rbEnglish)).setChecked(true);
                break;
        }
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("languageCode", languageCode);
        editor.apply();
    }

    private String getLanguageCode(String language) {
        switch (language) {
            case "Mandarin":
                return "md";
            case "Bahasa Melayu":
                return "ms";
            default:
                return "en";
        }
    }

    private void changeAppLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Update configuration with new locale
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);

        Context context = createConfigurationContext(config);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the HomeActivity with the new locale
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
