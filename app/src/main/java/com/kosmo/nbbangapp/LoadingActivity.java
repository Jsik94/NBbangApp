package com.kosmo.nbbangapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.kosmo.nbbangapp.databinding.ActivityLoadingBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private ActivityLoadingBinding activityLoadingBinding;
    private AlphaAnimation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        View view = getLayoutInflater().inflate(R.layout.activity_loading,null);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//        );
//        view.setBackgroundColor(Color.rgb(237,160,160));
//
//        params.topMargin = getStatusBarHeight();
//
//        view.setLayoutParams(params);
//        setContentView(view);
        activityLoadingBinding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(activityLoadingBinding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Log.d("CHECKER", "??: ");

//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        startActivity(intent);


    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CHECKER", "onStart: ");
        Thread thread=  new Thread(() -> {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d("CHECKER", "onStart: ");

                        String[] arr = getResources().getStringArray(R.array.tv_loading2);

                        animation = new AlphaAnimation(1.0f, 0.0f);
                        animation.setDuration(1500);
                        animation.setStartOffset(1500);
                        activityLoadingBinding.txtSubtitle.setAnimation(animation);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("CHECKER", "run: ");
                        //fadeIn
                        animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(1500);
                        animation.setStartOffset(1500);
                        activityLoadingBinding.txtSubtitle.setAnimation(animation);
                        activityLoadingBinding.txtSubtitle.setText(arr[1]);

                        Log.d("CHECKER", "onStart: ");
                    }
//                }
            });

        });

        thread.start();

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
//    startActivity(intent);


}

    public int getStatusBarHeight() {
        int result = 0;
        int resourceIdTop = getResources().getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
        );
        if (resourceIdTop > 0) {
            result = getResources().getDimensionPixelSize(resourceIdTop);
        }
        return result;
    }

    public int getNavigationBarHeight() {
        int result = 0;
        int resourceIdBottom = getResources().getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
        );
        if (resourceIdBottom > 0) {
            result = getResources().getDimensionPixelSize(resourceIdBottom);
        }
        return result;
    }
}
