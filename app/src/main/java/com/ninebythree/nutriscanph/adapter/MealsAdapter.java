package com.ninebythree.nutriscanph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.model.MealModel;

import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MyViewHolder> {

    public final MyInterface myInterfaces;

    Context context;
    List<MealModel> mealsModel;

    public MealsAdapter(Context context, List<MealModel> mealsModel, MyInterface myInterfaces){
        this.context = context;
        this.mealsModel = mealsModel;
        this.myInterfaces = myInterfaces;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType  ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_items_meals, parent, false);

        return new MyViewHolder(view, myInterfaces);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtCalories.setText(mealsModel.get(position).getCalories() + " cal");
        holder.txtMealName.setText(mealsModel.get(position).getMealName());
    }


    @Override
    public int getItemCount() {

        return mealsModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView txtCalories, txtMealName;

        public MyViewHolder(@NonNull View itemView, MyInterface myInterfaces) {
            super(itemView);

            txtCalories = itemView.findViewById(R.id.txtCalories);
            txtMealName = itemView.findViewById(R.id.txtMealName);


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
