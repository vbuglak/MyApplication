package com.example.crazycrosshair.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import sqlite.DataBase;

public class Property_Edit extends AppCompatActivity {
    TextView currentDateTime;
    Calendar dateAndTime=Calendar.getInstance();
    DataBase db;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            400, LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_edit);
        currentDateTime=(TextView)findViewById(R.id.datetime);
        setInitialDateTime();
        db = new DataBase(this);
        initpro();
    }

    private void initpro() {
        SQLiteDatabase datebase = db.getReadableDatabase();
        Cursor cursor = datebase.query(DataBase.TABLE_PRO, null, DataBase.KEY_ANIM + "= " + getIntent().getStringExtra("updateid"),null, null, null, null);
        cursor.moveToFirst();
        String[] columns = cursor.getColumnNames();
        for(int i=0;i<columns.length;i++){
            if(!columns[i].equals(DataBase.KEY_ID) && !columns[i].equals(DataBase.KEY_DATE) && !columns[i].equals(DataBase.KEY_ANIM) )
                addnewpro(columns[i]);


        }


    }

    private void addnewpro(String column) {
        LinearLayout ll = new LinearLayout(this);
        TextView tv1 = new TextView(this);
        EditText et1 = new EditText(this);
        LinearLayout rawll = (LinearLayout) findViewById(R.id.raw_ll);
        tv1.setText(column);
        tv1.setLayoutParams(params);
        et1.setLayoutParams(params);
        et1.setInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(tv1);
        ll.addView(et1);
        rawll.addView(ll);

    }

    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        ));
    }
    public void pro_close_clicked(View view) {


        super.onBackPressed();
    }

    public void pro_add_clicked(View view) {

        LinearLayout rawll = (LinearLayout) findViewById(R.id.raw_ll);
        final LinearLayout ll = new LinearLayout(this);
        final EditText ed1 = new EditText(this);
        Button confirm_but = new Button(this);
        ed1.setLayoutParams(params);
        ed1.setHint("Property");
        confirm_but.setLayoutParams(params);
        confirm_but.setText("Confirm");
        ll.addView(ed1);
        ll.addView(confirm_but);
        rawll.addView(ll);
        confirm_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean duplicate = false;
            if (ed1.getText().length()==0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Property_Edit.this);
                builder = builder.setTitle("Ошибка")
                        .setMessage("Пустое поле")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else{
                SQLiteDatabase database = db.getWritableDatabase();

                Cursor cursor = database.query(DataBase.TABLE_PRO, null,
                        DataBase.KEY_ID + "= " + getIntent().getIntExtra("id", 0),
                        null, null, null, null);
                cursor.moveToFirst();
                String[] columns = cursor.getColumnNames();
                cursor.close();
                for (int j = 0; j < columns.length; j++) {
                    if (columns[j].equals(ed1.getText().toString())) {
                        duplicate = true;
                    }
                }
                if (duplicate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Property_Edit.this);
                    builder = builder.setTitle("Ошибка")
                            .setMessage("Такое свойство есть в бд")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {

                    database.execSQL("ALTER TABLE " + DataBase.TABLE_PRO + " ADD COLUMN " + ed1.getText().toString() + " INTEGER ");
                    db.close();
                    String property = ed1.getText().toString();
                    ll.removeAllViewsInLayout();
                    TextView tv1 = new TextView(Property_Edit.this);
                    tv1.setText(property);
                    tv1.setLayoutParams(params);
                    EditText ed2 = new EditText(Property_Edit.this);
                    ed2.setLayoutParams(params);
                    ll.addView(tv1);
                    ll.addView(ed2);

                }
            }
            }
        });


    }

    public void datetime_clicked(View view) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setInitialDateTime();
    }
    };

    public void pro_save_clicked(View view) {
        ContentValues contentvalues = new ContentValues();
        SQLiteDatabase datebase = db.getReadableDatabase();


        LinearLayout rawll = findViewById(R.id.raw_ll);
        for (int i = 1; i < rawll.getChildCount(); i++) {
            LinearLayout tmpl = (LinearLayout) rawll.getChildAt(i);
            TextView tmptw = (TextView) tmpl.getChildAt(0);
            EditText tmped = (EditText) tmpl.getChildAt(1);
            contentvalues.put(tmptw.getText().toString(), tmped.getText().toString());
            }
            contentvalues.put(DataBase.KEY_ANIM,getIntent().getIntExtra("updateid",0));
            contentvalues.put(DataBase.KEY_DATE,currentDateTime.getText().toString().replaceAll(",",""));
            Cursor cursor = datebase.query(DataBase.TABLE_PRO,null,
                    DataBase.KEY_ANIM + " = " + getIntent().getIntExtra("updateid",0)
                        + " AND " + DataBase.KEY_DATE + " = '" +
                        currentDateTime.getText().toString().replaceAll(",","")+"'",null,
                null,null,null);
            if (!cursor.moveToFirst()){
                datebase.insert(DataBase.TABLE_PRO,null,contentvalues);
            }
            else {
                datebase.update(DataBase.TABLE_PRO,contentvalues,
                        DataBase.KEY_ANIM + " = " +
                            getIntent().getIntExtra("updateid",0)
                            + " AND " + DataBase.KEY_DATE + " ='" +
                            currentDateTime.getText().toString().replaceAll(",","")+"'",null);
            cursor.close();
            db.close();
            }
            }
}

