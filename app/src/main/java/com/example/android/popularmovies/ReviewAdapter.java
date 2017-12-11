package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sunilkumar on 05/12/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private Context mContext;
    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private ArrayList<String> reviewAuthorArrayList = new ArrayList<>();
    private ArrayList<String> reviewContentArrayList = new ArrayList<>();

    ReviewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {

        String reviewAuthor = reviewAuthorArrayList.get(position);
        String reviewContent = reviewContentArrayList.get(position);

        holder.author.setText(reviewAuthor);
        holder.content.setText(reviewContent);

        if (position == (reviewAuthorArrayList.size() - 1) || position == (reviewContentArrayList.size() - 1)){
            holder.view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == reviewAuthorArrayList || null == reviewContentArrayList) return 0;

            return (reviewAuthorArrayList.size());

    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView author;
        private TextView content;
        private View view;

        ReviewAdapterViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.tv_author);
            content = itemView.findViewById(R.id.tv_content);
            view = itemView.findViewById(R.id.v_seperator);
        }
    }

    public void setReviewArrayList(ArrayList<String> reviewAuthorArrayList, ArrayList<String> reviewContentArrayList ){
        this.reviewAuthorArrayList.clear();
        this.reviewContentArrayList.clear();
        this.reviewAuthorArrayList.addAll(reviewAuthorArrayList);
        this.reviewContentArrayList.addAll(reviewContentArrayList);

        notifyDataSetChanged();
    }


}
