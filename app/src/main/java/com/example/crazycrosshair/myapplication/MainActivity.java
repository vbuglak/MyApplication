package com.example.crazycrosshair.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import sqlite.DataBase;

public class MainActivity extends AppCompatActivity  {

DataBase db;
    private TableRow editrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DataBase(this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditActivity();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 0, Menu.NONE, "Изменить");
        menu.add(Menu.NONE, 1, Menu.NONE, "Удалить");
        editrow = (TableRow) v;
    }

    public boolean onContextItemSelected(MenuItem item) {
        TextView tv1 = (TextView) editrow.getChildAt(0);
        TextView tv2 = (TextView) editrow.getChildAt(1);
        TextView tv3 = (TextView) editrow.getChildAt(2);
        TextView tv4 = (TextView) editrow.getChildAt(3);
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this,EditDB.class);
                intent.putExtra("name", tv1.getText().toString().replaceAll("\\s+",""));
                intent.putExtra("age", tv2.getText().toString().replaceAll("\\s+",""));
                intent.putExtra("gender", tv3.getText().toString().replaceAll("\\s+",""));
                intent.putExtra("type", tv4.getText().toString().replaceAll("\\s+",""));
                startActivity(intent);
                break;
            case 1:
                SQLiteDatabase database = db.getWritableDatabase();
                database.delete(db.TABLE_ANIM, db.KEY_NAME + "= ? AND " + db.KEY_AGE + "= ? AND " +db.KEY_GENDER + "= ? AND " + db.KEY_TYPE + "= ?" , new String[]{tv1.getText().toString().replaceAll("\\s+",""), tv2.getText().toString().replaceAll("\\s+",""), tv3.getText().toString().replaceAll("\\s+",""), tv4.getText().toString().replaceAll("\\s+","")});
                init();
                break;

        }
        return true;
    }
    public void EditActivity (){
        Intent intent = new Intent(this,EditDB.class);
        startActivity(intent);
}

    public void init() {
        SQLiteDatabase database = db.getReadableDatabase();

        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        stk.removeAllViewsInLayout();
        TableRow tbrow0 = new TableRow(this);
        TextView tvr0 = new TextView(this);
        tvr0.setText("  Name  ");
        tvr0.setTextColor(Color.BLACK);
        tvr0.setTextSize(20);
        tbrow0.addView(tvr0);
        TextView tvr1 = new TextView(this);
        tvr1.setText("  Age  ");
        tvr1.setTextColor(Color.BLACK);
        tvr1.setTextSize(20);
        tbrow0.addView(tvr1);
        TextView tvr2 = new TextView(this);
        tvr2.setText("  Gender  ");
        tvr2.setTextColor(Color.BLACK);
        tvr2.setTextSize(20);
        tbrow0.addView(tvr2);
        TextView tvr3 = new TextView(this);
        tvr3.setText("  Type  ");
        tvr3.setTextColor(Color.BLACK);
        tvr3.setTextSize(20);
        tbrow0.addView(tvr3);
        stk.addView(tbrow0);

            Cursor cursor = database.query(db.TABLE_ANIM, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int age = cursor.getColumnIndex(db.KEY_AGE);
                int name = cursor.getColumnIndex(db.KEY_NAME);
                int gender = cursor.getColumnIndex(db.KEY_GENDER);
                int type = cursor.getColumnIndex(db.KEY_TYPE);
                do {
                    TableRow tbrow = new TableRow(this);
                    registerForContextMenu(tbrow);
                    TextView tv1 = new TextView(this);
                    tv1.setText(cursor.getString(name)+ "  ");
                    tv1.setTextColor(Color.WHITE);
                    tv1.setGravity(Gravity.CENTER);
                    tbrow.addView(tv1);
                    TextView tv2 = new TextView(this);
                    tv2.setText("  " +cursor.getString(age)+ "  ");
                    tv2.setTextColor(Color.WHITE);
                    tv2.setGravity(Gravity.CENTER);
                    tbrow.addView(tv2);
                    TextView tv3 = new TextView(this);
                    tv3.setText("  " +cursor.getString(gender)+ "  ");
                    tv3.setTextColor(Color.WHITE);
                    tv3.setGravity(Gravity.CENTER);
                    tbrow.addView(tv3);
                    TextView tv4 = new TextView(this);
                    tv4.setText("  " +cursor.getString(type)+ "  ");
                    tv4.setTextColor(Color.WHITE);
                    tv4.setGravity(Gravity.CENTER);
                    tbrow.addView(tv4);
                    stk.addView(tbrow);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();


    }
}
