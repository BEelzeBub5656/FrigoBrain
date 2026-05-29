package com.frigobrain.ui.inventory;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.FoodItem;
import com.frigobrain.util.Constants;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Calendar;
import java.util.concurrent.Executors;

public class FoodAddActivity extends AppCompatActivity {

    private AutoCompleteTextView actvFoodName;
    private Spinner spinnerCategory, spinnerUnit;
    private EditText etQuantity, etExpiryDays, etPrice, etNotes;
    private Button btnPurchaseDate, btnSave, btnScan;
    private AppDatabase db;
    private long userId;
    private long purchaseDate = System.currentTimeMillis();
    private long[] categoryIds;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    // Common preset food names for autocomplete
    private static final String[] PRESET_FOODS = {
        "番茄", "鸡蛋", "菠菜", "西兰花", "土豆", "胡萝卜", "白菜", "青菜", "黄瓜", "青椒",
        "生菜", "冬瓜", "香菇", "豆腐", "葱", "姜", "蒜",
        "苹果", "香蕉", "橙子", "西瓜", "葡萄", "草莓", "芒果", "梨",
        "猪肉", "牛肉", "鸡肉", "鸡胸肉", "排骨", "鲈鱼", "虾",
        "牛奶", "酸奶", "黄油", "芝士",
        "鸡蛋", "面粉", "大米", "面条", "燕麦", "油", "盐", "花生米"
    };

    private static final String[] UNITS = {"个", "g", "ml", "斤", "袋", "根", "颗", "块", "条", "瓣", "勺", "朵", "把", "杯"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_add);
        setupHeader("录入食材");

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        actvFoodName = findViewById(R.id.actv_food_name);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerUnit = findViewById(R.id.spinner_unit);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiryDays = findViewById(R.id.et_expiry_days);
        etPrice = findViewById(R.id.et_price);
        etNotes = findViewById(R.id.et_notes);
        btnPurchaseDate = findViewById(R.id.btn_purchase_date);
        btnSave = findViewById(R.id.btn_save);
        btnScan = findViewById(R.id.btn_scan);

        // Autocomplete: merge preset + historical names
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, PRESET_FOODS);
        actvFoodName.setAdapter(foodAdapter);
        actvFoodName.setThreshold(1);

        // Merge historical food names from DB
        Executors.newSingleThreadExecutor().execute(() -> {
            var names = db.foodItemDao().getAllFoodNames();
            runOnUiThread(() -> {
                if (names != null && !names.isEmpty()) {
                    String[] allNames = new String[PRESET_FOODS.length + names.size()];
                    System.arraycopy(PRESET_FOODS, 0, allNames, 0, PRESET_FOODS.length);
                    for (int i = 0; i < names.size(); i++) {
                        allNames[PRESET_FOODS.length + i] = names.get(i);
                    }
                    ArrayAdapter<String> merged = new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, allNames);
                    actvFoodName.setAdapter(merged);
                }
            });
        });

        // Unit spinner
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setSelection(0); // default: 个

        // Category spinner
        loadCategories();

        // Purchase date picker
        btnPurchaseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        purchaseDate = c.getTimeInMillis();
                        btnPurchaseDate.setText(Constants.DATE_FMT_CN.format(c.getTime()));
                    },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Save
        btnSave.setOnClickListener(v -> saveFood());

        // ZXing barcode scanner
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                actvFoodName.setText(result.getContents());
                Toast.makeText(this, "扫码结果: " + result.getContents(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "扫码已取消", Toast.LENGTH_SHORT).show();
            }
        });

        // Scan button - launch ZXing barcode scanner
        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
            options.setPrompt("扫描商品条码自动录入");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(false);
            barcodeLauncher.launch(options);
        });
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            var cats = db.foodCategoryDao().getAllSync();
            categoryIds = new long[cats.size()];
            String[] catNames = new String[cats.size()];
            for (int i = 0; i < cats.size(); i++) {
                catNames[i] = cats.get(i).getName();
                categoryIds[i] = cats.get(i).getCategoryId();
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, catNames);
                catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(catAdapter);
            });
        });
    }

    private void saveFood() {
        String name = actvFoodName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String expiryDaysStr = etExpiryDays.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入食材名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "请输入数量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(expiryDaysStr)) {
            Toast.makeText(this, "请输入保质期天数", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);
        int expiryDays = Integer.parseInt(expiryDaysStr);
        String unit = spinnerUnit.getSelectedItem() != null
                ? spinnerUnit.getSelectedItem().toString() : "个";

        long categoryId;
        if (spinnerCategory.getSelectedItemPosition() >= 0 && categoryIds != null) {
            categoryId = categoryIds[spinnerCategory.getSelectedItemPosition()];
        } else {
            categoryId = 5; // default: 其他
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(purchaseDate);
        cal.add(Calendar.DAY_OF_YEAR, expiryDays);
        long expiryDate = cal.getTimeInMillis();

        FoodItem food = new FoodItem(userId, categoryId, name, quantity, unit,
                purchaseDate, expiryDate);

        if (!TextUtils.isEmpty(priceStr)) {
            food.setPrice(Double.parseDouble(priceStr));
        }
        if (!TextUtils.isEmpty(etNotes.getText())) {
            food.setNotes(etNotes.getText().toString().trim());
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            db.foodItemDao().insert(food);
            runOnUiThread(() -> {
                Toast.makeText(this, "食材 " + name + " 已入库", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void setupHeader(String title) {
        TextView tv = findViewById(R.id.tv_page_title);
        if (tv != null) tv.setText(title);
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
