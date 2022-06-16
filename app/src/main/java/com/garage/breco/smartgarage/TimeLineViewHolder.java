package com.garage.breco.smartgarage;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by stormset on 2016. 11. 02.
 */
@SuppressLint("NonConstantResourceId")
public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.card_view)
    CardView mCardView;
    @BindView(R.id.text_timeline_date)
    TextView mDate;
    @BindView(R.id.text_timeline_title)
    TextView mMessage;
    @BindView(R.id.time_marker)
    TimelineView mTimelineView;
    @BindView(R.id.phone_name)
    TextView mPhoneName;
    @BindView(R.id.phone_os)
    ImageView mPhoneOS;
    @BindView(R.id.hour_marker)
    TextView mHour;
    @BindView(R.id.foreground)
    LinearLayout mForeground;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mTimelineView.initLine(viewType);
    }
}
