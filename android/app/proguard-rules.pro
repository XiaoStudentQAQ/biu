# Add project specific ProGuard rules here.

# Keep Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Keep OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data models - 只保留序列化必需的结构，允许混淆
-keep,allowobfuscation,allowshrinking class com.biu.music.data.model.** { 
    <init>(...);
    <fields>;
}
-keepclassmembers class com.biu.music.data.model.** {
    @kotlinx.serialization.SerialName <fields>;
}

# Keep Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# 移除日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# 混淆 BuildConfig 敏感信息（虽然我们已改用加密存储）
-assumenosideeffects class com.biu.music.BuildConfig {
    public static java.lang.String BILIBILI_COOKIE;
}

# 加密 SharedPreferences
-keep class androidx.security.crypto.** { *; }

# 通用安全增强
-repackageclasses
-allowaccessmodification
-optimizationpasses 5

