package com.zhoupeng.adminserver.Controller;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author xiao
 * @Create 2022/6/14 13:50
 * @Desc
 */
public class CommondUtil {
    //目标服务器端口,默认
    private static int port = 22;
    private static Connection conn = null;

    public Connection login(String ip, String userName, String userPwd) {
        boolean flg = false;
        Connection conn = null;
        try {
            conn = new Connection(ip, port);
            //连接
            conn.connect();
            //认证
            flg = conn.authenticateWithPassword(userName, userPwd);
            if (flg) {
                System.out.println("连接成功");
                return conn;
            }
        } catch (IOException e) {
            System.out.println("连接失败");
            e.printStackTrace();
        }
        return conn;
    }
    /*public ResponseEntity<byte[]> downloadFile(Connection conn, String fileName, String filePath, String localPath,String newName) throws IOException {
        HttpHeaders httpHeaders = null;
        ByteArrayOutputStream baos = null;
        SCPClient scpClient = conn.createSCPClient();
        try {
            SCPInputStream sis = scpClient.get(filePath + "/" + fileName);
            File f = new File(localPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            File newFile = null;


            if (StringUtils.isBlank(newName)) {
                newFile = new File(localPath + fileName);
            } else {
                newFile = new File(localPath + newName);
            }
            baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len =0;
            while((len=sis.read(buf))!=-1){
                baos.write(buf,0,len);
            }
            httpHeaders = new HttpHeaders();
            httpHeaders.setContentDispositionFormData("attachment",new String(newName.getBytes("UTF-8"),"ISO-8859-1"));
            return  new ResponseEntity<>(baos.toByteArray(),httpHeaders, HttpStatus.CREATED);
        } catch (IOException e) {
            System.out.println("文件不存在或连接失败");
            e.printStackTrace();
        } finally {
            System.out.println("服务关闭");
            closeConn();
        }
        return  new ResponseEntity<>(baos.toByteArray(),httpHeaders, HttpStatus.CREATED);
    }
    }*/
    public void downloadFile(Connection conn, String fileName, String filePath, String localPath, String newName) throws IOException {
        SCPClient scpClient = conn.createSCPClient();
        try {
            SCPInputStream sis = scpClient.get(filePath + "/" + fileName+".sql");
            File f = new File(localPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            File newFile = null;
            if (StringUtils.isBlank(newName)) {
                newFile = new File(localPath + fileName);
            } else {
                newFile = new File(localPath + newName);
            }
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] b = new byte[4096];
            int i;
            while ((i = sis.read(b)) != -1) {
                fos.write(b, 0, i);
            }
            fos.flush();
            fos.close();
            sis.close();
            System.out.println("下载完成");
        } catch (IOException e) {
            System.out.println("文件不存在或连接失败");
            e.printStackTrace();
        } finally {
            System.out.println("服务关闭");
            closeConn();
        }
    }
    public static void closeConn() {
        if (null == conn) {
            return;
        }
        conn.close();
    }
}