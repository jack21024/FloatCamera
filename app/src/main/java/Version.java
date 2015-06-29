/**
 * Created by jacktseng on 2015/6/24.
 */
public class Version {


    /**
     *  version of software
     */
    public static String BUILD_DATE = "";
    public static int VERSION_01 = 1;
    public static int VERSION_02 = 0;
    public static int VERSION_03 = 0;
    public static int VERSION_04 = 0;

    public static String getVersion() {
        return "v" + VERSION_01 + "." + VERSION_02 + "." + VERSION_03 + "." + VERSION_04 + "_" + BUILD_DATE;
    }

    /*

     */

}
