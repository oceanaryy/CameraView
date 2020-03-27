package com.serenegiant.obpreview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.serenegiant.common.BaseActivity;

public class payokActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //하단바 제거
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //상단바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.soface_payok);

        Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext()
                            , MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            Handler mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 3000);

        }
    /* 뒤로가기 물리버튼 막기 */
    @Override
    public void onBackPressed() { //super.onBackPressed(); }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_HOME:
                finish();
                System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
