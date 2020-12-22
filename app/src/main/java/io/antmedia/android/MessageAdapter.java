package io.antmedia.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.antmedia.android.broadcaster.R;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private int layout;
    private  List<Message> data;
    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layout = resource;
        this.data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        MessageAdapter.Holder holder;

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(layout,parent,false);

            holder = new Holder();
            holder.img = view.findViewById(R.id.imgAvaM);
            holder.msg = view.findViewById(R.id.tvMsgM);
            holder.name = view.findViewById(R.id.tvFullName);
            //save
            view.setTag(holder);
        }else{
            view = convertView;

            //read
            holder = (Holder) view.getTag();
        }

        Message message = data.get(position);
        Picasso.with(context).load(message.getAvartar()).into(holder.img);
        holder.name.setText(message.getFullName());
        holder.msg.setText(message.getMessageContent());

        return view;
    }

    static class Holder {
        CircleImageView img;
        TextView msg,name;
    }
}
