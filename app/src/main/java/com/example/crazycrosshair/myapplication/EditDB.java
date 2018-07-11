package com.example.crazycrosshair.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;




import sqlite.DataBase;

import static sqlite.DataBase.KEY_AGE;
import static sqlite.DataBase.KEY_ANIM;
import static sqlite.DataBase.KEY_GENDER;
import static sqlite.DataBase.KEY_ID;
import static sqlite.DataBase.KEY_NAME;
import static sqlite.DataBase.KEY_TYPE;
import static sqlite.DataBase.TABLE_ANIM;
import static sqlite.DataBase.TABLE_PRO;

public class EditDB extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean update;
    DataBase db;
    private int updateid;
    DataPoint[] datapoints;
    String mounth_onspin;
    String year_onspin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_db);
        db = new DataBase(this);
        mounth_onspin = "January";
        year_onspin = "2018";
        Intent intent = getIntent();
        TextView editname = (TextView) findViewById(R.id.edit_editText_name);
        TextView editage = (TextView) findViewById(R.id.edit_editText_age);
        TextView editgender = (TextView) findViewById(R.id.edit_editText_gender);
        TextView edittype = (TextView) findViewById(R.id.edit_editText_type);
        Spinner mounth_spin = findViewById(R.id.spinner);
        Spinner year_spin = findViewById(R.id.spinner2);

        LinearLayout ll_spin = findViewById(R.id.ll_spin);
        SQLiteDatabase database = db.getReadableDatabase();
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(19);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        if (intent.getStringExtra("name") == null){
            update = false;
            Button edit_pro_but = findViewById(R.id.edit_property_but);
            edit_pro_but.setVisibility(View.GONE);
            graph.setVisibility(View.GONE);
            ll_spin.setVisibility(View.GONE);

        }
        else {
            update = true;
            findid(database, intent.getStringExtra("name"), intent.getStringExtra("age"), intent.getStringExtra("gender"), intent.getStringExtra("type"));
            db.close();
            editname.setText(intent.getStringExtra("name"));
            editage.setText(intent.getStringExtra("age"));
            editgender.setText(intent.getStringExtra("gender"));
            edittype.setText(intent.getStringExtra("type"));
            year_spin.setOnItemSelectedListener(this);
            mounth_spin.setOnItemSelectedListener(this);


        }



    }
    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View itemSelected, int selectedItemPosition, long selectedId) {
        switch (parent.getId()) {
            case (R.id.spinner):
                String[] choose = getResources().getStringArray(R.array.mounth);
                mounth_onspin = choose[selectedItemPosition];
                showgraph();
                break;
            case (R.id.spinner2):
                String[] choose2 = getResources().getStringArray(R.array.year);
                year_onspin = choose2[selectedItemPosition];
                showgraph();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void showgraph() {
        db = new DataBase(this);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.query(TABLE_PRO, null, KEY_ANIM + "='" + updateid + "'", null, null, null, null);
        cursor.moveToFirst();
        String[] columns = cursor.getColumnNames();
        if (cursor.getCount() > 0) {
            datapoints = new DataPoint[cursor.getCount()];
            for (int i = 2; i < columns.length; i++) {
                int k = 0;
                if (!columns[i].equals(DataBase.KEY_ID) && !columns[i].equals(DataBase.KEY_ANIM)) {


                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                        String date = cursor.getString(1);
                        String[] lines = date.split(" ");
                        String mounth = lines[0];
                        String day = lines[1];
                        double value = Double.parseDouble(day);
                        String year = lines[2];

                        if (mounth.equals(mounth_onspin) && year.equals(year_onspin)) {
                            datapoints[k] = new DataPoint(value, cursor.getInt(i));
                            k++;
                        }


                    }
                    if (datapoints[0] != null) {
                        sortdata(datapoints);
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datapoints);
                        series.setTitle(columns[i]);
                        graph.addSeries(series);
                    }
                }


            }
        }
        cursor.close();
        db.close();
    }

    private void sortdata(DataPoint[] datapoints) {
        for (int i = 0; i < datapoints.length; i++) {

        double min = datapoints[i].getX();
        int min_i = i;
        for (int j = i+1; j < datapoints.length; j++) {

            if (datapoints[j].getX() < min) {
                min = datapoints[j].getX();
                min_i = j;
            }
        }

            if (i != min_i) {
                DataPoint tmp = datapoints[i];
                datapoints[i] = datapoints[min_i];
                datapoints[min_i] = tmp;
            }
        }
    }


    public void findid(SQLiteDatabase db, String name,String age,String gender,String type){
        Cursor cursor = db.query(TABLE_ANIM, new String[] {KEY_ID},KEY_NAME + "='" + name + "' AND " + KEY_AGE + "='" + age + "' AND " + KEY_GENDER + "='" + gender + "' AND " + KEY_TYPE  + "='" + type + "'" ,null,null,null,null);
        cursor.moveToFirst();
        updateid = cursor.getInt(0);
        cursor.close();
    }
    public void Save_but_Clicked(View view){
        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        TextView editname = (TextView) findViewById(R.id.edit_editText_name);
        TextView editage = (TextView) findViewById(R.id.edit_editText_age);
        TextView editgender = (TextView) findViewById(R.id.edit_editText_gender);
        TextView edittype = (TextView) findViewById(R.id.edit_editText_type);

    if (update){
        contentValues.put(db.KEY_NAME,editname.getText().toString());
        contentValues.put(db.KEY_AGE,editage.getText().toString());
        contentValues.put(db.KEY_GENDER,editgender.getText().toString());
        contentValues.put(db.KEY_TYPE,edittype.getText().toString());
        database.update(TABLE_ANIM,contentValues, db.KEY_ID + "= " + updateid , null);
        super.onBackPressed();

    } else{

        contentValues.put(db.KEY_NAME,editname.getText().toString());
        contentValues.put(db.KEY_AGE,editage.getText().toString());
        contentValues.put(db.KEY_GENDER,editgender.getText().toString());
        contentValues.put(db.KEY_TYPE,edittype.getText().toString());

        database.insert(TABLE_ANIM, null, contentValues);

    }
        db.close();
        super.onBackPressed();
    }

    public void Close_but_Clicked(View view){

        super.onBackPressed();
    }

    public void Property_but_Clicked(View view) {
        Intent intent = new Intent(this, Property_Edit.class);
        intent.putExtra("updateid", updateid);
        startActivity(intent);
    }
}
