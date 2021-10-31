package com.holarin.monefy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddFlowActivity extends AppCompatActivity {

    private Button addButton;
    private EditText price;
    private EditText description;
    ArrayList<Flow> flows = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flow);
        //Ниже мы присваиваем полям соответствующие UI объекты из Activity
        addButton = findViewById(R.id.addFlowButton);
        price = findViewById(R.id.lostSumTextField);
        description = findViewById(R.id.descriptionLostSumTextField);
        loadData();

        mRecyclerView = findViewById(R.id.flowRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Здесь ставим, что у RecyclerView будет фиксированный размер на экране раздувания не будет
        mLayoutManager = new LinearLayoutManager(this); //создание менеджмента слоёв
        mAdapter = new FlowAdapter(flows); //создание адаптера

        mRecyclerView.setLayoutManager(mLayoutManager); //установка менеджмента слоёв
        mRecyclerView.setAdapter(mAdapter); //Установка адаптера
        mRecyclerView.addItemDecoration(new SpacerItem(32)); //Установка спейсеров между элементами (хотите уменьшить/увеличить отступы между элементами)
    }


    public void insert(View view) {
        if (price.getText().toString().equalsIgnoreCase("") || description.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Поля обязательны к заполнению", Toast.LENGTH_LONG).show();
            return;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        flows.add(new Flow(Double.parseDouble(price.getText().toString()), calendar, description.getText().toString()));
        saveData();
        mAdapter.notifyDataSetChanged();
        description.setText("");
        price.setText("");
    }


    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flows);
        editor.putString("flows", json);
        editor.apply();
    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("flows", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flows", null);
        Type type = new TypeToken<ArrayList<Flow>>() {}.getType();
        flows = gson.fromJson(json, type);

        if(flows == null) {
            flows = new ArrayList<>();
        }
    }
}