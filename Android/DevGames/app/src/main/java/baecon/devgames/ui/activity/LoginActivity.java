package baecon.devgames.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.model.User;
import baecon.devgames.util.DummyHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button submit;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        checkBox = (CheckBox) findViewById(R.id.checkBox);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String u = "test";
                String p = "test";

                if(username.getText().toString().toLowerCase().equals(u) &&
                        password.getText().toString().toLowerCase().equals(p) ) {

                    if (checkBox.isChecked()) {

                        DevGamesApplication.get(LoginActivity.this).getPreferenceManager().setRememberPasswordEnabled(true);
                        DevGamesApplication.get(LoginActivity.this).getPreferenceManager().setLastUsedUsername(
                                username.getText().toString()
                        );

                    }

                    DevGamesApplication.get(LoginActivity.this).setLoggedInUser(
                            DummyHelper.getInstance().marcel
                    );

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                }
            }
        });
    }
}
