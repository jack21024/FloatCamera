package com.jack.library;

/**
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class Version {

    /**
     *  version of software
     */
    public static String BUILD_DATE = "150723";
    public static int VERSION_01 = 1; //for concept updating
    public static int VERSION_02 = 0; //for function increasing
    public static int VERSION_03 = 0; //for issue fixing

    public static String getVersion() {
        return "v" + VERSION_01 + "." + VERSION_02 + "." + VERSION_03 + "_" + BUILD_DATE;
    }

    /**
     * v1.0.0
     * The library is built to support kinds of common functions and provides easier way to use it.
     * Now just integrates the camera service of android api level 21 before.
     *
     * Note: The camera service of android level api 21 higher is not implemented.
     */
}
