package com.vik.assignment;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vik on 19/7/17.
 */

public class SearchImageAdapter extends RecyclerView.Adapter<SearchImageAdapter.ViewHolder> {

    private ArrayList<ImageTagModel> models;

    @Override
    public SearchImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchImageAdapter.ViewHolder holder, int position) {
        ImageTagModel imageTagModel = models.get(position);
        Uri uri = Uri.fromFile(new File(models.get(position).getImagePath()));
        Picasso.with(holder.itemView.getContext()).load(uri).placeholder(R.mipmap.ic_launcher).into(holder.image);
        holder.tags.setText(imageTagModel.getTags());
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    public void setSearchData(ArrayList<ImageTagModel> searchData) {
        this.models = searchData;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView tags;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tags = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
