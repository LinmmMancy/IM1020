package im.im1020.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.R;
import im.im1020.adapter.GroupListAdapter;
import im.im1020.model.Model;
import im.im1020.utils.ShowToast;

public class GroupListActivity extends AppCompatActivity {

    @InjectView(R.id.lv_grouplist)
    ListView lvGrouplist;


    private GroupListAdapter adapter;

    private LinearLayout groupListHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.inject(this);

        initView();

        initData();

        initListener();


    }


    private void initData() {

        Model.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    List<EMGroup> groups = EMClient.getInstance().groupManager()
                            .getJoinedGroupsFromServer();


                    // 内存和页面

                    refresh();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refresh() {
        List<EMGroup> allGroups = EMClient.getInstance().groupManager()
                .getAllGroups();

        if (allGroups == null) {
            return;
        }

        adapter.refresh(allGroups);



    }

    private void initView() {

        //添加头布局

        View headview = View.inflate(this, R.layout.group_list_head, null);

        groupListHead = (LinearLayout) headview.findViewById(R.id.ll_grouplist);


        lvGrouplist.addHeaderView(headview);

        //设置适配器

        adapter = new GroupListAdapter(this);


        lvGrouplist.setAdapter(adapter);


    }


    private void initListener() {


        groupListHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast.show(GroupListActivity.this, "阿福老师附体！");
            }
        });


    }
}
