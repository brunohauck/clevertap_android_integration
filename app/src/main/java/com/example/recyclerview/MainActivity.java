package com.example.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.InAppNotificationButtonListener;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements InAppNotificationButtonListener, CTInboxListener, CTPushNotificationListener
{
    Context ctx;
    private final LinkedList<String> mWordList = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;
    public CleverTapAPI clevertapDefaultInstance;

    Button inboxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = getApplicationContext();

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(ctx);
        clevertapDefaultInstance.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
        clevertapDefaultInstance.setCTPushNotificationListener(this);

        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }

        //It can be DEBUG

        //firebase Register ID Token
        String fcmRegId = FirebaseInstanceId.getInstance().getToken();
        //Push FcmRegId to clevertap API
        clevertapDefaultInstance.pushFcmRegistrationId(fcmRegId,true);
        //Track Networking information I need to be in compliance with GDPR - Need to know how It Works in Brazil
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        clevertapDefaultInstance.setInAppNotificationButtonListener(this::onInAppButtonClick);
        HashMap<String, Object> profileUpdate = new LinkedHashMap<>();
        profileUpdate.put("Name", "Bruno Hauck teste 7");    // String
        profileUpdate.put("Identity", 61026);      // String or number
        profileUpdate.put("Email", "brunohauck@gmail.com"); // Email address of the user
        profileUpdate.put("Phone", "+5531988724779");   // Phone (with the country code, starting with +)
        profileUpdate.put("Gender", "M");             // Can be either M or F
        profileUpdate.put("Time", "Cruzeiro");             // Can be either M or F
        profileUpdate.put("DOB", new Date());         // Date of Birth. Set the Date object to the appropriate value first

        // optional fields. controls whether the user will be sent email, push etc.
        // Marketing Outputs
        profileUpdate.put("MSG-email", true);        // Disable email notifications
        profileUpdate.put("MSG-push", true);          // Enable push notifications
        profileUpdate.put("MSG-sms", false);          // Disable SMS notifications
        profileUpdate.put("MSG-whatsapp", true);      // Enable WhatsApp notifications
        ArrayList<String> stuff = new ArrayList<String>();
        stuff.add("bag");
        stuff.add("shoes");
        stuff.add("Computers");
        stuff.add("BitCoins");
        profileUpdate.put("MyStuff", stuff);                        //ArrayList of Strings
        String[] otherStuff = {"Jeans","Perfume"};
        profileUpdate.put("MyStuff", otherStuff);                   //String Array

        clevertapDefaultInstance.onUserLogin(profileUpdate); // Funcion on your login

        //When the user updates
        HashMap<String, Object> profileUpdatePus = new LinkedHashMap<>();
        profileUpdate.put("NBA team", "Miami Heat");
        profileUpdate.put("Identity", 61026);      // String or number
        //clevertapDefaultInstance.pushProfile(profileUpdate);


        final ArrayList<CTInboxMessage> allInboxMessages = clevertapDefaultInstance.getAllInboxMessages();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                int wordListSize = mWordList.size();

                mWordList.addLast("+ Word " + wordListSize);
                mRecyclerView.getAdapter().notifyItemInserted(wordListSize);
                mRecyclerView.smoothScrollToPosition(wordListSize);

                HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
                prodViewedAction.put("Product Name", "Casio Chronograph Watch");
                prodViewedAction.put("Category", "Mens Accessories");
                prodViewedAction.put("Price", 59.99);
                prodViewedAction.put("Date", new java.util.Date());

                clevertapDefaultInstance.pushEvent("Product viewed - Test CleverTap", prodViewedAction);
            }
        });

        for (int i = 0; i < 20; i++) {
            mWordList.addLast("Word " + i);
        }

        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new WordListAdapter(this, mWordList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        //Use your custom logic for  the payload
        Log.d("CT -------<>-----",payload.toString());
        if(payload != null){
            //Read the values
            Log.d("Teste In App", payload.toString());
            Intent intent = new Intent(this, CallByPushActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onInAppButtonClick(HashMap<String, String> hashMap) {
        if(hashMap != null){
            //Read the values
            Log.d("Teste In App", hashMap.toString());
            Intent intent = new Intent(this, CallByPushActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            mWordList.clear();
            for (int i = 0; i < 20; i++) {
                mWordList.addLast("Word " + i);
            }
            mAdapter.notifyDataSetChanged();

            Log.i("-------<>------","Inbox call 1");
            if (clevertapDefaultInstance == null) {
                Log.i("-------<>------","Inbox call 2");
                return false;
            }
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //if (inboxButton == null) {
                    //    return;
                    //}
                    Log.i("-------<>------","Inbox call 3");
                    final int messageCount = clevertapDefaultInstance.getInboxMessageCount();
                    final int unreadMessageCount = clevertapDefaultInstance.getInboxMessageUnreadCount();
                    //inboxButton.setText(String.format(Locale.getDefault(),"Inbox: %d messages /%d unread", messageCount, unreadMessageCount));
                    //inboxButton.setVisibility(View.VISIBLE);
                    functionInbox();
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void functionInbox(){

        Log.i("-------<>------","Inbox call 4");
        mLastInboxClickTime = SystemClock.elapsedRealtime();

        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Promotions");
        tabs.add("Offers");
        tabs.add("Will Not Show");//Anything after the first 2 will be ignored
        CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
        styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
        styleConfig.setTabBackgroundColor("#FF0000");
        styleConfig.setSelectedTabIndicatorColor("#0000FF");
        styleConfig.setSelectedTabColor("#0000FF");
        styleConfig.setUnselectedTabColor("#FFFFFF");
        styleConfig.setBackButtonColor("#FF0000");
        styleConfig.setNavBarTitleColor("#FF0000");
        styleConfig.setNavBarTitle("MY INBOX");
        styleConfig.setNavBarColor("#FFFFFF");
        styleConfig.setInboxBackgroundColor("#ADD8E6");
        //clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
        clevertapDefaultInstance.showAppInbox();//Opens Activity with default style configs

    }

    private long mLastInboxClickTime = 0;
    @Override
    public void inboxDidInitialize() {

        if (inboxButton == null) {
            return;
        }

        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clevertapDefaultInstance == null) {
                    return;
                }
                if (SystemClock.elapsedRealtime() - mLastInboxClickTime < 1000){
                    return;
                }
                mLastInboxClickTime = SystemClock.elapsedRealtime();

                ArrayList<String> tabs = new ArrayList<>();
                tabs.add("Promotions");
                tabs.add("Offers");
                tabs.add("Will Not Show");//Anything after the first 2 will be ignored
                CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
                styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
                styleConfig.setTabBackgroundColor("#FF0000");
                styleConfig.setSelectedTabIndicatorColor("#0000FF");
                styleConfig.setSelectedTabColor("#0000FF");
                styleConfig.setUnselectedTabColor("#FFFFFF");
                styleConfig.setBackButtonColor("#FF0000");
                styleConfig.setNavBarTitleColor("#FF0000");
                styleConfig.setNavBarTitle("MY INBOX");
                styleConfig.setNavBarColor("#FFFFFF");
                styleConfig.setInboxBackgroundColor("#ADD8E6");
                //clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
                clevertapDefaultInstance.showAppInbox();//Opens Activity with default style configs
            }
        });
        updateInboxButton();
    }
    @Override
    public void inboxMessagesDidUpdate() {
        updateInboxButton();
    }

    private void updateInboxButton() {
        if (clevertapDefaultInstance == null) {
            return;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (inboxButton == null) {
                    return;
                }
                final int messageCount = clevertapDefaultInstance.getInboxMessageCount();
                final int unreadMessageCount = clevertapDefaultInstance.getInboxMessageUnreadCount();
                inboxButton.setText(String.format(Locale.getDefault(),"Inbox: %d messages /%d unread", messageCount, unreadMessageCount));
                inboxButton.setVisibility(View.VISIBLE);
            }
        });
    }
}