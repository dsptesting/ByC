package com.nap.bycab.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nap.bycab.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinner1;
    private ArrayList<String> list;
    private int pos;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUi();
    }

    private void initUi() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
        }

        list = new ArrayList<>();
        list.add("Pickup/Dropoff");
        list.add("Delivery");
        list.add("Driver");
        list.add("Service");
        list.add("Select Service");

        spinner1 = (Spinner) findViewById(R.id.spinner1);

        final HintAdapter adapter = new HintAdapter(this, list, R.layout.item_spinner_outter);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);

        pos = adapter.getCount();
        setSpinnerSelectionWithoutCallingListener(spinner1, pos);


        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

            }
        });
    }

    private void setSpinnerSelectionWithoutCallingListener(final Spinner spinner, final int selection) {

        final AdapterView.OnItemSelectedListener l = spinner.getOnItemSelectedListener();

        spinner.setOnItemSelectedListener(null);

        spinner.post(new Runnable() {
            @Override
            public void run() {

                spinner.setSelection(selection, false);

                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(spinner1 != null)
            spinner1.post(new Runnable() {
            @Override
            public void run() {
                spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());
            }
        });
    }

    /**
     *  Selection listener supplied to spinner.
     */
    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
/*
            String fileText = readFromFile(new File(getActivity().getFilesDir(), list.get(position)));

            if (fileText != null && fileText.length() > 0) {

                Log.v("Try",""+fileText);

                tvFileText.setText("" + fileText);
            }*/
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class HintAdapter extends ArrayAdapter<String> {

        List<String> objects;

        public HintAdapter(Context theContext, List<String> objects, int theLayoutResId) {
            super(theContext, theLayoutResId, objects);
            this.objects = objects;
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
/*

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.item_spinner, parent, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.tvSpnText);
            main_text.setText(objects.get(position));

            return mySpinner;
        }
*/

    }


}
