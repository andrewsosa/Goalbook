package com.andrewsosa.bounce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


// TODO clean up code

public class LoginActivity extends AppCompatActivity {

    // User fields
    EditText usernameEditText;
    EditText passwordEditText;
    Firebase ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

        ref = new Firebase(Bounce.URL);



        // Set up the submit button click handler
        Button signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        final Button newUsers = (Button) findViewById(R.id.new_user);
        newUsers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        findViewById(R.id.decoy).requestFocus();

    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();


        // Create a handler to handle the result of the authentication
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                startActivity(new Intent(LoginActivity.this, DispatchActivity.class));
                finish();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                dialog.hide();
                Snackbar.make(usernameEditText, "Error during login.", Snackbar.LENGTH_SHORT).show();
                Log.e("Register User", firebaseError.getMessage());

                // Something went wrong :(
                    /*switch (error.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            // handle a non existing user
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            // handle an invalid password
                            break;
                        default:
                            // handle other errors
                            break;
                    } */

            }
        };

        // Or with an email/password combination
        ref.authWithPassword(username, password, authResultHandler);




    }
}