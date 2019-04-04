package com.uoit.phlarit;

import android.content.Context;
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

public class LoginActivity extends AppCompatActivity {

    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void signupButtonPressed(View view){
        startActivity(new Intent(this,signupActivity.class));
    }
    // Login button pressed
    public void loginButtonPressed(View view)
    {
        // Declare variables
        TextView usernameText, passwordText;

        passwordText = findViewById(R.id.passwordText);
        usernameText = findViewById(R.id.usernameText);

        final String password = passwordText.getText().toString();
        final String username = usernameText.getText().toString();

        //check if either text field is empty

        if (password.length() == 0 || username.length() == 0){
            // if empty give user a message telling them to put an input
            Toast.makeText(this, "Please input username and password", Toast.LENGTH_SHORT).show();
        }
        else{
            // if not empty send out to server for login check

            // Get a RequestQueue
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).
                    getRequestQueue();

            String stringRequest = "http://ec2-54-160-8-114.compute-1.amazonaws.com/login.php";


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

                                if(response.equals("workSUCCESSFUL LOGIN\n")){
                                    Log.d("onResponse","SUCCESSFUL LOGIN DETECTED");
                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(i);
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
