package com.ninebythree.nutriscanph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.model.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    public final MyInterface myInterfaces;

    Context context;
    List<NotificationModel> notificationModels;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModels, MyInterface myInterfaces){
        this.context = context;
        this.notificationModels = notificationModels;
        this.myInterfaces = myInterfaces;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType  ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_items_notification, parent, false);

        return new MyViewHolder(view, myInterfaces);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtNotification.setText(notificationModels.get(position).getMessage());
    }


    @Override
    public int getItemCount() {

        return notificationModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView txtNotification;

        public MyViewHolder(@NonNull View itemView, MyInterface myInterfaces) {
            super(itemView);

            txtNotification = itemView.findViewById(R.id.txtNotification);


            itemView.setOnClickListener(view -> {
                if(myInterfaces != null ){
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        myInterfaces.onItemClick(pos, "meal");
                    }

                }
            });
        }
    }


}
