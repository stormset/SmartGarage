package com.garage.breco.smartgarage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.garage.breco.smartgarage.R;
import com.garage.breco.smartgarage.fragment.HomeFragment;
import com.garage.breco.smartgarage.fragment.NotificationsFragment;
import com.garage.breco.smartgarage.fragment.ScheduleFragment;
import com.garage.breco.smartgarage.fragment.SecurityFragment;
import com.garage.breco.smartgarage.fragment.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vlstr.blurdialog.BlurDialog;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.yugy.github.reveallayout.RevealLayout;


/**
 * Created by stormset on 2016. 10. 29.
 */
public class MainActivity extends AppCompatActivity {
    private static final String CODE = "0000"; // TODO: add pin setting
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_SCHEDULES = "schedules";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SECURITY = "security";
    private static final String TAG_SETTINGS = "settings";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    Bundle status;
    TextView toManyText;
    TextView attentionText;
    Animation animShake;
    boolean isCodePanelShow = false;
    LottieAnimationView animationView;
    int wrong_ct = 0;
    int restartWithin = 60;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = getIntent().getExtras();
        FrameLayout frameLayout = findViewById(R.id.frame);
        toManyText = findViewById(R.id.to_many_text);
        attentionText = findViewById(R.id.attention_holder);
        toolbar = findViewById(R.id.toolbar);
        animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        animationView = findViewById(R.id.animation_view);
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        findViewById(R.id.code_panel_back).setOnClickListener(view -> {
            if (isCodePanelShow) closeRevealKeypad();
        });
        View headerView = navigationView.getHeaderView(0);
        RelativeLayout header = headerView.findViewById(R.id.view_container);
        header.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(i);
        });
        BlurDialog blurDialog = findViewById(R.id.code_holder);
        blurDialog.create(getWindow().getDecorView(), 20);
        blurDialog.show();
        RevealLayout mRevealLayout = findViewById(R.id.reveal_layout);
        mRevealLayout.hide();
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just ic_close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // add to the message queue
        mHandler.post(mPendingRunnable);

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (CURRENT_TAG) {
            case TAG_HOME:
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(status);
                toolbar.setVisibility(View.GONE);
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                return homeFragment;
            case TAG_SECURITY:
                SecurityFragment securityFragment = new SecurityFragment();
                toolbar.setVisibility(View.VISIBLE);
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                return securityFragment;
            case TAG_SCHEDULES:
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                toolbar.setVisibility(View.VISIBLE);
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                return scheduleFragment;
            case TAG_NOTIFICATIONS:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                toolbar.setVisibility(View.VISIBLE);
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                return notificationsFragment;

            case TAG_SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                toolbar.setVisibility(View.VISIBLE);
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                return settingsFragment;
            default:
                toolbar.setVisibility(View.GONE);
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                HomeFragment defHomeFragment = new HomeFragment();
                defHomeFragment.setArguments(status);
                return defHomeFragment;
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @SuppressLint("NonConstantResourceId")
    private void setUpNavigationView() {
        // Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            //Check to see which item was being clicked and perform appropriate action
            switch (menuItem.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                case R.id.home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    break;
                case R.id.nav_schedules:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_SCHEDULES;
                    break;
                case R.id.nav_alerts:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_NOTIFICATIONS;
                    break;
                case R.id.nav_security:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_SECURITY;
                    break;
                case R.id.nav_settings:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_SETTINGS;
                    break;
                default:
                    navItemIndex = 0;
            }

            //Checking if the item is in checked state or not, if not make it in checked state
            menuItem.setChecked(!menuItem.isChecked());
            menuItem.setChecked(true);

            loadHomeFragment();

            return true;
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer ic_open as we don't want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (isCodePanelShow) {
            closeRevealKeypad();
            return;
        } else if (getHomeFragment() instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
            if (Objects.requireNonNull(homeFragment).isSearchEngineShow()) {
                homeFragment.closeSearchEngine();
                return;
            }
        } else {
            // flag to load home fragment when user presses back key
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    public void openRevealKeypad() {
        isCodePanelShow = true;
        animationView.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(findViewById(R.id.video_holder), "alpha", 0.6f).setDuration(100).start();
        PinLockView mPinLockView = findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        if (wrong_ct > 0) disableEnableControls(false, mPinLockView);
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (pin.equals(CODE)) {
                    HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                    Objects.requireNonNull(homeFragment).getStatusObject().sendRequest();
                    homeFragment.disableMainController();
                    findViewById(R.id.main_controller).setEnabled(false);
                    homeFragment.setCodeRequired(300);
                    mPinLockView.resetPinLockView();
                    wrong_ct = 0;
                    attentionText.setText(" ");
                    animationView.setAnimation(R.raw.success);
                    animationView.playAnimation();

                    animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            animationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animationView.cancelAnimation();
                            closeRevealKeypad();
                            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                            if (Objects.requireNonNull(homeFragment).isSearchEngineShow()) {
                                homeFragment.closeSearchEngine();
                            }
                        }
                    });
                } else {
                    mIndicatorDots.startAnimation(animShake);
                    attentionText.startAnimation(animShake);
                    vibrate();
                    attentionText.setText(getResources().getText(R.string.wrong_pin_code));
                    new Handler().postDelayed(mPinLockView::resetPinLockView, 250);
                    if (++wrong_ct == 5) {
                        disableEnableControls(false, mPinLockView);
                        toManyText.setVisibility(View.VISIBLE);
                        attentionText.startAnimation(animShake);
                        toManyText.startAnimation(animShake);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    wrong_ct = 0;
                                    restartWithin = 61;
                                    disableEnableControls(true, mPinLockView);
                                });
                            }
                        }, 60000);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (wrong_ct > 0) {
                                    restartWithin--;
                                    attentionText.setText(String.format(getResources().getString(R.string.wait_until_retry), restartWithin));
                                    handler.postDelayed(this, 1000);
                                } else {
                                    attentionText.setText(" ");
                                    toManyText.setVisibility(View.GONE);
                                }
                            }
                        }, 0);
                    }
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
        RevealLayout mRevealLayout = findViewById(R.id.reveal_layout);
        mRevealLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> mRevealLayout.show(300), 100);

    }

    public void closeRevealKeypad() {
        isCodePanelShow = false;
        ObjectAnimator.ofFloat(findViewById(R.id.video_holder), "alpha", 1f).setDuration(100).start();
        RevealLayout mRevealLayout = findViewById(R.id.reveal_layout);
        mRevealLayout.hide(500);
        new Handler().postDelayed(() -> mRevealLayout.setVisibility(View.GONE), 500);
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(200);
        }
    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }
}
