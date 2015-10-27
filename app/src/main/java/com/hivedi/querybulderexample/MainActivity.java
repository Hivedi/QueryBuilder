package com.hivedi.querybulderexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hivedi.querybuilder.QueryBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QueryBuilder qb = new QueryBuilder();
        qb.from("table").where("field=?").addParam(1);

        Log.i("tests", "SQL: " + qb.getSelect());
    }

}
