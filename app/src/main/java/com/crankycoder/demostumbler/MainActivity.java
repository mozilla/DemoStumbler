package com.crankycoder.demostumbler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.mozilla.mozstumbler.service.core.http.IHttpUtil;
import org.mozilla.mozstumbler.service.mainthread.PassiveServiceReceiver;
import org.mozilla.mozstumbler.svclocator.ServiceConfig;
import org.mozilla.mozstumbler.svclocator.ServiceLocator;
import org.mozilla.mozstumbler.svclocator.services.ISystemClock;
import org.mozilla.mozstumbler.svclocator.services.log.ILogger;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup ServiceConfig and ServiceLocator
        ServiceConfig svcConfig = new ServiceConfig();
        svcConfig.put(IHttpUtil.class,
                ServiceConfig.load("org.mozilla.mozstumbler.service.core.http.HttpUtil"));
        svcConfig.put(ISystemClock.class,
                ServiceConfig.load("org.mozilla.mozstumbler.svclocator.services.SystemClock"));
        svcConfig.put(ILogger.class,
                ServiceConfig.load("org.mozilla.mozstumbler.svclocator.services.log.ProductionLogger"));

        ServiceLocator.newRoot(svcConfig);

        Intent i = PassiveServiceReceiver.createStartIntent("a_moz_api_key",
                "Just Another User-Agent");
        startService(i);
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
