package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.MainActivity;
import im.im1020.R;
import im.im1020.model.Model;
import im.im1020.model.bean.UserInfo;
import im.im1020.utils.ShowToast;

import static im.im1020.utils.ShowToast.showUI;

public class LoginActivity extends AppCompatActivity {


    @InjectView(R.id.login_et_username)
    EditText loginEtUsername;
    @InjectView(R.id.login_et_password)
    EditText loginEtPassword;
    @InjectView(R.id.login_btn_register)
    Button loginBtnRegister;
    @InjectView(R.id.login_btn_login)
    Button loginBtnLogin;
    private String password;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.login_btn_register, R.id.login_btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_register:
                if (validate()) {

                    Model.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            //去服务器注册
                            try {
                                EMClient.getInstance()
                                        .createAccount(username, password);

                                ShowToast.showUI(LoginActivity.this,
                                        "注册成功");
                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                showUI(LoginActivity.this,
                                        "注册失败" + e.getMessage());
                            }
                        }
                    });

                }
                break;
            case R.id.login_btn_login:
                if (validate()) {
                    Model.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            EMClient.getInstance().login(username, password, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    //登录成功后需要的处理
                                    Model.getInstance().loginSuccess(EMClient.getInstance().getCurrentUser());
                                    //将用户的数据保存到数据
                                    Model.getInstance().getAccountDao()
                                            .addAccount(new UserInfo(EMClient.getInstance().getCurrentUser()));
                                    //跳转
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束
                                    finish();
                                }

                                @Override
                                public void onError(int i, String s) {
                                    ShowToast.showUI(LoginActivity.this,
                                            "登录失败" + s);

                                }

                                @Override
                                public void onProgress(int i, String s) {

                                    ShowToast.showUI(LoginActivity.this, "登录成功");


                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    private boolean validate() {
        password = loginEtPassword.getText().toString().trim();
        username = loginEtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(username)) {
            ShowToast.show(this, "账号或者密码为空");
            return false;
        }
        return true;

    }
}
