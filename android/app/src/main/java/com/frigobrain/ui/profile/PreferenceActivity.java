package com.frigobrain.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.DietaryPreference;
import com.frigobrain.util.Constants;

import java.util.concurrent.Executors;

/** 膳食偏好设置 */
public class PreferenceActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    private RadioGroup rgDietType;
    private RadioButton rbNormal, rbVegan, rbVegetarian, rbLowCal, rbHighProtein;
    private AutoCompleteTextView actvExcluded;
    private EditText etMaxCalories, etAllergyInfo;
    private Button btnSave;

    public PreferenceActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        db = FrigoBrainApp.getDatabase();
        userId = FrigoBrainApp.getCurrentUserId();

        rgDietType = findViewById(R.id.rg_diet_type);
        rbNormal = findViewById(R.id.rb_normal);
        rbVegan = findViewById(R.id.rb_vegan);
        rbVegetarian = findViewById(R.id.rb_vegetarian);
        rbLowCal = findViewById(R.id.rb_low_cal);
        rbHighProtein = findViewById(R.id.rb_high_protein);
        actvExcluded = findViewById(R.id.actv_excluded);
        etMaxCalories = findViewById(R.id.et_max_calories);
        etAllergyInfo = findViewById(R.id.et_allergy_info);
        btnSave = findViewById(R.id.btn_save_preferences);

        // Autocomplete for excluded ingredients
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Constants.COMMON_ALLERGENS);
        actvExcluded.setAdapter(adapter);
        actvExcluded.setThreshold(1);

        loadPreferences();
        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadPreferences() {
        Executors.newSingleThreadExecutor().execute(() -> {
            DietaryPreference pref = db.dietaryPreferenceDao().getByUser(userId);
            runOnUiThread(() -> {
                if (pref != null) {
                    setDietTypeRadio(pref.getDietType());
                    if (!TextUtils.isEmpty(pref.getExcludedIngredients()))
                        actvExcluded.setText(pref.getExcludedIngredients());
                    if (pref.getMaxCaloriesPerMeal() > 0)
                        etMaxCalories.setText(String.valueOf(pref.getMaxCaloriesPerMeal()));
                    if (!TextUtils.isEmpty(pref.getAllergyInfo()))
                        etAllergyInfo.setText(pref.getAllergyInfo());
                }
            });
        });
    }

    private void setDietTypeRadio(String type) {
        switch (type) {
            case Constants.DIET_NORMAL: rbNormal.setChecked(true); break;
            case Constants.DIET_VEGAN: rbVegan.setChecked(true); break;
            case Constants.DIET_VEGETARIAN: rbVegetarian.setChecked(true); break;
            case Constants.DIET_LOW_CAL: rbLowCal.setChecked(true); break;
            case Constants.DIET_HIGH_PROTEIN: rbHighProtein.setChecked(true); break;
            default: rbNormal.setChecked(true);
        }
    }

    private String getSelectedDietType() {
        int id = rgDietType.getCheckedRadioButtonId();
        if (id == R.id.rb_vegan) return Constants.DIET_VEGAN;
        if (id == R.id.rb_vegetarian) return Constants.DIET_VEGETARIAN;
        if (id == R.id.rb_low_cal) return Constants.DIET_LOW_CAL;
        if (id == R.id.rb_high_protein) return Constants.DIET_HIGH_PROTEIN;
        return Constants.DIET_NORMAL;
    }

    private void savePreferences() {
        String dietType = getSelectedDietType();
        String excluded = actvExcluded.getText().toString().trim();
        String maxCalStr = etMaxCalories.getText().toString().trim();
        String allergy = etAllergyInfo.getText().toString().trim();

        int maxCal = 0;
        if (!TextUtils.isEmpty(maxCalStr)) {
            try {
                maxCal = Integer.parseInt(maxCalStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的热量值", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        long now = System.currentTimeMillis();
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = db.dietaryPreferenceDao().countByUser(userId);
            if (count > 0) {
                db.dietaryPreferenceDao().update(userId, dietType, excluded, maxCal, allergy, now);
            } else {
                DietaryPreference pref = new DietaryPreference(userId, dietType);
                pref.setExcludedIngredients(excluded);
                pref.setMaxCaloriesPerMeal(maxCal);
                pref.setAllergyInfo(allergy);
                pref.setCreatedAt(now);
                pref.setUpdatedAt(now);
                db.dietaryPreferenceDao().insert(pref);
            }
            runOnUiThread(() ->
                    Toast.makeText(this, "偏好设置已保存", Toast.LENGTH_SHORT).show());
        });
    }
}
