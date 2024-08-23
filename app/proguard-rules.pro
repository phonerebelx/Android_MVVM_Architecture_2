## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-repackageclasses 'com.app.pb'
##-repackageclasses 'com.scottyab.rootbeer'
##-mergeinterfacesaggressively
##-overloadaggressively
##-allowaccessmodification
#
##-Firebase
## Firebase
#-keepattributes *Annotation*
#-keepattributes SourceFile,LineNumberTable
#-keep public class * extends java.lang.Exception
#-keep class com.crashlytics.** { *; }
#-dontwarn com.crashlytics.**
#
#
#-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
#-renamesourcefileattribute SourceFile
#
#
#-keep class com.example.meezan360.Mvvm.model**{*;}
#-keep class com.example.meezan360.Mvvm.network.network**{*;}
#-keep class com.example.meezan360.UI.activity**{*;}
#-keep class com.example.meezan360**{*;}
#-keep class com.example.meezan360.network.APIService{*;}
#-keep class com.example.meezan360.network.APIClient{*;}
#-keep class com.example.meezan360.ui.activities.DockActivity{*;}
#-keep class com.aheaditec.talsec_security{*;}
#-keep class com.aheaditec.talsec_security.security.api{*;}
#-keep class com.aheaditec.talsec_security.security.api{*;}
#-keep class com.aheaditec.talsec_security.security.api.TalsecConfig{*;}
#
#-dontwarn com.squareup.okhttp.**
#-keep class com.squareup.okhttp.** { *; }
#-keep class com.squareup.okhttp3.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#
#-dontwarn retrofit.appengine.UrlFetchClient
#-keep class retrofit.** { *; }
#
#
#
#-dontwarn retrofit.**
#-keep class retrofit** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** w(...);
#    public static *** v(...);
#    public static *** i(...);
#    public static *** e(...);
#}
#
#-assumenosideeffects class timber.log.Timber {
#    public static *** d(...);
#    public static *** w(...);
#    public static *** v(...);
#    public static *** i(...);
#    public static *** e(...);
#    public static *** wtf(...);
#}
#
#-assumenosideeffects class timber.log.Timber$Tree {
#    public *** d(...);
#    public *** w(...);
#    public *** v(...);
#    public *** i(...);
#    public *** e(...);
#    public *** wtf(...);
#}
#
#-ignorewarnings




# General Project ProGuard Rules
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve Annotation Information
-keepattributes *Annotation*

# Preserve Line Number and Source File Information for Better Stack Traces
-keepattributes SourceFile,LineNumberTable

# Keep Exception Classes
-keep public class * extends java.lang.Exception

# Firebase and Crashlytics Rules
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Preserve Retrofit Annotations
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn retrofit.**
-dontwarn retrofit.appengine.UrlFetchClient

# Preserve OkHttp Classes
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp.** { *; }

# Keep Your Specific App Classes
-keep class com.example.meezan360.Mvvm.model** { *; }
-keep class com.example.meezan360.model{ *; }
-keep class com.example.meezan360.Mvvm.network.network** { *; }
-keep class com.example.meezan360.UI.activity** { *; }
-keep class com.example.meezan360.network.APIService { *; }
-keep class com.example.meezan360.network.APIClient { *; }
-keep class com.example.meezan360.ui.activities.DockActivity { *; }
-keep class com.aheaditec.talsec_security { *; }
-keep class com.aheaditec.talsec_security.security.api { *; }
-keep class com.aheaditec.talsec_security.security.api.TalsecConfig { *; }

# Remove Logging (Android Log)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** wtf(...);
}

# Remove Logging (Timber)
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** wtf(...);
}

-assumenosideeffects class timber.log.Timber$Tree {
    public *** d(...);
    public *** w(...);
    public *** v(...);
    public *** i(...);
    public *** e(...);
    public *** wtf(...);
}

# Obfuscation Rules
-renamesourcefileattribute SourceFile
-ignorewarnings

# Optional Aggressive Obfuscation (Use only if confident it won't break anything)
# -repackageclasses
# -mergeinterfacesaggressively
# -overloadaggressively
# -allowaccessmodification

# Ignore Warnings for Specific Libraries
-dontwarn retrofit.**
-dontwarn com.squareup.okhttp.**

# Preserve Logging if Required for Specific Environments (Optional)
# Uncomment the following lines if you still want logging in certain cases
# (like internal testing but not in public builds):
# -keep class timber.log.Timber { *; }
# -keep class android.util.Log { *; }

# Ignore Any Remaining Warnings to Avoid Build Failures
-ignorewarnings
