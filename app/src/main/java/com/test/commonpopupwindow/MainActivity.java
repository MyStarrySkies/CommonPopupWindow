package com.test.commonpopupwindow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startWindow(View view) {
        CommonPopupWindow window = new CommonPopupWindow(R.layout.ppw_test,view);
        window.getBuilder().setShadow(this,0.8f).create().show();
        window.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
