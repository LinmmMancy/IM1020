package im.im1020.fragment;

import android.content.Intent;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

import im.im1020.activity.ChatAcativity;

/**
 * Created by Mancy_Lin on 2017-02-15.
 */
public class converstationFragment extends EaseConversationListFragment {

    @Override
    protected void initView() {
        super.initView();


        //清除原有的联系人  ，否则会出现重新状况

        conversationList.clear();


        //会话item点击事件

        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {


                //跳转

                Intent intent = new Intent(getActivity(), ChatAcativity.class);

                //传环信id

                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName());

                if (conversation.getType()
                        == EMConversation.EMConversationType.GroupChat) {

                    //传入群聊天类型

                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }

                startActivity(intent);

            }
        });


        //注册一个监听
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {

                //接受消息

                EaseUI.getInstance().getNotifier().onNewMesg(list);

                //刷新

                refresh();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }
}

