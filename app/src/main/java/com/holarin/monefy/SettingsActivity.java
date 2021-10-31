package com.holarin.monefy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class SettingsActivity extends AppCompatActivity {

    ArrayList<Flow> flows;
    MonefyModel mMonefyModel;
    TextView startDate;
    TextView endDate;
    EditText moneyBudget;

    Calendar firstDate;
    Calendar secondDate;
    DatePickerDialog.OnDateSetListener setListenerStart;
    DatePickerDialog.OnDateSetListener setListenerEnd;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     *  вызов календаря при клике на кнопки
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        moneyBudget = findViewById(R.id.moneyBudget);
        Calendar calendar = new GregorianCalendar();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        startDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SettingsActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListenerStart, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        setListenerStart = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = (dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth)  + "." + (month1 >= 10 ? month1 : "0" + month1) + "." + year1;
            startDate.setText(date);
            firstDate = new GregorianCalendar(year1, month1, dayOfMonth);
        };

        endDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SettingsActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListenerEnd, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        setListenerEnd = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = (dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth)  + "." + (month1 >= 10 ? month1 : "0" + month1) + "." + year1;
            endDate.setText(date);
           secondDate = new GregorianCalendar(year1, month1, dayOfMonth);
            System.out.println(year1);
        };
        loadData();
        //Здесь ниже опять установка нового recycler view, для топ трат
        ArrayList<Flow> recyclerFlows = getFlows();
        mRecyclerView = findViewById(R.id.flowTopRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FlowAdapter(recyclerFlows);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacerItem(32)); // <-- опять же отступы между элементами
    }


    public ArrayList<Flow> getFlows() {
        ArrayList<Flow> realFlow = new ArrayList<>();
        for (Flow flow : flows) {
            if (flow.calendar.after(mMonefyModel.start) && flow.calendar.before(mMonefyModel.end)) {
                boolean isFound = false;
                for (Flow flow1 : realFlow) {
                    if (flow.description.equalsIgnoreCase(flow1.description)) { //Если нашли дубликат, то суммируем деньги и не добавляем его заново
                        flow1.money += flow.money;
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) { //Если не нашли дубликат, то добавляем трату в список
                    realFlow.add(flow);
                }
            }
        }
        Collections.sort(realFlow, new CustomComparator()); //Здесь происходит сортировка с помощью компаратора
        return realFlow;
    }


    public class CustomComparator implements Comparator<Flow> {
        @Override
        public int compare(Flow o1, Flow o2) {
            return ((int)(o2.money - o1.money));
        }
    }

    /**
     * Это сохранение бюджета, вначале получаем число из текстового поля, если оно больше нуля, то
     * меняем значение в модели и сохраняем её, о чём сообщаем.
     * */
    public void onSaveBudgetClick(View view) {
        if (Double.parseDouble(moneyBudget.getText().toString()) >= 0) {
            mMonefyModel.maxMoney = Double.parseDouble(moneyBudget.getText().toString());
            saveData();
            Toast.makeText(this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
        }
    }


    public void onSaveClick(View view) {
        if (firstDate != null && secondDate != null) {
            if (firstDate.before(secondDate)) {
                mMonefyModel.start = firstDate;
                mMonefyModel.end = secondDate;
                saveData();
                Toast.makeText(this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Начальная дата должна быть раньше конечной", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Ниже методы, которые уже разбирались, они такие же, только уже под специфичные условия этого Activity
     * */
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mMonefyModel);
        editor.putString("model", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flows", null);
        Type type = new TypeToken<ArrayList<Flow>>() {}.getType();
        flows = gson.fromJson(json, type);

        String modelJson = sharedPreferences.getString("model", null);
        mMonefyModel = gson.fromJson(modelJson, MonefyModel.class);

        if (mMonefyModel == null) {
            mMonefyModel = new MonefyModel();
        }

        if(flows == null) {
            flows = new ArrayList<>();
        }
    }
}