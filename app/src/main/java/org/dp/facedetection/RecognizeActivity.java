package org.dp.facedetection;

import android.content.pm.PackageManager;
import android.graphics.*;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.dp.facedetection.model.DrawInfo;
import org.dp.facedetection.util.*;
import org.dp.facedetection.util.camera.CameraHelper;
import org.dp.facedetection.util.camera.CameraListener;
import org.dp.facedetection.widget.FaceRectView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caydencui on 2019/5/5.
 */
public class RecognizeActivity extends AppCompatActivity {
    private static final String TAG = "RecognizeActivity";
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private View previewView;

    private FaceRectView faceRectView;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    private Mat mat = null;
    private FaceDetectUtils faceDetect = new FaceDetectUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recognize);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        init();
        initCamera();
        if (cameraHelper != null) {
            cameraHelper.start();
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mat = new MatOfRect();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void init() {
        if (!checkPermissions(Constant.NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constant.NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        previewView = findViewById(R.id.texture_preview);
        //在布局结束后才做初始化操作
        faceRectView = findViewById(R.id.face_rect_view);
    }

    boolean isSave = true;

    private void saveImage(int index, byte[] nv21, int width, int height) {
        //保存一张照片
        String fileName = "IMG_" + String.valueOf(index) + ".jpg";  //jpeg文件名定义
        File sdRoot = Environment.getExternalStorageDirectory();    //系统路径
        String dir = "/jpeg/";   //文件夹名
        File mkDir = new File(sdRoot, dir);
        if (!mkDir.exists()) {
            mkDir.mkdirs();   //目录不存在，则创建
        }


        File pictureFile = new File(sdRoot, dir + fileName);
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile();

                FileOutputStream filecon = new FileOutputStream(pictureFile);

                YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);   //将NV21 data保存成YuvImage
                //图像压缩
                image.compressToJpeg(
                        new Rect(0, 0, image.getWidth(), image.getHeight()),
                        70, filecon);   // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
                Log.d(TAG, "success:" + (dir + fileName) + ",width:" + width + ",height:" + height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }

                if (isSave) {
                    saveImage(1, nv21, previewSize.width, previewSize.height);
                    isSave = false;
                }
//              new Thread(new Runnable() {
//                  @Override
//                  public void run() {
                /*将nv21转bitmap*/
                NV21ToBitmap nv21ToBitmap = new NV21ToBitmap();
                Bitmap bmp = nv21ToBitmap.nv21ToBitmap(nv21, previewSize.width, previewSize.height);
                bmp = ImageUtil.bitMapScale(bmp, 0.25f);
                bmp = ImageUtil.getRotateBitmap(bmp, 90);
                String str = "image size = " + bmp.getWidth() + "x" + bmp.getHeight() + "\n";
                Log.i("OpenCV", str);
//                      Bitmap bmp2=bmp.copy(bmp.getConfig(),true);
                Utils.bitmapToMat(bmp, mat);
                Log.i("OpenCV1", "" + (mat == null ? true : false));
                long startTime = System.currentTimeMillis();
                Log.i("OpenCV21", "" + mat.getNativeObjAddr());
                Face[] faces = faceDetect.facedetect(mat.getNativeObjAddr());
                str = str + "detectTime = " + (System.currentTimeMillis() - startTime) + "ms\n";

                if (null != faces) {
                    str = str + "face num = " + faces.length + "\n";
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faces.length; i++) {
                        Face face = faces[i];
                        org.opencv.core.Rect rect = face.faceRect;
                        Rect rect1 = new Rect(rect.x*4, rect.y*4, rect.width*4, rect.height*4);
                        DrawInfo drawInfo = new DrawInfo(rect1, "test");
                        drawInfoList.add(drawInfo);
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }
                DLog.d(TAG, str);
                if (bmp != null) {
                    bmp.recycle();
                    bmp = null;
                }
//                  }
//              }).start();

            }

            @Override
            public void onCameraClosed() {
                DLog.d(TAG, "onCameraClosed");
            }

            @Override
            public void onCameraError(Exception e) {
                DLog.e(TAG, e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(true)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();

    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    protected void onDestroy() {

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        super.onDestroy();
    }
}
