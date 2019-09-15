package com.example.alsist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class ManualActivity extends AppCompatActivity {

    private static final String TAG = "BLUETOOTH";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:04:21:15:97";

    final int RECEIVE_MESSAGE = 1;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    public static ConnectedThread mConnectedThread;

    private int distance, angle;
    private boolean isRun = false;

    Handler h;

    JoyStick joyStick;

    RelativeLayout layout_joystick;
    Button mButtonStop;
    TextView mTextViewX, mTextViewY, mTextViewAngle, mTextViewDistance, mTextViewDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try {
            bluetoothSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        bluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "...Connecting...");
        try {

            bluetoothSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        mConnectedThread = new ConnectedThread(bluetoothSocket);
        mConnectedThread.start();


        layout_joystick = (RelativeLayout)findViewById (R.id.layout_joystick);
        mButtonStop = (Button) findViewById(R.id.button_stop);
        mTextViewX = (TextView) findViewById(R.id.text_view_x);
        mTextViewY = (TextView) findViewById(R.id.text_view_y);
        mTextViewAngle = (TextView) findViewById(R.id.text_view_angle);
        mTextViewDistance = (TextView) findViewById(R.id.text_view_distance);
        mTextViewDirection = (TextView) findViewById(R.id.text_view_direction);

        joyStick = new JoyStick(getApplicationContext(), layout_joystick, R.drawable.image_button);
        joyStick.setStickSize(150, 150);
        joyStick.setLayoutSize(500, 500);
        joyStick.setLayoutAlpha(150);
        joyStick.setStickAlpha(100);
        joyStick.setOffset(90);
        joyStick.setMinimumDistance(50);

        startThreads();

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                joyStick.drawStick(arg1);

                if(arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    mTextViewX.setText("X : " + String.valueOf(joyStick.getX()));
                    mTextViewY.setText("Y : " + String.valueOf(joyStick.getY()));
                    mTextViewAngle.setText("Angle : " + String.valueOf(joyStick.getAngle()));
                    mTextViewDistance.setText("Distance : " + String.valueOf(joyStick.getDistance()));

                    int direction = joyStick.get8Direction();
                    if(direction == JoyStick.STICK_UP) {
                        mTextViewDirection.setText("Direction : Up");
                    } else if(direction == JoyStick.STICK_UPRIGHT) {
                        mTextViewDirection.setText("Direction : Up Right");
                    } else if(direction == JoyStick.STICK_RIGHT) {
                        mTextViewDirection.setText("Direction : Right");
                    } else if(direction == JoyStick.STICK_DOWNRIGHT) {
                        mTextViewDirection.setText("Direction : Down Right");
                    } else if(direction == JoyStick.STICK_DOWN) {
                        mTextViewDirection.setText("Direction : Down");
                    } else if(direction == JoyStick.STICK_DOWNLEFT) {
                        mTextViewDirection.setText("Direction : Down Left");
                    } else if(direction == JoyStick.STICK_LEFT) {
                        mTextViewDirection.setText("Direction : Left");
                    } else if(direction == JoyStick.STICK_UPLEFT) {
                        mTextViewDirection.setText("Direction : Up Left");
                    } else if(direction == JoyStick.STICK_NONE) {
                        mTextViewDirection.setText("Direction : Center");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    mTextViewX.setText("X :");
                    mTextViewY.setText("Y :");
                    mTextViewAngle.setText("Angle :");
                    mTextViewDistance.setText("Distance :");
                    mTextViewDirection.setText("Direction :");
                }

                if(joyStick.getDistance() > 200) {
                    distance = 99;
                } else {
                    distance = (int) joyStick.getDistance() / 2;
                }
                angle = (int)(joyStick.getAngle() - 180) / 2;

                switch(arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRun = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        isRun = false;
                        mConnectedThread.write("00/45\n");
                        break;
                }

                return true;
            }
        });

        mButtonStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRun = false;
                mConnectedThread.write("00/45\n");
            }
        });
    }

    public void startThreads() {
        new Thread() {
            public void run() {
                while(true) {
                    try {
                        if(isRun) {
                            if(angle < 0)
                                continue;
                            else if(distance < 10 && angle < 10)
                                mConnectedThread.write("0" + distance + "/0" + angle + "\n");
                            else if(distance < 10)
                                mConnectedThread.write("0" + distance + "/" + angle + "\n");
                            else if(angle < 10)
                                mConnectedThread.write(distance + "/0" + angle + "\n");
                            else if(angle >= 10 && distance >= 10)
                                mConnectedThread.write(distance + "/" + angle + "\n");
                        }

                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void errorExit(String title, String message){
        Toast.makeText(this, title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void checkBTState() {
        if(bluetoothAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if(bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String message) {
            byte[] msgBuffer = message.getBytes();

            try {
                Log.d(TAG, "...Data to send: " + message + "..." + msgBuffer.length);
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
