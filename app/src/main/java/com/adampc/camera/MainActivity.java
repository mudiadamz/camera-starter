package com.adampc.camera;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String NEW_FOTO_PATH = "new_foto_path";
    private Context context;
    private CameraView cameraView;
    private String TAG = "adam";
    private int permissionStorage=0;
    private Facing facing = Facing.BACK;
    private String newFotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        cameraView = findViewById(R.id.camera);
        cameraView.setLifecycleOwner(this);

        final Button cameraShutter = findViewById(R.id.cameraShutter);
        final Button galleryGo = findViewById(R.id.galleryGo);
        final Button rotate = findViewById(R.id.rotate);

        cameraShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
                ViewGroup.LayoutParams layoutParams = cameraShutter.getLayoutParams();
                layoutParams.width = dpToPx(context,60);
                layoutParams.height = dpToPx(context,60);
                cameraShutter.setLayoutParams(layoutParams);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                ViewGroup.LayoutParams layoutParams = cameraShutter.getLayoutParams();
                                layoutParams.width = dpToPx(context,80);
                                layoutParams.height = dpToPx(context,80);
                                cameraShutter.setLayoutParams(layoutParams);
                            }
                        },
                        300);
            }
        });

        galleryGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newFotoPath==null)return;

                ViewGroup.LayoutParams layoutParams = galleryGo.getLayoutParams();
                layoutParams.width = dpToPx(context,60);
                layoutParams.height = dpToPx(context,60);
                galleryGo.setLayoutParams(layoutParams);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                ViewGroup.LayoutParams layoutParams = galleryGo.getLayoutParams();
                                layoutParams.width = dpToPx(context,80);
                                layoutParams.height = dpToPx(context,80);
                                galleryGo.setLayoutParams(layoutParams);
                            }
                        },
                        300);

                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(NEW_FOTO_PATH, newFotoPath);
                startActivity(intent);
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(v, "rotation", 0, 180).start();
                facing = facing==Facing.BACK?Facing.FRONT:Facing.BACK;
                cameraView.setFacing(facing);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionStorage);
        }

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String format = sdf.format(new Date());

                final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "photo-"+format+".jpg");

                newFotoPath = file.getPath();
                result.toFile(file, new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file1) {
                        Log.i(TAG, "onPictureTaken: "+file);
                    }
                });

                result.toBitmap(100, 100, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        RoundedBitmapDrawable bdrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                        bdrawable.setCornerRadius(40);
                        galleryGo.setBackground(bdrawable);
                    }
                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
    public static int dpToPx(Context context, int dp){
        return ((Float)(dp * context.getResources().getDisplayMetrics().density)).intValue();
    }
}
