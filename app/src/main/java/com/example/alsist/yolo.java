package com.example.alsist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class yolo extends AppCompatActivity {
    static final int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;

    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevie;
    BluetoothSocket mSocket = null;

    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    char mCharDelimiter =  '\n';

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;
    String temp = "UNKNOWN/00.00/00.00/";

    private ImageView imageView;
    private TextView textViewClass, textViewDistance, textViewTime;

    SoundPool mPool;
    int mDdok;
    AudioManager mAm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolo);

        imageView = (ImageView) findViewById(R.id.image_view_img);
        textViewClass = (TextView) findViewById(R.id.text_view_class);
        textViewDistance = (TextView) findViewById(R.id.text_view_distance);
        textViewTime = (TextView) findViewById(R.id.text_view_time);

        mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mDdok = mPool.load(this, R.raw.collision, 1);
        mAm = (AudioManager) getSystemService(AUDIO_SERVICE);

        checkBluetooth();
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            beginListenForData();

        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;
        readBuffer = new byte[1024];

        mWorkerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        try {
                            mOutputStream.write("R".getBytes());
                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(), "Data Send Error", Toast.LENGTH_SHORT).show();
                        }

                        int byteAvailable = 0;

                        for(;;)
                        {
                            // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
                            byteAvailable = mInputStream.available();   // 수신 데이터 확인

                            if(byteAvailable > 0) break;
                        }

                        Log.e("BYTE AVAILABLE", "" + byteAvailable);
                        if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes = new byte[byteAvailable];
                            // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                            mInputStream.read(packetBytes);

                            for(int i=0; i<byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
                                    //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "US-ASCII");
                                    temp = data;
                                    readBufferPosition = 0;
                                    // Log.e("RECEIVE DATA : ", data);

                                    try {
                                        mOutputStream.write("S\n".getBytes());
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "데이터 전송중 오류 발생", Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                    handler.post(new Runnable(){
                                        // 수신된 문자열 데이터에 대한 처리.
                                        @Override
                                        public void run() {
                                            String classStr, distanceStr, ttcStr;
                                            int division = data.indexOf('/');
                                            classStr = data.substring(0, division);
                                            temp = data.substring(division, data.length());
                                            division = temp.indexOf('/');
                                            temp = temp.substring(division+1, temp.length());
                                            division = temp.indexOf('/');
                                            distanceStr = temp.substring(0, division);
                                            temp = temp.substring(division+1, temp.length());
                                            division = temp.indexOf('/');
                                            ttcStr = temp.substring(0, division);

                                            if(Double.parseDouble(distanceStr) < 0.5 || (Double.parseDouble(ttcStr) <=2 && Double.parseDouble(ttcStr) > 0)) {
                                                if(classStr.equals("unknown"))
                                                    imageView.setImageResource(R.drawable.unknown_red);
                                                else if(classStr.equals("bicycle"))
                                                    imageView.setImageResource(R.drawable.bicycle_red);
                                                else if(classStr.equals("car"))
                                                    imageView.setImageResource(R.drawable.car_red);
                                                else if(classStr.equals("cat"))
                                                    imageView.setImageResource(R.drawable.cat_red);
                                                else if(classStr.equals("dog"))
                                                    imageView.setImageResource(R.drawable.dog_red);
                                                else if(classStr.equals("motorbike"))
                                                    imageView.setImageResource(R.drawable.motorbike_red);
                                                else if(classStr.equals("person"))
                                                    imageView.setImageResource(R.drawable.person_red);

                                                textViewClass.setText(classStr);
                                                textViewDistance.setText(distanceStr);
                                                textViewTime.setText(ttcStr);

                                                textViewClass.setTextColor(Color.RED);
                                                textViewDistance.setTextColor(Color.RED);
                                                textViewTime.setTextColor(Color.RED);

                                                mPool.play(mDdok, 1, 1, 0, 0, 1);

                                            } else {
                                                if(classStr.equals("unknown"))
                                                    imageView.setImageResource(R.drawable.unknown);
                                                else if(classStr.equals("bicycle"))
                                                    imageView.setImageResource(R.drawable.bicycle);
                                                else if(classStr.equals("car"))
                                                    imageView.setImageResource(R.drawable.car);
                                                else if(classStr.equals("cat"))
                                                    imageView.setImageResource(R.drawable.cat);
                                                else if(classStr.equals("dog"))
                                                    imageView.setImageResource(R.drawable.dog);
                                                else if(classStr.equals("motorbike"))
                                                    imageView.setImageResource(R.drawable.motorbike);
                                                else if(classStr.equals("person"))
                                                    imageView.setImageResource(R.drawable.person);

                                                textViewClass.setText(classStr);
                                                textViewDistance.setText(distanceStr);
                                                textViewTime.setText(ttcStr);

                                                textViewClass.setTextColor(Color.BLACK);
                                                textViewDistance.setTextColor(Color.BLACK);
                                                textViewTime.setTextColor(Color.BLACK);
                                            }

                                            Log.e("RECEIVE DATA : ", data);

                                        }

                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {
                        //Toast.makeText(getApplicationContext(), "데이터 수신중 오류 발생", Toast.LENGTH_LONG).show();
                        //finish();
                    }
                }
            }

        });

        mWorkerThread.start();

    }

    void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0 ) {
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Bluetooth Device");

        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("Cancel");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items,     new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPairedDeviceCount) {
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }


    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if(!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
                selectDevice();
        }
    }

    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt();
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
