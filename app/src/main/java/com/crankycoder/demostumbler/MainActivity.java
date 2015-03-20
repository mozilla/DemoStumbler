package com.crankycoder.demostumbler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.mozilla.mozstumbler.service.core.http.IHttpUtil;
import org.mozilla.mozstumbler.service.core.http.ILocationService;
import org.mozilla.mozstumbler.service.mainthread.PassiveServiceReceiver;
import org.mozilla.mozstumbler.service.stumblerthread.datahandling.DataStorageManager;
import org.mozilla.mozstumbler.service.uploadthread.UploadAlarmReceiver;
import org.mozilla.mozstumbler.svclocator.ServiceConfig;
import org.mozilla.mozstumbler.svclocator.ServiceLocator;
import org.mozilla.mozstumbler.svclocator.services.ISystemClock;
import org.mozilla.mozstumbler.svclocator.services.log.ILogger;
import org.mozilla.mozstumbler.svclocator.services.log.LoggerUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    private static String LOG_TAG = LoggerUtil.makeLogTag(MainActivity.class);
    private static ILogger Log = (ILogger) ServiceLocator.getInstance().getService(ILogger.class);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSaveButton();

        // Setup ServiceConfig and ServiceLocator
        ServiceConfig svcConfig = new ServiceConfig();
        svcConfig.put(IHttpUtil.class,
                ServiceConfig.load("com.crankycoder.demostumbler.DebugHttpUtil"));

        /* In a production enviroment, you should use the real HttpUtil */
        // svcConfig.put(IHttpUtil.class,
        //        ServiceConfig.load("org.mozilla.mozstumbler.service.code.http.HttpUtil"));

        svcConfig.put(ISystemClock.class,
                ServiceConfig.load("org.mozilla.mozstumbler.svclocator.services.SystemClock"));
        svcConfig.put(ILocationService.class,
                ServiceConfig.load("org.mozilla.mozstumbler.service.core.http.MLS"));
        svcConfig.put(ILogger.class,
                ServiceConfig.load("org.mozilla.mozstumbler.svclocator.services.log.DebugLogger"));

        /* In a production enviroment, you should use the production
         * logger */
        // svcConfig.put(ILogger.class,
        //        ServiceConfig.load("org.mozilla.mozstumbler.svclocator.services.log.ProductionLogger"));

        ServiceLocator.newRoot(svcConfig);

        final String MOZ_API_KEY = "69702f37-5b49-4fbb-856d-87cbd379911b";
        Intent i = PassiveServiceReceiver.createStartIntent(this,
                 MOZ_API_KEY,
                "Just Another User-Agent");
        startService(i);


        /*
         By default, the passive stumbler is designed to be extremely minimal for power
         consumption.  In Firefox Android, we only upload on initial process startup, and
         we only upload if a wifi connection is available.

         We delay upload slightly by ~4 seconds so that the upload process doesn't impede the
         startup performance of the application.  All of this is done behind the scenes for you
         if you want to do the same thing.

         In general, if your application is a 'normal' standalone application that is not a widget
         you should probably do what Firefox Android does to minimize user impact.  Ignore the
         TimerTask code that follows.

         If you are writing a widget, your process may run for a very long time if the device
         is never rebooted and your process is never killed.  In this case, you may want to
         schedule a repeating alarm that will upload data back to the server.

         To do that, consider adding some code like below.

         Currently the passive stumbler will *only* upload when a wifi connection is available.

         Even with this repeating alarm, if your process is killed - on restart, the stumbler
         library will attempt to upload.
         */
        TimerTask delayedDownloadSchedule= new TimerTask()  {
            @Override
            public void run() {
                // kill the fennec startup only upload mechanism
                UploadAlarmReceiver.cancelAlarm(MainActivity.this, false);

                // Schedule for upload every hour.  Express this in seconds.
                final long ONE_HOUR = 60 * 60;
                UploadAlarmReceiver.scheduleAlarm(MainActivity.this, ONE_HOUR, true);
            }
        };

        // We delay by 30 seconds before we schedule the repeating hourly upload job.
        long DELAY_MS = 30*1000;
        Timer timer = new Timer();
        timer.schedule(delayedDownloadSchedule, DELAY_MS);
    }

    /*
     Setups up the upload button and a listener for clicks
     */
    private void setupSaveButton() {

        Button uploadBtn = (Button) findViewById(R.id.flushBtn);
        uploadBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Button was clicked!");

                DataStorageManager manager = DataStorageManager.getInstance();
                if (manager == null) {
                    Log.i(LOG_TAG, "Can't acquire DSM!");
                    return;
                }
                try {
                    manager.saveCurrentReportsToDisk();
                    Log.i(LOG_TAG, "Saved reports to disk!");
                } catch (IOException ioException) {
                    Log.e(LOG_TAG, ioException.toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
