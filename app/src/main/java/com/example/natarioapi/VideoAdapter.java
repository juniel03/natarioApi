package com.example.natarioapi;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mCtx;
    private List<Vid> videolist;

    public VideoAdapter(Context mCtx, List<Vid> videolist) {
        this.mCtx = mCtx;
        this.videolist = videolist;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Vid vid = videolist.get(position);
        holder.tvusername.setText(vid.id);
//        holder.tvUrl.setText(vid.uri);

    }

    @Override
    public int getItemCount() {
        return videolist.size();

    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView tvusername;
        Uri tvUrl;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvusername = itemView.findViewById(R.id.tv_item_name);
//            tvUrl = itemView.findViewById(R.id.text_view_name);
        }
    }
}
