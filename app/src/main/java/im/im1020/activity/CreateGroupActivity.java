package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.R;
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
            startActivity(intent);
        }
    }

    private boolean Validate() {

        String desc = etNewgroupDesc.getText().toString().trim();
        String groupname = etNewgroupName.getText().toString().trim();

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
