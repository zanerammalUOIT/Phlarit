package com.uoit.phlarit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class signupActivity extends AppCompatActivity {

    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }
    // Login button pressed
    public void loginButtonPressed(View view)
    {
        // Declare variables
        TextView usernameText, passwordText, emailText;

        passwordText = findViewById(R.id.passwordText);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);

        final String password = passwordText.getText().toString();
        final String username = usernameText.getText().toString();
        final String email = emailText.getText().toString();
        //check if either text field is empty

        if (password.length() == 0 || username.length() == 0 || email.length() == 0){
            // if empty give user a message telling them to put an input
            Toast.makeText(this, "Please input username, password, and email", Toast.LENGTH_SHORT).show();
        }
        else{
            // if not empty send out to server for login check

            // Get a RequestQueue
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).
                    getRequestQueue();

            String stringRequest = "http://ec2-54-160-8-114.compute-1.amazonaws.com/signup.php";


            // Instatiate Volley queue
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                    .getRequestQueue();
            // Add a request (in this example, called stringRequest) to your RequestQueue.
            // RequestQueueSingleton.getInstance(this).addToRequestQueue(stringRequest);
            try {

                String url = stringRequest;
                StringRequest loginRequest = new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("onResponse", "|||"+response+"|||");

                                if(response.substring(0,17).equals("workSUCCESSFUL SIGNUP")){
                                    //if signup successful then send user back to login page
                                    Log.d("onResponse","SUCCESSFUL SIGNUP");
                                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(i);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "problem signing up", Toast.LENGTH_SHORT);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("ErrorResponse", error.toString());
                            }

                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("username", username);
                        params.put("password", password);
                        params.put("email", email);
                        return params;

                    }

                };


                loginRequest.setTag("JSONOBject");
                queue.add(loginRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
