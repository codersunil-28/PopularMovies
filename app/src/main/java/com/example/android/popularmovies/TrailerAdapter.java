package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilkumar on 02/12/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>  {

    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private List<String> trailerArrayList = new ArrayList<>();
    private final Activity activity;

    TrailerAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {

        final String trailerKey = trailerArrayList.get(position);
        holder.itemView.setTag(trailerKey);
        holder.trailerThumbnail.initialize(YoutubeConfig.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(trailerKey);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == trailerArrayList) return 0;
        return trailerArrayList.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {

        private YouTubeThumbnailView trailerThumbnail;

        public TrailerAdapterViewHolder(final View itemView) {
            super(itemView);
            trailerThumbnail = itemView.findViewById(R.id.tn_youtube_trailer);
            trailerThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                            activity,
                            YoutubeConfig.YOUTUBE_API_KEY,
                            itemView.getTag().toString(),
                            0,
                            true,
                            false);
                    activity.startActivity(intent);

                }
            });
        }

    }

    public void setTrailerArrayList(List<String> trailerArrayList){
        this.trailerArrayList.clear();
        this.trailerArrayList.addAll(trailerArrayList);

        notifyDataSetChanged();
    }

}
