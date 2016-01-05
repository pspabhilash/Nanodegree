/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.android.sunshine.app.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.myapp.abhilash.wear.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final Typeface BOLD_TYPEFACE = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
    private static String TAG= MyWatchFace.class.getSimpleName();
    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE,MMM d yyyy");
        private final SimpleDateFormat TIME_FORMAT_WITH_SECS = new SimpleDateFormat("hh:mm:ss aa");
        private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:MM a");
        private final String LOG_TAG = CanvasWatchFaceService.class.getSimpleName();



        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint,mTextTimePaint, mTextDatePaint, mTextDateAmbientPaint, mTextTempHighPaint, mTextTempLowPaint, mTextTempLowAmbientPaint;
        Paint mTextPaint;
        boolean mAmbient;


        private Calendar mCalender;


        //time dimensions
        float mXOffsetTime, mXOffsetTimeAmbient,mXOffsetTimeRound, mXOffsetTimeAmbinetRound, mYOffsetTime;
        //date dimensions
        float mXOffsetDate, mXOffsetDateRound, mYOffsetDate;

        float mDividerHeight, mDividerYOffset;

        float mWeatherYOffset;
        String mHighTemp, mLowTemp;
        Bitmap mWeatherImg;

        // same as MyWatchFaceService
        private static final String WEATHER_PATH = "/weather";

        //adding aa uuid as there are no request parameters
        private static final String KEY_UUID ="uuid";
        private static final String WEATHER_DATA = "/weather_data";
        private static final String KEY_HIGH = "high";
        private static final String KEY_LOW = "low";
        private static final String KEY_WEATHER_ID = "icon_id";


        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               mCalender.setTimeZone(TimeZone.getDefault());
                mCalender = Calendar.getInstance();
            }
        };
        int mTapCount;

        //float mXOffset;
        //float mYOffset;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MyWatchFace.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API).build();







        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)

                    .setAcceptsTapEvents(true)
                    .build());
            readDimensions();
            setupTextObjects();
        }

        private void setupTextObjects(){

            Resources resources = MyWatchFace.this.getResources();
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.primary));

            mTextTimePaint = new Paint();
            mTextTimePaint = createTextPaint(resources.getColor(R.color.primary_light));

            mCalender = Calendar.getInstance();

            mTextDatePaint = new Paint();
            mTextDatePaint = createTextPaint(resources.getColor(R.color.primary_light));

            mTextDateAmbientPaint = new Paint();
            mTextDateAmbientPaint = createTextPaint(Color.WHITE);

            mTextTempHighPaint = convertToBold(Color.WHITE);
            mTextTempLowPaint = createTextPaint(resources.getColor(R.color.primary_light));
            mTextTempLowAmbientPaint = createTextPaint(Color.WHITE);

        }

        private Paint convertToBold(int color) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setTypeface(BOLD_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }
        private void readDimensions() {
            Resources resources = MyWatchFace.this.getResources();
            mYOffsetTime = resources.getDimension(R.dimen.time_y_offset);
            mYOffsetDate = resources.getDimension(R.dimen.date_y_offset);
            mDividerYOffset = resources.getDimension(R.dimen.divider_y_offset);
            mWeatherYOffset = resources.getDimension(R.dimen.weather_y_offset);
            mDividerHeight = resources.getDimension(R.dimen.divider_height);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                if(mGoogleApiClient != null){
                    mGoogleApiClient.connect();
                }

                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalender.setTimeZone(TimeZone.getDefault());
                mCalender.setTime(new Date());

            } else {
                unregisterReceiver();
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    Wearable.DataApi.removeListener(mGoogleApiClient,this);
                    mGoogleApiClient.disconnect();
                }
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = MyWatchFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffsetTime = resources.getDimension(isRound ? R.dimen.time_x_offset_round : R.dimen.time_x_offset);
            mXOffsetDate = resources.getDimension(isRound ? R.dimen.date_x_offset_round : R.dimen.date_x_offset);
            mXOffsetTimeAmbient = resources.getDimension(isRound ? R.dimen.time_x_offset_round_ambient : R.dimen.time_x_offset_ambient);
            float timeTextSize = resources.getDimension(isRound ? R.dimen.time_text_size_round : R.dimen.time_text_size);
            float dateTextSize = resources.getDimension(isRound ? R.dimen.date_text_size_round : R.dimen.date_text_size);
            float tempTextSize = resources.getDimension(isRound ? R.dimen.temp_text_size_round : R.dimen.temp_text_size);

            mTextTimePaint.setTextSize(timeTextSize);
            mTextDatePaint.setTextSize(dateTextSize);
            mTextDateAmbientPaint.setTextSize(dateTextSize);
            mTextTempHighPaint.setTextSize(tempTextSize);
            mTextTempLowAmbientPaint.setTextSize(tempTextSize);
            mTextTempLowPaint.setTextSize(tempTextSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTextTimePaint.setAntiAlias(!inAmbientMode);
                    mTextDatePaint.setAntiAlias(!inAmbientMode);
                    mTextDateAmbientPaint.setAntiAlias(!inAmbientMode);
                    mTextTempHighPaint.setAntiAlias(!inAmbientMode);
                    mTextTempLowAmbientPaint.setAntiAlias(!inAmbientMode);
                    mTextTempLowPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Resources resources = MyWatchFace.this.getResources();
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    mTapCount++;
                    mBackgroundPaint.setColor(resources.getColor(mTapCount % 2 == 0 ?
                            R.color.primary_dark : R.color.primary));
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.

            mCalender.setTime(new Date());
            String timeStr = getTimeFormattedString();
            float xOffsetTime = mTextTimePaint.measureText(timeStr) / 2;
            canvas.drawText(timeStr, bounds.centerX() - xOffsetTime, mYOffsetTime, mTextTimePaint);

            Paint datePaint = mAmbient ? mTextDateAmbientPaint : mTextDatePaint;
            String dateStr = getDateFormattedString();
            canvas.drawText(dateStr, bounds.centerX() - mXOffsetDate, mYOffsetDate, datePaint);
            //divider - added 20 as spacing offset
            canvas.drawLine(bounds.centerX() - 20, mDividerYOffset, bounds.centerX() + 20, mDividerYOffset, datePaint);
            if (mHighTemp != null && mLowTemp != null) {
                float highTextLen = mTextTempHighPaint.measureText(mHighTemp);
                if (mAmbient) {
                    float lowTextLen = mTextTempLowAmbientPaint.measureText(mLowTemp);
                    float xOffset = bounds.centerX() - ((highTextLen + lowTextLen) / 2);
                    canvas.drawText(mHighTemp, xOffset, mWeatherYOffset, mTextTempHighPaint);
                    //Adding arbitary 40 to give space between High and Low temps
                    canvas.drawText(mLowTemp, xOffset + highTextLen + 40, mWeatherYOffset, mTextTempLowAmbientPaint);
                } else {
                    float xOffset = bounds.centerX() - (highTextLen / 2);
                    canvas.drawText(mHighTemp, xOffset, mWeatherYOffset, mTextTempHighPaint);
                    //Adding arbitary 40 to give space between High and Low temps
                    canvas.drawText(mLowTemp, bounds.centerX() + (highTextLen / 2) + 40, mWeatherYOffset, mTextTempLowPaint);
                    //Creating space for the icon and offset
                    float iconXOffset = bounds.centerX() - ((highTextLen / 2) + mWeatherImg.getWidth());
                    canvas.drawBitmap(mWeatherImg, iconXOffset, mWeatherYOffset - mWeatherImg.getHeight(), null);
                }
            }

        }
        private String getDateFormattedString() {
            return DATE_FORMAT.format(mCalender.getTime());
        }

        private String getTimeFormattedString() {
            return mAmbient ? TIME_FORMAT.format(mCalender.getTime()) : TIME_FORMAT_WITH_SECS.format(mCalender.getTime());
        }


        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
            getWeatherInfo();
        }
        private void getWeatherInfo() {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEATHER_PATH);
            putDataMapRequest.getDataMap().putString(KEY_UUID, UUID.randomUUID().toString());
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Data call failed");
                            }
                        }
                    });
        }
        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            for (DataEvent dataEvent : dataEventBuffer) {
                if (DataEvent.TYPE_CHANGED == dataEvent.getType()) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    String path = dataEvent.getDataItem().getUri().getPath();

                    if (path.equals(WEATHER_DATA)) {
                        if (dataMap.containsKey(KEY_HIGH)) {
                            mHighTemp = dataMap.getString(KEY_HIGH);
                        } else {
                            Log.e(TAG, "High Temp not found from data sync");
                        }

                        if (dataMap.containsKey(KEY_LOW)) {
                            mLowTemp = dataMap.getString(KEY_LOW);
                        } else {
                            Log.e(TAG, "High Temp not found from data sync");
                        }

                        if (dataMap.containsKey(KEY_WEATHER_ID)) {
                            int weatherId = dataMap.getInt(KEY_WEATHER_ID);
                            Drawable b = getResources().getDrawable(getIconResourceForWeatherCondition(weatherId));
                            Bitmap icon = ((BitmapDrawable) b).getBitmap();
                            float scaledWidth = (mTextTempHighPaint.getTextSize() / icon.getHeight()) * icon.getWidth();
                            mWeatherImg = Bitmap.createScaledBitmap(icon, (int) scaledWidth, (int) mTextTempHighPaint.getTextSize(), true);
                            Log.d(LOG_TAG, "mWeatherImg" + mWeatherImg);
                        } else {
                            Log.e(TAG, "Weather ID not found from data sync");
                        }
                        invalidate();
                    }
                }
            }
        }

        private int getIconResourceForWeatherCondition(int weatherId) {
            //Attempted WeatherID resolutionto the icon as R.drawable.icon within adaptor, but does not work.
            //copying from Utility.java
            // Based on weather code data found at:
            // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
            if (weatherId >= 200 && weatherId <= 232) {
                return R.drawable.ic_storm;
            } else if (weatherId >= 300 && weatherId <= 321) {
                return R.drawable.ic_light_rain;
            } else if (weatherId >= 500 && weatherId <= 504) {
                return R.drawable.ic_rain;
            } else if (weatherId == 511) {
                return R.drawable.ic_snow;
            } else if (weatherId >= 520 && weatherId <= 531) {
                return R.drawable.ic_rain;
            } else if (weatherId >= 600 && weatherId <= 622) {
                return R.drawable.ic_snow;
            } else if (weatherId >= 701 && weatherId <= 761) {
                return R.drawable.ic_fog;
            } else if (weatherId == 761 || weatherId == 781) {
                return R.drawable.ic_storm;
            } else if (weatherId == 800) {
                return R.drawable.ic_clear;
            } else if (weatherId == 801) {
                return R.drawable.ic_light_clouds;
            } else if (weatherId >= 802 && weatherId <= 804) {
                return R.drawable.ic_cloudy;
            }
            return -1;
        }




        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }
}
