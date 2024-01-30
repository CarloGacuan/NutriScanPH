# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# It's important to keep the line number information for
# debugging stack traces only in non-release builds.
# For release builds, you should strip this information to make reverse engineering harder.
#-dontkeepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep specific class members
-keepclassmembers class com.ninebythree.nutriscanph.testing.** {
    public *;
}

# Keep all classes in specific packages
#-keep class com.example.** { *; }


# Keep specific classes from javax.annotation and org.conscrypt packages
-keep class javax.annotation.** { *; }
-keep class org.conscrypt.** { *; }

# Keep the names of classes with native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Renaming fields
-repackageclasses ''
-allowaccessmodification


# Suppress warnings related to Conscrypt library
# Suppress warnings related to Conscrypt library
-dontwarn org.conscrypt.**
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn org.conscrypt.Conscrypt