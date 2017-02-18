package im.im1020.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.hyphenate.easeui.ui.EaseChatFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.R;

public class ChatAcativity extends AppCompatActivity {

    @InjectView(R.id.chat_fl)
    FrameLayout chatFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acativity);
        ButterKnife.inject(this);

        initData();


    }

    private void initData() {


        //聊天的fragment

        EaseChatFragment chatFragment = new EaseChatFragment();

        chatFragment.setArguments(getIntent().getExtras());


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_fl, chatFragment).commit();


    }
}
