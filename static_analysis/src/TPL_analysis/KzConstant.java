package TPL_analysis;

public class KzConstant {
    public static String[] target_dataset ={
        //location类
        "address",
        "country",
        //"city",
        "gps",
        "language",
        "requestLocationUpdates",
        //"location",//由于location有一些歧义，比如installloacation等，所以不使用，但是由于要获得location一般都会调用一些特定的api，所以这里用api代替这个location
        //telephone类
        "phonenumber",
        "phone number",
        "imei",
        "deviceid",
        "device id",
        "device_id",
        "android id",
        "ContactsContract.PhoneLookup",
        //sms类
        "email",
        //network类
        "network",
        "ipaddress",
        "ip address",
        "macaddress",
        "mac address",
        "mac_address",
        "physical address",
        "wifiaddress",
        "wifi address",
        "wifiinfo",
        "cookie",
        "getDetailedStateOf",
        //audio类
        "audio",
        "getAudioSource",
        "android.media.MediaRecorder",
        //bluetooth类
        "bluetooth",
        //account类
        "account",
        "password",
        "username",
        "user name",
        //sensor类
        "sensor",
        //camera类
        "camera",
        //"photo",
        //contact类
        "contact",
        "friend",
        "interaction",

        "ContactsContract.Contacts.DISPLAY_NAME", // 获取联系人名字
        "ContactsContract.Contacts.HAS_PHONE_NUMBER",// 是否有号码
        "ContactsContract.Contacts._ID",// 获取联系人ID（每个联系人名字会有一个唯一对应的ID）
        "ContactsContract.CommonDataKinds.Email.CONTENT_URI",//获取email信息的url

        //time类
        "timezone",
        //SharedPreferences类
        "interest",
        "preference",
        //deviceinfo类
        "deviceinfo",
        "device info",
        //ad类
        "adid",
        "adinfo",
        "advertisinginfo",
        "advertising id",
        "identification number",
        "internal identifiers",
        "online identifiers",
        "facebook id",
        "google id",
        "google_email",
        "google_id",
        "google_token",
        "facebook_token",
        "fb_token",
        "interactions with ads",
        "click behavior",
        "getadvertisingidInfo",
        //log类
        "logs",
        "crash log",
        "crash report",
        "request and referral URLs",
        //其他
        "birthday",
        "payment",
        "bank account",
        "videos",
        "records",
        "religious",
        "health",
        "Security questions"

    };
    public static String[] personal_information={
////            "name",
////            "location",
////            "locat",
//            "phonenumber",
//            "email",
//            "contact",
////            "imei",
//            "deviceid",
//            "network",
//            "sim",
//            "username",
//            "password",
//            "wifiaddress",
//            "macaddress",
//            "googleadvertiserid",
//            "idvertisinginfo",
////            "adid", "gsfid", "androdiid", "serialnumber", "simserialnumber",
            //----------------------------------
            "name",
            "location",
            "phonenumber",
            "telephone",
            "email",
            "imei",
            "contact",
            "deviceid",
            "network",
            "sim",
            "username",
            "account",
            "password",
            "wifiaddress",
            "macaddress",
            "adid",
            "advertisinginfo",
            "deviceinfo",
            "payment",
            "logs",
            "vedio",
            "audio",
            "records",
            "religious",
            "health",
            "security questions",
            "language"
    };
    public static String[] target_dataset_1 = {
            //第一版ad_result所使用的数据集
            "location",
            "phonenumber",
            "telephone",
            "email",
            "imei",
            "contact",
            "deviceid",
            "network",
            //"sim",
            "username",
            "account",
            "password",
            "wifiaddress",
            "macaddress",
            "adid",
            "advertisinginfo",
            "deviceinfo",
            "payment",
            "logs",
            "vedio",
            "audio",
            "records",
            "religious",
            "health",
            "security questions",
            "language",
            /** method name**/
//            "advertisingidClient getadvertisingidInfo",
//            "android.bluetooth.BluetoothAdapter getAddress",
//            "android.hardware.Camera setPreviewDisplay",
//            "android.telephony.TelephonyManager getCellLocation",
//            "android.provider.ContactsContract PhoneLookup",
//            "android.location.Location getLatitude",
//            "android.location.Location getLongitude",
//            "android.location.LocationManager getLastKnownLocation",
//            "android.location.LocationManager requestLocationUpdates",
//            "android.location.LocationManager getLongitude",
//            "android.telephony.TElephonyManager getSimSerialNumber",
//            "android.telephony.TElephonyManager getDeviceId",
//            "android.telephony.TElephonyManager getSubscriberId",
//            "java.util.Calendar getTimeZone",
//            "android.net.wifi.WifiInfo getMacAddress",
//            "android.media.MediaRecorder getAudioSource",
//            "android.media.MediaRecorder startRecording",
//            "android.accounts.AccountManager getPassword",
//            "android.telephony.TelephonyManager getLine1Number",
//            "android.hardware.SensorManager getDefaultSensor",
//            "android.telephony.SmsManager sendTextMessage",
//            "android.net.wifi.WifiInfo getSSID",
//            "android.accounts.AccountManager getAccounts",
//            "android.os.UserManager getUserName",
            "getadvertisingidInfo",
            "getAddress",
            "setPreviewDisplay",
            "getCellLocation",
            "PhoneLookup",
            "getLatitude",
            "getLongitude",
            "getLastKnownLocation",
            "requestLocationUpdates",
            "getLongitude",
            "getSimSerialNumber",
            "getDeviceId",
            "getSubscriberId",
            "getTimeZone",
            "getMacAddress",
            "getAudioSource",
            "startRecording",
            "getPassword",
            "getLine1Number",
            "getDefaultSensor",
            "sendTextMessage",
            "getSSID",
            "getAccounts",
            "getUserName",
            //
            "getHttpVersion",
            "getEntity",
            "getStatusLine",
            "getContentCharSet",
            "getCountry",
            "getCid",
            "getLac",
            //
            "ContactsContract.CommonDataKinds.Email ",
            "advertisingidClient",
            "android.bluetooth.BluetoothAdapter",
            "android.hardware.Camera",
            "android.telephony.TelephonyManager",
            "android.provider.ContactsContract",
            "android.location.Location",
            "android.location.LocationManager",
            //"android.telephony.TElephonyManager",
            "java.util.Calendar",
            "android.net.wifi.WifiInfo",
            "android.media.MediaRecorder",
            "android.accounts.AccountManager",
            "android.telephony.TelephonyManager",
            "android.hardware.SensorManager",
            "android.telephony.SmsManager",
            "android.net.wifi.WifiInfo",
            "android.accounts.AccountManager",
            "android.os.UserManager",
            "ContactsContract.CommonDataKinds.Email"
    };

