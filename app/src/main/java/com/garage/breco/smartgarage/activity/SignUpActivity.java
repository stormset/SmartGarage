package com.garage.breco.smartgarage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.garage.breco.smartgarage.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import me.yugy.github.reveallayout.RevealLayout;


/**
 * Created by stormset on 2016. 10. 29.
 */
public class SignUpActivity extends AppCompatActivity {
    private static final int fromShrinkDP = 180;
    private static final int toShrinkDP = 38;
    EditText mEmail;
    EditText mPassword;
    EditText mPassword2;
    Button loginButton;
    RelativeLayout loginButtonHolder;
    ProgressBar progressBar;
    boolean changed = false;
    private FirebaseAuth mAuth;
    private RevealLayout mRevealLayout;

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mAuth = FirebaseAuth.getInstance();


        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.password2);

        mPassword2.setOnFocusChangeListener((view, b) -> {
            if (b) {
                String password = mPassword.getText().toString();
                if (password.isEmpty()) {
                    mPassword2.setText("");
                    mPassword.requestFocus();
                    snackbarMessage("A jelszó megerősítése előtt adja meg jelszavát!");
                } else if (!isStrong(password)) {
                    mPassword.setText("");
                    mPassword2.setText("");
                    mPassword.requestFocus();
                    hideKeyboard(SignUpActivity.this);
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, android.R.style.Theme_DeviceDefault_Light));
                    builder.setTitle("Gyenge jelszó")
                            .setMessage("A jelszónak tartalmaznia kell legalább 1 számot, 1 nagybetűt, továbbá legalább 8 karakter hosszúságúnak kell lennie.")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            }).show();
                }
            }
        });

        loginButton = findViewById(R.id.login_button);
        loginButtonHolder = findViewById(R.id.login_button_holder);
        progressBar = findViewById(R.id.progress_bar);
        mRevealLayout = findViewById(R.id.reveal_layout);
        mRevealLayout.hide(0, 0, 1);
        LinearLayout emailHolder = findViewById(R.id.email_holder);
        LinearLayout passwordHolder = findViewById(R.id.password_holder);
        LinearLayout password2Holder = findViewById(R.id.password2_holder);
        new Handler().postDelayed(() -> {
            slideIn(emailHolder);
            emailHolder.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                slideIn(passwordHolder);
                passwordHolder.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                    slideIn(loginButtonHolder);
                    loginButtonHolder.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        slideIn(password2Holder);
                        password2Holder.setVisibility(View.VISIBLE);
                    }, 80);
                }, 80);
            }, 80);
        }, 450);

        loginButton.setOnClickListener(view -> signUp());

        findViewById(R.id.sign_in).setOnClickListener(view -> {
            if (mAuth.getCurrentUser() != null) mAuth.signOut();
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_down, R.anim.no_change);
        });

    }

    public void slideIn(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(180);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    public void shrinkButton(View v, boolean shrinking) {
        ValueAnimator anim;
        if (shrinking) {
            anim = ValueAnimator.ofInt(v.getMeasuredWidth(), convertDpToPixel(toShrinkDP));
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                loginButton.setEnabled(false);
                loginButton.setText(" ");
            }, 100);
        } else {
            progressBar.setVisibility(View.GONE);
            anim = ValueAnimator.ofInt(v.getMeasuredWidth(), convertDpToPixel(fromShrinkDP));
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                loginButton.setEnabled(true);
                loginButton.setText("BEJELENTKEZÉS");
            }, 100);
        }
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.width = val;
            v.setLayoutParams(layoutParams);
        });
        anim.setDuration(200);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (shrinking) progressBar.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    public void signUp() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();

        if (password.equals(password2)) {
            if (!email.matches("") && !password.matches("")) {
                if (isEmailValid(email)) {
                    if (isNetworkConnected()) {
                        mEmail.setEnabled(false);
                        mPassword.setEnabled(false);
                        mEmail.clearFocus();
                        mPassword.clearFocus();
                        shrinkButton(loginButton, true);
                        new InternetCheck(internet -> {
                            if (internet) {
                                shrinkButton(loginButton, true);
                                if (isStrong(password)) {
                                    FirebaseAuth.AuthStateListener mAuthListener = firebaseAuth -> changed = true;
                                    mAuth.addAuthStateListener(mAuthListener);
                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(this, task -> {
                                                if (!task.isSuccessful()) {
                                                    try {
                                                        throw Objects.requireNonNull(task.getException());
                                                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                                        snackbarMessage("A jelszó túl gyenge!");
                                                    } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                                        snackbarMessage("Helytelen e-mail címet adott meg!");
                                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                                        snackbarMessage("Az e-mail cím már foglalt!");
                                                    } catch (Exception e) {
                                                        snackbarMessage("A fiók létrehozása sikertelen!\n" + e.getMessage());
                                                    }
                                                    shrinkButton(loginButton, false);
                                                } else {
                                                    if (changed) {
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        if (user != null) {
                                                            changed = false;
                                                            hideKeyboard(SignUpActivity.this);
                                                            sendVerificationEmail();
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    hideKeyboard(SignUpActivity.this);
                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, android.R.style.Theme_DeviceDefault_Light));
                                    builder.setTitle("Gyenge jelszó")
                                            .setMessage("A jelszónak tartalmaznia kell legalább 1 számot, 1 nagybetűt, továbbá legalább 8 karakter hosszúságúnak kell lennie.")
                                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            }).show();
                                    mPassword.setText("");
                                    mPassword2.setText("");
                                    mPassword.requestFocus();
                                }
                            } else {
                                shrinkButton(loginButton, false);
                                snackbarMessage("Nincs internetelérés.");
                            }
                        });
                    } else {
                        snackbarMessage("Csatlakozzon egy hálózathoz!");
                    }

                } else {
                    snackbarMessage("Helytelen e-mail címet adott meg!");
                }
            } else if (email.matches("") && !password.matches("")) {
                snackbarMessage("Adja meg az e-mail címét!");
            } else if (password.matches("") && !email.matches("")) {
                snackbarMessage("Adja meg a jelszavát!");
            } else {
                snackbarMessage("Adja meg új fiókja adatait!");
            }
        } else {
            mPassword.setText("");
            mPassword2.setText("");
            mPassword.requestFocus();
            snackbarMessage("A jelszavak nem egyeznek");
        }
    }

    private void snackbarMessage(String message) {
        // Create the Snackbar
        View containerLayout = findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(containerLayout, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = layout.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.brand_primary));
        layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ghost_white));
        snackbar.show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isStrong(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$");

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut();
                        if (!SignUpActivity.this.isFinishing()) {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, android.R.style.Theme_DeviceDefault_Light));
                            builder.setOnDismissListener(dialogInterface -> startLoginActivity());
                            builder.setTitle("Regisztráció megerősítése")
                                    .setMessage("Elküldtük a(z) " + user.getEmail() + " e-mail címre a regisztráció megerősítéséhez szükséges linket.")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> startLoginActivity()).show();
                            //show dialog
                        }
                    } else {
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        new Handler().postDelayed(() -> {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, android.R.style.Theme_DeviceDefault_Light));
                            builder.setTitle("Próbálja újra!")
                                    .setMessage("Hiba lépett fel az e-mail küldése során.")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    }).show();
                        }, 1000);
                    }
                });
    }

    public void startLoginActivity() {
        int[] coordinates = new int[2];
        loginButton.getLocationOnScreen(coordinates);
        coordinates[0] += loginButton.getHeight() / 2;
        coordinates[1] += loginButton.getWidth() / 2;
        mRevealLayout.show(coordinates[0], coordinates[1], 600);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 600);
    }

}
