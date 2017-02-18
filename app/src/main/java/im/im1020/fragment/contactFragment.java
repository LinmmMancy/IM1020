package im.im1020.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import im.im1020.R;
import im.im1020.activity.ChatAcativity;
import im.im1020.activity.InviteActvity;
import im.im1020.activity.InviteMessageActivity;
import im.im1020.model.Model;
import im.im1020.model.bean.UserInfo;
import im.im1020.utils.Constant;
import im.im1020.utils.ShowToast;
import im.im1020.utils.SpUtils;

/**
 * Created by Mancy_Lin on 2017-02-15.
 */
public class contactFragment extends EaseContactListFragment {

    @InjectView(R.id.contanct_iv_invite)
    ImageView contanctIvInvite;
    @InjectView(R.id.ll_new_friends)
    LinearLayout llNewFriends;
    @InjectView(R.id.ll_groups)
    LinearLayout llGroups;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isShow();
        }
    };
    private LocalBroadcastManager manager;

    @Override
    protected void initView() {
        super.initView();
        //初始化 头布局
        View view = View.inflate(getActivity(), R.layout.fragment_contact, null);
        ButterKnife.inject(this, view);
        //添加头布局

        listView.addHeaderView(view);

        //添加actionbar右侧的加号

        titleBar.setRightImageResource(R.mipmap.em_add);

        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转到 邀请界面

                Intent intent = new Intent(getActivity(), InviteActvity.class);

                startActivity(intent);
            }
        });

        //初始化小红点

        isShow();

        //注册广播

        manager = LocalBroadcastManager.getInstance(getActivity());

        manager.registerReceiver(receiver, new IntentFilter(Constant.NEW_INVITE_CHANGE));

        initData();

        //监听事件

        initListener();

    }

    private void initListener() {


        //联系人点击监听

        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                //跳转

                Intent intent = new Intent(getActivity(), ChatAcativity.class);


                //参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());


                startActivity(intent);
            }
        });
    }


    @Override
    protected void setUpView() {
        super.setUpView();


    }


    private void initData() {


        //获取联系人

        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                //从服务器连接

                try {
                    List<String> contacts = EMClient.getInstance().contactManager()

                            .getAllContactsFromServer();


                    //保存数据库

                    //转化数据

                    List<UserInfo> userInfos = new ArrayList<UserInfo>();
                    for (int i = 0; i < contacts.size(); i++) {

                        userInfos.add(new UserInfo(contacts.get(i)));

                    }

                    Model.getInstance().getDbManager().getContactDao()
                            .saveContacts(userInfos, true);

                    //内存和网页

                    refreshContact();


                } catch (HyphenateException e) {
                    e.printStackTrace();


                }
            }
        });
    }

    private void refreshContact() {

        //从本地取数据

        List<UserInfo> contancts = Model.getInstance().getDbManager().getContactDao()
                .getContacts();

        //校验

        if (contancts == null) {

            return;
        }

        //转换数据

        Map<String, EaseUser> maps = new HashMap<>();


        for (UserInfo userInfo : contancts) {
            EaseUser user = new EaseUser(userInfo.getHxid());

            maps.put(userInfo.getHxid(), user);
        }

        setContactsMap(maps);

        refresh();
    }


    @OnClick({R.id.ll_new_friends, R.id.ll_groups})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_new_friends:

                SpUtils.getInstace().save(SpUtils.NEW_INVITE, false);

                isShow();

                //跳转
                Intent intent = new Intent(getActivity(), InviteMessageActivity.class);
                startActivity(intent);


                break;
            case R.id.ll_groups:
                ShowToast.show(getActivity(), "bbb");
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);

        manager.unregisterReceiver(receiver);
    }

    public void isShow() {

        boolean isShow = SpUtils.getInstace().getBoolean(SpUtils.NEW_INVITE, false);
        contanctIvInvite.setVisibility(isShow ? View.VISIBLE : View.GONE);


    }
}
