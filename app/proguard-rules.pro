# Add narrowly scoped keep rules here only when a release build reports a
# reflection or JNI requirement. Firebase dependencies provide their own
# consumer rules; this app currently uses Firestore maps rather than reflective
# conversion of app model classes.

# Keep error and warning logs in release builds, but remove debug/verbose logs.
-assumenosideeffects class android.util.Log {
    public static int d(java.lang.String, java.lang.String);
    public static int d(java.lang.String, java.lang.String, java.lang.Throwable);
    public static int v(java.lang.String, java.lang.String);
    public static int v(java.lang.String, java.lang.String, java.lang.Throwable);
}
