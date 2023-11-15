package Util;



/*
把一个编码过的xml文件转化成可阅读的xml：
先切换到AndroidManefest.xml所在的文件夹下
java -jar D:\software\idea\ideaProject\neo4jTest\axmlprinter2-2016-07-27.jar AndroidManifest.xml > manifest.xml

再加一个功能，把apk内的dex文件提取出来，然后调python代码，把几个dex文件拼在一起，然后让ConstructAPG的输入(process dir)的内容变为dex文件
 */



import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class readapkxml {

    String jar_path = "D:\\software\\_download\\apk\\androidManifest1";//存放暂时的androidManifest.xml文件



    public File read(String apkpath) throws IOException {
        File zipfile = null;
        File file = new File(apkpath);
        File unfile=null;
        if (file.isDirectory()) {
            //针对的是一个文件夹内放了很多apk的情况，就要一个一个处理
            File[] files = file.listFiles();
            for (File singlefile : files) {
                zipfile = changename(singlefile);
                if (zipfile == null) {
                    break;
                }
                //System.out.println("change name :" + zipfile.getPath());
                unfile = unzipByFile(zipfile);
                if (unfile == null) {
                    System.out.println("解压" + zipfile.getName() + "失败");
                }
                changenameReverse(zipfile);
            }
        } else {
            //针对要处理的文件就是一个apk的情况
            zipfile = changename(file);
            if (zipfile != null) {
                System.out.println("change name :" + zipfile.getPath());
                unfile = unzipByFile(zipfile);
                if (unfile== null) {
                    System.out.println("解压" + zipfile.getName() + "失败");
                }
                changenameReverse(zipfile);
            }
        }
        return unfile;
    }
    public String dexProcessWithPy(String apkpath) throws IOException, InterruptedException {
        //调用D://python//ssdeep//dex.py文件，把apk内的dex文件提取出来，拼接在一起，最后返回dex文件所在的路径，可以作为soot分析的输入文件
        File zipfile=null;//存放修改后缀后的zip文件
        File file = new File(apkpath);
        zipfile = changename(file);
        String dexPath=null;//存放把dex合并之后的classes.dex文件的路径

        String cmd="python D://python//ssdeep//dex.py ";
        String path=zipfile.getPath();
        path=path.replace("\\","//");
        //"cmd /c start /b " +
        cmd="cmd /c start /b " + cmd+ path;
        System.out.println("cmd:   " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        int res=process.waitFor();
        System.out.println("res: "+res);
        InputStream in = process.getInputStream();
        try{
            //这个是为了看报错信息
            BufferedReader isError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"gbk"));
            String line;
            while((line=isError.readLine())!=null){
                System.out.println(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));
            dexPath=reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            changenameReverse(zipfile);//再把后缀名改回来
            System.out.println("dexPath: "+dexPath);
            return dexPath;
        }
    }
    public String dexProcessWithPy_2(String apkpath) throws IOException, InterruptedException {
        //这个是含有多个dex的路径，并没有把dex合并在一起
        //调用D://python//ssdeep//dex.py文件，把apk内的dex文件提取出来，拼接在一起，最后返回dex文件所在的路径，可以作为soot分析的输入文件
        File zipfile=null;//存放修改后缀后的zip文件
        File file = new File(apkpath);
        zipfile = changename(file);
        String dexPath=null;//存放把dex合并之后的classes.dex文件的路径

        String cmd="python D://python//ssdeep//dex1.py ";
        String path=zipfile.getPath();
        path=path.replace("\\","//");
        //"cmd /c start /b " +
        cmd="cmd /c start /b " + cmd+ path;
        //System.out.println("cmd:   " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        int res=process.waitFor();
        //System.out.println("res: "+res);
        InputStream in = process.getInputStream();
        try{
            //这个是为了看报错信息
            BufferedReader isError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"gbk"));
            String line;
            while((line=isError.readLine())!=null){
                System.out.println(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));
            dexPath=reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            changenameReverse(zipfile);//再把后缀名改回来
            //System.out.println("dexPath: "+dexPath);
            return dexPath;
        }
    }
    public File changename(File file) {
        if (file.isDirectory()) {
            System.out.println("error");
            return null;
        } else {
            String oldname = file.getName();
            String newname = null;
            //System.out.println("old name:" + oldname);
            if (oldname.endsWith(".apk")) {
                newname = oldname.substring(0, oldname.lastIndexOf(".") + 1);
                newname += "zip";
                System.out.println("new name:" + newname);
            }
            file.renameTo(new File(file.getParent() + "\\" + newname));
            //System.out.println(file.getParent() + "\\" + newname);
            //System.out.println(file.getPath() + "   rename");
            return new File(file.getParent() + "\\" + newname);
        }
    }

    public File changenameReverse(File file) {
        if (file.isDirectory()) {
            System.out.println("error");
            return null;
        } else {
            String oldname = file.getName();
            String newname = null;
            //System.out.println("old name:" + oldname);
            if (oldname.endsWith(".zip")) {
                newname = oldname.substring(0, oldname.lastIndexOf(".") + 1);
                newname += "apk";
                //System.out.println("new name:" + newname);
            }
            file.renameTo(new File(file.getParent() + "\\" + newname));
            //System.out.println(file.getParent() + "\\" + newname);
            //System.out.println(file.getPath() + "   rename");
            return new File(file.getParent() + "\\" + newname);
        }
    }

    public File unzipByFile(File file) throws IOException   //path后无/
    {
        File unfile = null;
        try {
            ZipFile zip = new ZipFile(file);
            for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String name = entry.getName();
                if (name.equals("AndroidManifest.xml")) {
                    System.out.println("---------------------------------------------");
                    //System.out.println("unfile:  "+file.toString());
                    //-------------
                    //之前使用绝对路径的时候
                    //unfile = new File(jar_path + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_" + name);
                    //-------------
                    //unfile=new File(temp_path+File.separator+file.getName().substring(0, file.getName().lastIndexOf(".")) + "_" + name);
                    unfile = new File(jar_path+File.separator+file.getName()+"_"+name);
                    if (!unfile.getParentFile().exists()) {
                        unfile.getParentFile().mkdir();
                    }
                    unfile.createNewFile();
                    InputStream in = zip.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(unfile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) fos.write(buf, 0, len);
                    fos.close();
                    in.close();

                }

            }
            zip.close();
            assert unfile != null;
            //System.out.println("1:  "+unfile.getPath());
            File xmlfile=parse(unfile);
            //把androidManefest.xml文件传入parse这个方法，使用axmlprinter.jar进行处理
            return xmlfile;
        } catch (IOException e) {
            return null;
        }

    }

    private File parse(File file) throws IOException {
        //使用axmlprinter.jar，把AndroidManifest.xml文件转成我们看得懂的形式，并保存在一个新的文件里面
        String cmd = "java -jar D:\\software\\idea\\ideaProject\\neo4jTest\\axmlprinter2-2016-07-27.jar ";
        String newName = file.getPath().replace("AndroidManifest", "manifest");
        //System.out.println("name   :" + newName);
        File newFile=new File(newName);
        cmd = "cmd /c start /b " + cmd + file.getPath() + " > " + newName;
        //System.out.println("cmd:   " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream in = process.getInputStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));
            String line;
            while ((line = reader.readLine()) != null) {
                writerow(newFile,line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            file.delete();
            //最后删掉原来的没有经过axmlManifest.xml处理的AndroidManifest.xml文件
            return newFile;
        }


    }
    private static void writerow (File ofile, String s){
        FileWriter fw = null;
        try {
            fw = new FileWriter(ofile, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(s);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

