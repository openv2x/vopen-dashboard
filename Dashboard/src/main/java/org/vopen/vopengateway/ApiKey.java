package org.vopen.vopengateway;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ApiKey extends Activity {

    private String apiKey;
    private EditText apiKeyEditText;


    private void loadApiKey(Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        apiKey = sharedPreferences.getString("broker_apiKey","Change:Me");



        if (intent != null && intent.getData() != null)
        {
            Uri openUri = intent.getData();
            apiKey = openUri.toString().substring(8); // remove "vopen://"
        }

        apiKeyEditText.setText(apiKey);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_api_key);
       // apiKeyEditText = (EditText)findViewById(R.id.apiKey);

        loadApiKey(getIntent());
    }



    @Override
    protected void onNewIntent (Intent intent)
    {
        loadApiKey(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_api_key, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public void cancelClicked(View view)
    {
        finish();
    }


    public void okClicked(View view)
    {
        apiKey = apiKeyEditText.getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.edit().putString("broker_apiKey",apiKey).commit())
        {
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }
}
