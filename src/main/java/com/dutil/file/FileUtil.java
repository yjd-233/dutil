package com.dutil.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yangjiandong
 * @date 2020/5/7
 */
@Slf4j
public class FileUtil {

    private static final int  BUFFER_SIZE = 2 * 1024;

    public static String getAbsolutePath() {
        ApplicationHome h = new ApplicationHome(FileUtil.class);
        File jarF = h.getSource();
        return jarF.getParent() + File.separator;
    }

    public static void createDir(String s) {
        File dir = new File(s);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    public static String execCmd(String[] cmdArr) throws IOException {
        StringBuilder sb = new StringBuilder();
        log.info("start excPython :{} arr:{}", cmdArr[1], Arrays.toString(cmdArr));
        Process process = Runtime.getRuntime().exec(cmdArr);
        try ( BufferedInputStream in = new BufferedInputStream(process.getInputStream())){
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))){
                String lineStr;
                while ((lineStr = br.readLine()) != null) {
                    sb.append(lineStr + " ");
                    log.info(lineStr);
                }
            }
        }
        return sb.toString();
    }

    public static void setFileResponse(File file, String contentType, HttpServletResponse response){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            // 设置相关格式
            response.setHeader("content-type", contentType);
//                response.setHeader("Content-Disposition","attachment; filename=" + file.getName());
            // 设置下载后的文件名以及header
            // 创建输出对象
            OutputStream os = response.getOutputStream();
            // 常规操作
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException{
        toZip(srcDir, out, KeepDirStructure, true, new ArrayList<>());
    }

    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure, boolean ignore, List<String> fileNameList)
            throws RuntimeException{
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            HashSet<String> set = new HashSet<>(fileNameList);
            compress(sourceFile,zos,sourceFile.getName(), KeepDirStructure, ignore, set);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @param fileNameSet
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure, boolean ignore, Set<String> fileNameSet) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            String fileName = sourceFile.getName();
            if (ignore || fileNameSet.contains(fileName)) {
                // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                zos.putNextEntry(new ZipEntry(name));
                // copy文件到zip输出流中
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                // Complete the entry
                zos.closeEntry();
                in.close();
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure, ignore, fileNameSet);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure, ignore, fileNameSet);
                    }
                }

            }
        }
    }

    public static List<String> getFileNameList(String path) throws IOException {
        ArrayList<String> list = new ArrayList<>();
//        String[] arr = new String[] {Constant.LS, path};
//        String s = FileUtil.execCmd(arr);
//        List<String> list = new ArrayList<>(Arrays.asList(s.split(" ")));
//        list.removeIf(StringUtils::isBlank);
        File file = new File(path);
        File[] array = file.listFiles();
        for (File son : array) {
            if (son.isFile()) {
                list.add(son.getName());
            }
        }
        log.info("getFileNameList : {}", list.toString());
        return list;
    }

    public static void delteFile(File file){
        File[] filearray = file.listFiles();
        if(filearray!=null){
            for(File f:filearray){
                if(f.isDirectory()){
                    delteFile(f);
                }else{
                    f.delete();
                }
            }
            file.delete();
        }
    }

}
