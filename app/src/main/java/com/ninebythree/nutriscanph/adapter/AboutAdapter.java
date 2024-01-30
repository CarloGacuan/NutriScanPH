package com.ninebythree.nutriscanph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.model.AboutModel;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder> {

    public final MyInterface myInterfaces;
    Context context;
    List<AboutModel> aboutModels;

    public AboutAdapter(Context context, List<AboutModel> aboutModels, MyInterface myInterfaces){
        this.context = context;
        this.aboutModels = aboutModels;
        this.myInterfaces = myInterfaces;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType  ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_items_about, parent, false);

        return new MyViewHolder(view, myInterfaces);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(aboutModels.get(position).getName());
        holder.txtDescription.setText(aboutModels.get(position).getDescription());
        holder.image.setImageResource(aboutModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {

        return aboutModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView txtName, txtDescription;
        ImageView image;


        public MyViewHolder(@NonNull View itemView, MyInterface myInterfaces) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.image);
            txtDescription = itemView.findViewById(R.id.txtDescription);

            itemView.setOnClickListener(view -> {
                if(myInterfaces != null ){
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        myInterfaces.onItemClick(pos, "about");
                    }

                }
            });
        }
    }


}
