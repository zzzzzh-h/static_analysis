package TPL_analysis;

import java.io.File;

public class TPL_Util {


    public static void checkFolder(String folder_name) {
        File tmp = new File(folder_name);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
    }

    public static String transAar2Jar(File ver, String tmp_folder) {
        checkFolder(tmp_folder);
        ZipUtil.unZip(ver, tmp_folder);
        String jar_file = tmp_folder + "/classes.jar";
        return jar_file;
    }
}
