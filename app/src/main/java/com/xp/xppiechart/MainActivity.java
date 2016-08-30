package com.xp.xppiechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xp.xppiechart.view.CustomPieChartView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CustomPieChartView pieChartViewAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieChartViewAnimation = (CustomPieChartView) findViewById(R.id.pie_chart);
    }

    public void dianji(View view){
        initData();
    }

    private void initData() {
        List<CustomPieChartView.CakeValue> itemBeanList = new ArrayList<>();
        itemBeanList.add(new CustomPieChartView.CakeValue("#FABD3B",0.1f));
        itemBeanList.add(new CustomPieChartView.CakeValue("#F9943C",200));
        itemBeanList.add(new CustomPieChartView.CakeValue("#FFD822",3));
        itemBeanList.add(new CustomPieChartView.CakeValue("#F7602B",400));
        pieChartViewAnimation.setData(itemBeanList);
    }
}