    public static String[] tpl_package_name = {
            // ad-networks
            "co.adcel.android",//Adcel Airpush
//            "co.adcel.android",//Adcel Revmob
//            "co.adcel.android",//Adcel Smaato
//            "co.adcel.android",//Adcel Supersonic
            "com.adjust.sdk",//Adjust Android SDK
            "com.adtoapp.android",//Adtoapp Avocarrot
//            "com.adtoapp.android",//Adtoapp Nativex
//            "com.adtoapp.android",//Adtoapp Tapsense
            "com.amazonaws",//AWS Java SDK For Amazon S3
            "com.applovin",//AppLovin SDK
            "com.applovin.mediation",//Fyber Adapter
            "com.applovin.mediation.sdks.nend",//Nend
            "com.appnexus.opensdk",//AppNexus Android SDK
            "com.appnexus.opensdk.mediatedviews",//AppNexus Android SDK: AdMarvel Mediation Adapter
            "com.appsflyer",//AppsFlyerSDK
            "com.bingzer.android.ads",//Adnets Leadbolt
            "com.buzzvil.buzzscreen",//Buzzscreen OutBrain SDK For Android
            "com.chartboost",//Chartboost
            "com.cleveradssolutions",//KIDOZ
            "com.conversantmedia",//Disruptor
            "com.criteo.publisher",//Criteo Publisher SDK
            "com.facebook.android",//Facebook Android SDK
            "com.fasterxml.jackson.core",//Jackson Core
            "com.fiftyfive.cargo.handlers.MobileAppTracking",//Cargo
            "com.github.moceanapi",//Mocean SDK Java
            "com.github.vungle",//Vungle Android SDK
            "com.google.ads.mediation",//IronSource Mediation Adapter For The Google Mobile Ads SDK
            "com.google.android.gms",//Play Services Ads
//            "com.google.android.gms",//Play Services Ads
//            "com.google.android.gms",//Play Services Ads
//            "com.google.android.gms",//Play Services Ads
//            "com.google.android.gms",//Play Services Ads
            "com.google.api.grpc",//Proto Google Cloud Video Intelligence V1
            "com.google.guava",//Guava: Google Core Libraries For Java
            "com.igaworks.adpopcorn",//IgawAdPopcorn
            "com.inmobi.monetization",//InMobi Mobile Ads
            "com.inneractive.jenkins.plugins",//Consul Plugin
            "com.iqzone.thirdparty",//AdColony
//            "com.iqzone.thirdparty",//Hyprmx
//            "com.iqzone.thirdparty",//Mintegral AndroidManifest
//            "com.iqzone.thirdparty",//MobFox
//            "com.iqzone.thirdparty",//Ogury
//            "com.iqzone.thirdparty",//Startapp
//            "com.iqzone.thirdparty",//VERVE
            "com.lootsie.sdk",//LootsieAerserv
            "com.madvertise.cmp",//MAdvertiseCmp
            "com.mobidevelop.robovm",//RoboPods Appodeal IOS
            "com.mopub",//MoPub Android SDK
            "com.mparticle",//Android Kochava Kit
            "com.my.target",//MyTarget SDK
            "com.netflix.turbine",//Turbine Core
            "com.noqoush.adfalcon.android",//SDK
            "com.pollfish",//Pollfish GooglePlay
            "com.spiffey.adlib",//AdLib
            "com.taboola",//Taboola Cronyx API
            "com.tapdaq",//Tapdaq
            "com.tapjoy",//Tapjoy Android SDK
            "com.tappx.sdk.android",//OMSDK For Tappx
            "com.tradplusad",//TradPlus Appnext
            "com.unity3d.ads",//Unity Ads
            "com.vdopia.ads.lw",//SDK
            "com.yomob",//TGSDKADDomob
            "com.yumimobi.ads.mediation",//AdMob
            "com.yumimobi.ads.thirdparty",//Mobvista
            "io.display",//DisplayIOAndroidSDK
            "ir.adad.androidsdkv3",//AdadSDKv3
            "me.kiip.sdk",//Kiip
            "net.minidev",//JSON Small and Fast Parser
            "net.nan21.dnet",//DNet AD Domain
            "net.nan21.dnet",//DNet AD Domain
            "net.pubnative",//Advertising ID Client
            "net.pubnative",//Library
            "net.sf.supercsv",//Super CSV Core
            "net.youmi.ads",//YoumiNativeAdS
            "org.eclipse.emf.ecore",//EMF Ecore Change Model
            "org.glassfish.jersey.media",//Jersey Media JSON Jackson
            "org.glassfish.jersey.media",//Jersey Media JSON Jackson
            "org.glassfish.jersey.media",//Jersey Media JSON Jackson
            "org.keedio.openx.data",//JSON
            "org.mobicents.servers.jainslee.core",//Components Test DU 1 Events
            "org.odpi.egeria",//Open Connector Framework (OCF)
            "org.renjin.cran",//Apc
            "org.robovm",//RoboPods Heyzap Android
            "org.savarese",//VServ TCPIP
            "org.springframework.mobile",//Spring Mobile Device Resolution Support
            "org.springframework.mobile",//Spring Mobile Device Resolution Support
            //social libraries
            "com.agoda.kakao",//Kakao
            "com.amazonaws",//AWS Java SDK For Amazon S3
            "com.amazonaws",//AWS Java SDK For Amazon S3
            "com.facebook.android",//Facebook Android SDK
            "com.google.guava",//Guava: Google Core Libraries For Java
            "com.magnet.mmx.ext",//MMX Android Smack
            "com.socialize",//Loopy SDK Android
            "com.tencent.mm.opensdk",//Wechat SDK For Android
            "http-kit",//HTTP Kit
//            "http-kit",//HTTP Kit
//            "http-kit",//HTTP Kit
//            "http-kit",//HTTP Kit
            "org.grails.plugins",//Grails Twitter Plugin.
            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
            "org.robovm",//RoboPods Heyzap Android
            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
            "org.springframework.social",//Foundational Module Containing The ServiceProvider Connect Framework and Service API Invocation Support.
            "org.twitter4j",//Twitter4J Core
            "org.wildfly.swarm",//WildFly Swarm: Bootstrap
            "software.amazon.awscdk",//Core
            //development_tool
            "android.arch.work",//Android WorkManager Runtime
            "androidx.activity",//Activity
            "androidx.appcompat",//Android AppCompat Library
            "androidx.appcompat",//Android AppCompat Library
            "androidx.loader",//Android Support Library Loader
            "androidx.print",//Android Support Library Print
            "androidx.transition",//Android Transition Support Library
            "androidx.versionedparcelable",//VersionedParcelable
            "androidx.viewpager",//Android Support Library View Pager
            "aviary",//Aviary Core
            "bouncer",//Bouncer
            "bouncycastle",//Legion of The Bouncy Castle Java Cryptography APIs
            "ch.acra",//Application Crash Report For Android
            "clj-http",//Clj HTTP
            "clj-http",//Clj HTTP
            "com.adobe.air.framework",//Airglobal
            "com.afollestad",//Material Dialogs
            "com.airbnb.android",//Lottie
            "com.alipay.sdk",//Alipay SDK Java
            "com.amazonaws",//AWS Java SDK For Amazon S3
            "com.android.support",//Android Support Library V4
            "com.android.support",//Android Support VectorDrawable
            "com.androidplot",//Androidplot
            "com.appsee",//Appsee
            "com.atlassian.plugins",//Atlassian Plugins Core
            "com.atlassian.plugins",//Atlassian Plugins Core
            "com.atlassian.studio",//JIRA Studio Theme Core
            "com.bluelinelabs",//Conductor
            "com.bluelinelabs",//LoganSquare
            "com.caverock",//AndroidSVG
            "com.charon.vitamio",//Vitamio
            "com.chauthai.swipereveallayout",//SwipeRevealLayout
            "com.clevertap.android",//CleverTap Android SDK
            "com.coditory.sherlock",//Sherlock Common
            "com.crittercism",//Crittercism Agent For Android
            "com.davemorrissey.labs",//SubsamplingScaleImageView
            "com.dreamliner",//AVLoadingIndicatorView
            "com.esotericsoftware",//Kryo
            "com.evernote",//Android Job
            "com.facebook.android",//Facebook Android SDK
            "com.facebook.android",//Facebook Android SDK
            "com.facebook.litho",//LithoCore
            "com.fasterxml.jackson.core",//Jackson Annotations
//            "com.fasterxml.jackson.core",//Jackson Core
//            "com.fasterxml.jackson.core",//Jackson Databind
//            "com.fasterxml.jackson.core",//Jackson Databind
            "com.getkeepsafe.relinker",//ReLinker
            "com.github.PhilJay",//MPAndroidChart
            "com.github.Raizlabs.DBFlow",//DBFlow
            "com.github.axet.fbreader",//Android FBReader Library
            "com.github.bumptech.glide",//Glide
            "com.github.bumptech.glide",//Glide Disk LRU Cache Library
            "com.github.castorflex.smoothprogressbar",//SmoothProgressBar Library
            "com.github.chrisbanes",//Chrisbanes/PhotoView
            "com.github.hotchemi",//PermissionsDispatcher
            "com.github.houbb",//Segment
            "com.github.jakob-grabner",//Jakob Grabner/Circle Progress View
            "com.github.nisrulz",//EasyDeviceInfo Base
            "com.github.rubensousa",//GravitySnapHelper
            "com.github.vicpinm",//ActiveAndroidRx
            "com.github.yalantis",//UCrop
            "com.google.android.gms",//Play Services Analytics
            "com.google.android.material",//Material Components For Android
            "com.google.code.findbugs",//FindBugs JSR305
            "com.google.code.gson",//Gson
            "com.google.dagger",//Dagger
            "com.google.firebase",//Firebase Messaging
            "com.google.guava",//Guava: Google Core Libraries For Java
            "com.google.inject",//Google Guice Core Library
            "com.google.zxing",//ZXing Core
            "com.ineunet",//Knife Utilities
            "com.j256.ormlite",//ORMLite Core
            "com.jakewharton.timber",//Timber
            "com.jcraft",//JSch
            "com.kobakei",//Android RateThisApp Library
            "com.koushikdutta.async",//AndroidAsync
            "com.krux",//Hyperion Core
            "com.leanplum",//Leanplum Core
            "com.makeramen",//RoundedImageView
            "com.mapbox.mapboxsdk",//Mapbox Maps SDK For Android
            "com.mikepenz",//AboutLibraries Library
            "com.mikepenz",//FastAdapter Library
            "com.mikepenz",//MaterialDrawer Library
            "com.mixpanel.android",//Mixpanel Android
            "com.mobidevelop.robovm",//RoboPods Flurry Analytics IOS
            "com.naver.nid",//Naveridlogin Android SDK
            "com.nineoldandroids",//Nine Old Androids
            "com.nostra13.universalimageloader",//Universal Image Loader Library
            "com.nuance",//SpeechKit
            "com.onesignal",//OneSignal
            "com.optimizely.ab",//Optimizely Java SDK
            "com.orhanobut",//Logger
            "com.otaliastudios",//CameraView
            "com.paypal.sdk",//PayPal Android SDK
            "com.pixplicity.easyprefs",//EasyPrefs
            "com.pubnub",//PubNub Java SDK
            "com.pushwoosh",//Pushwoosh
            "com.qaprosoft",//HockeyApp Utility
            "com.revenuecat.purchases",//Purchases Android
            "com.rmt.android",//Card IO
            "com.rudderstack.android.integration",//Rudder Integration Braze Android
            "com.scottyab",//Rootbeer
            "com.segment.analytics.android",//Apptimize Integration For Segment Android Analytics
            "com.sendbird.sdk",//Sendbird Android SDK
            "com.senspark.ee",//Soomla Store
            "com.splunk",//Splunk
            "com.squareup.leakcanary",//LeakCanary Android
            "com.squareup.moshi",//Moshi
            "com.squareup.okhttp3",//OkHttp
            "com.squareup.okio",//Okio
            "com.squareup.picasso",//Picasso
            "com.squareup.retrofit2",//Adapter: RxJava
            "com.squareup.retrofit2",//Retrofit
            "com.stripe",//Stripe Java
            "com.sylversky.library",//Sugarorm
            "com.teamagam.androidslidinguppanel",//Library
            "com.theartofdev.edmodo",//Android Image Cropper
            "com.umeng.analytics",//Analytics
            "com.unity3d.ads",//Unity Ads
            "com.urbanairship.android",//UrbanAirship SDK
            "com.zhihu.android",//Matisse
            "commons-codec",//Apache Commons Codec
            "commons-collections",//Apache Commons Collections
            "commons-io",//Apache Commons IO
//            "commons-io",//Apache Commons IO
//            "commons-io",//Apache Commons IO
//            "commons-io",//Apache Commons IO
//            "commons-io",//Apache Commons IO
//            "commons-io",//Apache Commons IO
            "commons-logging",//Apache Commons Logging
            "de.golfgl.gdxgameanalytics",//Gdx Gameanalytics
            "de.hdodenhof",//CircleImageView
            "de.psdev.licensesdialog",//LicensesDialog
            "dnsjava",//DNSJava
            "eu.codlab",//Android AndEngine Fork
            "gnu-regexp",//GNU RegExp
            "io.circe",//Circe Parser
            "io.fotoapparat",//Fotoapparat
            "io.grpc",//GRPC Stub
            "io.intercom.android",//Intercom SDK Base
            "io.micrometer",//Micrometer Registry New Relic
            "io.opencensus",//OpenCensus
            "io.reactivex.rxjava2",//RxJava
            "io.realm",//Realm Annotations
            "io.segment.analytics.android",//Amplitude
//            "io.segment.analytics.android",//Countly
//            "io.segment.analytics.android",//Localytics
//            "io.segment.analytics.android",//Quantcast
            "io.sentry",//Sentry SDK
            "io.vertx",//Vert.x Core
            "it.sephiroth.android.library.imagezoom",//ImageViewZoom
            "javax.mail",//JavaMail API JAR
            "javax.persistence",//Javax Persistence API
            "joda-time",//Joda Time
            "jp.wasabeef",//RecyclerView Animators
            "me.grantland",//Android AutofitTextView Library
            "me.leolin",//ShortcutBadger
            "me.zhanghai.android.materialprogressbar",//MaterialProgressBar Library
            "net.mingsoft",//MS Basic
            "net.minidev",//JSON Small and Fast Parser
            "net.sf.kxml",//KXML 2 IS A Small XML Pull Parser Based On The Common XML Pull API
            "net.sourceforge.htmlcleaner",//HtmlCleaner
            "net.sourceforge.jchardet",//Java Port of Mozilla Charset Detector
            "nl.dionsegijn",//Konfetti
            "nl.siegmann.epublib",//Epublib Core
            "oauth.signpost",//Signpost Core
            "org",//Jaudiotagger
            "org.altbeacon",//Android Beacon Library
            "org.apache.commons",//Apache Commons Lang
            "org.apache.curator",//Curator Framework
            "org.apache.httpcomponents",//Apache HttpClient
            "org.apache.httpcomponents",//Apache HttpClient
            "org.apache.thrift",//Apache Thrift
            "org.apereo.cas",//CAS Server Support Geolocation
            "org.bouncycastle",//Bouncy Castle Provider
            "org.ccil.cowan.tagsoup",//TagSoup
            "org.checkerframework",//Checker Qual
            "org.codehaus.groovy",//Apache Groovy
            "org.dom4j",//Dom4j
            "org.greenrobot",//EventBus
            "org.greenrobot",//GreenDAO
            "org.holoeverywhere",//HoloEverywhere SlidingMenu
            "org.jdom",//JDOM
            "org.jenkins-ci.plugins",//Branch API Plugin
//            "org.jenkins-ci.plugins",//Jenkins Git Plugin
            "org.jetbrains",//JetBrains Java Annotations
            "org.jetbrains",//JetBrains Java Annotations
            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Android Extensions Runtime
//            "org.jetbrains.kotlin",//Kotlin Stdlib
            "org.json",//JSON In Java
            "org.json4s",//Json4s Native
            "org.jsoup",//Jsoup Java HTML Parser
            "org.junit.jupiter",//JUnit Jupiter API
            "org.mobicents.servers.media.codecs",//SPEEX
            "org.mozilla.components",//Mozilla Mobile
            "org.msgpack",//MessagePack For Java
            "org.netbeans.api",//Mozilla Rhino Patched
            "org.netbeans.api",//NetBeans API Visual
            "org.ocpsoft.prettytime",//PrettyTime Core
            "org.odpi.egeria",//Open Connector Framework (OCF)
            "org.onepf",//OpenIAB Library
            "org.reactivestreams",//Reactive Streams
            "org.renjin",//Tools
            "org.renjin",//Utilities
            "org.scala-lang",//Scala Library
//            "org.scala-lang",//Scala Library
//            "org.scala-lang",//Scala Library
//            "org.scala-lang",//Scala Library
//            "org.scala-lang",//Scala Library
//            "org.scala-lang",//Scala Library
            "org.scribe",//Scribe OAuth Library
            "org.skyscreamer",//Yoga Core
            "org.slf4j",//JUL to SLF4J Bridge
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J API Module
//            "org.slf4j",//SLF4J Simple Binding
//            "org.slf4j",//SLF4J Simple Binding
            "org.springframework",//Spring TestContext Framework
            "org.springframework.social",//Foundational Module Containing The ServiceProvider Connect Framework and Service API Invocation Support.
            "org.webjars.npm",//Node Fetch
            "pub.devrel",//EasyPermissions
            "ru.yandex.qatools.ashot",//Yandex QATools AShot WebDriver Utility
            "se.emilsjolander",//StickyListHeaders Library
            "software.amazon.awscdk",//Core
            "tablelayout",//TableLayout
            "xmlpull",//XML Pull Parsing API
            // other
            "microsoft"

    };
}
