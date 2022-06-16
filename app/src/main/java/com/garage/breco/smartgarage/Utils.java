package com.garage.breco.smartgarage;
/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import net.protyposis.android.mediaplayer.MediaSource;
import net.protyposis.android.mediaplayer.UriSource;
import net.protyposis.android.mediaplayer.dash.AdaptationLogic;
import net.protyposis.android.mediaplayer.dash.DashSource;
import net.protyposis.android.mediaplayer.dash.SimpleRateBasedAdaptationLogic;


/**
 * Created by maguggen on 28.08.2014.
 * Extended by stormset on 2016. 11. 04.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    // resource loading utils
    public static MediaSource uriToMediaSource(Context context, Uri uri) {
        MediaSource source = null;

        // A DASH source is either detected if the given URL has an .mpd extension or if the DASH
        // pseudo protocol has been prepended.
        if (uri.toString().endsWith(".mpd") || uri.toString().startsWith("dash://")) {
            AdaptationLogic adaptationLogic;

            // Strip dash:// pseudo protocol
            if (uri.toString().startsWith("dash://")) {
                uri = Uri.parse(uri.toString().substring(7));
            }

            //adaptationLogic = new ConstantPropertyBasedLogic(ConstantPropertyBasedLogic.Mode.HIGHEST_BITRATE);
            adaptationLogic = new SimpleRateBasedAdaptationLogic();

            source = new DashSource(context, uri, adaptationLogic);
        } else {
            source = new UriSource(context, uri);
        }
        return source;
    }

    public static void uriToMediaSourceAsync(final Context context, Uri uri, MediaSourceAsyncCallbackHandler callback) {
        LoadMediaSourceAsyncTask loadingTask = new LoadMediaSourceAsyncTask(context, callback);

        try {
            loadingTask.execute(uri).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // Date-time utils
    public static String timeToDisplayString(Context context, long days, long hours, long minutes) {
        String day, hour, minute;
        StringBuilder text = new StringBuilder();

        if (days > 1) day = context.getResources().getString(R.string.day_spelling_more);
        else day = context.getResources().getString(R.string.day_spelling_1);
        if (hours > 1) hour = context.getResources().getString(R.string.hour_spelling_more);
        else hour = context.getResources().getString(R.string.hour_spelling_1);
        if (minutes > 1) minute = context.getResources().getString(R.string.minute_spelling_more);
        else minute = context.getResources().getString(R.string.minute_spelling_1);
        text.append(context.getResources().getString(R.string.notification_left_open_1st_syntax));
        if (days > 0) {
            if (hours > 0)
                text.append(days).append(" ").append(day).append(" ").append(hours).append(" ").append(hour);
            else
                text.append(days).append(" ").append(day);
        } else if (hours > 0) {
            if (minutes > 0)
                text.append(hours).append(" ").append(hour).append(" ").append(minutes).append(" ").append(minute);
            else
                text.append(hours).append(" ").append(hour);
        } else {
            text.append(minutes).append(" ").append(minute);
        }
        text.append(context.getResources().getString(R.string.notification_left_open_2st_syntax));

        return text.toString();
    }

    public interface MediaSourceAsyncCallbackHandler {
        void onMediaSourceLoaded(MediaSource mediaSource);

        void onException(Exception e);
    }

    private static class LoadMediaSourceAsyncTask extends AsyncTask<Uri, Void, MediaSource> {

        private final Context mContext;
        private final MediaSourceAsyncCallbackHandler mCallbackHandler;
        private MediaSource mMediaSource;
        private Exception mException;

        public LoadMediaSourceAsyncTask(Context context, MediaSourceAsyncCallbackHandler callbackHandler) {
            mContext = context;
            mCallbackHandler = callbackHandler;
        }

        @Override
        protected MediaSource doInBackground(Uri... params) {
            try {
                mMediaSource = Utils.uriToMediaSource(mContext, params[0]);
                return mMediaSource;
            } catch (Exception e) {
                mException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(MediaSource mediaSource) {
            if (mediaSource != null && mCallbackHandler != null) {
                if (mException != null) {
                    mCallbackHandler.onException(mException);
                } else {
                    mCallbackHandler.onMediaSourceLoaded(mMediaSource);
                }
            }
        }
    }
}
