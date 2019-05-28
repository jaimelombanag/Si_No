package com.rightway.si_no;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button btn_si;
    private Button btn_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_si = (Button) findViewById(R.id.btn_si);
        btn_no = (Button) findViewById(R.id.btn_no);

    }



    public void BotonSi(View v){

        Log.i("SINO", "==============   "    +   btn_si.getBackground());

        if(btn_si.getBackground().equals(R.drawable.boton_si)){
            btn_si.setBackgroundResource(R.drawable.boton_si_over);
        }else{
            btn_si.setBackgroundResource(R.drawable.boton_si);
        }

    }

    public void BotonNo(View v){

        Log.i("SINO", "==============   "    +   btn_no.getBackground());

        if(btn_no.getBackground().equals(R.drawable.boton_no)){
            btn_no.setBackgroundResource(R.drawable.boton_no_over);
        }else{
            btn_no.setBackgroundResource(R.drawable.boton_no);
        }
    }
}
