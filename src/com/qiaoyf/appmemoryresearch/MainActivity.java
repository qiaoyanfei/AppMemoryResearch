package com.qiaoyf.appmemoryresearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<IntegerArray> dataArray = new ArrayList<IntegerArray>();
    private static ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    private static final int MEMORY_SIZE=1024*5;//KB

    private Button addData, addBitmap, removeData, removeBitmap, gc,addNativeMem,removeNativeMem;
    private TextView max, vmHeap, vmAllocated, nativeHeap, nativeAllocated,totalAllocated,
            dataSize, bitmapSize,nativeSize;

    private int nativeCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        timerRefresh();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initViews(){
        setContentView(R.layout.activity_main);
        long maxMemory = Runtime.getRuntime().maxMemory();
        max = (TextView) findViewById(R.id.max);
        max.setText((maxMemory / 1024 / 1024) + "MB");
        vmHeap = (TextView) findViewById(R.id.vmHeap);
        vmAllocated = (TextView) findViewById(R.id.vmAllocated);
        nativeHeap = (TextView) findViewById(R.id.nativeHeap);
        nativeAllocated = (TextView) findViewById(R.id.nativeAllocated);
        totalAllocated = (TextView)findViewById(R.id.totalAllocated);
        dataSize = (TextView) findViewById(R.id.dataSize);
        bitmapSize = (TextView) findViewById(R.id.bitmapSize);
        nativeSize = (TextView) findViewById(R.id.nativeSize);
        addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    dataArray.add(new IntegerArray(new int[MEMORY_SIZE * 256]));
                } catch (OutOfMemoryError e) {
                    showErrorToast(e);
                }
                updateTextView();
            }
        });
        addBitmap = (Button) findViewById(R.id.addBitmap);
        addBitmap.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(MEMORY_SIZE, 256, Bitmap.Config.ARGB_8888);
                    bitmapArray.add(bitmap);
                } catch (OutOfMemoryError e) {
                    showErrorToast(e);
                }
                updateTextView();
            }
        });


        addNativeMem = (Button) findViewById(R.id.addNativeMem);
        addNativeMem.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (addNativeMem(MEMORY_SIZE))
                    nativeCount++;
                updateTextView();
            }
        });

        removeNativeMem = (Button) findViewById(R.id.removeNativeMem);
        removeNativeMem.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (removeNativeMem(MEMORY_SIZE))
                    nativeCount--;
                updateTextView();
            }
        });


        removeData = (Button) findViewById(R.id.removeData);
        removeData.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (dataArray.size() > 0) {
                    dataArray.remove(dataArray.size()-1);
                }
                updateTextView();
            }
        });
        removeBitmap = (Button) findViewById(R.id.removeBitmap);
        removeBitmap.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (bitmapArray.size() > 0) {
                    bitmapArray.remove(bitmapArray.size()-1);

                }
                updateTextView();
            }
        });
        gc = (Button) findViewById(R.id.gc);
        gc.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.gc();
                updateTextView();
            }
        });

        updateTextView();
    }

    private void updateTextView() {
        Runtime rt = Runtime.getRuntime();
        long vmAlloc =  rt.totalMemory() - rt.freeMemory();
        long nativeAlloc = Debug.getNativeHeapAllocatedSize();
        vmHeap.setText(formatMemoeryText(rt.totalMemory()));
        vmAllocated.setText(formatMemoeryText(vmAlloc));
        nativeAllocated.setText(formatMemoeryText(nativeAlloc));
        totalAllocated.setText(formatMemoeryText(nativeAlloc+vmAlloc));
        nativeHeap.setText(formatMemoeryText(Debug.getNativeHeapSize()));
        int dataSizeNumber = 0;
        for(IntegerArray intArray:dataArray){
            dataSizeNumber+=intArray.array.length;
        }

        dataSize.setText(formatMemoeryText(dataSizeNumber*4));
        int bitmapSizeNumber = 0;
        for(Bitmap bitmap:bitmapArray){
            bitmapSizeNumber += bitmap.getRowBytes() * bitmap.getHeight();
        }
        bitmapSize.setText(formatMemoeryText(bitmapSizeNumber));

        nativeSize.setText(formatMemoeryText(nativeCount * MEMORY_SIZE*1024));

        bitmapSize.setText(formatMemoeryText(bitmapSizeNumber));
    }

    private String formatMemoeryText(long memory) {
        float memoryInMB = memory * 1f / 1024 / 1024;
        return String.format("%.1f MB", memoryInMB);
    }

    private void showErrorToast(OutOfMemoryError error) {
        String message = error.getMessage();
        if(message==null||message.equals("null")){
            message = "";
        }
        Toast.makeText(this, "OutOfMemoryError:" + message,
                Toast.LENGTH_LONG).show();
    }

    private void timerRefresh() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    public void run() {
                        updateTextView();
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 500);
    }

    static class IntegerArray{
        public IntegerArray(int[] array) {
            this.array = array;
        }
        int[] array;
    }

    public native boolean  addNativeMem(int memorySize);
    public native boolean  removeNativeMem(int memorySize);
    static {
        System.loadLibrary("native_memory");
    }


}
