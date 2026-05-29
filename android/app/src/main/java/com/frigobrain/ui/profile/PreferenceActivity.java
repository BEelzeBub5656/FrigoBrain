package com.frigobrain.ui.profile;

import android.content.ContentValues;
import android.database.Cursor;
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
import com.frigobrain.util.Constants;

import java.util.concurrent.Executors;

/**
 * 膳食偏好设置 - 素食/低卡/高蛋白等模式选择
 */
public class PreferenceActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    private RadioGroup rgDietType;
    private RadioButton rbNormal, rbVegan, rbVegetarian, rbLowCal, rbHighProtein;
    private AutoCompleteTextView actvExcluded;
    private EditText etMaxCalories, etAllergyInfo;
    private Button btnSave;

    // Common ingredient suggestions for autocomplete
    private static final String[] COMMON_INGREDIENTS = {
            "花生", "花生油", "海鲜", "虾", "螃蟹", "鱼", "贝类",
            "辣椒", "花椒", "大蒜", "洋葱", "生姜",
            "牛奶", "奶酪", "黄油", "酸奶",
            "鸡蛋", "豆腐", "豆制品",
            "猪肉", "牛肉", "羊肉", "鸡肉",
            "小麦", "面粉", "麸质",
            "芝麻", "香油", "芥末"
    };

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

        // Setup AutoCompleteTextView for excluded ingredients
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, COMMON_INGREDIENTS);
        actvExcluded.setAdapter(ingredientAdapter);
        actvExcluded.setThreshold(1);

        // Load existing preferences
        loadPreferences();

        // Save button
        btnSave.setOnClickListener(v -> savePreferences());
    }

    /**
     * Load existing dietary preferences from the database
     */
    private void loadPreferences() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Cursor cursor = db.getOpenHelper().getReadableDatabase().query(
                    "dietary_preferences",
                    null,
                    "user_id = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String dietType = cursor.getString(cursor.getColumnIndexOrThrow("diet_type"));
                String excluded = cursor.getString(cursor.getColumnIndexOrThrow("excluded_ingredients"));
                int maxCal = cursor.getInt(cursor.getColumnIndexOrThrow("max_calories_per_meal"));
                String allergy = cursor.getString(cursor.getColumnIndexOrThrow("allergy_info"));

                runOnUiThread(() -> {
                    // Set diet type radio button
                    setDietTypeRadio(dietType);

                    // Set excluded ingredients
                    if (!TextUtils.isEmpty(excluded)) {
                        actvExcluded.setText(excluded);
                    }

                    // Set max calories
                    if (maxCal > 0) {
                        etMaxCalories.setText(String.valueOf(maxCal));
                    }

                    // Set allergy info
                    if (!TextUtils.isEmpty(allergy)) {
                        etAllergyInfo.setText(allergy);
                    }
                });
            }

            if (cursor != null) {
                cursor.close();
            }
        });
    }

    /**
     * Map diet type string to radio button selection
     */
    private void setDietTypeRadio(String dietType) {
        switch (dietType) {
            case Constants.DIET_NORMAL:
                rbNormal.setChecked(true);
                break;
            case Constants.DIET_VEGAN:
                rbVegan.setChecked(true);
                break;
            case Constants.DIET_VEGETARIAN:
                rbVegetarian.setChecked(true);
                break;
            case Constants.DIET_LOW_CAL:
                rbLowCal.setChecked(true);
                break;
            case Constants.DIET_HIGH_PROTEIN:
                rbHighProtein.setChecked(true);
                break;
            default:
                rbNormal.setChecked(true);
                break;
        }
    }

    /**
     * Get selected diet type string from radio group
     */
    private String getSelectedDietType() {
        int checkedId = rgDietType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_vegan) return Constants.DIET_VEGAN;
        if (checkedId == R.id.rb_vegetarian) return Constants.DIET_VEGETARIAN;
        if (checkedId == R.id.rb_low_cal) return Constants.DIET_LOW_CAL;
        if (checkedId == R.id.rb_high_protein) return Constants.DIET_HIGH_PROTEIN;
        return Constants.DIET_NORMAL;
    }

    /**
     * Save dietary preferences to the database (upsert)
     */
    private void savePreferences() {
        String dietType = getSelectedDietType();
        String excluded = actvExcluded.getText().toString().trim();
        String maxCalStr = etMaxCalories.getText().toString().trim();
        String allergyInfo = etAllergyInfo.getText().toString().trim();

        int maxCalories = 0;
        if (!TextUtils.isEmpty(maxCalStr)) {
            try {
                maxCalories = Integer.parseInt(maxCalStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的热量值", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final int finalMaxCalories = maxCalories;
        long now = System.currentTimeMillis();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if preference exists
            Cursor cursor = db.getOpenHelper().getReadableDatabase().query(
                    "dietary_preferences",
                    new String[]{"pref_id"},
                    "user_id = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );

            boolean exists = cursor != null && cursor.moveToFirst();
            long existingId = exists ? cursor.getLong(0) : -1;
            if (cursor != null) cursor.close();

            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("diet_type", dietType);
            values.put("excluded_ingredients", excluded);
            values.put("max_calories_per_meal", finalMaxCalories);
            values.put("allergy_info", allergyInfo);
            values.put("updated_at", now);

            if (exists) {
                db.getOpenHelper().getWritableDatabase().update(
                        "dietary_preferences",
                        values,
                        "pref_id = ?",
                        new String[]{String.valueOf(existingId)}
                );
            } else {
                values.put("created_at", now);
                db.getOpenHelper().getWritableDatabase().insert(
                        "dietary_preferences",
                        null,
                        values
                );
            }

            runOnUiThread(() ->
                    Toast.makeText(PreferenceActivity.this,
                            "膳食偏好已保存", Toast.LENGTH_SHORT).show());
        });
    }
}
