package com.example.mdp_team04;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FragControl extends Fragment {
    ImageButton forwardImageBtn, rightImageBtn, backwardImageBtn, leftImageBtn;
    ImageButton forwardRightImageBtn, forwardLeftImageBtn, backwardRightImageBtn, backwardLeftImageBtn;
    TextView movementStatusTextView;
    Switch autoManualSwitch;
    Button calibrateButton;

    private final String STM = "STM,";

    private final String forward = "w";
    private final String backward = "s";
    private final String forwardRight = "d";
    private final String forwardLeft = "a";
    private final String backwardRight = "e";
    private final String backwardLeft = "q";
    private final String rotateRight = "x";
    private final String rotateLeft = "z";

    private final String forwardText = "Forward";
    private final String backwardText = "Backward";
    private final String forwardRightText = "Forward Right";
    private final String forwardLeftText = "Forward Left";
    private final String backwardRightText = "Backward Right";
    private final String backwardLeftText = "Backward Left";
    private final String rotateRightText = "Rotate Right";
    private final String rotateLeftText = "Rotate Left";

//    private Handler manualControllerHandler = new Handler ();

    private final static String TAG = "FragmentControl";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_control, container, false);

        forwardImageBtn = root.findViewById(R.id.northImageBtn);
        rightImageBtn = root.findViewById(R.id.eastImageBtn);
        backwardImageBtn = root.findViewById(R.id.southImageBtn);
        leftImageBtn = root.findViewById(R.id.westImageBtn);

        forwardRightImageBtn = root.findViewById(R.id.northEastImageBtn);
        forwardLeftImageBtn = root.findViewById(R.id.northWestImageBtn);
        backwardRightImageBtn = root.findViewById(R.id.southEastImageBtn);
        backwardLeftImageBtn = root.findViewById(R.id.southWestImageBtn);

//        autoManualSwitch = root.findViewById(R.id.autoManualSwitch);
//        calibrateButton = root.findViewById(R.id.calibrateButton);
        movementStatusTextView = root.findViewById(R.id.movementStatusTV);

        // Button Listener

//        forwardImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARD";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARD");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        forwardRightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        rightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, RIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("RIGHT");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        backwardRightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        backwardImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARD";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARD");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        backwardLeftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        leftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, LEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("LEFT");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        forwardLeftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });

        forwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + forward;
                    movementStatusTextView.setText(forwardText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        backwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + backward;
                    movementStatusTextView.setText(backwardText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        forwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + forwardRight;
                    movementStatusTextView.setText(forwardRightText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        forwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + forwardLeft;
                    movementStatusTextView.setText(forwardLeftText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        backwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + backwardRight;
                    movementStatusTextView.setText(backwardRightText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        backwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + backwardLeft;
                    movementStatusTextView.setText(backwardLeftText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        rightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + rotateRight;
                    movementStatusTextView.setText(rotateRightText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        leftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    String tmp = STM + rotateLeft;
                    movementStatusTextView.setText(rotateLeftText);
                    BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

//    private void delay() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }
}
