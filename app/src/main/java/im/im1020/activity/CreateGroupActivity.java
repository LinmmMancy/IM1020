package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.R;
import im.im1020.model.Model;
import im.im1020.utils.ShowToast;

public class CreateGroupActivity extends AppCompatActivity {

    @InjectView(R.id.et_newgroup_name)
    EditText etNewgroupName;
    @InjectView(R.id.et_newgroup_desc)
    EditText etNewgroupDesc;
    @InjectView(R.id.cb_newgroup_public)
    CheckBox cbNewgroupPublic;
    @InjectView(R.id.cb_newgroup_invite)
    CheckBox cbNewgroupInvite;
    @InjectView(R.id.bt_newgroup_create)
    Button btNewgroupCreate;
    private String desc;
    private String groupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.inject(this);


    }

    @OnClick(R.id.bt_newgroup_create)
    public void onClick() {

        if (Validate()) {

            //跳转

            Intent intent = new Intent(CreateGroupActivity.this, PickContactActivity.class);

            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            //创建群组
            creatGroup(data);


        }
    }

    private void creatGroup(final Intent data) {

        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {

                try {

                    String[] memberses = data.getStringArrayExtra("members");

                    if (memberses == null) {

                        return;
                    }

                    if (memberses.length == 0) {
                        ShowToast.showUI(CreateGroupActivity.this, "没人还加啥");

                        return;


                    }

                    EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();

                    options.maxUsers = 200;  //群成员数量

                    if (cbNewgroupPublic.isChecked()) {
                        if (cbNewgroupInvite.isChecked()) {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;

                        } else {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;

                        }
                    } else {

                        if (cbNewgroupInvite.isChecked()) {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                        } else {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                        }
                    }

                    EMClient.getInstance().groupManager()
                            .createGroup(groupname, desc, memberses, "", options);

                    ShowToast.showUI(CreateGroupActivity.this, "创建群成功");


                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();

                    ShowToast.showUI(CreateGroupActivity.this, "创建群失败" + e.getMessage());

                }
            }
        });
    }

    private boolean Validate() {

        desc = etNewgroupDesc.getText().toString().trim();
        groupname = etNewgroupName.getText().toString().trim();

        if (TextUtils.isEmpty(desc)) {
            ShowToast.show(this, "群简介不能为空");

            return false;
        }

        if (TextUtils.isEmpty(groupname)) {
            ShowToast.show(this, "群名字不能为空");

            return false;
        }
        return true;


    }
}
