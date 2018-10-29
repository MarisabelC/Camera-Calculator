package com.marisabelchanggmail.firstapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;

/**
 * Camera Calculator application. The Camera can detect handwriting and typewriting. Use API 23
 */
public class MainActivity extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID= 1001;
    FocusBoxView box;

    public final static int MY_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestCameraPermissionID:{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        box=new FocusBoxView(this,20,700,400,550);
        addContentView(box, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cameraView= (SurfaceView)findViewById(R.id.surface_view);
        textView= (TextView)findViewById(R.id.text_view);

        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Log.w("MainActivity","Detector dependencies are not yet available");
        }else{
            cameraSource= new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1024,1280)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try{
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    final ShuntingYard exp = new ShuntingYard();
                    final StringBuilder stringBuilder = new StringBuilder();

                    if (items.size() != 0) {
                        Rect ocrBox;
                        int bounder = (box.getRect().bottom - box.getRect().top) / 2;
                        String str="";
                        for (int i = 0; i < items.size(); ++i) {
                            TextBlock block = items.valueAt(i);
                            for (Text item : block.getComponents()) {
                                //extract scanned text lines here
                                ocrBox = item.getBoundingBox();
                                int dy = Math.abs(ocrBox.centerY() - box.getRect().centerY());
                                if (dy<= bounder){
                                   str=item.getValue();
                                   bounder=dy;
                                }
                            }
                        }
                        stringBuilder.append(str+"\n");
                        //Evaluate and compute the expression
                        try {
                            double total = exp.compute(stringBuilder.toString());
                            stringBuilder.append(" = ");
                            stringBuilder.append(total);
                        } catch (NumberFormatException e) {
                            stringBuilder.append(e.getMessage());

                        }
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                //Show the expression and result to the screen
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}
