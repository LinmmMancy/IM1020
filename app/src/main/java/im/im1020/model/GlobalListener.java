package im.im1020.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

import im.im1020.model.bean.GroupInfo;
import im.im1020.model.bean.InvitationInfo;
import im.im1020.model.bean.UserInfo;
import im.im1020.utils.Constant;
import im.im1020.utils.SpUtils;

/**
 * Created by Mancy_Lin on 2017-02-16.
 */
public class GlobalListener {

    private final LocalBroadcastManager manager;

    public GlobalListener(Context context) {

        //注册联系人监听
        EMClient.getInstance().contactManager().setContactListener(listener);

        //注册群监听


        EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);

        //本地广播
        manager = LocalBroadcastManager.getInstance(context);

    }

    EMContactListener listener = new EMContactListener() {
        //收到好友邀请  别人加你
        @Override
        public void onContactInvited(String username, String reason) {

            //加载到邀请信息表

            InvitationInfo invitation = new InvitationInfo();

            invitation.setUserInfo(new UserInfo(username));
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);
            Log.e("TAG", "GlobalListener onContactInvited()");
            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);
            Log.e("TAG", "GlobalListener onContactInvited()1");
            //保存小红点

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.NEW_INVITE_CHANGE));
        }

        //好友请求被同意  你加别人的时候 别人同意了
        @Override
        public void onContactAgreed(String username) {


            //添加到邀请信息表

            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setUserInfo(new UserInfo(username));

            invitationInfo.setReason("邀请被接受");

            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);


            //保存小红点的状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.NEW_INVITE_CHANGE));


        }

        //被删除时回调此方法
        @Override
        public void onContactDeleted(String username) {

            Model.getInstance().getDbManager().getInvitationDao()
                    .removeInvitation(username);

            //删除联系人

            Model.getInstance().getDbManager().getContactDao()
                    .deleteContactByHxId(username);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.CONTACT_CHANGE));


        }


        //增加了联系人时回调此方法  当你同意添加好友
        @Override
        public void onContactAdded(String username) {
            //保存联系人


            Model.getInstance().getDbManager().getContactDao()
                    .saveContact(new UserInfo(username), true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.CONTACT_CHANGE));


        }

        //好友请求被拒绝  你加别人 别人拒绝了
        @Override
        public void onContactRefused(String username) {

            //保存小红点
            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.NEW_INVITE_CHANGE));

        }
    };


    EMGroupChangeListener groupListener = new EMGroupChangeListener() {
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {


            //添加邀请到数据库

            InvitationInfo invitation = new InvitationInfo();

            invitation.setReason(reason);

            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);

            invitation.setGroupInfo(new GroupInfo(groupName, groupId, inviter));


            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //保存小红点状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播
            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));
        }


        //收到加群申请 别人要加你的群
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            InvitationInfo invitation = new InvitationInfo();

            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);

            invitation.setGroupInfo(new GroupInfo(groupName, groupId, applyer));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //保存小红点状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播
            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }
        // 加群申请呗同意  你家别人的群  别人同意了

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason("");

            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, accepter));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);
            //保存小红点状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播
            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }
        //加群申请呗拒绝   你加别人的群 别人拒绝了

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {

            InvitationInfo invitation = new InvitationInfo();

            invitation.setReason(reason);

            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);

            invitation.setGroupInfo(new GroupInfo(groupName, groupId, decliner));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //保存小红点状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播
            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }
        //群组邀请被接受  你邀请别人   别人接受了

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(reason);

            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);


            //保存小红点状态

            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播
            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }

        //群组邀请被拒绝   你邀请别人被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(reason);

            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);

            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, invitee));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);

            //保存小红点
            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }

        //d当前用户被光理由移除群组
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId,
                                                    String inviter, String inviteMessage) {
            InvitationInfo invitation = new InvitationInfo();

            invitation.setReason(inviteMessage);

            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            invitation.setGroupInfo(new GroupInfo(groupId, groupId, inviter));

            Model.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //保存小红点
            SpUtils.getInstace().save(SpUtils.NEW_INVITE, true);

            //发送广播

            manager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHAGE));


        }
    };
}
