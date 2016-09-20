package com.xcc.advancedday13.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xcc.advancedday13.R;
import com.xcc.advancedday13.model.City;

import java.util.List;

/**
 * Created by bukeyishidecheng on 16/9/20.
 */
public class ItemGridViewAdapter extends RecyclerView.Adapter<ItemGridViewAdapter.ViewHolder> {

    private List<City.DataBean.DestinationsBean> data;
    private LayoutInflater inflater;
    private Context context;

    public ItemGridViewAdapter(Context context,List<City.DataBean.DestinationsBean> data) {
        this.data = data;
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    public City.DataBean.DestinationsBean getItem(int position){
        return data.get(position);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=inflater.inflate(R.layout.strategy_item_gv_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());

        Picasso.with(context).load(data.get(position).getPhoto_url()).into(holder.image);


    }

    public  static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image= (ImageView) itemView.findViewById(R.id.strategy_item_gv_item_image);
            name= (TextView) itemView.findViewById(R.id.strategy_item_gv_item_name);
        }
    }
}
