package com.athif_innovatives.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomDictionaryBase;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class list_view extends AppCompatActivity {

    private File file;
    private AttributeList list;
    private Scanner scn;
    private int length;
    private String[][] strs;
    private Set set;
    private ArrayList<Dicom_Extractor> dicom_extractors = new ArrayList<>();
    private Toolbar toolbar;
    private ImageButton imageButton;
    private ImageView imageView;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        System.loadLibrary("imebra_lib");



        imageView = findViewById(R.id.imageView4);

//
        imageButton = findViewById(R.id.imageButton);
        toolbar = findViewById(R.id.toolbar3);


//        toolbar.setCollapseIcon(R.drawable.back);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        ListView listView =  (ListView) findViewById(R.id.listView);
        String value = getIntent().getStringExtra("file");
        System.out.println(value);
        file = new File(value);

//

        try {
            DicomInputStream dic = new DicomInputStream(file);
            list = new AttributeList();
            list.setDecompressPixelData(false);
            list.read(dic);
            list.removeUnsafePrivateAttributes();
            scn = new Scanner(list.toString());
            length = list.keySet().size();
        } catch (IOException | DicomException e) {
            e.printStackTrace();
        }
        itStr();

        DicomList dicomList = new DicomList(this,R.layout.activity_dicom,dicom_extractors);
        listView.setAdapter(dicomList);
        displayImage();



    }
    public void displayImage() {
        try {
            Uri selectedfile = Uri.parse(getIntent().getStringExtra("fileFinal"));
            CodecFactory.setMaximumImageSize(8000, 8000);

            if (selectedfile == null) {
                return;
            }
            InputStream stream = getContentResolver().openInputStream(selectedfile);
            PipeStream imebraPipe = new PipeStream(32000);

            Thread pushThread = new Thread(new PushToImebraPipe(imebraPipe, stream));
            pushThread.start();
            DataSet loadDataSet = CodecFactory.load(new StreamReader(imebraPipe.getStreamInput()));
            Image dicomImage = loadDataSet.getImageApplyModalityTransform(0);

            TransformsChain chain = new TransformsChain();

            if (ColorTransformsFactory.isMonochrome(dicomImage.getColorSpace())) {
                VOILUT voilut = new VOILUT(VOILUT.getOptimalVOI(dicomImage, 0, 0, dicomImage.getWidth(), dicomImage.getHeight()));
                chain.addTransform(voilut);
            }
            DrawBitmap drawBitmap = new DrawBitmap(chain);
            Memory memory = drawBitmap.getBitmap(dicomImage, drawBitmapType_t.drawBitmapRGBA, 4);

            Bitmap renderBitmap = Bitmap.createBitmap((int) dicomImage.getWidth(), (int) dicomImage.getHeight(), Bitmap.Config.ARGB_8888);
            byte[] memoryByte = new byte[(int) memory.size()];
            memory.data(memoryByte);
            ByteBuffer byteBuffer = ByteBuffer.wrap(memoryByte);
            renderBitmap.copyPixelsFromBuffer(byteBuffer);

            // Update the image
            imageView.setImageBitmap(renderBitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Update the text with the patient name
        } catch (Exception e) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("This DICOM Image doesnt have a Proper Frame Size in order to Visualize ");
            dlgAlert.setTitle("Error");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //dismiss the dialog
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            String test = "Test";
        }
    }





    private void itStr() {
        strs = new String[list.size()][3];
        int i = 0;
        DicomDictionaryBase bse = new DicomDictionary();
        for (Map.Entry<AttributeTag, Attribute> entry : list.entrySet()) {
            AttributeTag key = entry.getKey();
            Attribute value = entry.getValue();
            strs[i][0] = key.toString(); // Tag ID
            strs[i][1] = bse.getFullNameFromTag(key); // Tag Name
            strs[i][2] = value.getSingleStringValueOrEmptyString(); // Description
            Dicom_Extractor dicom = new Dicom_Extractor(strs[i][0],strs[i][1],strs[i][2]);
            dicom_extractors.add(dicom);
            i++;
        }

    }
}
