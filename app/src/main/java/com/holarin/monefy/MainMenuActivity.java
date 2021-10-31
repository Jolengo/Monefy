package com.holarin.monefy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.resources.TextAppearanceFontCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainMenuActivity extends AppCompatActivity {

    ArrayList<Flow> flows = new ArrayList<>();
    MonefyModel mModel = null;
    ProgressBar mProgressBar;
    TextView mMoney;
    TextView mDate;

    /**
     * соединяем прогресс бары и текстовыми полями с теми,
     * что на экране с помощью findViewById
     * Потом загружаем данные и устанавливаем UI, подробнее ниже
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mMoney = findViewById(R.id.moneyTextView);
        mDate = findViewById(R.id.dateTextView);
        mProgressBar = findViewById(R.id.progressBar);
        loadData();
        setUi();
    }


    public void onAddFlowButtonClick(View view) {
        startActivity(new Intent(this, AddFlowActivity.class));
    }


    public void onMoreFlowButtonClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * просто считает все деньги,
     * которые мы потратили, которые находятся между началом и концом по датам, которые мы выбрали
     * */
    double countAllFlows() {
        double counter = 0;
        for (Flow flow : flows) {
            if (flow.calendar.after(mModel.start) && flow.calendar.before(mModel.end)) {
                counter += flow.money;
            }
        }
        return counter;
    }


    /**
     * Это метод, загружает данные, ранее объяснял как именно, из JSON формата с помощью GSON библиотеки */
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flows", null);
        Type type = new TypeToken<ArrayList<Flow>>() {}.getType();
        flows = gson.fromJson(json, type);

        String modelJson = sharedPreferences.getString("model", null);
        mModel = gson.fromJson(modelJson, MonefyModel.class);

        if (mModel == null) {
            mModel = new MonefyModel();
        }

        if(flows == null) {
            flows = new ArrayList<>();
        }
    }

    /**
     * При рестарте активности заного загружаем дату и обновляем UI, так как могли произойти изменения
     * */
    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
        setUi();
    }


    public void setUi() {
        mProgressBar.setMax((int) mModel.maxMoney);
        mProgressBar.setProgress((int) (mModel.maxMoney - countAllFlows()));
        mMoney.setText((int) (mModel.maxMoney - countAllFlows()) + "/" + ((int) mModel.maxMoney));
        mDate.setText(
                (mModel.start.get(Calendar.DAY_OF_MONTH) >= 10 ? mModel.start.get(Calendar.DAY_OF_MONTH) : ("0" + mModel.start.get(Calendar.DAY_OF_MONTH))) + "."
                        + (mModel.start.get(Calendar.MONTH) >= 10 ? mModel.start.get(Calendar.MONTH) : "0" + mModel.start.get(Calendar.MONTH)) + "."
                        + mModel.start.get(Calendar.YEAR) +
                "/" +
                        (mModel.end.get(Calendar.DAY_OF_MONTH) >= 10 ? mModel.end.get(Calendar.DAY_OF_MONTH) : ("0" + mModel.end.get(Calendar.DAY_OF_MONTH))) + "."
                        + (mModel.end.get(Calendar.MONTH) >= 10 ? mModel.end.get(Calendar.MONTH) : "0" + mModel.end.get(Calendar.MONTH)) + "."
                        + mModel.end.get(Calendar.YEAR));
    }

    /**
     * Сохранение, анлогичное, всё в JSON, а при загрузке из JSON в рабочие объекты
     * */
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flows);
        editor.putString("flows", json);
        editor.apply();
    }

}