package com.example.recyclerview;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;
import java.util.LinkedList;


public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    Context ctx;
    private final LinkedList<String> mWordList;
    private final LayoutInflater mInflater;


    public WordListAdapter(Context context, LinkedList<String> wordList) {
        mInflater = LayoutInflater.from(context);
        this.ctx = context;
        this.mWordList = wordList;
    }

    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordItemView;
        final WordListAdapter mAdapter;

        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int mPosition = getLayoutPosition();
            String element = mWordList.get(mPosition);
            mWordList.set(mPosition, "Clicked! " + element);

            int wordListSize = mWordList.size();
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(ctx);
            clevertapDefaultInstance.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);

            HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
            prodViewedAction.put("Product Name", "MAC Mini");
            prodViewedAction.put("Category", "Computers");
            prodViewedAction.put("Price", 899.99);
            prodViewedAction.put("Date", new java.util.Date());

            clevertapDefaultInstance.pushEvent("Added to cart", prodViewedAction);

            mAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View mItemView = mInflater.inflate(
                R.layout.wordlist_item, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordListAdapter.WordViewHolder holder,
                                 int position) {

        String mCurrent = mWordList.get(position);
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }
}
