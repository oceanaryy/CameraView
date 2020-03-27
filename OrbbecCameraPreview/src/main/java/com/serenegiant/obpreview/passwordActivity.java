package com.serenegiant.obpreview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.serenegiant.common.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class passwordActivity extends BaseActivity {

    //비밀번호 담을 공간 / 4자이므로 사이즈 4로 생성 - String은 메모리에 남아서 사용하면 안됨
    static char ch[] = new char[4];
    Button vnum1,vnum2,vnum3,vnum4,vnum5,vnum6,vnum7,vnum8,vnum9,vnum0,vpopups;
    ImageButton allRemove,back;
    /*static ImageView iv1 = (ImageView)  ((Activity)c).findViewById(R.id.passwordchk1);
    static ImageView iv2 = (ImageView)  ((Activity)c).findViewById(R.id.passwordchk2);
    static ImageView iv3 = (ImageView)  ((Activity)c).findViewById(R.id.passwordchk3);
    static ImageView iv4 = (ImageView)  ((Activity)c).findViewById(R.id.passwordchk4);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //하단바 제거
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //상단바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        java.util.Arrays.fill(ch, (char)0x20);
        setContentView(R.layout.soface_password);

        vnum1 = (Button) findViewById(R.id.num1);
        vnum2 = (Button) findViewById(R.id.num2);
        vnum3 = (Button) findViewById(R.id.num3);
        vnum4 = (Button) findViewById(R.id.num4);
        vnum5 = (Button) findViewById(R.id.num5);
        vnum6 = (Button) findViewById(R.id.num6);
        vnum7 = (Button) findViewById(R.id.num7);
        vnum8 = (Button) findViewById(R.id.num8);
        vnum9 = (Button) findViewById(R.id.num9);
        vnum0 = (Button) findViewById(R.id.num0);
        allRemove = (ImageButton) findViewById(R.id.numallremove);
        back = (ImageButton) findViewById(R.id.numback);
        vpopups = (Button) findViewById(R.id.popups);

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.num1 :
                        buttonProcess(vnum1);
                        break;
                    case R.id.num2 :
                        buttonProcess(vnum2);
                        break;
                    case R.id.num3 :
                        buttonProcess(vnum3);
                        break;
                    case R.id.num4 :
                        buttonProcess(vnum4);
                        break;
                    case R.id.num5 :
                        buttonProcess(vnum5);
                        break;
                    case R.id.num6 :
                        buttonProcess(vnum6);
                        break;
                    case R.id.num7 :
                        buttonProcess(vnum7);
                        break;
                    case R.id.num8 :
                        buttonProcess(vnum8);
                        break;
                    case R.id.num9 :
                        buttonProcess(vnum9);
                        break;
                    case R.id.num0 :
                        buttonProcess(vnum0);
                        break;
                }

            }
        };


        ImageButton.OnClickListener onClickListener2 = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.numback :
                        buttonProcess(back);
                        break;
                    case R.id.numallremove :
                        buttonProcess(allRemove);
                        break;
                }

            }
        };
        vnum1.setOnClickListener(onClickListener);
        vnum2.setOnClickListener(onClickListener);
        vnum3.setOnClickListener(onClickListener);
        vnum4.setOnClickListener(onClickListener);
        vnum5.setOnClickListener(onClickListener);
        vnum6.setOnClickListener(onClickListener);
        vnum7.setOnClickListener(onClickListener);
        vnum8.setOnClickListener(onClickListener);
        vnum9.setOnClickListener(onClickListener);
        vnum0.setOnClickListener(onClickListener);
        allRemove.setOnClickListener(onClickListener2);
        back.setOnClickListener(onClickListener2);
    }

    /* 뒤로가기 물리버튼 막기 */
    @Override
    public void onBackPressed() { //super.onBackPressed(); }
    }

    public void buttonProcess(ImageButton b) {

        //Toast.makeText(getApplicationContext(),s + "========" + ch[0] + "====" + ch[1] + "====" + ch[2]+ "====" + ch[3] + "+++++" + s.charAt(0) , Toast.LENGTH_SHORT).show();
        ImageView iv1 = (ImageView)  findViewById(R.id.passwordchk1);
        ImageView iv2 = (ImageView)  findViewById(R.id.passwordchk2);
        ImageView iv3 = (ImageView)  findViewById(R.id.passwordchk3);
        ImageView iv4 = (ImageView)  findViewById(R.id.passwordchk4);
        //전체삭제 클릭 시
        if(b == findViewById(R.id.numallremove)){
            iv1.setImageResource(R.drawable.passwordoff);
            iv2.setImageResource(R.drawable.passwordoff);
            iv3.setImageResource(R.drawable.passwordoff);
            iv4.setImageResource(R.drawable.passwordoff);
            java.util.Arrays.fill(ch, (char)0x20);
        }

        //하나만 지우기 클릭 시
        if(b == findViewById(R.id.numback)) {
            if (ch[0] == ' ' ) {
            } else if (ch[1] == ' ' ) {
                ch[0] = ' ';
                iv1.setImageResource(R.drawable.passwordoff);
            } else if (ch[2] == ' ' ) {
                ch[1] = ' ';
                iv2.setImageResource(R.drawable.passwordoff);
            } else if (ch[3] == ' ' ) {
                ch[2] = ' ';
                iv3.setImageResource(R.drawable.passwordoff);
            }
        }
    }

    public void buttonProcess(Button b){
        //4개의 숫자 입력받음
        String s = b.getText().toString();
        //Toast.makeText(getApplicationContext(),s + "========" + ch[0] + "====" + ch[1] + "====" + ch[2]+ "====" + ch[3] + "+++++" + s.charAt(0) , Toast.LENGTH_SHORT).show();
        ImageView iv1 = (ImageView)  findViewById(R.id.passwordchk1);
        ImageView iv2 = (ImageView)  findViewById(R.id.passwordchk2);
        ImageView iv3 = (ImageView)  findViewById(R.id.passwordchk3);
        ImageView iv4 = (ImageView)  findViewById(R.id.passwordchk4);

        //각각 비교
        if(ch[0]==' '){
            ch[0] = s.charAt(0);
            iv1.setImageResource(R.drawable.passwordon);
        } else if(ch[1] == ' '){
            ch[1] = s.charAt(0);
            iv2.setImageResource(R.drawable.passwordon);
        } else if(ch[2] == ' '){
            ch[2] = s.charAt(0);
            iv3.setImageResource(R.drawable.passwordon);
        } else if(ch[3] == ' '){
            ch[3] = s.charAt(0);
            iv4.setImageResource(R.drawable.passwordon);
            //비밀번호 틀리면 toast와 함께 입력받았던 데이터들 초기화
            String result =  ""+ch[0]+ch[1]+ch[2]+ch[3]; //평문
            String hex="";
            //암호화 시작
            try {
                //개발서버 salt값
                String salt = "69C2E0544F753B45C12026A5743502603B6057C677528FAC408F7A7A9EF40F96";
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt.getBytes());
                md.update(result.getBytes());
                byte byteData[] = md.digest();
                StringBuffer sb = new StringBuffer();
                for(int i = 0 ; i < byteData.length ; i++) sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
                hex = sb.toString();
                Log.e("hexxxxxxxxxxxxxxxxxx", hex );
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("data", "암호화 오류입니다. 잠시후에 다시 시도해주세요.");
                startActivityForResult(intent, 1);
            }


            //실제 서버에서 받아온 값이랑 비교해야 함
            if(!hex.equals("ea0a671ae3293b6f584f6f923a0ec0bcffea823bba0e6c9ab3d16c36a12ab8b2")){
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("data", "비밀번호 오류입니다. \n 다시 입력해주세요.");
                startActivityForResult(intent, 1);
                iv1.setImageResource(R.drawable.passwordoff);
                iv2.setImageResource(R.drawable.passwordoff);
                iv3.setImageResource(R.drawable.passwordoff);
                iv4.setImageResource(R.drawable.passwordoff);
                java.util.Arrays.fill(ch, (char)0x20);
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;
                boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
                //상단바 제거
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                Intent intent = new Intent(getApplicationContext(), payokActivity.class);
                startActivity(intent);
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;
                boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
                //상단바 제거
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }
    //취소 버튼 클릭
    public void mOnCancel(View v){
        Intent intent = new Intent(this, PopupSelectActivity.class);
        intent.putExtra("data", "결제를 취소하시겠습니까?");
        startActivityForResult(intent, 1);
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
