package com.adampc.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = "adam";
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        context=this;
        final Intent intent = getIntent();
        String path = intent.getStringExtra(MainActivity.NEW_FOTO_PATH);
        final File imgFile = new File(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        //detect if image is rotated
        try {
            ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception ignored) {

        }

        //using photoView for image pinching/zooming
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageBitmap(myBitmap);

        final Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup.LayoutParams layoutParams = delete.getLayoutParams();
                layoutParams.width = MainActivity.dpToPx(context,60);
                layoutParams.height = MainActivity.dpToPx(context,60);
                delete.setLayoutParams(layoutParams);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                ViewGroup.LayoutParams layoutParams = delete.getLayoutParams();
                                layoutParams.width = MainActivity.dpToPx(context,80);
                                layoutParams.height = MainActivity.dpToPx(context,80);
                                delete.setLayoutParams(layoutParams);
                            }
                        },
                        300);

                if (imgFile.exists()) {
                    imgFile.delete();
                }
                Intent intent1 = new Intent(context, MainActivity.class);
                startActivity(intent1);
            }
        });
    }

}
