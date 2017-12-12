package com.whb.ijkplayerw;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import whb.com.ijklibrary.widget.IjkVideoView;
import whb.com.ijklibrary.widget.SkinController;


public class MainActivity extends AppCompatActivity {
    IjkVideoView ijkVideoView;
    SkinController skinController;
    RelativeLayout rlv_content;

    int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ijkVideoView=findViewById(R.id.view_ijkvideoView);
        rlv_content=findViewById(R.id.rlv_content);


        screenWidth = getResources().getDisplayMetrics().widthPixels;
        rlv_content.getLayoutParams().height = (int) (((float) 9 / 16) * screenWidth);
        skinController=new SkinController(this)
                .withActivity(this)
                .setMediaPlayer(ijkVideoView)
                .setTitle("测试")
                .setPath("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
        rlv_content.addView(skinController);
        skinController.startPlayer();
    }

    /**
     * 屏幕失去焦点
     */
    public void onPause() {
        skinController.onPause();
        super.onPause();
    }

    /**
     * 重新获得焦点
     */
   public void onResume() {
       skinController.onResume();
       super.onResume();
   }

    @Override
    protected void onDestroy() {
        skinController.onDestory();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        skinController.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandScapeView();
        } else {
            setPortraitView();
        }
    }

    /**
     * 竖屏
     */
    public void setLandScapeView(){
        rlv_content.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
    }

    /**
     * 横屏
     */
    public void setPortraitView(){
        rlv_content.getLayoutParams().height = (int) (((float) 9 / 16) * screenWidth);
    }


    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            super.onBackPressed();
        }
    }
}
