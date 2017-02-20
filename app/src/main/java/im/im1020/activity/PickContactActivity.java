package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.R;
import im.im1020.adapter.PickAdapter;
import im.im1020.model.Model;
import im.im1020.model.bean.PickInfo;
import im.im1020.model.bean.UserInfo;
import im.im1020.utils.ShowToast;

public class PickContactActivity extends AppCompatActivity {

    @InjectView(R.id.tv_pick_save)
    TextView tvPickSave;
    @InjectView(R.id.lv_pick)
    ListView lvPick;


    private PickAdapter adapter;

    private List<PickInfo> pickInfos;
    private PickAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.inject(this);

        initView();

        //获取数据

        initData();

        inirListener();


    }


    private void inirListener() {

        //设置item的点击事件

        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //获取itemd checkbox

                CheckBox cbpick = (CheckBox) findViewById(R.id.cb_item_pick_contacts);

                //对当前checkbox状态进行取反

                cbpick.setChecked(!cbpick.isChecked());

                PickInfo pickInfo = pickInfos.get(position);


                //设置当前状态

                pickInfo.setCheck(cbpick.isChecked());

                adapter.refresh(pickInfos);

            }
        });


    }

    private void initData() {

        //获取联系人

        // 本地

        List<UserInfo> contacts = Model.getInstance().getDbManager().
                getContactDao().getContacts();
        if (contacts == null) {

            return;
        }
        if (contacts.size() == 0) {
            ShowToast.show(this, "您还没有好友");


        }


        //转换数据

        pickInfos = new ArrayList<>();

        for (UserInfo userInfo : contacts) {
            pickInfos.add(new PickInfo(userInfo, false));

        }
        adapter.refresh(pickInfos);

    }

    private void initView() {

        adapter = new PickAdapter(this);

        lvPick.setAdapter(adapter1);


    }

    //保存联系人


    @OnClick(R.id.tv_pick_save)
    public void onClick() {

        List<String> contactCheck = adapter.getContactCheck();

        if (contactCheck == null) {
            return;
        }
        Intent intent = new Intent();

        intent.putExtra("members", contactCheck.toArray(new String[contactCheck.size()]));

        setResult(1, intent);

        //结束当前页面

        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //返回事件的处理的事情

            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);

    }
}
