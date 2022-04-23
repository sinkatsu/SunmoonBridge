package com.example.sunmoonbridge.ui.Help;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunmoonbridge.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import androidx.appcompat.app.AlertDialog;

public class PicassoSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        Intent It = getIntent();
        final String image = It.getStringExtra("image");

        final PhotoView photoView = findViewById(R.id.iv_photo);

        Picasso.get()
                .load(image)
                .into(photoView);

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PicassoSampleActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Are you going to download this image?").setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isExternalStorageWritable()) {
                            Uri uri = Uri.parse(image);
                            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                            request.setTitle("image is download Now");
                            request.setDescription("Android data download using DownloadManager.");
                            request.allowScanningByMediaScanner();
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/image/"+"/"+"salmankhan"+".png");
                            request.setMimeType("*/*");
                            downloadManager.enqueue(request);
                        }
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                return true;
            }
        });

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}
