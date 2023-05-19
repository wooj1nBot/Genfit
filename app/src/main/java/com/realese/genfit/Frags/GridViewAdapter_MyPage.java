package com.realese.genfit.Frags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realese.genfit.R;

import java.util.List;

public class GridViewAdapter_MyPage extends BaseAdapter {

    private Context context;
    private List<Item> items;

    public GridViewAdapter_MyPage(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_mypage, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.grid_imageView_mypage);
            //holder.textView_nickname = convertView.findViewById(R.id.grid_text_nickname);
            //holder.likes = convertView.findViewById(R.id.grid_likes);
            //holder.profile_img = convertView.findViewById(R.id.chat_img);
            //holder.textView_tagging = convertView.findViewById(R.id.grid_text_tagging);
            //holder.heartImageView = (ImageView) convertView.findViewById(R.id.heart_shape);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = items.get(position);

        holder.imageView.setBackgroundResource(item.getImageResId());
        //holder.textView_nickname.setText(item.getNick_name());
        //holder.textView_tagging.setText(item.getText_tag());
        //holder.likes.setText(item.getLikes());
        //holder.profile_img.setImageResource(item.getProfile_img());

        return convertView;
    }

    static class ViewHolder {
        ImageView heartImageView;
        ImageView imageView;
        ImageView profile_img;
        TextView textView_tagging;
        TextView textView_nickname;
        TextView likes;
    }
}
