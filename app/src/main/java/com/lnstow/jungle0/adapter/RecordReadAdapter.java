package com.lnstow.jungle0.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnstow.jungle0.database.entity.Read;
import com.lnstow.jungle0.fragment.main.ReadFragment;
import com.lnstow.jungle0.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.WeakHashMap;

public class RecordReadAdapter extends RecyclerView.Adapter<RecordReadAdapter.ViewHolder> {
    private ArrayList<Read> data;
    private ReadFragment fragment;
    private ReadClickListener clickListener;
    private static ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xff7C4DFF);
    private static int dp = ViewUtil.dp2px(1);
    private static Locale locale = Locale.getDefault(Locale.Category.FORMAT);
    private static Drawable drawable;

    public void setData(ArrayList<Read> data) {
        this.data = data;
    }

    public RecordReadAdapter(ArrayList<Read> data, ReadFragment fragment, int type) {
        this.data = data;
        this.fragment = fragment;
        switch (type) {
            case 0:
                clickListener = new UnreadClickListener(this);
                break;
            case 1:
                clickListener = null;
                break;
            case 2:
                clickListener = new ReadClickListener(this);
                break;
        }
        if (drawable == null)
            drawable = fragment.getActivity().getTheme().obtainStyledAttributes(
                    new int[]{android.R.attr.selectableItemBackgroundBorderless}).getDrawable(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLines(2);
        textView.setOnClickListener(clickListener);
        textView.setOnLongClickListener(clickListener);
        textView.setPadding(dp * 8, dp * 5, dp * 2, dp * 5);
        textView.setForeground(drawable);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Read read = data.get(data.size() - 1 - position);
        SpannableString string = new SpannableString(String.format(
                locale, "%1$tF  %1$tR\n%2$s",
                read.getTime(), read.getTitle()));
        string.setSpan(colorSpan, 0, 17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.textView.setText(string);
        if (clickListener != null)
            clickListener.weakHashMap.put(holder.textView, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    static class ReadClickListener implements View.OnClickListener, View.OnLongClickListener {
        WeakHashMap<View, Integer> weakHashMap;
        WeakReference<RecordReadAdapter> weakReference;

        public ReadClickListener(RecordReadAdapter adapter) {
            weakHashMap = new WeakHashMap<>(32);
            weakReference = new WeakReference<>(adapter);
        }

        @Override
        public void onClick(View v) {
            RecordReadAdapter adapter = weakReference.get();
            Read read = adapter.data.get(
                    adapter.data.size() - 1 - weakHashMap.get(v));
            adapter.fragment.operateRightDrawer(false, true);
            adapter.fragment.clickTo(read.getType(), read.getLink(), read.getTitle());
        }

        @Override
        public boolean onLongClick(View v) {
            RecordReadAdapter adapter = weakReference.get();
            Read read = adapter.data.get(
                    adapter.data.size() - 1 - weakHashMap.get(v));
            adapter.fragment.insertIntoUnread(read.getType(), read.getLink(), read.getTitle());
            Toast.makeText(v.getContext(), "已加入未读", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    static class UnreadClickListener extends ReadClickListener {

        public UnreadClickListener(RecordReadAdapter adapter) {
            super(adapter);
        }

        @Override
        public void onClick(View v) {
            RecordReadAdapter adapter = weakReference.get();
            int position = weakHashMap.get(v);
            Read read = adapter.data.remove(adapter.data.size() - 1 - position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount() - position + 1);
            adapter.fragment.deleteFromUnread(read);
            adapter.fragment.operateRightDrawer(false, true);
            adapter.fragment.clickTo(read.getType(), read.getLink(), read.getTitle());
        }

        @Override
        public boolean onLongClick(View v) {
            RecordReadAdapter adapter = weakReference.get();
            int position = weakHashMap.get(v);
            Read read = adapter.data.remove(adapter.data.size() - 1 - position);
            adapter.notifyItemRangeRemoved(position, 1);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount() - position + 1);
            adapter.fragment.deleteFromUnread(read);
            adapter.fragment.insertIntoRead(read.getType(), read.getLink(), read.getTitle());
            Toast.makeText(v.getContext(), "已加入已读", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
