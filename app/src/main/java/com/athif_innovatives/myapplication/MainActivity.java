package com.athif_innovatives.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imebra.CodecFactory;
import com.imebra.ColorTransformsFactory;
import com.imebra.DataSet;
import com.imebra.DrawBitmap;
import com.imebra.Image;
import com.imebra.Memory;
import com.imebra.PatientName;
import com.imebra.PipeStream;
import com.imebra.StreamReader;
import com.imebra.TagId;
import com.imebra.TransformsChain;
import com.imebra.VOILUT;
import com.imebra.drawBitmapType_t;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView; // Used to display the image
    private TextView mTextView;  // Used to display the patient name
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // First thing: load the Imebra library
        System.loadLibrary("imebra_lib");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
        }

        // We will use the ImageView widget to display the DICOM image
        button = findViewById(R.id.dicom);
        button.setText(R.string.choose_file);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a DICOM file"), 123);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {


            // Get the selected URI, then open an input stream
            Uri selectedfile = data.getData();
            File file = new File(selectedfile.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            String filePath = split[1];

            System.out.println("This is the URI"+selectedfile);
            String path = "/storage/emulated/0/"+filePath;
            System.out.println("This is the Path"+path);
            Intent intent = new Intent(MainActivity.this, list_view.class);
            intent.putExtra("fileFinal", selectedfile.toString());
            intent.putExtra("file", path);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1001:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission Granted ",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"Permission Denied ",Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }
}
