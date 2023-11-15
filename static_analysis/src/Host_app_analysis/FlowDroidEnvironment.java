package Host_app_analysis;

import TPL_analysis.KzConfig;
import soot.G;

import java.util.ArrayList;

public class FlowDroidEnvironment {
    public static ArrayList<String> args;

    public static void reset() {
        args = new ArrayList<String>();
    }

    public static void init(String apkPath, String platformPath) throws Exception {
        // Clean up any old Soot instance we may have
        G.reset();

        // configure Flowdroid arguments
        args.add("-a"); args.add(apkPath);
        //apk
        args.add("-p"); args.add(platformPath);
        //android.jar
        args.add("-s"); args.add(KzConfig.taintFile);
        //options.addOption(OPTION_SOURCES_SINKS_FILE, "sourcessinksfile"
        args.add("-r");
        //options.addOption(OPTION_REFLECTION, "enablereflection", false,
        args.add("-tw"); args.add("NONE");
        //options.addOption(OPTION_TAINT_WRAPPER, "taintwrapper", true,
        //                "Use the specified taint wrapper algorithm (NONE, EASY, STUBDROID, MULTI)");
        args.add("-t"); args.add(KzConfig.wrapperFile);
        //options.addOption(OPTION_TAINT_WRAPPER_FILE, "taintwrapperfile",
        args.add("-cp");
        args.add("-d");
        //multi dex
        args.add("-ps");
        // options.addOption(OPTION_PATH_SPECIFIC_RESULTS, "pathspecificresults", false,
        //                "Report different results for same source/sink pairs if they differ in their propagation paths");
        args.add("-cg"); args.add("SPARK");
        // args.add("-ce"); args.add("NONE");
//        args.add("-w");没有-w这个选项


        // timeout
        args.add("-dt"); args.add("300"); // seconds
        args.add("-ct"); args.add("300"); // seconds
        args.add("-rt"); args.add("300"); // seconds
    }
}
