package im.im1020.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.im1020.R;
import im.im1020.model.bean.UserInfo;

/**
 * Created by Mancy on 2017/2/21.
 */

public class GroupDetailAdapter extends BaseAdapter {

    //上下文
    private Context context;

    private boolean isModify;


    private boolean isDeleteModle = false;


    private List<UserInfo> userInfos;


    public GroupDetailAdapter(Context context, boolean isModify, OnMembersChangListener onMembersChangListener) {
        this.context = context;
        this.isModify = isModify;
        userInfos = new ArrayList<>();
        this.onMembersChangListener = onMembersChangListener;

    }

    public void refresh(List<UserInfo> userInfos) {

        if (userInfos == null || userInfos.size() == 0) {
            return;

        }

        //清除数据

        this.userInfos.clear();

        //添加群成员
        isitUser();
        this.userInfos.addAll(0, userInfos);

        notifyDataSetChanged();

        //添加加减号


    }

    private void isitUser() {
        //减号
        this.userInfos.add(new UserInfo("remove"));
        //加号
        this.userInfos.add(0, new UserInfo("add"));
    }


    @Override
    public int getCount() {
        return userInfos == null ? 0 : userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            convertView = View.inflate(context, R.layout.adapter_group_members, null);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        if (isModify) {

            //群主

            /***
             *
             * 处理视图
             */


            if (position == userInfos.size() - 1) {

                //减号

                if (isDeleteModle) {

                    //删除模式下的减号
                    convertView.setVisibility(View.GONE);//隐藏整个减号


                } else {
                    convertView.setVisibility(View.VISIBLE);//展示整个减号

                    viewHolder.ivMemberDelete.setVisibility(View.GONE); //隐藏小减号

                    viewHolder.tvMemberName.setVisibility(View.INVISIBLE);// 隐藏名字

                    viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_smiley_minus_btn_pressed);


                }

            } else if (position == userInfos.size() - 2) {

                if (isDeleteModle) {

                    //删除模式下的减号

                    convertView.setVisibility(View.GONE);  //隐藏整个减号
                } else {

                    convertView.setVisibility(View.VISIBLE); //展示整个建号

                    viewHolder.ivMemberDelete.setVisibility(View.GONE);

                    viewHolder.tvMemberName.setVisibility(View.INVISIBLE);

                    viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_smiley_add_btn_pressed);


                }
            } else {


                //群成员

                convertView.setVisibility(View.VISIBLE);
                viewHolder.tvMemberName.setVisibility(View.VISIBLE);

                //根据删除模式是否展示小减号

                if (isDeleteModle) {

                    viewHolder.ivMemberDelete.setVisibility(View.VISIBLE);

                } else {

                    //否则就显示

                    viewHolder.ivMemberDelete.setVisibility(View.GONE);

                }

                viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_default_avatar);

                viewHolder.tvMemberName.setText(userInfos.get(position).getUsername());
            }

            /***
             *
             * 监听事件
             *
             *
             *
             */

            if (position == userInfos.size() - 1) {


                viewHolder.ivMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isDeleteModle) {
                            isDeleteModle = true;

                            notifyDataSetChanged();
                        }
                    }

                });
            } else if (position == userInfos.size() - 2) {

                viewHolder.ivMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMembersChangListener != null) {

                            onMembersChangListener.onAddGroupMember(userInfos.get(position));
                        }

                    }
                });
            } else {

                //群成员

                viewHolder.ivMemberDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMembersChangListener != null) {


                            onMembersChangListener.onRemoveGroupMember(userInfos.get(position));
                        }
                    }
                });
            }
        } else {

            if (position == userInfos.size() - 1) {

                convertView.setVisibility(View.GONE);


            } else if (position == userInfos.size() - 2) {

                convertView.setVisibility(View.GONE);

            } else {

                convertView.setVisibility(View.VISIBLE);

                viewHolder.tvMemberName.setText(userInfos.get(position).getUsername());

                viewHolder.ivMemberDelete.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public boolean getDeleteModel() {
        return isDeleteModle;
    }

    public void setDeleteModel(boolean isDeleteModle) {
        this.isDeleteModle = isDeleteModle;
        notifyDataSetChanged();


    }

    class ViewHolder {
        @InjectView(R.id.iv_member_photo)
        ImageView ivMemberPhoto;
        @InjectView(R.id.tv_member_name)
        TextView tvMemberName;
        @InjectView(R.id.iv_member_delete)
        ImageView ivMemberDelete;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private OnMembersChangListener onMembersChangListener;

    public interface OnMembersChangListener {

        void onRemoveGroupMember(UserInfo userInfo);

        void onAddGroupMember(UserInfo userInfo);
    }

}
