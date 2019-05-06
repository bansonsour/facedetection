package org.dp.facedetection.util;

import android.Manifest;
import android.os.Environment;

import java.io.File;

public class Constant {
    public static boolean DEBUG = true;
    /**
     * 人脸核心存放路径
     */
    public static String rootDir = Environment.getExternalStorageDirectory().toString() + "/FaceResource/";
    /**
     * 所需的所有权限信息
     */
    public static String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };
    public static String readDir = rootDir + "readResource/";
    public static String writeDir = rootDir + "write/";
    public static final String RTSP_URL_SUB = "rtsp://admin:admin123@192.168.1.2:554";
    public static final int HTTP_PORT = 12355;
    public static final String DIR_IN_SDCARD = "FaceTransfer";
//    public static final  String MQ_HOST = "tcp://192.144.143.189:61613";
//    public static final  String MQ_USERNAME = "admin";
//    public static final  String MQ_PASSWORD = "Apassword";
//    public static final String BASE_URL="http://118.25.87.143:8081/face_check_work/";

    public static final  String MQ_HOST = "tcp://wx.hws-zhtc.com:61613";
    public static final  String MQ_USERNAME = "admin";
    public static final  String MQ_PASSWORD = "facePassword";

    public static final String BASE_URL="https://admin.hws-zhtc.com/face_attendance/";

    public static final String SERVER_URL=BASE_URL+"api/recognitionResult";

    public static final String REGISTER_URL=BASE_URL+"api/personOperation";
    public static final String ACTION_REOPEN="com.tencent.reopen";
    public static final String ACTION_RECORD="com.tencent.facerecord";
    public static final String ACTION_REGISTER="register";
    public static final String ACTION_SEARCH="search";
    public static final String ACTION_DELETE="delete";
    /**
     * 人脸特征点建模存放路径
     */
    public static String savePath = Environment.getExternalStorageDirectory().toString() + "/model/";

    /**
     * 人脸图片存放路径
     */
    public static String registerPath = Environment.getExternalStorageDirectory().toString() + "/register/";
    /**
     * FTP服务器路径
     */
    public static String ftpPath = Environment.getExternalStorageDirectory().toString() + "/ftp/";

    public static final File DIR = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.DIR_IN_SDCARD);

    /**
     * 人脸识别比分
     */
    public static float fraction = 0.6f;
    /**
     * 默认打开2 CPU工作
     */
    public static int cpuNum = 2;
    /**
     * 人脸特征存放文件名称，注册名称
     */
    public static String faceName = "";
    public static int iBackCameraIndex = 1;

    //=====================================================================================
    public static float xmlHeight = 750;
    public static float xmlWidth = 1000;
    public static int chickWidth = 320;//识别图像的宽
    public static int RchickWidth = 200;//批量注册图像的宽 身份证照更清晰
    public static float scaleWidth = 0;
    public static boolean checkFps = true;

}
