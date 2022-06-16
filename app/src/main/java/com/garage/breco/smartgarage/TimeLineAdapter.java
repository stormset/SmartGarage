package com.garage.breco.smartgarage;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.garage.breco.smartgarage.fragment.HomeFragment;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;


public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private final Orientation mOrientation;
    private final boolean mWithLinePadding;
    private final HomeFragment homeFragment;
    public List<TimeLineModel> mFeedList;
    private Context mContext;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding, HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        if (mOrientation == Orientation.HORIZONTAL) {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_horizontal_line_padding : R.layout.item_timeline_horizontal, parent, false);
        } else {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_line_padding : R.layout.item_timeline, parent, false);
        }

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, int position) {
        holder.mPhoneName.setSelected(false);
        holder.mCardView.setOnClickListener(view -> scrollText(holder.mPhoneName));
        TimeLineModel timeLineModel = mFeedList.get(position);

        if (timeLineModel.getStatus() == GarageStatus.OPENED) {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_opened));
        } else if (timeLineModel.getStatus() == GarageStatus.CLOSED) {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_closed));
        } else if (timeLineModel.getStatus() == GarageStatus.AWAY) {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_away));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_arrived));
        }
        if (timeLineModel.getOS() != null) {
            holder.mPhoneOS.setVisibility(View.VISIBLE);
            if (timeLineModel.getOS() == PhoneOS.ANDROID) {
                holder.mPhoneOS.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_android));
            } else if (timeLineModel.getOS() == PhoneOS.IOS) {
                holder.mPhoneOS.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_ios));
            } else {
                holder.mPhoneOS.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_remote));
            }
        } else {
            holder.mPhoneOS.setVisibility(View.GONE);
        }

        if (!timeLineModel.getDate().isEmpty()) {
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(timeLineModel.getDate());
        } else
            holder.mDate.setVisibility(View.GONE);

        holder.mMessage.setText(timeLineModel.getMessage());
        holder.mHour.setText(timeLineModel.getHour());
        if (timeLineModel.getOS() != null) {
            holder.mPhoneName.setVisibility(View.VISIBLE);
            holder.mPhoneName.setText(timeLineModel.getPhoneName());
        } else {
            holder.mPhoneName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    public void removeItem(int position) {
        mFeedList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        notifyItemRemoved(position);
        if (mFeedList.size() == 0) {
            if (homeFragment.isSearchEngineShow())
                homeFragment.showNoResultHolder("Nincs találat.");
            else homeFragment.showNoHistoryes();
        }
    }

    public void restoreItem(TimeLineModel item, int position) {
        mFeedList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    private void scrollText(final TextView textView) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> textView.setSelected(true), 100);
        final Handler handler2 = new Handler();
        handler2.postDelayed(() -> textView.setSelected(false), 8000);
    }
}
