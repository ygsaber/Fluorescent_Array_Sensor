package com.sunshine.Fluorescent_Array_Sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // define components and constants
    private Button Load_a_image;
    private Button Calculate;
    private Button Save;

    private ImageView image;
    private LinearLayout LL;
    private TextView textView;
    private EditText row;
    private EditText col;
    private TextView result;

    private final int LOAD_A_IMAGE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        setOnClickListener();

    }

    private void findViewById(){

        Load_a_image = (Button) findViewById(R.id.Load_a_image);
        Calculate = (Button) findViewById(R.id.Calculate);
        image = (ImageView) findViewById(R.id.image);
        LL = (LinearLayout) findViewById(R.id.LL);
        textView = (TextView) findViewById(R.id.textView);
        result = (TextView) findViewById(R.id.result);
        row = (EditText) findViewById(R.id.row);
        col = (EditText) findViewById(R.id.col);
        Save = (Button) findViewById(R.id.Save);
    }

    private void setOnClickListener(){

        Load_a_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, LOAD_A_IMAGE);
            }
        });

        Calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (row.getText() != null && col.getText() != null && image.getDrawable() != null) {
                    calculate();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter Row or Col! or Load a image!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (row.getText() != null && col.getText() != null && image.getDrawable() != null) {
                    if (checkStoragePermission()) {
                        save();
                    };
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter Row or Col! or Load a image!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_A_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            image.setImageURI(uri);
        }
    }

    private void calculate(){

        Bitmap bm = ((BitmapDrawable) image.getDrawable()).getBitmap();
        int rowSize = Integer.parseInt(row.getText().toString());
        int colSize = Integer.parseInt(col.getText().toString());
        SparseArray <float[][]> dotI = GetDotIntensity.getIntensity(bm, rowSize, colSize);

        float[][] meanB = dotI.get(1);
        float[][] meanF = dotI.get(2);
        float[][] medianB = dotI.get(3);
        float[][] medianF = dotI.get(4);
        float[][] totalF = dotI.get(5);
        float[][] totalF_medianB = dotI.get(6);
        float[][] threshold = dotI.get(7);

        String text1 = "Dot Intensity: F_Median";
        for (int i = 0; i < rowSize; i++){
            text1 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text1 += medianF[i][j] + "  ";
            }
        }

        String text2 = "\nDot Intensity: F532 Mean";
        for (int i = 0; i < rowSize; i++){
            text2 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text2 += meanF[i][j] + "  ";
            }
        }

        String text3 = "\nDot Intensity: B532 Median";
        for (int i = 0; i < rowSize; i++){
            text3 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text3 += medianB[i][j] + "  ";
            }
        }

        String text4 = "\nDot Intensity: B532 Mean";
        for (int i = 0; i < rowSize; i++){
            text4 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text4 += meanB[i][j] + "  ";
            }
        }

        String text5 = "\nDot Intensity: F532 Median - B Median";
        for (int i = 0; i < rowSize; i++){
            text5 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text5 += (medianF[i][j] - medianB[i][j]) + "  ";
            }
        }

        String text6 = "\nDot Intensity: F532 Mean - B Median";
        for (int i = 0; i < rowSize; i++){
            text6 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text6 += (meanF[i][j] - medianB[i][j]) + "  ";
            }
        }

        String text7 = "\nDot Intensity: F532 Total";
        for (int i = 0; i < rowSize; i++){
            text7 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text7 += totalF[i][j] + "  ";
            }
        }

        String text8 = "\nDot Intensity: F532 Mean - B Mean";
        for (int i = 0; i < rowSize; i++){
            text8 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text8 += (meanF[i][j] - meanB[i][j]) + "  ";
            }
        }

        String text9 = "\nDot Intensity: F532 Median - B Mean";
        for (int i = 0; i < rowSize; i++){
            text9 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text9 += (medianF[i][j] - meanB[i][j]) + "  ";
            }
        }

        String text10 = "\nDot Intensity: F532 Total - B Median";
        for (int i = 0; i < rowSize; i++){
            text10 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text10 += totalF_medianB[i][j] + "  ";
            }
        }

        String text11 = "\nthreshold";
        for (int i = 0; i < rowSize; i++){
            text11 += "\nRow " + (i+1) + ": ";
            for (int j = 0; j < colSize; j++){
                text11 += threshold[i][j] + "  ";
            }
        }

        String text = text1 + text2 + text3 + text4 + text5 + text6 + text7 + text8 + text9 + text10 + text11;

        result.setText(text);
    }

    private void save() {

        Bitmap bm = ((BitmapDrawable) image.getDrawable()).getBitmap();
        int rowSize = Integer.parseInt(row.getText().toString());
        int colSize = Integer.parseInt(col.getText().toString());
        SparseArray <float[][]> dotI = GetDotIntensity.getIntensity(bm, rowSize, colSize);

        float[][] meanB = dotI.get(1);
        float[][] meanF = dotI.get(2);
        float[][] medianB = dotI.get(3);
        float[][] medianF = dotI.get(4);
        float[][] totalF = dotI.get(5);
        float[][] totalF_medianB = dotI.get(6);
        float[][] threshold = dotI.get(7);

        String text1 = "Dot Intensity: F532 Median\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text1 += medianF[i][j] + "\n";
            }
        }

        String text2 = "\nDot Intensity: F532 Mean\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text2 += meanF[i][j] + "\n";
            }
        }

        String text3 = "\nDot Intensity: B532 Median\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text3 += medianB[i][j] + "\n";
            }
        }

        String text4 = "\nDot Intensity: B532 Mean\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text4 += meanB[i][j] + "\n";
            }
        }

        String text5 = "\nDot Intensity: F532 Median - B Median\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text5 += (medianF[i][j] - medianB[i][j]) + "\n";
            }
        }

        String text6 = "\nDot Intensity: F532 Mean - B Median\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text6 += (meanF[i][j] - medianB[i][j]) + "\n";
            }
        }

        String text7 = "\nDot Intensity: F532 Total\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text7 += totalF[i][j] + "\n";
            }
        }

        String text8 = "\nDot Intensity: F532 Mean - B Mean\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text8 += (meanF[i][j] - meanB[i][j]) + "\n";
            }
        }

        String text9 = "\nDot Intensity: F532 Median - B Mean\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text9 += (medianF[i][j] - meanB[i][j]) + "\n";
            }
        }

        String text10 = "\nDot Intensity: F532 Total - B Median\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text10 += totalF_medianB[i][j] + "\n";
            }
        }

        String text11 = "\nthreshold\n";
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                text11 += threshold[i][j] + "\n";
            }
        }

        String text = text1 + text2 + text3 + text4 + text5 + text6 + text7 + text8 + text9 + text10 + text11;

