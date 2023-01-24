package com.example.mdp_team04;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mdp_team04.BluetoothUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FragMsg {
}private ListView listViewSentMessages;
    private ListView listViewReceivedMessages;
    private EditText edCreateMessage;
    private Button btnSendMessage;
    private ImageButton deleteSentMessages;
    private ImageButton deleteReceivedMessages;
    private static ArrayAdapter<String> adapterSentMessages;
    private static ArrayAdapter<String> adapterReceivedMessages;
    private static final String TAG = "FragmentMessage";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_message, container, false);

        listViewSentMessages = root.findViewById(R.id.list_sent_messages);
        listViewSentMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listViewReceivedMessages = root.findViewById(R.id.list_received_messages);
        listViewReceivedMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        edCreateMessage = root.findViewById(R.id.edit_enter_message);
        btnSendMessage = root.findViewById(R.id.btn_send_msg);
        deleteSentMessages = root.findViewById(R.id.deleteSentMessages);
        deleteReceivedMessages = root.findViewById(R.id.deleteReceivedMessages);

        adapterSentMessages = new ArrayAdapter<String>(getActivity(), R.layout.message_layout);
        listViewSentMessages.setAdapter(adapterSentMessages);

        adapterReceivedMessages = new ArrayAdapter<String>(getActivity(), R.layout.message_layout);
        listViewReceivedMessages.setAdapter(adapterReceivedMessages);

        adapterSentMessages.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewSentMessages.setSelection(adapterSentMessages.getCount() - 1);
            }
        });

        adapterReceivedMessages.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewReceivedMessages.setSelection(adapterReceivedMessages.getCount() - 1);
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("sendButton Clicked");
                String message = edCreateMessage.getText().toString();
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    if (!message.isEmpty()) {
                        edCreateMessage.setText("");
                        BluetoothUtils.write(message.getBytes());
                        showLog("Bluetooth message sent");
                    }
                } else {
                    showToast("Not connected. Please connect to a bluetooth device");
                    showLog("Not connected. Please connect to a bluetooth device");
                }
            }
        });

        deleteSentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterSentMessages.clear();
                adapterSentMessages.notifyDataSetChanged();
            }
        });

        deleteReceivedMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterReceivedMessages.clear();
                adapterReceivedMessages.notifyDataSetChanged();
            }
        });

        return root;
    }

    public static void addToAdapterSentMessages(String owner, String message) {
        adapterSentMessages.add(getCurrentTime() + " : " + message);
        adapterSentMessages.notifyDataSetChanged();
    }

    public static void addToAdapterReceivedMessages(String owner, String message) {
        adapterReceivedMessages.add(getCurrentTime() + " : " + message);
        adapterReceivedMessages.notifyDataSetChanged();
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return date.format(currentLocalTime);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }
}
