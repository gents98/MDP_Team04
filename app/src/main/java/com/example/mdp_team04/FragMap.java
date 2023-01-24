package com.example.mdp_team04;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FragMap extends Fragment {
    private static final String TAG = "MapFragment";

    Button resetMapBtn, updateMapBtn;
    ToggleButton setStartPointToggleBtn, setEditToggleBtn, clearToggleBtn, northObstacleToggleBtn, southObstacleToggleBtn, eastObstacleToggleBtn, westObstacleToggleBtn;
    GridMap gridMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        gridMap = MainActivity.getGridMap();

        updateMapBtn = root.findViewById(R.id.updateMapBtn);
        resetMapBtn = root.findViewById(R.id.resetMapBtn);
        setStartPointToggleBtn = root.findViewById(R.id.setStartPointToggleBtn);
        northObstacleToggleBtn = root.findViewById(R.id.northObstacleToggleBtn);
        southObstacleToggleBtn = root.findViewById(R.id.southObstacleToggleBtn);
        eastObstacleToggleBtn = root.findViewById(R.id.eastObstacleToggleBtn);
        westObstacleToggleBtn = root.findViewById(R.id.westObstacleToggleBtn);
        clearToggleBtn = root.findViewById(R.id.clearToggleBtn);
        setEditToggleBtn = root.findViewById(R.id.setEditToggleBtn);

        updateMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked updateMapBtn");
                showToast("Updating map...");
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED)
                    gridMap.updateMap();
                else
                    showToast("Not connected. Please connect to a bluetooth device");
            }
        });

        resetMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked resetMapBtn");
                showToast("Reseting map...");
                gridMap.resetMap();
            }
        });

        setEditToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked setEditToggleBtn");
                if (setEditToggleBtn.getText().equals("EDIT")) {
                    showToast("Cancelled edit map");
                    gridMap.setEditMapStatus(false);
                }
                else if (setEditToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please edit map");
                    gridMap.setEditMapStatus(true);
                    gridMap.toggleCheckedBtn("setEditToggleBtn");
                }
                showLog("Exiting setEditToggleBtn");
            }
        });

        setStartPointToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked setStartPointToggleBtn");
                if (setStartPointToggleBtn.getText().equals("STARTING POINT"))
                    showToast("Cancelled selecting starting point");
                else if (setStartPointToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please select starting point");
                    gridMap.setStartCoordStatus(true);
                    gridMap.toggleCheckedBtn("setStartPointToggleBtn");
                }
                showLog("Exiting setStartPointToggleBtn");
            }
        });

        northObstacleToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked northObstacleToggleBtn");
                if (northObstacleToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please plot north obstacles");
                    gridMap.setSetNorthObstacleStatus(true);
                    gridMap.toggleCheckedBtn("northObstacleToggleBtn");
                }
                else if (northObstacleToggleBtn.getText().equals("PLOT")) {
                    gridMap.setSetNorthObstacleStatus(false);
                    showToast("Plotting north obstacles cancelled");
                }
                showLog("Exiting northObstacleToggleBtn");
            }
        });

        southObstacleToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked southObstacleToggleBtn");
                if (southObstacleToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please plot south obstacles");
                    gridMap.setSetSouthObstacleStatus(true);
                    gridMap.toggleCheckedBtn("southObstacleToggleBtn");
                }
                else if (southObstacleToggleBtn.getText().equals("PLOT")) {
                    showToast("Plotting south obstacles cancelled");
                    gridMap.setSetSouthObstacleStatus(false);
                }
                showLog("Exiting southObstacleToggleBtn");
            }
        });

        westObstacleToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked westObstacleToggleBtn");
                if (westObstacleToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please plot west obstacles");
                    gridMap.setSetWestObstacleStatus(true);
                    gridMap.toggleCheckedBtn("westObstacleToggleBtn");
                }
                else if (westObstacleToggleBtn.getText().equals("PLOT")) {
                    showToast("Plotting west obstacles cancelled");
                    gridMap.setSetWestObstacleStatus(false);
                }
                showLog("Exiting westObstacleToggleBtn");
            }
        });

        eastObstacleToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked eastObstacleToggleBtn");
                if (eastObstacleToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please plot east obstacles");
                    gridMap.setSetEastObstacleStatus(true);
                    gridMap.toggleCheckedBtn("eastObstacleToggleBtn");
                }
                else if (eastObstacleToggleBtn.getText().equals("PLOT")) {
                    showToast("Plotting east obstacles cancelled");
                    gridMap.setSetEastObstacleStatus(false);
                }
                showLog("Exiting eastObstacleToggleBtn");
            }
        });

        clearToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked clearToggleBtn");
                if (clearToggleBtn.getText().equals("CANCEL")) {
                    showToast("Please remove cells");
                    gridMap.setUnSetCellStatus(true);
                    gridMap.toggleCheckedBtn("clearToggleBtn");
                }
                else if (clearToggleBtn.getText().equals("REMOVE")) {
                    showToast("Removing cells cancelled");
                    gridMap.setUnSetCellStatus(false);
                }
                showLog("Exiting clearToggleBtn");
            }
        });

        return root;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}