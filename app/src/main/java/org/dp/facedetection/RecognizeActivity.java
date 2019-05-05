package org.dp.facedetection;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import org.dp.facedetection.util.camera.CameraHelper;

/**
 * Created by caydencui on 2019/5/5.
 */
public class RecognizeActivity extends AppCompatActivity {
    private static final String TAG = "RecognizeActivity";
    private CameraHelper cameraHelper;
    private Camera.Size previewSize;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recognize);



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
}
