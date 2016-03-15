package baecon.devgames.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.otto.Subscribe;

import baecon.devgames.R;
import baecon.devgames.connection.task.LoginTask;
import baecon.devgames.events.LoginEvent;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;

public class LoginActivity extends DevGamesActivity {

    private EditText username, password;
    private Button submit;
    private CheckBox checkBox;
    private LinearLayout credentialsContainer, busyContainer;
    private TextView loginSuccess;
    private boolean performingLoginTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        username = (EditText) findViewById(R.id.edit_username);
        password = (EditText) findViewById(R.id.edit_password);

        checkBox = (CheckBox) findViewById(R.id.remember_username);

        submit = (Button) findViewById(R.id.button_login);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String usernameText = username.getText().toString().trim();
                final String passwordText = password.getText().toString().trim();

                if (usernameText.isEmpty()) {
                    Utils.createToast(LoginActivity.this, R.string.enter_username, Toast.LENGTH_SHORT);
                    username.requestFocus();
                    return;
                }

                if (passwordText.isEmpty()) {
                    Utils.createToast(LoginActivity.this, R.string.enter_password, Toast.LENGTH_SHORT);
                    password.requestFocus();
                    return;
                }

                getPreferenceManager().setRememberPasswordEnabled(checkBox.isChecked());

                L.d("username and password are entered properly, starting LoginTask. rememberUsername checked={0}",
                        checkBox.isChecked());

                performLogin(usernameText, passwordText);
            }
        });

        TextView version = (TextView) findViewById(R.id.version_information);
        version.setText(
                String.format(
                        getString(R.string.app_version_name),
                        Utils.getAppVersionName(this)
                )
        );

        credentialsContainer = (LinearLayout) findViewById(R.id.login_credentials_container);
        credentialsContainer.setVisibility(View.VISIBLE);

        busyContainer = (LinearLayout) findViewById(R.id.login_waiting_container);
        busyContainer.setVisibility(View.INVISIBLE);

        loginSuccess = (TextView) findViewById(R.id.login_ok_check_mark);
        loginSuccess.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void doLoginCheck() {
        // We should not check before the user logged in
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (performingLoginTask) {
            super.findViewById(R.id.button_login).performClick();
        }
    }

    private void performLogin(final String uuid, final String passwordHash) {

        performingLoginTask = true;

        credentialsContainer.setVisibility(View.VISIBLE);
        busyContainer.setVisibility(View.INVISIBLE);
        loginSuccess.setVisibility(View.INVISIBLE);

        ViewHelper.setAlpha(credentialsContainer, 1L);
        ViewHelper.setAlpha(busyContainer, 0L);
        ViewHelper.setAlpha(credentialsContainer, 0L);

        ViewPropertyAnimator.animate(credentialsContainer)
                .alpha(0f)
                .setStartDelay(100L)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        credentialsContainer.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        ViewPropertyAnimator.animate(busyContainer)
                .alpha(1f)
                .setStartDelay(200L)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        busyContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new LoginTask(getApplication(), uuid, passwordHash).executeThreaded();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .start();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLoginEvent(final LoginEvent event) {

        L.d("onLoginEvent, event: {0}", event);

        if (event.success) {

            if (getPreferenceManager().isRememberPasswordEnabled()) {
                L.v("Remember password enabled, storing in preferences...");
                getPreferenceManager().setLastUsedUsername(username.getText().toString().trim());
            }

            ViewHelper.setAlpha(loginSuccess, 0L);

            ViewPropertyAnimator busyContainerAnimator = ViewPropertyAnimator.animate(busyContainer);
            busyContainerAnimator
                    .alpha(0f)
                    .setStartDelay(200L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            busyContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();

            ViewPropertyAnimator.animate(loginSuccess)
                    .alpha(1L)
                    .setStartDelay(100L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            loginSuccess.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    })
                    .start();
        }
        else {
            L.w("unsuccessful login attempt: {0}", event.message);

            ViewPropertyAnimator.animate(credentialsContainer)
                    .alpha(1f)
                    .setStartDelay(100L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            credentialsContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
            ViewPropertyAnimator.animate(busyContainer)
                    .alpha(0f)
                    .setStartDelay(200L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            busyContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();

            if (event.message == null) {
                Utils.createToast(LoginActivity.this, R.string.unsuccessful_login, Toast.LENGTH_SHORT);
            }
            else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.createToast(LoginActivity.this, event.message, Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }
}
