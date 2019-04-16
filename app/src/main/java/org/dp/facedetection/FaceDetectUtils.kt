package org.dp.facedetection


/**
 * Created by caydencui on 2019/4/16.
 */
class FaceDetectUtils {

    external fun facedetect(matAddr: Long): Array<Face>

    companion object {
        // Used to load the 'facedetection' library on application startup.
        init {
            System.loadLibrary("facedetection")
        }
    }

}
