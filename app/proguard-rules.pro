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
-keep class com.example.meezan360.model** { *; }
-keep class com.example.meezan360.model{ *; }
-keep class com.example.meezan360.network.network** { *; }
-keep class com.example.meezan360.network** { *; }
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
