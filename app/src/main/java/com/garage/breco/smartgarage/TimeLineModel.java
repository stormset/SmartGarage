package com.garage.breco.smartgarage;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by stormset on 2016. 11. 02.
 */
public class TimeLineModel implements Parcelable {

    public static final Creator<TimeLineModel> CREATOR = new Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
    private final String mMessage;
    private final GarageStatus mStatus;
    private String mID;
    private String mDate;
    private String mHour;
    private String mPhoneName;
    private PhoneOS mOS;

    public TimeLineModel(String mID, String mMessage, String mDate, GarageStatus mStatus, String mHour, PhoneOS mOS, String mPhoneName) {
        this.mID = mID;
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
        this.mHour = mHour;
        this.mPhoneName = mPhoneName;
        this.mOS = mOS;
    }

    protected TimeLineModel(Parcel in) {
        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : GarageStatus.values()[tmpMStatus];
    }

    public String getID() {
        return mID;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public GarageStatus getStatus() {
        return mStatus;
    }

    public String getHour() {
        return mHour;
    }

    public PhoneOS getOS() {
        return mOS;
    }

    public String getPhoneName() {
        return mPhoneName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }
}
