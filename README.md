# Android MVVM Clean Architecture with Koin, Retrofit, and Coroutines

This project demonstrates a scalable Android architecture based on MVVM, leveraging Dependency Injection, asynchronous data handling, and modular design principles.

---

## 🏗️ Architecture Overview

- **MVVM (Model-View-ViewModel)**: Ensures separation of concerns and testability.
- **Koin**: Lightweight dependency injection for managing objects and services.
- **Retrofit**: Efficient HTTP client for networking and API interaction.
- **Coroutines**: Manages background tasks asynchronously with minimal boilerplate.
- **Navigation Components**: Simplifies fragment-based navigation using a navigation graph.
- **Talsec Security**: Detects root, emulator, and other security risks.

---

## 🔑 Core Components

### 🧠 Dependency Injection with Koin
- Injects ViewModels, repositories, and API services.
- Simplifies object graph management and promotes modularity.

### 🔄 Networking with Retrofit & GSON
- Retrofit manages RESTful API calls.
- GSON handles JSON parsing.
- OkHttp interceptor provides HTTP request/response logging.

### 🧵 Asynchronous Tasks with Coroutines
- Used for I/O-bound operations like API calls or database access.
- Ensures smooth main-thread UI performance.

### 📦 ViewModel & Lifecycle Management
- ViewModels hold and manage UI-related data.
- Lifecycle-aware components help avoid memory leaks.

### 🧭 Navigation
- Navigation Component provides safe and structured screen transitions.

---

## 🔐 Root Detection & App Security

### 🔒 Talsec Security
Talsec is used to enhance the security of the app by detecting:
- Rooted devices
- Emulators
- Debugging attempts
- Repackaged apps

> You can use callbacks provided by Talsec to handle threats (e.g., force logout, show warning dialog, block features).

---

## 🧰 ProGuard Rules

If you're using **ProGuard** or **R8**, make sure to add the following rules to prevent obfuscation of critical classes:
# --------------------------------------------
# 🛡️ General Project Rules
# --------------------------------------------

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable,Signature,Exceptions

# Preserve all exception classes
-keep public class * extends java.lang.Exception

# --------------------------------------------
# 🧨 Firebase Crashlytics
# --------------------------------------------
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# --------------------------------------------
# 🌐 Retrofit & OkHttp
# --------------------------------------------

# Retrofit core
-keep class retrofit.** { *; }
-dontwarn retrofit.**
-dontwarn retrofit.appengine.UrlFetchClient

# Retrofit annotations
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# OkHttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp.** { *; }

# --------------------------------------------
# 📈 MPAndroidChart (if used)
# --------------------------------------------
-keep class com.github.mikephil.charting.** { *; }

# --------------------------------------------
# 🔐 Talsec Security
# --------------------------------------------
-keep class com.aheaditec.talsec_security.** { *; }
-dontwarn com.aheaditec.talsec_security.**
-keep class com.aheaditec.talsec_security.security.api.** { *; }

# --------------------------------------------
# 🧠 Koin (if used)
# --------------------------------------------
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# --------------------------------------------
# 🔧 Your App's Custom Packages
# --------------------------------------------

# Models, Network, and Activities
-keep class com.example.meezan360.model.** { *; }
-keep class com.example.meezan360.network.** { *; }
-keep class com.example.meezan360.ui.activities.** { *; }
-keep class com.example.meezan360.network.APIService { *; }
-keep class com.example.meezan360.network.APIClient { *; }
-keep class com.example.meezan360.ui.activities.DockActivity { *; }

# SSL
-keep class javax.net.ssl.** { *; }
-keep class okhttp3.** { *; }

# --------------------------------------------
# 📵 Remove Logging for Release
# --------------------------------------------

# Android Log
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** wtf(...);
}

# Timber (optional)
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

# --------------------------------------------
# 🌀 Obfuscation Options
# --------------------------------------------

-renamesourcefileattribute SourceFile
-ignorewarnings

# Optional (Use only if confident)
# -repackageclasses
# -mergeinterfacesaggressively
# -overloadaggressively
# -allowaccessmodification
