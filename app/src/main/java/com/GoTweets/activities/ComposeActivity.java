package com.GoTweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.GoTweets.helpers.TwitterApplication;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.R;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends AppCompatActivity {
    private TwitterClient client;
    private static final int LENGTH_BOUND = 140;
    private EditText etCompose;
    private TextView tvCharCount,imagepath;
    private Button btnCompose,addimage;
    LinearLayout Linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        // To show back button


        // get the client
        client = TwitterApplication.getRestClient(); //singleton client
        etCompose = (EditText) findViewById(R.id.etcompose);
        etCompose.setSelection(0);
        etCompose.requestFocus();
        tvCharCount = (TextView) findViewById(R.id.tvcharCount);
        imagepath =(TextView)findViewById(R.id.imagepath);
        btnCompose = (Button) findViewById(R.id.btnCompose);
        Linear =(LinearLayout)findViewById(R.id.Linear);
        addimage = (Button)findViewById(R.id.add_image);

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ComposeActivity.this,Display_Img.class);
                startActivityForResult(i, 1);
            }
        });

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                // Do nothing.
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                int length = text.length();
                tvCharCount.setText(String.valueOf(140-length));
                if (length > LENGTH_BOUND) {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnCompose.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    btnCompose.setEnabled(false);
                } else {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.black));
                    btnCompose.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    btnCompose.setEnabled(true);
                }
            }
        });
    }

    public void onClickCompose(View view) {

        if(imagepath.getText().toString().equals("image_attachment"))
        {
            Snackbar.make(Linear, "Please add image attachment!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
        }
        else {
            String body = etCompose.getText().toString();
            client.postTweet(body, imagepath.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    //Tweet tweet = new Tweet();
                    Intent data = new Intent();
                    //data.putExtra("tweet", tweet);
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(ComposeActivity.this, "Unable to send tweet. Try again", Toast.LENGTH_LONG).show();
                    Log.e("TWEET", errorResponse.toString());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            imagepath.setText(data.getStringExtra("path"));
        }


    }



}
