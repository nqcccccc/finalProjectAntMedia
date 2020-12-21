package io.antmedia.android.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.antmedia.android.Message;
import io.antmedia.android.broadcaster.R;

public class MessageAdapter extends BaseAdapter {

    private List<Message> listMessage;
    private LayoutInflater layoutInflater;
    private Context context;

    public MessageAdapter(ChatActivity aContext, List<Message> listMessage)
    {
        this.listMessage = listMessage;
        this.context = aContext;
        this.layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Object getItem(int i) {
        return listMessage.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CustomViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_message, null);

            holder = new CustomViewHolder();
            holder.avartar = view.findViewById(R.id.img_avartar_mess);
            holder.fullName =  view.findViewById(R.id.tv_name_mess);
            holder.email =  view.findViewById(R.id.tv_email_mess);
            holder.messageContent = view.findViewById(R.id.tv_message);

            view.setTag(holder);
        } else {
            holder = (CustomViewHolder) view.getTag();
        }

        Message message = this.listMessage.get(i);
        holder.fullName.setText(message.getFullName());
        holder.email.setText(message.getEmail());
        holder.messageContent.setText(message.getMessageContent());

        Picasso.with(context).load(message.getAvartar()).into(holder.avartar);

        return view;
    }

    static class CustomViewHolder {
        ImageView avartar;
        TextView fullName;
        TextView email;
        TextView messageContent;
    }
}
