package com.nickbullet.messageitmobile;

import com.example.messageitmobile.R;
import com.nickbullet.messageitmobile.other.Message;

import java.util.List;
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class MessagesListAdapter extends BaseAdapter {
 
    private Context context;
    private List<Message> messagesItems;
 
    public MessagesListAdapter(Context context, List<Message> navDrawerItems) {
        this.context = context;
        this.messagesItems = navDrawerItems;
    }
 
    @Override
    public int getCount() {
        return messagesItems.size();
    }
 
    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        Message m = messagesItems.get(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
 
        // Идентификация владельца сообщения
        if (messagesItems.get(position).isSelf()) {
            // если сообщение принадлежит Вам, то подгружаем layout правого сообщения 
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // если сообщение принадлежит другому человек, подгружаем аналогичный layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }
 
        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        
        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getFromName());
        tvTime.setText(m.getTime());
 
        return convertView;
    }
}