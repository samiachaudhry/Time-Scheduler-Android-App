package com.saqi.time_scheduler.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.MyTime;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.TimeSlot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SaveFile {
    private final String TAG = "SaveFile";
    Activity context;
    private static SaveFile _INSTANCE;

    public static SaveFile getInstance(Activity context) {
        if (_INSTANCE == null) {
            _INSTANCE = new SaveFile(context);
        }
        return _INSTANCE;
    }

    private SaveFile(Activity context) {
        this.context = context;
    }

    private String saveToInternalStorageMethod2(Context context, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("", MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String IMAGE_PATH_ABSOLUTE = directory.getAbsolutePath();
        SharedPreferences sharedPreferences = context.getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("imagepath", IMAGE_PATH_ABSOLUTE);
        editor.apply();

        Log.d(TAG, "Profile Activity: saveToInternalStorage(): Image saved: absolute path = " + IMAGE_PATH_ABSOLUTE);
        return IMAGE_PATH_ABSOLUTE;
    }

    private void saveToTextFile(String sBody, String fileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Download");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void saveToExcel(List<ClassDetail> TIME_TABLE, List<Room> roomList, List<TimeSlot> timeSlotList) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "GRANTED");
        } else {
            Log.i(TAG, "NOT GRANTED");
            context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        String fileNameExcel = "TimeTable.xls";
        String directory = "Download";
        HSSFWorkbook workbook = new HSSFWorkbook();

        for (int dayIndex = 1; dayIndex <= 5; dayIndex++) {
            HSSFSheet day = workbook.createSheet(FormateDate.getInstance().getDayByIndex(dayIndex));
            for (int rowindex = 0; rowindex <= roomList.size(); rowindex++) {
                HSSFRow row = day.createRow(rowindex);
                if (rowindex > 0) {
                    HSSFCell cellRoomNo = row.createCell(0);
                    cellRoomNo.setCellValue(new HSSFRichTextString(roomList.get(rowindex - 1).getName()));
                }
                for (int colindex = 1; colindex <= timeSlotList.size(); colindex++) {
                    HSSFCell cellTime = row.createCell(colindex);
                    String sTime = timeSlotList.get(colindex - 1).getStartTime();
                    String eTime = timeSlotList.get(colindex - 1).getEndTime();
                    MyTime st = FormateDate.getInstance().formatTime(sTime);
                    MyTime et = FormateDate.getInstance().formatTime(sTime);
                    String ST = sTime;
                    String ET = eTime;
                    if (st != null) {
                        ST = st.getHour() + ":" + st.getMint() + " " + st.getAMPM();
                    }
                    if (et != null) {
                        ET = et.getHour() + ":" + et.getMint() + " " + et.getAMPM();
                    }
                    if (rowindex == 0) {
                        cellTime.setCellValue(new HSSFRichTextString(ST + " - " + ET));
                    } else {
                        ClassDetail classDetail =
                                LinearSearch.getInstance().isAvailableClass(TIME_TABLE, roomList.get(rowindex - 1).getName(), dayIndex, sTime);

                        String fillData = "";
                        if (classDetail != null) {
                            fillData = classDetail.getProgramme() + "\n" + classDetail.getSubject() + "\n" + classDetail.getTeachername();
                            Log.i(TAG, "PrintTimeTable: Xls: class Added for Day:" + dayIndex + "::Room:"
                                    + roomList.get(rowindex - 1).getName() + "::Start Time:" + ST);
                        } else {
                            Log.i(TAG, "PrintTimeTable: Xls: class Null for Day:" + dayIndex + "::Room:"
                                    + roomList.get(rowindex - 1).getName() + "::Start Time:" + ST);
                        }
                        cellTime.setCellValue(new HSSFRichTextString(fillData));
                    }
                }
            }
        }

        try {
            FileOutputStream fos;
            File root = new File(Environment.getExternalStorageDirectory(), directory);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, fileNameExcel);
            Log.i(TAG, "File:" + file.getPath());
            fos = new FileOutputStream(file);
            workbook.write(fos);
            try {
                fos.flush();
                fos.close();
                Toast.makeText(context, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            Toast.makeText(context, "Exception in saving excel", Toast.LENGTH_SHORT).show();
        }
    }
}
