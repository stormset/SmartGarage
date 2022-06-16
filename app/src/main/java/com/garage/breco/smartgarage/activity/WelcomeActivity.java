package com.garage.breco.smartgarage.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.garage.breco.smartgarage.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


/**
 * Created by stormset on 2016. 10. 29.
 */
public class WelcomeActivity extends AppCompatActivity {

    ImageView tryAgain;
    SpinKitView progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
        tryAgain = findViewById(R.id.try_again);
        final View parent = (View) tryAgain.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            tryAgain.getHitRect(rect);
            rect.top -= 100;
            rect.left -= 100;
            rect.bottom += 100;
            rect.right += 100;
            parent.setTouchDelegate(new TouchDelegate(rect, tryAgain));
        });
        login();
        tryAgain.setOnClickListener(view -> tryAgain());

    }

    private void snackbarMessage(String message) {
        // Create the Snackbar
        View containerLayout = findViewById(R.id.welcome_container);
        Snackbar snackbar = Snackbar.make(containerLayout, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = layout.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ghost_white));
        snackbar.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
    }

    private void showTryAgain() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            tryAgain.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(tryAgain, "alpha", 1f).setDuration(600).start();
            ObjectAnimator.ofFloat(progressBar, "alpha", 0f).setDuration(600).start();
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(800);
            rotate.setInterpolator(new LinearInterpolator());
            tryAgain.startAnimation(rotate);
        }, 800);
    }

    private void hideTryAgain() {
        ObjectAnimator.ofFloat(tryAgain, "alpha", 0f).setDuration(600).start();
        ObjectAnimator.ofFloat(progressBar, "alpha", 1f).setDuration(600).start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> tryAgain.setVisibility(View.GONE), 600);
    }

    private void tryAgain() {
        int delay;
        if (isNetworkConnected()) {
            delay = 800;
        } else {
            delay = 0;
        }
        RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(800);
        rotate.setInterpolator(new LinearInterpolator());
        tryAgain.startAnimation(rotate);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (isNetworkConnected()) {
                new InternetCheck(internet -> {
                    if (internet) {
                        hideTryAgain();
                        login();
                    } else {
                        snackbarMessage("Nincs internetelérés.");
                    }
                });
            } else {
                snackbarMessage("Csatlakozzon egy hálózathoz!");
            }
        }, delay);
    }

    private void login() {
        if (isNetworkConnected()) {
            new InternetCheck(internet -> {
                if (internet) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        startLoginActivity();
                    } else {
                        if (firebaseUser.isEmailVerified()) {
                            startMainActivity();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(WelcomeActivity.this, android.R.style.Theme_DeviceDefault_Light));
                            builder.setOnDismissListener(dialogInterface -> {
                                mAuth.signOut();
                                startLoginActivity();
                            });
                            builder.setTitle("Regisztráció megerősítése szükséges")
                                    .setMessage("Erősítse meg regisztrációját, hogy használhassa szolgáltatásainkat!")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        mAuth.signOut();
                                        startLoginActivity();
                                    })
                                    .setNegativeButton("ÚJRAKÜLDÉS", (dialog, which) -> resendVerificationEmail()).show();
                        }
                    }
                } else {
                    showTryAgain();
                    snackbarMessage("Nincs internetelérés.");
                }
            });
        } else {
            showTryAgain();
            snackbarMessage("Csatlakozzon egy hálózathoz!");
        }
    }

    private void startMainActivity() {
        FirebaseDatabase.getInstance().getReference().child("state").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("state", Objects.requireNonNull(dataSnapshot.getValue()).toString());
                i.putExtras(b);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startLoginActivity() {
        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void resendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(new ContextThemeWrapper(WelcomeActivity.this, android.R.style.Theme_DeviceDefault_Light));
                            builder.setOnDismissListener(dialogInterface -> startLoginActivity());
                            builder.setTitle("Regisztráció megerősítése")
                                    .setMessage("Elküldtük a(z) " + user.getEmail() + " e-mail címre a regisztráció megerősítéséhez szükséges linket.")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> startLoginActivity()).show();
                        } else {
                            new Handler().postDelayed(() -> {
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(new ContextThemeWrapper(WelcomeActivity.this, android.R.style.Theme_DeviceDefault_Light));
                                builder.setTitle("Próbálja újra később!")
                                        .setMessage("Hiba lépett fel az e-mail újraküldése során.")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        }).show();
                            }, 1000);
                        }
                    });
        }
    }
}
