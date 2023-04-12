package com.example.easysaccoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easysaccoapp.Pockdata.PocketPos;
import com.example.easysaccoapp.util.AppConstants;
import com.example.easysaccoapp.util.DateUtil;
import com.example.easysaccoapp.util.FontDefine;
import com.example.easysaccoapp.util.P25ConnectionException;
import com.example.easysaccoapp.util.P25Connector;
import com.example.easysaccoapp.util.Printer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class PrintActivity extends AppCompatActivity {
    private Button mEnableBtn, mPrintReceiptBtn, mConnectBtn;
    private Spinner mDeviceSp;
    private ProgressDialog mProgressDlg, mConnectingDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private P25Connector mConnector;
    static SQLiteDatabase db;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Print Report");
        setSupportActionBar(myToolbar);

        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mEnableBtn = (Button) findViewById(R.id.btn_enable);
        mPrintReceiptBtn = (Button) findViewById(R.id.btn_print_receipt);
        mDeviceSp = (Spinner) findViewById(R.id.sp_device);

        db = openOrCreateDatabase("BosaDb", Context.MODE_PRIVATE, null);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);
                    updateDeviceList();
                }
            }

            mProgressDlg = new ProgressDialog(this);
            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);
            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);
            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {
                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();
                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });

            //enable bluetooth
            mEnableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);
                }
            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    connect();
                }
            });

            mPrintReceiptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    printStruk();
                }
            });
        }
    }

    private void printStruk() {
        Bundle getBundle = this.getIntent().getExtras();
        int transType = getBundle.getInt("transType");
        String memberNo = getBundle.getString("memberNo");
        long milis1 = System.currentTimeMillis();
        String date = DateUtil.timeMilisToString(milis1, "yyyy-MM-dd");

        String time = DateUtil.timeMilisToString(milis1, "  HH:mm a");
        StringBuffer buffer = new StringBuffer();

        String query = "SELECT sum(amount),auditId FROM loanRepay WHERE printed='0' AND memberNo='" + memberNo + "' AND transdate='" + date + "'";
        if (transType == AppConstants.SHARESCONTRIB) {
            query = "SELECT sum(amount),auditId FROM sharesContrib WHERE printed='0' AND memberNo='" + memberNo + "' AND transdate='" + date + "'";
        }

        String strAmount = "";
        String strAuditId = "";
        Cursor c1 = db.rawQuery(query, null);
        while (c1.moveToNext()) {
            strAmount = c1.getString(0);
            strAuditId = c1.getString(1);
        }

        String transaction = "Loan Repay";
        if (transType == AppConstants.SHARESCONTRIB) {
            transaction = "Shares Contribution";
        }
        buffer.append("Member No    :" + memberNo + "\n");
        buffer.append("Quantity       : KES. " + strAmount + "\n");
        buffer.append("Received By    :" + strAuditId + "\n");

        /*
        buffer.append("Office TelNo.   : 0746776828\n");
        buffer.append("Vet   : \n");
        * */
        StringBuilder content2Sb = new StringBuilder();
        content2Sb.append("\n" + AppConstants.SACCO + "\n\n\t" + transaction + " RECEIPT" + "\n\n");
        content2Sb.append("-----------------------------" + "\n");
        content2Sb.append("" + buffer.toString() + "" + "\n");
        content2Sb.append("--------------------------" + "\n");
        content2Sb.append("Date:" + date + "" + "," + "Time:" + time + "" + "\n");
        content2Sb.append("--------------------------" + "\n");
        content2Sb.append("DESIGNED & DEVELOPED BY" + "\n");
        content2Sb.append("AMTECH TECHNOLOGIES LTD" + "\n");
        content2Sb.append("www.amtechafrica.com" + "\n");
        content2Sb.append("--------------------------" + "\n");

        byte[] content2Byte = Printer.printfont(content2Sb.toString(), FontDefine.FONT_32PX, FontDefine.Align_LEFT, (byte) 0x1A,
                PocketPos.LANGUAGE_ENGLISH);
        byte[] totalByte = new byte[content2Byte.length];
        int offset = 0;
        System.arraycopy(content2Byte, 0, totalByte, offset, content2Byte.length);
        offset += content2Byte.length;
        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);
        sendData(senddata, memberNo, transType);
    }

    private void sendData(byte[] bytes, String memberNo, int transType) {
        try {
            String query = "UPDATE loanRepay set printed='1' where printed='0' AND memberNo='" + memberNo + "';";
            if (transType == AppConstants.SHARESCONTRIB) {
                query = "UPDATE sharesContrib set printed='1' where printed='0' AND memberNo='" + memberNo + "';";
            }
            db.execSQL(query);
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");
                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }


        //my code
    }

    private void createBond(BluetoothDevice device) throws Exception {
        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};
            Method method = cl.getMethod("createBond", par);
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item2, getArray(mDeviceList));
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];
        if (data == null) return list;
        int size = data.size();
        list = new String[size];
        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }
        return list;
    }

    private void showDisonnected() {
        showToast("Disconnected");
        mConnectBtn.setText("Connect");
        mPrintReceiptBtn.setEnabled(false);
        mDeviceSp.setEnabled(true);
    }

    private void showConnected() {
        showToast("Connected");
        mConnectBtn.setText("Disconnect");
        mPrintReceiptBtn.setEnabled(true);
        mDeviceSp.setEnabled(false);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");
        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
        mDeviceSp.setVisibility(View.GONE);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");
        mConnectBtn.setEnabled(false);
        mPrintReceiptBtn.setEnabled(false);
        mDeviceSp.setEnabled(false);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}