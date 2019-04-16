package org.dp.facedetection;


import java.util.List;

/**
 * Created by caydencui on 2019/4/16.
 */
public class FaceDetect {
    // Used to load the 'facedetection' library on application startup.
    static{
        System.loadLibrary("facedetection");
    }

    public static native List<Face> facedetect(Long matAddr);

}
