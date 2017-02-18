package im.im1020;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.fragment.SettingsFragment;
import im.im1020.fragment.contactFragment;
import im.im1020.fragment.converstationFragment;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_fl)
    FrameLayout mainFl;

    @InjectView(R.id.rg_main)
    RadioGroup rgMain;
    private Fragment settingsFragment;
    private Fragment converstationFragment;
    private Fragment contactFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        initData();


        initListener();
    }


    private void initData() {


        //创建Fragment

        settingsFragment = new SettingsFragment();
        converstationFragment = new converstationFragment();
        contactFragment = new contactFragment();
        switchFragment(R.id.rb_main_conversation);

    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //切换fragment
                switchFragment(checkedId);

            }
        });


    }

    private void switchFragment(int checkedId) {
        Fragment fragment = null;

        switch (checkedId) {
            case R.id.rb_main_conversation:
                fragment = converstationFragment;
                break;
            case R.id.rb_main_contact:
                fragment = contactFragment;
                break;
            case R.id.rb_main_setting:
                fragment = settingsFragment;
                break;
        }

        if (fragment == null) {
            return;


        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fl, fragment).commit();

    }


}

