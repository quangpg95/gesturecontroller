package com.example.thinh.gesturecontroller.util;

//Bkav QuangNDb os utility
public class OSUtil {
    private static boolean sIsAtLeastM;
    private static boolean sIsAtLeastN;
    private static boolean sIsAtLeastO;
    static {
        final int v = getApiVersion();
        sIsAtLeastM = v >= android.os.Build.VERSION_CODES.M;
        sIsAtLeastO = v >= android.os.Build.VERSION_CODES.O;
        sIsAtLeastN = v >= android.os.Build.VERSION_CODES.N;
    }

    /**
     * @return The Android API version of the OS that we're currently running on.
     */
    public static int getApiVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * @return True if the version of Android that we're running on is at least M
     *  (API level 23).
     */
    public static boolean isAtLeastM() {
        return sIsAtLeastM;
    }

    /**
     * @return True if the version of Android that we're running on is at least N
     *  (API level 24).
     */
    public static boolean isAtLeastN() {
        return sIsAtLeastN;
    }

    /**
     * @return True if the version of Android that we're running on is at least N
     *  (API level 24).
     */
    public static boolean isAtLeastO() {
        return sIsAtLeastO;
    }

}
