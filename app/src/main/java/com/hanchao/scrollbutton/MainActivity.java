package com.hanchao.scrollbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hanchao.scrollbutton.btn.ScrollHorizontalRightButton;

public class MainActivity extends AppCompatActivity {

    ScrollHorizontalRightButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.scroll_bt);

        button.setOnScrollListener(new ScrollHorizontalRightButton.OnScrollRightListener() {
            @Override
            public void onScrollingMoreThanCritical() {

            }

            @Override
            public void onScrollingLessThanCriticalX() {

            }

            @Override
            public void onSlideFinishSuccess() {
                Toast.makeText(MainActivity.this, "onSlideFinishSuccess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSlideFinishCancel() {
                Toast.makeText(MainActivity.this, "onSlideFinishCancel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}