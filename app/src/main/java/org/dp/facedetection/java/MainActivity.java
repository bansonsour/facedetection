package org.dp.facedetection.java;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.dp.facedetection.Face;
import org.dp.facedetection.FaceDetect;
import org.dp.facedetection.R;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by caydencui on 2019/4/16.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }

    }
    Mat matOfRect = null;
    private void  testFacedetect(){
        imageView=(ImageView)findViewById(R.id.imageView);
        textView=(TextView)findViewById(R.id.textView);
        try{
            Bitmap bitmap=getAssetsBitmap("test11.jpeg");
            String str = "image size = "+bitmap.getWidth()+"x"+bitmap.getHeight()+"\n";
            imageView.setImageBitmap(bitmap);
            Log.i("OpenCV", str);
            Bitmap bitmap1=bitmap.copy(bitmap.getConfig(),true);
            Utils.bitmapToMat(bitmap1,matOfRect);
            Log.i("OpenCV1", str);
            Scalar FACE_RECT_COLOR =new Scalar(255.0, 0.0, 0.0);
            int FACE_RECT_THICKNESS = 3;
            long startTime = System.currentTimeMillis();
            Log.i("OpenCV1", ""+startTime);
            Face [] faces= FaceDetect.facedetect(matOfRect.dataAddr());
            str = str + "face num = "+faces.length+"\n";
            Log.i("OpenCV1", ""+str);
            for (Face face : faces) {
                Imgproc.rectangle(matOfRect, face.faceRect, FACE_RECT_COLOR, FACE_RECT_THICKNESS);
            }

            str = str + "detectTime = "+(System.currentTimeMillis()-startTime)+"ms\n";
            Log.i("OpenCV", str);
            Utils.matToBitmap(matOfRect, bitmap1);
            imageView.setImageBitmap(bitmap1);
            textView.setText(str);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    matOfRect=new MatOfRect();
                    testFacedetect();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    public void onResume()
    {
        super.onResume();

    }
    /**
     * 根据路径获取Bitmap图片
     * @param path
     * @return
     */
    public Bitmap getAssetsBitmap(String path){
        AssetManager am = this.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = am.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

}
