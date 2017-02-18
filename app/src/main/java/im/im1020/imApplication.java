package im.im1020;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import im.im1020.model.Model;

/**
 * Created by Mancy_Lin on 2017-02-14.
 */

public class imApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();


        initHXSdk();
        // 初始化模型层数据
        Model.getInstance().init(this);

        context = this;


    }

    private void initHXSdk() {
        EMOptions options = new EMOptions();

        options.setAutoAcceptGroupInvitation(false);// 不自动接受群邀请信息
        options.setAcceptInvitationAlways(false);// 不总是一直接受所有邀请

        // 初始化EaseUI
        EaseUI.getInstance().init(this, options);


    }

    public static Context getContext() {
        return context;
    }


}