//        FileOutputStream outputStream;
//        BufferedWriter bufferedWriter = null;
//        try {
//            outputStream = openFileOutput("data", Context.MODE_APPEND);
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
//
//            bufferedWriter.write(text);
//
//            Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if(bufferedWriter!=null){
//                try {
//                    bufferedWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }






        //外部存储（即往手机SD卡里面存储）

        //1.判断外部存储设备是否可用
        //Environment是android对外部设备的一个抽象封装，里面有许多关于SD卡此类外部设备的方法和属性使用
        //getExternalStorageState()获取SD卡的状态，返回一个字符串
        //Environment.MEDIA_MOUNTED  表示SD卡可用的常量
        Log.i("MainActivity",Environment.getExternalStorageState());
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this, "SDcard not found! Data save failed!", Toast.LENGTH_SHORT).show();
            return;//若SD卡不可用 直接返回。
        }

        //获取SD卡根目录
        File file = Environment.getExternalStorageDirectory();
        //创建自己的文件
        File myFile = new File(file, "myData.txt");

        //进行IO操作存储 数据（此处就不判断文件是否存在等因素了）
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myFile);
            //进行IO存储（简单的存储一下，不模拟复杂的方式了）
            Log.i("MainActivity","---------->");
            fos.write(text.getBytes());
            Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Data save failed", Toast.LENGTH_SHORT).show();
        }finally{
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                save();
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
