package com.jack;

/**
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class Version {

    /**
     *  version of software
     */
    public static String BUILD_DATE = "150723";
    public static int VERSION_01 = 1; //for concept updating
    public static int VERSION_02 = 1; //for function increasing
    public static int VERSION_03 = 0; //for issue fixing
    public static int VERSION_04 = 1; //for other changing

    public static String getVersion() {
        return getVersionWithoutDate() + "_" + BUILD_DATE;
    }

    public static String getVersionWithoutDate() {
        return "v" + VERSION_01 + "." + VERSION_02 + "." + VERSION_03 + "." + VERSION_04;
    }

    /**
     * v1.1.0.0
     * The application was rebuilt by new architecture which separating the module of camera
     * and widgets to a library.
     *
     * Note: This is not support api level 21 upper.
     */
}
