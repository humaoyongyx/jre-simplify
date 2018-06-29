package issac.demo.jre;

import jodd.io.StreamUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static java.net.URLDecoder.decode;

/**
 * Created by issac.hu on 2018/6/28.
 */
public class App {
     private static String jarDir;

    static {
        try {
            jarDir = decode(App.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
            jarDir=jarDir.substring(0,jarDir.lastIndexOf("/"))+"/";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

     private static String configFilePath=jarDir+"config.properties";

     private static String default_rt_dir=jarDir+"rt/";
     private static String default_output_dir=jarDir+"out/";
     private static String default_class_txt_path=jarDir+"class.txt";
     private static String default_jar_name=jarDir+"tool.jar";
     private static String default_jar_dir=jarDir+"tool/";

    public static void main(String[] args) throws IOException {


        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFilePath));
        } catch (IOException e) {
            System.out.println("config.properties not extist，will read default config");
        }
        String rt_dir=default_rt_dir;
        String output_dir=default_output_dir;
        String class_txt_path =default_class_txt_path;
        String tool_jar_name =default_jar_name;
        String tool_jar_dir =default_jar_dir;
        if (!properties.isEmpty()){
              properties.list(System.out);
              if (properties.get("rt_dir")!=null&&StringUtils.isNotBlank(properties.get("rt_dir")+"")){
                  rt_dir=properties.get("rt_dir")+"";
              }

             if (properties.get("output_dir")!=null&&StringUtils.isNotBlank(properties.get("output_dir")+"")){
                 output_dir=properties.get("output_dir")+"";
             }

             if (properties.get("class_txt_path")!=null&&StringUtils.isNotBlank(properties.get("class_txt_path")+"")){
                 class_txt_path = properties.get("class_txt_path")+"";
             }

            if (properties.get("tool_jar_name")!=null&&StringUtils.isNotBlank(properties.get("tool_jar_name")+"")){
                tool_jar_name = properties.get("tool_jar_name")+"";
            }
            if (properties.get("tool_jar_dir")!=null&&StringUtils.isNotBlank(properties.get("tool_jar_dir")+"")){
                tool_jar_dir = properties.get("tool_jar_dir")+"";
            }

             System.out.println("properties:rt_dir:"+rt_dir+",output_dir:"+output_dir+",class_txt_path:"+class_txt_path+",tool_jar_name:"+tool_jar_name+",tool_jar_dir:"+tool_jar_dir);
         }else {
             System.out.println("rt_dir:"+rt_dir+",output_dir:"+output_dir+",class_txt_path:"+class_txt_path+",tool_jar_name:"+tool_jar_name+",tool_jar_dir:"+tool_jar_dir);

         }

        StringWriter stringWriter=new StringWriter();
        StreamUtil.copy(new FileInputStream(new File(class_txt_path)),stringWriter);
        String classTxt = stringWriter.getBuffer().toString();
        String[] split = classTxt.split("\r\n");


        List<String> rtList=new ArrayList<>();
        List<String> toolList=new ArrayList<>();
        for (String line :split){
            System.out.println(line);
            if (line.contains("rt.jar")&&!line.contains("[Opened")){
                String tmp = line.replace("[Loaded ", "").replaceAll(" from .*", "").replace(".","/")+".class";
                rtList.add(tmp);
            }else if (line.contains(tool_jar_name)){
                String tmp = line.replace("[Loaded ", "").replaceAll(" from .*", "").replace(".","/")+".class";
                toolList.add(tmp);
            }

           // System.out.println(line);
        }

        System.out.println("rtList-----------------");
        rtList.stream().forEach(System.out::println);
        new File(output_dir).delete();
        if (!rtList.isEmpty()){
            for (String filePath:rtList){
                String dirPath = output_dir+"rt_new/"+filePath.substring(0,filePath.lastIndexOf("/"));
                File file = new File(dirPath);
                if (!file.exists()){
                    file.mkdirs();
                }

                 StreamUtil.copy(new FileInputStream(rt_dir+filePath),new FileOutputStream(output_dir+"rt_new/"+filePath));
            }

        }
        System.out.println("toolList-----------------");

        toolList.stream().forEach(System.out::println);

        if (!toolList.isEmpty()){
            for (String filePath:toolList){
                String dirPath = output_dir+"tool_new/"+filePath.substring(0,filePath.lastIndexOf("/"));
                File file = new File(dirPath);
                if (!file.exists()){
                    file.mkdirs();
                }
                StreamUtil.copy(new FileInputStream(tool_jar_dir+filePath),new FileOutputStream(output_dir+"tool_new/"+filePath));
            }

        }

    }

}
