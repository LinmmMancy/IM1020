package im.im1020.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.R;
import im.im1020.model.bean.GroupInfo;
import im.im1020.model.bean.InvitationInfo;
import im.im1020.model.bean.UserInfo;

/**
 * Created by Mancy_Lin on 2017-02-17.
 */

public class InviteMessageAdapter extends BaseAdapter {


    private Context context;

    private List<InvitationInfo> invitationInfos;

    public InviteMessageAdapter(Context context, OnInviteChangeListener onInviteChangeListener) {
        this.context = context;
        this.onInviteChangeListener = onInviteChangeListener;
        invitationInfos = new ArrayList<>();

    }


    public void refresh(List<InvitationInfo> invitationInfos) {

        //校验

        if (invitationInfos == null) {
            return;

        }

        this.invitationInfos.clear();

        //添加数据

        this.invitationInfos.addAll(invitationInfos);

        //刷新界面

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return invitationInfos == null ? 0 : invitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return invitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //创建viewholder
        ViewHolder viewholder = null;

        if (convertView == null) {

            //创建 convertView
            convertView = View.inflate(context, R.layout.adapter_invite_message_item, null);

            viewholder = new ViewHolder(convertView);

            convertView.setTag(viewholder);


        } else {

            viewholder = (ViewHolder) convertView.getTag();


        }


        //绑定数据

        final InvitationInfo invitationInfo = invitationInfos.get(position);


        GroupInfo groupInfo = invitationInfo.getGroupInfo();

        if (groupInfo != null) {

            //群邀请

            viewholder.tvInviteName.setText(groupInfo.getInviteperson());


            //隐藏按钮

            viewholder.btInviteReject.setVisibility(View.GONE);
            viewholder.btInviteAccept.setVisibility(View.GONE);

            switch (invitationInfo.getStatus()) {
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:

                    viewholder.tvInviteReason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    viewholder.tvInviteReason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    viewholder.tvInviteReason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    viewholder.tvInviteReason.setText("您的群邀请已经被拒绝");
                    break;

                //您收到了群邀请

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    viewholder.tvInviteReason.setText("您收到了群邀请");

                    viewholder.btInviteReject.setVisibility(View.VISIBLE);
                    viewholder.btInviteAccept.setVisibility(View.VISIBLE);
                    viewholder.btInviteAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onInviteChangeListener != null) {

                                onInviteChangeListener.onApplicationAccept(invitationInfo);
                            }
                        }
                    });

                    viewholder.btInviteReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onInviteChangeListener != null) {
                                onInviteChangeListener.onApplicationReject(invitationInfo);

                            }
                        }
                    });
                    break;
                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    viewholder.tvInviteReason.setText("你接受了群邀请");
                    break;

                // 您批准了群申请
                case GROUP_ACCEPT_APPLICATION:
                    viewholder.tvInviteReason.setText("您批准了群申请");
                    break;

                // 你拒绝了群邀请
                case GROUP_REJECT_INVITE:
                    viewholder.tvInviteReason.setText("你拒绝了群邀请");
                    break;

                // 您拒绝了群申请
                case GROUP_REJECT_APPLICATION:
                    viewholder.tvInviteReason.setText("您拒绝了群申请");
                    break;


            }


        } else {
            //联系人邀请

            UserInfo userInfo = invitationInfo.getUserInfo();

            viewholder.tvInviteName.setText(userInfo.getUsername());

            //隐藏buttonc

            viewholder.btInviteAccept.setVisibility(View.GONE);

            viewholder.btInviteReject.setVisibility(View.GONE);


            //新邀请

            if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.NEW_INVITE) {


                //展示 button

                viewholder.btInviteReject.setVisibility(View.VISIBLE);

                viewholder.btInviteAccept.setVisibility(View.VISIBLE);

                //设置reason
                if (invitationInfo.getReason() == null) {
                    viewholder.tvInviteReason.setText("邀请好友");


                } else {
                    viewholder.tvInviteReason.setText(invitationInfo.getReason());
                }


                //接受按钮的监听

                viewholder.btInviteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onInviteChangeListener != null) {

                            onInviteChangeListener.onAccept(invitationInfo);

                        }
                    }
                });


                //拒绝


                viewholder.btInviteReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onInviteChangeListener != null) {

                            onInviteChangeListener.onReject(invitationInfo);

                        }
                    }
                });
            } else if (invitationInfo.getStatus()

                    == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {

                if (invitationInfo.getReason() == null) {

                    viewholder.tvInviteReason.setText("邀请被接受");


                } else {

                    viewholder.tvInviteReason.setText(invitationInfo.getReason());

                }

            } else if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.INVITE_ACCEPT) {

                if (invitationInfo.getReason() == null) {

                    viewholder.tvInviteReason.setText("接受邀请");

                } else {
                    viewholder.tvInviteReason.setText(invitationInfo.getReason());

                }

            }


        }

        //返回布局

        return convertView;
    }


    class ViewHolder {
        @InjectView(R.id.tv_invite_name)
        TextView tvInviteName;
        @InjectView(R.id.tv_invite_reason)
        TextView tvInviteReason;
        @InjectView(R.id.bt_invite_accept)
        Button btInviteAccept;
        @InjectView(R.id.bt_invite_reject)
        Button btInviteReject;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    //第一部   定义接口

    //第二步  定义接口变量

    //第三部   设置set方法

    //第四步   接受接口的实例对象

    //第五步   调用接口方法


    private OnInviteChangeListener onInviteChangeListener;

    public void setOnInviteChangeListener(OnInviteChangeListener onInviteChangeListener) {
        this.onInviteChangeListener = onInviteChangeListener;
    }


    public interface OnInviteChangeListener {

        void onAccept(InvitationInfo info); //接受

        void onReject(InvitationInfo info);  //拒绝


        void onInvireAccept(InvitationInfo info);

        void onInvireReject(InvitationInfo info);


        void onApplicationAccept(InvitationInfo info);

        void onApplicationReject(InvitationInfo info);

    }


}


