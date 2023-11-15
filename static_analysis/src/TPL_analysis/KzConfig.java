package TPL_analysis;

import java.io.File;

public class KzConfig {
    public static String platformPath="D:\\software\\android\\android-sdk\\platforms";
    //public static String taintFile="SourcesAndSinks.txt";
    //public static String taintFile = "D:\\software\\idea\\ideaProject\\binary_code_analysis\\soot-infoflow-android\\SourcesAndSinks.txt";
    //public static String wrapperFile = "D:\\software\\idea\\ideaProject\\binary_code_analysis\\soot-infoflow\\EasyTaintWrapperSource.txt";
    public static String wrapperFile=new File(".\\static_analysis\\EasyTaintWrapperSource.txt").getAbsolutePath();
    public static String taintFile=new File(".\\static_analysis\\SourcesAndSinks.txt").getAbsolutePath();
}

