package com.realese.genfit.Frags;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realese.genfit.R;

import java.util.*;

public class GridViewAdapter_Home extends BaseAdapter {

    private Context context;
    private List<Item> items;
    private OnHeartClickListener heartClickListener;

    public GridViewAdapter_Home(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnHeartClickListener(OnHeartClickListener listener) {
        this.heartClickListener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_home, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.grid_imageView);
            holder.textView_nickname = convertView.findViewById(R.id.grid_text_nickname);
            holder.likes = convertView.findViewById(R.id.grid_likes);
            holder.profile_img = convertView.findViewById(R.id.chat_img);
            holder.textView_tagging = convertView.findViewById(R.id.grid_text_tagging);
            holder.heartImageView = (ImageView) convertView.findViewById(R.id.heart_shape);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = items.get(position);

        holder.imageView.setBackgroundResource(item.getImageResId());
        holder.textView_nickname.setText(item.getNick_name());
        holder.textView_tagging.setText(item.getText_tag());
        holder.likes.setText(item.getLikes());
        holder.profile_img.setImageResource(item.getProfile_img());

        // Set heart image based on state
        if (item.isHeartFilled()) {
            holder.heartImageView.setImageResource(R.drawable.heart_real); // filled heart image
        } else {
            holder.heartImageView.setImageResource(R.drawable.heart_line); // empty heart image
        }

        holder.heartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("heart_clicking", "click__");
                if (heartClickListener != null) {
                    // Toggle heart state
                    item.setHeartFilled(item.isHeartFilled());
                    boolean curr_heart_sign = item.isHeartFilled();
                    // Change heart image based on state
                    Log.d("curr_heart_sign: " + curr_heart_sign, "");
                    if (!curr_heart_sign) {
                        //Log.d("curr_heart_sign: " + curr_heart_sign, "change_black_");
                        holder.likes.setText(String.valueOf(Integer.parseInt(item.getLikes()) + 1));
                        holder.heartImageView.setImageResource(R.drawable.heart_real); // filled heart image
                        item.setLikes(String.valueOf(Integer.parseInt(item.getLikes()) + 1));
                    } else {
                        holder.likes.setText(String.valueOf(Integer.parseInt(item.getLikes()) - 1));
                        holder.heartImageView.setImageResource(R.drawable.heart_line); // empty heart image
                        item.setLikes(String.valueOf(Integer.parseInt(item.getLikes()) - 1));
                    }
                    item.setHeartFilled(!curr_heart_sign);
                    heartClickListener.onHeartClick(position);
                }
            }
        });

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

    public interface OnHeartClickListener {
        void onHeartClick(int position);
    }
}
