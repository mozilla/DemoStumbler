# DemoStumbler - A demo of how to embed the Mozilla Stumbler as a library in your Android Application

The [Mozilla Stumbler](https://github.com/mozilla/MozStumbler/) is an Android wifi and cell tower stumbling application which 
is designed to collect location data and submit it to our location service [Ichnaea](http://github.com/mozilla/ichnaea/).

The standard Android application runs in what we call 'active' mode where we aggressively scan for Wi-fi routers, cell tower data 
and GPS supplied latitude and longitude. 

We have a second mode though - for passive stumbling.  We use a version of this code in Firefox for Android to do extremely low power
stumbling.

Now you can use it too!

Full documentation can be found on the primary Mozilla Stumbler [wiki](https://github.com/mozilla/MozStumbler/wiki/Using-libstumbler)

## But I'm impatient. Just show me the code.

You'll need to import some classes:
```java
import org.mozilla.mozstumbler.service.core.http.IHttpUtil;
import org.mozilla.mozstumbler.service.mainthread.PassiveServiceReceiver;
import org.mozilla.mozstumbler.svclocator.ServiceConfig;
import org.mozilla.mozstumbler.svclocator.ServiceLocator;
import org.mozilla.mozstumbler.svclocator.services.ISystemClock;
import org.mozilla.mozstumbler.svclocator.services.log.ILogger;
```

Next, you'll need to configure the ServiceLocator and then start the service with an Intent. 

```java
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
```
