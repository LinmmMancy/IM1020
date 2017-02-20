package im.im1020.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
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
import im.im1020.activity.GroupListActivity;
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

    private BroadcastReceiver groupRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isShow();
        }
    };
    private LocalBroadcastManager manager;

    private List<UserInfo> contacts;

    private BroadcastReceiver contactRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshContact();
        }
    };
    private List<UserInfo> contancts;

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

        manager.registerReceiver(contactRecevier, new IntentFilter(Constant.CONTACT_CHANGE));

        manager.registerReceiver(groupRecevier, new IntentFilter(Constant.GROUP_INVITE_CHAGE));

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


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return false;

                }
                showDiglog(position);

                return true;
            }


        });
    }

    private void showDiglog(final int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage("确定要删除吗?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletecontact(position);

                    }
                })
                .create()
                .show();


    }

    private void deletecontact(final int position) {

        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {


                try {
                    //获取到这个用户的环信id


                    final UserInfo userInfo = contacts.get(position - 1);

                    //网络删除

                    EMClient.getInstance().contactManager().deleteContact(userInfo.getHxid());

                    // 本地删除  删除联系人

                    Model.getInstance().getDbManager().getContactDao()
                            .deleteContactByHxId(userInfo.getHxid());

                    //  根据环信id 删除邀请信息

                    Model.getInstance().getDbManager().getInvitationDao()
                            .removeInvitation(userInfo.getHxid());

                    //刷新

                    refreshContact();

                    ShowToast.showUI(getActivity(), "删除成功");


                } catch (HyphenateException e) {
                    e.printStackTrace();

                    ShowToast.showUI(getActivity(), "删除失败");

                }

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

        contancts = Model.getInstance().getDbManager().getContactDao()
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

    @Override
    public void onResume() {
        super.onResume();
        refreshContact();
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


                // 群列  展示
                Intent groupintent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(groupintent);

                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);

        manager.unregisterReceiver(receiver);

        manager.unregisterReceiver(contactRecevier);

        manager.unregisterReceiver(groupRecevier);
    }

    public void isShow() {

        boolean isShow = SpUtils.getInstace().getBoolean(SpUtils.NEW_INVITE, false);
        contanctIvInvite.setVisibility(isShow ? View.VISIBLE : View.GONE);


    }
}
