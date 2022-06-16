package com.garage.breco.smartgarage.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garage.breco.smartgarage.CircularTextView;
import com.garage.breco.smartgarage.CustomDateTimePicker;
import com.garage.breco.smartgarage.GarageStatus;
import com.garage.breco.smartgarage.Orientation;
import com.garage.breco.smartgarage.PhoneOS;
import com.garage.breco.smartgarage.R;
import com.garage.breco.smartgarage.StatusObject;
import com.garage.breco.smartgarage.TimeLineAdapter;
import com.garage.breco.smartgarage.TimeLineModel;
import com.garage.breco.smartgarage.TimeLineViewHolder;
import com.garage.breco.smartgarage.Utils;
import com.garage.breco.smartgarage.activity.MainActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.collect.Lists;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.protyposis.android.mediaplayer.MediaSource;
import net.protyposis.android.mediaplayer.VideoView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;


/**
 * Created by stormset on 2016. 11. 07.
 */
public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 60;
    static final private TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
    static Context context;
    static StatusObject statusObject;
    static int openedID;
    static int closedID;
    static int leftID;
    static int arrivedID;
    static int androidID;
    static int iosID;
    static int remoteID;
    static int colorBrandPrimary;
    static int colorClose;
    static int colorDisabled;
    static int colorOpen;
    static int colorRunning;
    static String stringOpenEntry;
    static String stringCloseEntry;
    static String stringArrivedEntry;
    static String stringLeftEntry;
    static String stringRemoved;
    static String stringUndo;
    private static String packageName;
    final Handler errorHandler = new Handler();
    private final List<TimeLineModel> mDataList = new ArrayList<>();
    public boolean isSearchEngineShow = false;
    int codeRequired = 0;
    RelativeLayout viewContainer;
    boolean isNoHistoriesShow;
    boolean called = false;
    boolean yourAction = false;
    boolean firstEntriesReady = false;
    int lastRemovedID = 0;
    boolean shouldDelete = true;
    Runnable errorTask;
    int timeout = 0;
    boolean opening = false;
    boolean deleteInProcess = false;
    boolean restored = false;
    boolean loadingInProcess = false;
    boolean cannotLoadMore = false;
    Query historyQuery;
    FirebaseDatabase database;
    DatabaseReference databaseHistoryReference;
    boolean isGoTopFabShown = false;
    boolean isShowTryAgain = false;
    boolean searching = false;
    int results;
    CircularTextView circularTextView;
    SpinKitView progressBar;
    ImageView tryAgain;
    TextView noHistories;
    FloatingActionButton goToStart;
    NestedScrollView nestedScrollView;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appbar;
    VideoView mVideoView;
    SpinKitView videoProgressBar;
    boolean isVideoProgressBarShow = false;
    boolean isPauseShow = false;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int lastStatus = -1;
    int lastCarPos = -1;
    int openingTimeProgress = 0;
    FloatingActionButton mainController;
    FloatingActionButton insideLight;
    FloatingActionButton outsideLight;
    FrameLayout videoCover;
    ImageView pauseView;
    CardView searchField;
    CardView searchField2;
    CardView timelineHolder;
    ImageView menuIcon;
    LinearLayout searchBarContent;
    TextView searchBarHint;
    LinearLayout searchFilters;
    TextInputEditText fromDate;
    TextInputEditText toDate;
    Calendar fromDateSelected;
    Calendar toDateSelected;
    Calendar fromTimeSelected;
    Calendar toTimeSelected;
    Date oldestEntry;
    Date latestEntry;
    LinearLayout noResultHolder;
    LinearLayout timeline;
    TextView noResultText;
    Button searchButton;
    AppCompatCheckBox checkOpened;
    AppCompatCheckBox checkClosed;
    AppCompatCheckBox checkLeft;
    AppCompatCheckBox checkArrived;
    boolean isNoResultShow = false;
    private View mFabHolder;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private MediaSource mMediaSource = null;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        packageName = requireContext().getPackageName();
        View view = inflater.inflate(R.layout.activity_main_content, container, false);
        initView(view);
        database = FirebaseDatabase.getInstance();
        statusObject = new StatusObject(database.getReference().child("state"));
        Bundle bundle = getArguments();
        statusObject.refreshStatus(Objects.requireNonNull(bundle).getString("state"));
        initStatus(false);
        prepareDatabaseReferences();
        reorderHistoryQuery();
        loadFirstEntries();
        getOldestEntry();
        getLatestEntry();
        return view;
    }

    private void initView(View view) {
        if (isAdded()) {
            context = getContext();
            openedID = getResources().getInteger(R.integer.open_id);
            closedID = getResources().getInteger(R.integer.close_id);
            arrivedID = getResources().getInteger(R.integer.car_arrived_id);
            leftID = getResources().getInteger(R.integer.car_left_id);
            androidID = getResources().getInteger(R.integer.android_id);
            iosID = getResources().getInteger(R.integer.ios_id);
            remoteID = getResources().getInteger(R.integer.remote_id);
            colorBrandPrimary = getResources().getColor(R.color.brand_primary);
            colorClose = getResources().getColor(R.color.close);
            colorDisabled = getResources().getColor(R.color.disabled);
            colorOpen = getResources().getColor(R.color.open);
            colorRunning = getResources().getColor(R.color.running);
            stringOpenEntry = getResources().getString(R.string.open_entry);
            stringCloseEntry = getResources().getString(R.string.close_entry);
            stringArrivedEntry = getResources().getString(R.string.car_arrived_entry);
            stringLeftEntry = getResources().getString(R.string.car_left_entry);
            stringRemoved = getResources().getString(R.string.removed);
            stringUndo = getResources().getString(R.string.undo);
            timeline = view.findViewById(R.id.content_timeline);
            searchButton = view.findViewById(R.id.apply_filters);
            noResultHolder = view.findViewById(R.id.no_result_holder);
            noResultText = view.findViewById(R.id.no_result_text);
            fromDate = view.findViewById(R.id.from_date);
            toDate = view.findViewById(R.id.to_date);
            searchFilters = view.findViewById(R.id.search_filters);
            searchBarHint = view.findViewById(R.id.search_bar_hint);
            menuIcon = view.findViewById(R.id.menu);
            searchBarContent = view.findViewById(R.id.search_bar_content);
            timelineHolder = view.findViewById(R.id.timeline_holder);
            viewContainer = view.findViewById(R.id.container);
            searchField2 = view.findViewById(R.id.search_field2);
            searchField = view.findViewById(R.id.search_field);
            videoCover = view.findViewById(R.id.video_cover);
            pauseView = view.findViewById(R.id.pause_view);
            mainController = view.findViewById(R.id.main_controller);
            insideLight = view.findViewById(R.id.inside_light);
            outsideLight = view.findViewById(R.id.outside_light);
            FirebaseMessaging.getInstance().subscribeToTopic("2803");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
            errorTask = new Runnable() {
                public void run() {
                    if (deleteInProcess && timeout <= 5) {
                        timeout++;
                        if (timeout == 5) {
                            timeout = 0;
                            if (context != null) {
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault_Light));
                                builder.setTitle("Nincs internetelérés!")
                                        .setMessage("A törölni kívánt bejegyzést a kapcsolat helyreálltával eltávolítjuk!")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            // continue with delete
                                        }).show();
                            }
                        } else {
                            errorHandler.postDelayed(this, 1000);
                        }
                    } else {
                        timeout = 0;
                    }
                }
            };

            circularTextView = view.findViewById(R.id.circularText);
            appbar = view.findViewById(R.id.myAppbar);
            appbar.addOnOffsetChangedListener(this);
            mFabHolder = view.findViewById(R.id.buttons);
            mRecyclerView = view.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(getLinearLayoutManager());
            mRecyclerView.setNestedScrollingEnabled(false);
            collapsingToolbarLayout = view.findViewById(R.id.myCollapsing);
            mVideoView = view.findViewById(R.id.videoView);
            videoProgressBar = view.findViewById(R.id.video_progress_bar);
            nestedScrollView = view.findViewById(R.id.scroll_view);
            coordinatorLayout = view.findViewById(R.id.annonce_main_coordinator);
            progressBar = view.findViewById(R.id.progress_bar);
            tryAgain = view.findViewById(R.id.try_again);
            noHistories = view.findViewById(R.id.no_histories);
            goToStart = view.findViewById(R.id.go_top);
            goToStart.setOnClickListener(view111 -> {
                ObjectAnimator.ofInt(nestedScrollView, "scrollY", 0).setDuration(1000).start();
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (nestedScrollView.getScrollY() < 10) {
                        appbar.setExpanded(true);
                    }
                }, 1000);
            });
            tryAgain.setOnClickListener(view110 -> {
                final Handler handler = new Handler();
                handler.postDelayed(() -> tryAgain.setEnabled(true), 600);
                tryAgain.setEnabled(false);
                RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(800);
                rotate.setInterpolator(new LinearInterpolator());
                tryAgain.startAnimation(rotate);
                reorderHistoryQuery();
                loadFirstEntries();
            });
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                float currentScrollPercentage = ((float) scrollY / (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) * 100;
                if (currentScrollPercentage > 25) {
                    if (!isGoTopFabShown) {
                        isGoTopFabShown = true;
                        goToStart.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                        goToStart.startAnimation(animation);
                    }
                } else if (isGoTopFabShown) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
                    goToStart.startAnimation(animation);
                    goToStart.setVisibility(View.GONE);
                    isGoTopFabShown = false;
                }
                if (currentScrollPercentage > 95) {
                    if (!loadingInProcess && !cannotLoadMore) {
                        loadMore();
                    }
                }

            });

            view.findViewById(R.id.search_bar).setOnClickListener(view19 -> {
                if (nestedScrollView.getScrollY() != 0) {
                    ObjectAnimator.ofInt(nestedScrollView, "scrollY", 0).setDuration(50).start();
                    appbar.setExpanded(true, false);
                }
                if (!isSearchEngineShow) openSearchEngine();
            });


            insideLight.setOnClickListener(view18 -> statusObject.switchInsideLight());

            outsideLight.setOnClickListener(view17 -> statusObject.switchOutsideLight());
            mVideoView.setZOrderMediaOverlay(true);
            mainController.setOnClickListener(view16 -> {
                if (codeRequired > 0) {
                    statusObject.sendRequest();
                    disableMainController();
                } else {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).openRevealKeypad();
                    }
                }
            });


            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    onSwipedItem(viewHolder);
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    if (!canExecute() || deleteInProcess) return 0;
                    return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.START | ItemTouchHelper.END);
                }
            };


            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
            mTimeLineAdapter = new TimeLineAdapter(mDataList, Orientation.VERTICAL, false, HomeFragment.this);
            mRecyclerView.setAdapter(mTimeLineAdapter);
            fromDate.setOnFocusChangeListener((view15, b) -> {
                if (b) {
                    if (isSearchEngineShow) {
                        openDatePicker(true);
                    }
                }
            });
            fromDate.setOnClickListener(view14 -> {
                if (isSearchEngineShow) {
                    openDatePicker(true);
                }
            });
            toDate.setOnFocusChangeListener((view13, b) -> {
                if (b) {
                    if (isSearchEngineShow) {
                        openDatePicker(false);
                    }
                }
            });
            toDate.setOnClickListener(view12 -> {
                if (isSearchEngineShow) {
                    openDatePicker(false);
                }
            });
            checkOpened = view.findViewById(R.id.opened);
            checkClosed = view.findViewById(R.id.closed);
            checkLeft = view.findViewById(R.id.left);
            checkArrived = view.findViewById(R.id.arrived);
            searchButton.setOnClickListener(view1 -> {
                if (fromDateSelected != null && toDateSelected != null && anyFilterChecked() && !searching) {
                    searching = true;
                    searchEntries();
                }
            });

            checkOpened.setOnCheckedChangeListener(this::onCheckedChanged);
            checkClosed.setOnCheckedChangeListener(this::onCheckedChanged);
            checkLeft.setOnCheckedChangeListener(this::onCheckedChanged);
            checkArrived.setOnCheckedChangeListener(this::onCheckedChanged);
        } else {
            new Handler().postDelayed(() -> initView(view), 500);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void searchEntries() {
        showProgressBar();
        mTimeLineAdapter.mFeedList.clear();
        mDataList.clear();
        mTimeLineAdapter.notifyDataSetChanged();
        int from;
        int to;
        Calendar fromDate = (Calendar) fromDateSelected.clone();
        Calendar toDate = (Calendar) toDateSelected.clone();
        if (fromTimeSelected != null) {
            fromDate.set(Calendar.HOUR_OF_DAY, fromTimeSelected.get(Calendar.HOUR_OF_DAY));
            fromDate.set(Calendar.MINUTE, fromTimeSelected.get(Calendar.MINUTE));
            fromDate.set(Calendar.SECOND, 0);
        } else {
            fromDate.set(Calendar.HOUR_OF_DAY, 0);
            fromDate.set(Calendar.MINUTE, 0);
            fromDate.set(Calendar.SECOND, 0);
        }
        if (toTimeSelected != null) {
            toDate.set(Calendar.HOUR_OF_DAY, toTimeSelected.get(Calendar.HOUR_OF_DAY));
            toDate.set(Calendar.MINUTE, toTimeSelected.get(Calendar.MINUTE));
            toDate.set(Calendar.SECOND, 59);
        } else {
            toDate.set(Calendar.HOUR_OF_DAY, 23);
            toDate.set(Calendar.MINUTE, 59);
            toDate.set(Calendar.SECOND, 59);
        }
        from = calendarTimeToTimestamp(fromDate);
        to = calendarTimeToTimestamp(toDate);
        loadFilteredEntries(from, to);
    }

    private void loadFilteredEntries(int fromTimestamp, int toTimestamp) {
        results = 0;
        cannotLoadMore = false;
        firstEntriesReady = true;
        final boolean[] added = {false};
        historyQuery.startAt(String.valueOf(fromTimestamp)).endAt(String.valueOf(toTimestamp)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new Handler().postDelayed(() -> hideProgressBar(), 300);
                new Handler().postDelayed(() -> {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        int event = Integer.parseInt(Objects.requireNonNull(child.child("event").getValue()).toString());
                        if (filterEvent(event)) {
                            results++;
                            added[0] = true;
                            String UNIX = child.getKey();
                            if ((event == arrivedID) || (event == leftID)) {
                                mDataList.add(mDataList.size(), createModelFromEntries(event, null, null, UNIX));
                            } else {
                                mDataList.add(mDataList.size(), createModelFromEntries(event, Objects.requireNonNull(child.child("phone_os").getValue()).toString(), Objects.requireNonNull(child.child("phone_name").getValue()).toString(), UNIX));
                            }
                            mTimeLineAdapter.notifyItemInserted(mDataList.size() - 1);
                        }
                    }
                    if (dataSnapshot.getChildrenCount() == 0 || !added[0]) {
                        showNoResultHolder("Nincs találat");
                    } else {
                        hideNoResultHolder();
                    }
                    searching = false;
                }, 600);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean filterEvent(int event) {
        if (event == openedID && checkOpened.isChecked() ||
                event == closedID && checkClosed.isChecked() ||
                event == leftID && checkLeft.isChecked()) return true;
        else return event == arrivedID && checkArrived.isChecked();
    }

    private void openDatePicker(boolean isFrom) {
        Calendar now = Calendar.getInstance();
        if (isFrom && fromDateSelected != null) now = fromDateSelected;
        else if (!isFrom && toDateSelected != null) now = toDateSelected;
        CustomDateTimePicker dpd = new CustomDateTimePicker();
        dpd.initialize((view, year, monthOfYear, dayOfMonth) -> {
                    if (isFrom) {
                        fromTimeSelected = null;
                        fromDateSelected = Calendar.getInstance();
                        fromDateSelected.set(year, monthOfYear, dayOfMonth);
                        fromDate.setText(getDate(String.valueOf(componentTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0)), false));
                        searchButton.setEnabled(true);
                        if (toDateSelected != null && anyFilterChecked()) {
                            searchButton.setAlpha(1f);
                        }
                        if (toTimeSelected != null) {
                            fromTimeSelected = Calendar.getInstance();
                            fromTimeSelected.set(1970, 0, 1, 0, 0, 0);
                            fromDate.setText(Objects.requireNonNull(fromDate.getText()).append('\n').append("00:00"));
                        }
                    } else {
                        toTimeSelected = null;
                        toDateSelected = Calendar.getInstance();
                        toDateSelected.set(year, monthOfYear, dayOfMonth);
                        toDate.setText(getDate(String.valueOf(componentTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0)), false));
                        searchButton.setEnabled(true);
                        if (fromDateSelected != null && anyFilterChecked()) {
                            searchButton.setAlpha(1f);
                        }
                        if (fromDateSelected == null) {
                            fromDateSelected = Calendar.getInstance();
                            fromDateSelected.setTime(oldestEntry);
                            fromDate.setText(getDate(String.valueOf(componentTimeToTimestamp(fromDateSelected.get(Calendar.YEAR), fromDateSelected.get(Calendar.MONTH), fromDateSelected.get(Calendar.DAY_OF_MONTH), 0, 0)), false));
                        }
                        if (fromTimeSelected != null) {
                            toTimeSelected = Calendar.getInstance();
                            toTimeSelected.set(1970, 0, 1, 23, 59, 59);
                            toDate.setText(Objects.requireNonNull(toDate.getText()).append('\n').append("23:59"));
                        }
                    }
                    if (fromDateSelected != null && toDateSelected != null && anyFilterChecked()) {
                        searchButton.setEnabled(true);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dpd.setOnTimeSetClickListener(view -> openTimePicker(isFrom));
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setAccentColor(colorBrandPrimary);
        if (oldestEntry != null) {
            Calendar min = Calendar.getInstance();
            min.setTime(oldestEntry);
            dpd.setMinDate(min);
        }
        if (latestEntry != null) {
            Calendar max = Calendar.getInstance();
            max.setTime(latestEntry);
            dpd.setMaxDate(max);
        }
        if (isFrom) {
            dpd.setTitle("Select Date From");
            if (toDateSelected != null) dpd.setMaxDate(toDateSelected);
        } else {
            dpd.setTitle("Select Date To");
            if (fromDateSelected != null) dpd.setMinDate(fromDateSelected);
        }
        dpd.show(requireActivity().getFragmentManager(), "DatePicker");
    }

    @SuppressLint("SetTextI18n")
    private void openTimePicker(boolean isFrom) {
        Calendar now = Calendar.getInstance();
        if (isFrom && fromTimeSelected != null) now = fromTimeSelected;
        else if (!isFrom && toTimeSelected != null) now = toTimeSelected;

        TimePickerDialog tpd;
        tpd = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    String Shour = String.valueOf(hourOfDay);
                    String Sminute = String.valueOf(minute);
                    if (hourOfDay < 10) {
                        Shour = '0' + String.valueOf(hourOfDay);
                    }
                    if (minute < 10) {
                        Sminute = '0' + String.valueOf(minute);
                    }
                    if (isFrom) {
                        fromTimeSelected = Calendar.getInstance();
                        fromTimeSelected.set(1970, 0, 1, hourOfDay, minute, 59);
                        String current = String.valueOf(fromDate.getText());
                        String[] result = current.split("\n", 2);
                        fromDate.setText(result[0] + '\n' + Shour + ":" + Sminute);
                        if (toTimeSelected == null && toDateSelected != null) {
                            toTimeSelected = Calendar.getInstance();
                            toTimeSelected.set(1970, 0, 1, 23, 59, 59);
                            toDate.setText(Objects.requireNonNull(toDate.getText()).append('\n').append("23:59"));
                        }
                    } else {
                        toTimeSelected = Calendar.getInstance();
                        toTimeSelected.set(1970, 0, 1, hourOfDay, minute, 59);
                        String current = String.valueOf(toDate.getText());
                        String[] result = current.split("\n", 2);
                        toDate.setText(result[0] + '\n' + Shour + ":" + Sminute);
                        if (fromTimeSelected == null) {
                            fromTimeSelected = Calendar.getInstance();
                            fromTimeSelected.set(1970, 0, 1, 0, 0, 0);
                            fromDate.setText(Objects.requireNonNull(fromDate.getText()).append('\n').append("00:00"));
                        }
                    }
                    if (fromDateSelected != null && toDateSelected != null && anyFilterChecked()) {
                        searchButton.setEnabled(true);
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        if (isFrom && toDateSelected != null && toTimeSelected != null) {
            if ((fromDateSelected.get(Calendar.YEAR) == toDateSelected.get(Calendar.YEAR)) && (fromDateSelected.get(Calendar.MONTH) == toDateSelected.get(Calendar.MONTH)) && (fromDateSelected.get(Calendar.DAY_OF_MONTH) == toDateSelected.get(Calendar.DAY_OF_MONTH))) {
                tpd.setMaxTime(toTimeSelected.get(Calendar.HOUR_OF_DAY), toTimeSelected.get(Calendar.MINUTE), 0);
            }
        }
        if (!isFrom && fromDateSelected != null && fromTimeSelected != null) {
            if ((fromDateSelected.get(Calendar.YEAR) == toDateSelected.get(Calendar.YEAR)) && (fromDateSelected.get(Calendar.MONTH) == toDateSelected.get(Calendar.MONTH)) && (fromDateSelected.get(Calendar.DAY_OF_MONTH) == toDateSelected.get(Calendar.DAY_OF_MONTH))) {
                tpd.setMinTime(fromTimeSelected.get(Calendar.HOUR_OF_DAY), fromTimeSelected.get(Calendar.MINUTE), 0);
            }
        }
        tpd.setVersion(TimePickerDialog.Version.VERSION_1);
        tpd.setAccentColor(colorBrandPrimary);
        tpd.show(requireActivity().getFragmentManager(), "TimePicker");
    }

    @Override
    public void onResume() {
        super.onResume();
        called = true;
        database.getReference().child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (called) {
                    searchButton.setEnabled(true);
                    called = false;
                    statusObject.refreshStatus(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    initStatus(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (anyFilterChecked() && fromDateSelected != null && toDateSelected != null) {
            searchButton.setEnabled(true);
            searchButton.setAlpha(1f);
        } else {
            searchButton.setEnabled(false);
            searchButton.setAlpha(0.2f);
            showNoResultHolder(getResources().getText(R.string.start_search_placeholder).toString());
        }
    }

    private boolean anyFilterChecked() {
        return checkOpened.isChecked() || checkClosed.isChecked() || checkLeft.isChecked() || checkArrived.isChecked();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }
        if (mMaxScrollSize != 0) {
            int currentScrollPercentage = (Math.abs(i)) * 100
                    / mMaxScrollSize;

            if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
                if (!mIsImageHidden) {
                    mIsImageHidden = true;
                    ViewCompat.animate(mFabHolder).scaleY(0).scaleX(0).start();
                    ViewCompat.animate(circularTextView).scaleY(0).scaleX(0).start();
                }
            }

            if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
                if (mIsImageHidden) {
                    mIsImageHidden = false;
                    ViewCompat.animate(mFabHolder).scaleY(1).scaleX(1).start();
                    ViewCompat.animate(circularTextView).scaleY(1).scaleX(1).start();
                }
            }
        }
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void onSwipedItem(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof TimeLineViewHolder) {
            deleteInProcess = true;
            yourAction = true;
            errorHandler.postDelayed(errorTask, 1000);
            final TimeLineModel deletedItem = mDataList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            databaseHistoryReference.child(mDataList.get(viewHolder.getAdapterPosition()).getID()).removeValue().addOnCompleteListener(task -> {
                deleteInProcess = false;
                mTimeLineAdapter.removeItem(viewHolder.getAdapterPosition());
                new Handler().postDelayed(() -> yourAction = false, 500);
            });
            restored = false;

            Snackbar snackbar = Snackbar.make(coordinatorLayout, stringRemoved, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(colorBrandPrimary);
            snackbar.setAction(stringUndo, view -> {
                if (!restored) {
                    restored = true;
                    snackbar.dismiss();
                    restoreDatabaseChild(deletedItem);
                    mTimeLineAdapter.restoreItem(deletedItem, deletedIndex);
                    if (isNoHistoriesShow) hideNoHistoryes();
                    else if (isNoResultShow) hideNoResultHolder();
                }
            });

            snackbar.show();
            if (shouldLoadMore() && !cannotLoadMore && !isSearchEngineShow) {
                loadMore();
            }

        }
    }

    private void restoreDatabaseChild(TimeLineModel deletedItem) {
        Map<String, String> userData = new HashMap<>();
        int eventID;
        int phoneOS;
        if (deletedItem.getStatus() == GarageStatus.AWAY) eventID = leftID;
        else if (deletedItem.getStatus() == GarageStatus.ARRIVED) eventID = arrivedID;
        else if (deletedItem.getStatus() == GarageStatus.OPENED) eventID = openedID;
        else eventID = closedID;
        if (deletedItem.getOS() == PhoneOS.ANDROID) phoneOS = androidID;
        else if (deletedItem.getOS() == PhoneOS.IOS) phoneOS = iosID;
        else phoneOS = remoteID;
        userData.put("event", String.valueOf(eventID));
        if (eventID == closedID || eventID == openedID) {
            userData.put("phone_os", String.valueOf(phoneOS));
            userData.put("phone_name", deletedItem.getPhoneName());
        }
        DatabaseReference m = databaseHistoryReference.child(deletedItem.getID());
        m.setValue(userData);
    }

    private void loadFirstEntries() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            cannotLoadMore = false;
            historyQuery.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hideProgressBar();
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        firstEntriesReady = true;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            int event = Integer.parseInt(Objects.requireNonNull(child.child("event").getValue()).toString());
                            String UNIX = child.getKey();
                            if ((event == arrivedID) || (event == leftID)) {
                                mDataList.add(0, createModelFromEntries(event, null, null, UNIX));
                            } else {
                                mDataList.add(0, createModelFromEntries(event, Objects.requireNonNull(child.child("phone_os").getValue()).toString(), Objects.requireNonNull(child.child("phone_name").getValue()).toString(), UNIX));
                            }
                            mTimeLineAdapter.notifyItemInserted(0);

                        }
                        if (dataSnapshot.getChildrenCount() == 0) {
                            showNoHistoryes();
                            cannotLoadMore = true;
                            if (!isShowTryAgain) showTryAgain();
                        } else {
                            hideNoHistoryes();
                            if (isShowTryAgain) hideTryAgain();
                        }
                        if (shouldLoadMore() && !cannotLoadMore) {
                            loadMore();
                        }
                    }, 600);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showTryAgain();
                }
            });
        }
    }

    private void loadMore() {
        if (!isSearchEngineShow) {
            setLoadingInProcess(true);
            historyQuery.endAt(String.valueOf(Integer.parseInt(mDataList.get(mDataList.size() - 1).getID()) - 1)).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<DataSnapshot> myList = Lists.newArrayList(dataSnapshot.getChildren());
                    Collections.reverse(myList);
                    for (DataSnapshot child : myList) {
                        if (child != null) {
                            int event = Integer.parseInt(Objects.requireNonNull(child.child("event").getValue()).toString());
                            String UNIX = child.getKey();
                            if ((event == arrivedID) || (event == leftID)) {
                                mDataList.add(mDataList.size(), createModelFromEntries(event, null, null, child.getKey()));
                            } else {
                                mDataList.add(mDataList.size(), createModelFromEntries(event, Objects.requireNonNull(child.child("phone_os").getValue()).toString(), Objects.requireNonNull(child.child("phone_name").getValue()).toString(), UNIX));
                            }
                            mTimeLineAdapter.notifyItemInserted((mDataList.size() - 1));
                        }
                    }

                    if (dataSnapshot.getChildrenCount() == 0) {
                        cannotLoadMore = true;
                    }
                    setLoadingInProcess(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    cannotLoadMore = true;
                    setLoadingInProcess(false);
                }
            });
        }
    }

    private void getOldestEntry() {
        historyQuery.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child != null) {
                        oldestEntry = new java.util.Date(Integer.parseInt(Objects.requireNonNull(child.getKey())) * 1000L);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getLatestEntry() {
        historyQuery.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child != null) {
                        latestEntry = new java.util.Date(Integer.parseInt(Objects.requireNonNull(child.getKey())) * 1000L);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private TimeLineModel createModelFromEntries(int event, String phoneOS, String phoneName, String UNIX) {
        int phoneOSID;
        if (phoneOS != null) phoneOSID = Integer.parseInt(phoneOS);
        else phoneOSID = -1;

        String mMessage = "";
        GarageStatus mGarageStatus = GarageStatus.CLOSED;
        PhoneOS mPhoneOS = null;

        if (event == openedID) {
            mMessage = stringOpenEntry;
            mGarageStatus = GarageStatus.OPENED;
        } else if (event == closedID) {
            mMessage = stringCloseEntry;
        } else if (event == leftID) {
            mMessage = stringLeftEntry;
            mGarageStatus = GarageStatus.AWAY;
        } else if (event == arrivedID) {
            mMessage = stringArrivedEntry;
            mGarageStatus = GarageStatus.ARRIVED;
        }
        if (phoneOSID == androidID) {
            mPhoneOS = PhoneOS.ANDROID;
        } else if (phoneOSID == iosID) {
            mPhoneOS = PhoneOS.IOS;
        } else if (phoneOSID == remoteID) {
            mPhoneOS = PhoneOS.REMOTE;
        }
        return new TimeLineModel(UNIX, mMessage, getDate(UNIX, true), mGarageStatus, getHour(UNIX), mPhoneOS, phoneName);
    }

    private String getDate(String UNIX, boolean localTimezone) {
        if (context != null) {
            String pattern = "yyyy/MM/dd";
            final DateFormat shortDateFormat = android.text.format.DateFormat.getDateFormat(context);
            if (shortDateFormat instanceof SimpleDateFormat) {
                pattern = ((SimpleDateFormat) shortDateFormat).toPattern();
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            if (localTimezone) sdf.setTimeZone(timeZone);
            return sdf.format(new Date(Integer.parseInt(UNIX) * 1000L));
        } else return "";
    }

    int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (int) (c.getTimeInMillis() / 1000L);
    }

    private Date cvtToGmt(Date date) {
        TimeZone tz = timeZone;
        Date ret = new Date(date.getTime() - tz.getRawOffset());
        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if (tz.inDaylightTime(ret)) {
            Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if (tz.inDaylightTime(dstDate)) {
                ret = dstDate;
            }
        }
        return ret;
    }

    int calendarTimeToTimestamp(Calendar calendar) {
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        mCal.setTime(cvtToGmt(calendar.getTime()));
        return (int) (mCal.getTimeInMillis() / 1000L);
    }

    private String getHour(String UNIX) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date(Integer.parseInt(UNIX) * 1000L));
    }

    private void showTryAgain() {
        isShowTryAgain = true;
        new Handler().postDelayed(() -> {
            tryAgain.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(tryAgain, "alpha", 1f).setDuration(600).start();
            hideProgressBar();
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(800);
            rotate.setInterpolator(new LinearInterpolator());
            tryAgain.startAnimation(rotate);
        }, 800);
    }

    private void hideTryAgain() {
        isShowTryAgain = false;
        ObjectAnimator.ofFloat(tryAgain, "alpha", 0f).setDuration(600).start();
        new Handler().postDelayed(() -> tryAgain.setVisibility(View.GONE), 600);
    }

    private void hideProgressBar() {
        ObjectAnimator.ofFloat(progressBar, "alpha", 0f).setDuration(600).start();
        new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 600);
    }

    private void showProgressBar() {
        if (isNoResultShow) hideNoResultHolder();
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(progressBar, "alpha", 1f).setDuration(600).start();
    }

    public void showNoHistoryes() {
        isNoHistoriesShow = true;
        noHistories.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(noHistories, "alpha", 1f).setDuration(600).start();
    }

    private void hideNoHistoryes() {
        isNoHistoriesShow = false;
        ObjectAnimator.ofFloat(noHistories, "alpha", 0f).setDuration(600).start();
        new Handler().postDelayed(() -> noHistories.setVisibility(View.GONE), 600);
    }

    private void prepareDatabaseReferences() {
        statusObject.setOnRequestTimeoutListener(() -> {
            enableMainController();
            snackbarMessage("Hiba!");
        });
        databaseHistoryReference = database.getReference().child("history");
        databaseHistoryReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (firstEntriesReady && !isSearchEngineShow) {
                    if (Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())) == lastRemovedID) {
                        shouldDelete = false;
                    } else if (mDataList != null && mDataList.size() > 0) {
                        if (Integer.parseInt(dataSnapshot.getKey()) != Integer.parseInt(mDataList.get(mDataList.size() - 1).getID())) {
                            int event = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("event").getValue()).toString());
                            String UNIX = dataSnapshot.getKey();
                            latestEntry = new java.util.Date(Integer.parseInt(UNIX) * 1000L);
                            if ((event == arrivedID) || (event == leftID)) {
                                mDataList.add(0, createModelFromEntries(event, null, null, dataSnapshot.getKey()));
                                mTimeLineAdapter.notifyItemInserted(0);
                            } else {
                                mDataList.add(0, createModelFromEntries(event, Objects.requireNonNull(dataSnapshot.child("phone_os").getValue()).toString(), Objects.requireNonNull(dataSnapshot.child("phone_name").getValue()).toString(), UNIX));
                                mTimeLineAdapter.notifyItemInserted(0);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int ID = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                lastRemovedID = ID;
                if (!yourAction) {
                    shouldDelete = true;
                    deleteItem(ID);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database.getReference().child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statusObject.refreshStatus(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                initStatus(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteItem(int ID) {
        new Handler().postDelayed(() -> {
            if (shouldDelete) {
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Integer.parseInt(mDataList.get(i).getID()) == ID) {
                        mTimeLineAdapter.removeItem(i);
                        snackbarMessage("Egy bejegyzést eltávolítottak.");
                    }
                }
                if (shouldLoadMore() && !cannotLoadMore) {
                    loadMore();
                }
            }
        }, 3000);
    }

    private void initStatus(boolean resume) {
        if (isAdded()) {
            if (isVideoProgressBarShow) hideVideoProgressBar();
            if (isPauseShow) hidePause();
            int videoDuration = 30000;
            int openingTime = 30000;
            int closingTime = 20000;
            int doorState = statusObject.getDoorState();
            long time = statusObject.getTime();
            int isCarInside = statusObject.getCarPos();
            boolean insideState = statusObject.getInsideLightState();
            boolean outsideState = statusObject.getOutsideLightState();
            if (insideState) {
                insideLight.setImageResource(R.drawable.ic_inside_on);
            } else {
                insideLight.setImageResource(R.drawable.ic_inside_off);
            }
            if (outsideState) {
                outsideLight.setImageResource(R.drawable.ic_outside_on);
            } else {
                outsideLight.setImageResource(R.drawable.ic_outside_off);
            }
            if (!opening) circularTextView.setTime(time);
            else if (openingTimeProgress < time) {
                openingTimeProgress = (int) time;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (opening) {
                            openingTimeProgress++;
                            circularTextView.setTime(openingTimeProgress);
                            handler.postDelayed(this, 1000);
                        }
                    }
                }, 1000);
            }
            time *= 1000;
            float speed;
            int seekTo;

            if (((doorState != lastStatus) || (isCarInside != lastCarPos)) || resume) {
                if (isCarInside == 1) {
                    switch (doorState) {
                        case 0:
                            opening = false;
                            openingTimeProgress = 0;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorClose));
                            mainController.setImageResource(R.drawable.ic_close);
                            enableMainController();
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewcar, 100, 1f, false, false);
                            break;
                        case 1:
                            opening = false;
                            openingTimeProgress = 0;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorOpen));
                            mainController.setImageResource(R.drawable.ic_open);
                            enableMainController();
                            initPlayer("android.resource://" + packageName + "/" + R.raw.openwcar, 100, 1f, false, false);
                            break;
                        case 2:
                            opening = true;
                            speed = (float) videoDuration / openingTime;
                            if (time > 2000) seekTo = (int) (time * speed);
                            else seekTo = 100;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorRunning));
                            mainController.setImageResource(R.drawable.ic_stop);
                            disableMainController();
                            initPlayer("android.resource://" + packageName + "/" + R.raw.openwcar, seekTo, speed, true, false);
                            break;
                        case 3:
                            opening = true;
                            speed = (float) videoDuration / closingTime;
                            if (time > 2000) seekTo = (int) (time * speed);
                            else seekTo = 100;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorRunning));
                            mainController.setImageResource(R.drawable.ic_stop);
                            disableMainController();
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewcar, seekTo, speed, true, false);
                            break;
                        case 4:
                            opening = false;
                            openingTimeProgress = 0;
                            speed = (float) videoDuration / closingTime;
                            seekTo = (int) (time * speed);
                            if (lastStatus == 2) {
                                mainController.setBackgroundTintList(ColorStateList.valueOf(colorClose));
                                mainController.setImageResource(R.drawable.ic_close);
                            } else {
                                mainController.setBackgroundTintList(ColorStateList.valueOf(colorOpen));
                                mainController.setImageResource(R.drawable.ic_open);
                            }
                            enableMainController();
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewcar, seekTo, speed, false, true);
                            break;
                    }
                } else {
                    switch (doorState) {
                        case 0:
                            opening = false;
                            openingTimeProgress = 0;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorClose));
                            mainController.setImageResource(R.drawable.ic_close);
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewucar, 100, 1f, false, false);
                            break;
                        case 1:
                            opening = false;
                            openingTimeProgress = 0;
                            mainController.setBackgroundTintList(ColorStateList.valueOf(colorOpen));
                            mainController.setImageResource(R.drawable.ic_open);
                            initPlayer("android.resource://" + packageName + "/" + R.raw.openwucar, 100, 1f, false, false);
                            break;
                        case 2:
                            opening = true;
                            speed = (float) videoDuration / openingTime;
                            if (time > 2000) seekTo = (int) (time * speed);
                            else seekTo = 100;
                            initPlayer("android.resource://" + packageName + "/" + R.raw.openwucar, seekTo, speed, true, false);
                            break;
                        case 3:
                            opening = true;
                            speed = (float) videoDuration / closingTime;
                            if (time > 2000) seekTo = (int) (time * speed);
                            else seekTo = 100;
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewucar, seekTo, speed, true, false);
                            break;
                        case 4:
                            opening = false;
                            openingTimeProgress = 0;
                            speed = (float) videoDuration / closingTime;
                            seekTo = (int) (time * speed);
                            initPlayer("android.resource://" + packageName + "/" + R.raw.closewucar, seekTo, speed, false, true);
                            break;
                    }
                }
                lastStatus = doorState;
                lastCarPos = isCarInside;
            }
        } else {
            new Handler().postDelayed(() -> initStatus(false), 500);
        }
    }

    private void initPlayer(String path, int seekTo, float speed, boolean startPlaying, boolean showPause) {
        if (context != null) {
            mMediaSource = null;
            mVideoView.setOnPreparedListener(vp -> {
                if (mVideoView.isPlaying() && !startPlaying) {
                    mVideoView.pause();
                } else if (!mVideoView.isPlaying() && startPlaying) {
                    mVideoView.start();
                }
                if (mVideoView.getAlpha() != 1f) {
                    ObjectAnimator.ofFloat(mVideoView, "alpha", 1f).setDuration(1000).start();
                    ObjectAnimator.ofFloat(videoProgressBar, "alpha", 0f).setDuration(600).start();
                }
                if (showPause) showPause();
            });

            mVideoView.setOnCompletionListener(mp -> showVideoProgressBar());


            Utils.MediaSourceAsyncCallbackHandler mMediaSourceAsyncCallbackHandler =
                    new Utils.MediaSourceAsyncCallbackHandler() {
                        @Override
                        public void onMediaSourceLoaded(MediaSource mediaSource) {
                            mMediaSource = mediaSource;
                            mVideoView.setVideoSource(mediaSource);
                            mVideoView.seekTo(seekTo);
                            mVideoView.setPlaybackSpeed(speed);
                        }

                        @Override
                        public void onException(Exception e) {
                            Log.e("MutedVideoView", "error loading video", e);
                        }
                    };
            if (mMediaSource == null) {
                Utils.uriToMediaSourceAsync(context, Uri.parse(path), mMediaSourceAsyncCallbackHandler);
            } else {
                mMediaSourceAsyncCallbackHandler.onMediaSourceLoaded(mMediaSource);
            }
        }
    }

    private void reorderHistoryQuery() {
        historyQuery = databaseHistoryReference.orderByKey();
    }

    private void setLoadingInProcess(boolean isLoadingInProcess) {
        loadingInProcess = isLoadingInProcess;
//        if (isLoadingInProcess) {
//            progressBarContainer.setVisibility(View.VISIBLE);
//            ObjectAnimator.ofFloat(progressBarContainer, "alpha", 1f).setDuration(600).start();
//        } else {
//            ObjectAnimator.ofFloat(progressBarContainer, "alpha", 0f).setDuration(600).start();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    progressBarContainer.setVisibility(View.GONE);
//                }
//            }, 600);
//        }
    }

    private boolean canExecute() {
        if (getActivity() != null && context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm != null && cm.getActiveNetworkInfo() != null;
        } else return false;
    }

    private void snackbarMessage(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private boolean shouldLoadMore() {
        if (getActivity() != null && context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            int m = mRecyclerView.getHeight();
            return m < height;
        } else return false;
    }

    private void showVideoProgressBar() {
        ObjectAnimator.ofFloat(videoProgressBar, "alpha", 1f).setDuration(600).start();
        ObjectAnimator.ofFloat(videoCover, "alpha", 0.65f).setDuration(600).start();
        isVideoProgressBarShow = true;
    }

    private void hideVideoProgressBar() {
        ObjectAnimator.ofFloat(videoProgressBar, "alpha", 0f).setDuration(600).start();
        ObjectAnimator.ofFloat(videoCover, "alpha", 0f).setDuration(600).start();
        isVideoProgressBarShow = false;
    }

    private void showPause() {
        ObjectAnimator.ofFloat(pauseView, "alpha", 1f).setDuration(600).start();
        ObjectAnimator.ofFloat(videoCover, "alpha", 0.65f).setDuration(600).start();
        isPauseShow = true;
    }

    private void hidePause() {
        ObjectAnimator.ofFloat(pauseView, "alpha", 0f).setDuration(600).start();
        ObjectAnimator.ofFloat(videoCover, "alpha", 0f).setDuration(600).start();
        isPauseShow = false;
    }

    private void hideFabs() {
        ViewCompat.animate(mFabHolder).scaleY(0).scaleX(0).start();
        ViewCompat.animate(circularTextView).scaleY(0).scaleX(0).start();
    }

    private void showFabs() {
        ViewCompat.animate(mFabHolder).scaleY(1).scaleX(1).start();
        ViewCompat.animate(circularTextView).scaleY(1).scaleX(1).start();
    }

    private void openSearchEngine() {
        if (firstEntriesReady) {
            mTimeLineAdapter.mFeedList.clear();
            mDataList.clear();
            mTimeLineAdapter.notifyDataSetChanged();
            if (fromDateSelected == null || toDateSelected == null) {
                searchButton.setEnabled(false);
                searchButton.setAlpha(0.2f);
                showNoResultHolder(getResources().getText(R.string.start_search_placeholder).toString());
            } else if (anyFilterChecked()) {
                searchButton.setEnabled(true);
                searchButton.setAlpha(1f);
            }
            ObjectAnimator.ofFloat(timeline, "alpha", 0f).setDuration(300).start();
            searchField2.setVisibility(View.VISIBLE);
            searchFilters.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(searchBarHint, "alpha", 0f).setDuration(150).start();
            isSearchEngineShow = true;
            menuIcon.setImageResource(R.drawable.ic_back);
            AppBarLayout.LayoutParams params2 = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            hideFabs();
            ValueAnimator va = ValueAnimator.ofInt((int) convertDpToPixel(35, context), (int) convertDpToPixel(190, context));
            va.setDuration(300);
            va.addUpdateListener(animation -> {
                Integer value = (Integer) animation.getAnimatedValue();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
                AppBarLayout.ScrollingViewBehavior behavior = (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
                Objects.requireNonNull(behavior).setOverlayTop(value);
                nestedScrollView.requestLayout();
            });
            va.start();
            ValueAnimator va2 = ValueAnimator.ofInt((int) convertDpToPixel(55, context), (int) convertDpToPixel(175, context));
            va2.setDuration(300);
            va2.addUpdateListener(animation -> {
                searchField2.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                searchField2.requestLayout();
            });
            va2.start();
            ValueAnimator va3 = ValueAnimator.ofFloat((int) convertDpToPixel(4, context), (int) convertDpToPixel(12, context));
            va3.setDuration(300);
            va3.addUpdateListener(animation -> {
                Float value = (float) animation.getAnimatedValue();
                searchField2.setRadius(value);
                searchField2.requestLayout();
            });
            va3.start();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            Objects.requireNonNull(behavior).setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            new Handler().postDelayed(() -> {
                mTimeLineAdapter.mFeedList.clear();
                mDataList.clear();
                mTimeLineAdapter.notifyDataSetChanged();
                ObjectAnimator.ofFloat(timeline, "alpha", 1f).setDuration(300).start();
                params2.setScrollFlags(0);
                params2.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
                searchField.setVisibility(View.GONE);
                appbar.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) timelineHolder.getLayoutParams();
                layoutParams.setMargins((int) convertDpToPixel(8, context), (int) convertDpToPixel(8, context), (int) convertDpToPixel(8, context), (int) convertDpToPixel(140, context));
                timelineHolder.requestLayout();
                ViewGroup.LayoutParams params12 = searchField2.getLayoutParams();
                params12.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                searchField2.setLayoutParams(params12);
                searchField2.requestLayout();
            }, 301);
            new Handler().postDelayed(() -> {
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params1.gravity = Gravity.TOP;
                searchBarContent.setLayoutParams(params1);
                searchBarContent.requestLayout();
            }, 100);
        }
    }

    public void closeSearchEngine() {
        hideNoResultHolder();
        mDataList.clear();
        mTimeLineAdapter.mFeedList.clear();
        mTimeLineAdapter.notifyDataSetChanged();
        loadFirstEntries();
        isSearchEngineShow = false;
        int delay = 0;
        nestedScrollView.scrollBy(0, 0);
        ObjectAnimator.ofInt(nestedScrollView, "scrollY", 0).setDuration(800).start();
        delay = 800;
        new Handler().postDelayed(() -> {
            searchFilters.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_menu);
            ObjectAnimator.ofFloat(searchBarHint, "alpha", 1f).setDuration(150).start();
            appbar.setExpanded(true);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) timelineHolder.getLayoutParams();
            Context ctx = requireContext();
            layoutParams.setMargins((int) convertDpToPixel(8, ctx), (int) convertDpToPixel(8, ctx), (int) convertDpToPixel(8, ctx), (int) convertDpToPixel(8, ctx));
            timelineHolder.requestLayout();
            searchField.setVisibility(View.VISIBLE);
            AppBarLayout.LayoutParams params2 = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            params2.setScrollFlags(0);
            params2.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            appbar.setVisibility(View.VISIBLE);
            showFabs();
            ValueAnimator va = ValueAnimator.ofInt((int) convertDpToPixel(190, getContext()), (int) convertDpToPixel(35, getContext()));
            va.setDuration(300);
            va.addUpdateListener(animation -> {
                Integer value = (Integer) animation.getAnimatedValue();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
                AppBarLayout.ScrollingViewBehavior behavior = (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
                Objects.requireNonNull(behavior).setOverlayTop(value);
                nestedScrollView.requestLayout();
            });
            new Handler().postDelayed(() -> searchField2.setVisibility(View.GONE), 300);
            va.start();
            ValueAnimator va2 = ValueAnimator.ofInt((int) convertDpToPixel(175, getContext()), (int) convertDpToPixel(55, getContext()));
            va2.setDuration(300);
            va2.addUpdateListener(animation -> {
                searchField2.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                searchField2.requestLayout();
            });
            va2.start();
            ValueAnimator va3 = ValueAnimator.ofFloat((int) convertDpToPixel(12, getContext()), (int) convertDpToPixel(4, getContext()));
            va3.setDuration(300);
            va3.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                searchField2.setRadius(value);
                searchField2.requestLayout();
            });
            va3.start();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            Objects.requireNonNull(behavior).setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return true;
                }
            });
        }, delay);

    }

    public boolean isSearchEngineShow() {
        return isSearchEngineShow;
    }

    public void showNoResultHolder(String text) {
        if (mTimeLineAdapter.mFeedList.size() == 0) {
            if (!(noResultText.getText() == text) || !isNoResultShow) {
                ObjectAnimator.ofFloat(noResultHolder, "alpha", 0f).setDuration(150).start();
                new Handler().postDelayed(() -> {
                    noResultText.setText(text);
                    ObjectAnimator.ofFloat(noResultHolder, "alpha", 1f).setDuration(150).start();
                }, 150);
            }
            isNoResultShow = true;
        }
    }

    public void hideNoResultHolder() {
        isNoResultShow = false;
        ObjectAnimator.ofFloat(noResultHolder, "alpha", 0f).setDuration(500).start();
    }

    public StatusObject getStatusObject() {
        return statusObject;
    }

    public void setCodeRequired(int codeRequiredTime) {
        codeRequired = codeRequiredTime;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                codeRequired--;
                if (codeRequired != 0) {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 0);
    }

    public void disableMainController() {
        mainController.setEnabled(false);
        ObjectAnimator.ofFloat(mainController, "alpha", 0.8f).setDuration(500).start();
    }

    public void enableMainController() {
        mainController.setEnabled(true);
        ObjectAnimator.ofFloat(mainController, "alpha", 1f).setDuration(500).start();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
