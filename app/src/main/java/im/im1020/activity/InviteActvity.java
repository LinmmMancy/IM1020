package im.im1020.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.R;
import im.im1020.model.Model;
import im.im1020.utils.ShowToast;

public class InviteActvity extends AppCompatActivity {

    @InjectView(R.id.invite_btn_search)
    Button inviteBtnSearch;


    @InjectView(R.id.invite_tv_username)
    TextView inviteTvUsername;
    @InjectView(R.id.invite_btn_add)
    Button inviteBtnAdd;
    @InjectView(R.id.invite_ll_item)
    LinearLayout inviteLlItem;
    @InjectView(R.id.invite_et_search)
    EditText inviteEtSearch;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_actvity);
        ButterKnife.inject(this);


    }

    @OnClick({R.id.invite_btn_search, R.id.invite_btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invite_btn_search:


                //搜索

                //验证

                if (validate()) {

                    //显示搜索结果
                    inviteLlItem.setVisibility(View.VISIBLE);

                    inviteTvUsername.setText(username);

                } else {
                    inviteLlItem.setVisibility(View.GONE);
                }
                break;


            case R.id.invite_btn_add:


                //添加

                Model.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        //去环信服务器添加好友

                        //参数为要添加的好友的 username 添加理由

                        try {
                            EMClient.getInstance().contactManager()
                                    .addContact(username, "添加好友");
                            ShowToast.showUI(InviteActvity.this, "添加好友成功");
                        } catch (HyphenateException e) {
                            e.printStackTrace();

                            ShowToast.showUI(InviteActvity.this, "添加好友失败" + e.getMessage());
                        }
                    }
                });

                break;
        }
    }

    private boolean validate() {
        //本地验证

        username = inviteEtSearch.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ShowToast.show(this, "用户名不能为空");
            return false;
        }

        //服务器验证
        return true;


    }
}
