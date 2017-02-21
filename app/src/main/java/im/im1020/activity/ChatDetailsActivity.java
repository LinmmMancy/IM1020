package im.im1020.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.R;
import im.im1020.adapter.GroupDetailAdapter;
import im.im1020.imApplication;
import im.im1020.model.Model;
import im.im1020.model.bean.UserInfo;
import im.im1020.utils.Constant;
import im.im1020.utils.ShowToast;

public class ChatDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.gv_group_detail)
    GridView gvGroupDetail;
    @InjectView(R.id.bt_group_detail)
    Button btGroupDetail;
    private String groupid;


    private GroupDetailAdapter adapter;
    private EMGroup group;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.inject(this);

        initData();

        initView();

        getGroupData();

        //获取群成员
        getGroipMembers();
    }

    private void getGroipMembers() {


        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                //获取群组


                try {
                    EMGroup emgroup = EMClient.getInstance().groupManager()
                            .getGroupFromServer(groupid);

                    //获取群成员

                    List<String> members = emgroup.getMembers();

                    // 转内型


                    final List<UserInfo> userInfos = new ArrayList<UserInfo>();


                    for (String hxid : members) {

                        userInfos.add(new UserInfo(hxid));

                    }

                    // 内存 和网页

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // 刷新
                            adapter.refresh(userInfos);

                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();

                }

            }
        });


    }

    private void getGroupData() {


    }

    private void initView() {

        //  GroupListAdapter adapter = new GroupListAdapter();
        // gvGroupDetail.setAdapter();

    }

    private void initData() {


        //获取群id
        groupid = getIntent().getStringExtra("groupid");

        if (TextUtils.isEmpty(groupid)) {

            return;
        }

        //获取当前的群组

        group = EMClient.getInstance().groupManager().getGroup(groupid);

        //获取群主

        owner = group.getOwner();

        if (EMClient.getInstance().getCurrentUser().equals(owner)) {


            btGroupDetail.setText("解散群");


            btGroupDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager()
                                        .destroyGroup(groupid);

                                //退群
                                exitGroup();
                                //结束当前页面


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        finish();

                                        ShowToast.show(ChatDetailsActivity.this, "解散群成功");
                                    }
                                });

                                exitGroup();
                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                ShowToast.showUI(ChatDetailsActivity.this, "解散群失败" + e.getMessage());
                            }


                        }
                    });
                }
            });

        } else {

            //是群成员

            btGroupDetail.setText("退群");

            btGroupDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                EMClient.getInstance().groupManager()
                                        .leaveGroup(groupid);


                                exitGroup();

                                //结束当前页面


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();

                                        ShowToast.show(ChatDetailsActivity.this, "退群成功");
                                    }
                                });


                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                ShowToast.showUI(ChatDetailsActivity.this, "退群失败" + e.getMessage());
                            }
                        }
                    });


                }
            });
        }


        // 判断是否有邀请的权限

        boolean isModify = EMClient.getInstance()
                .getCurrentUser().equals(owner) || group.isPublic();
        adapter= new GroupDetailAdapter(this, isModify, new GroupDetailAdapter.OnMembersChangListener() {
            @Override
            public void onRemoveGroupMember(UserInfo userInfo) {
                ShowToast.show(ChatDetailsActivity.this,"删除成功");
            }

            @Override
            public void onAddGroupMember(UserInfo userInfo) {

                 ShowToast.show(ChatDetailsActivity.this,"添加成功");

            }
        });

        gvGroupDetail.setAdapter(adapter);
    }


    private void exitGroup() {

        //注意上下文
        LocalBroadcastManager manager = LocalBroadcastManager
                .getInstance(imApplication.getContext());

        Intent intent = new Intent(Constant.DESTORY_GROUP);

        intent.putExtra("groupid", groupid);

        manager.sendBroadcast(intent);


    }

}
