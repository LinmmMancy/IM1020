package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.chat.EMClient;

import im.im1020.MainActivity;
import im.im1020.R;
import im.im1020.model.Model;

public class WelcomeActiviy extends AppCompatActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                //进入主页面或登录界面
                enterMainOrLogin();


            }
        }

    };

    private void enterMainOrLogin() {

        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器获取是否登录
                boolean loggedInbefore = EMClient.getInstance().isLoggedInBefore();


                if (loggedInbefore) {
                    //登录成功后需要的处理
                    Model.getInstance().loginSuccess(EMClient.getInstance().getCurrentUser());
                    //登录过
                    Intent intent = new Intent(WelcomeActiviy.this, MainActivity.class);
                    startActivity(intent);

                    //结束当前页面
                    finish();
                } else {
                    //没有登录
                    //跳转到登录页面
                    Intent intent = new Intent(WelcomeActiviy.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activiy);


        //发送延迟消息
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除messages
        handler.removeCallbacksAndMessages(null);
    }
}
