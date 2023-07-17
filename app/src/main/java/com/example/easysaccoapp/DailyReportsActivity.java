package com.example.easysaccoapp;

import static java.util.Calendar.DATE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easysaccoapp.Model.DailyReport;
import com.example.easysaccoapp.Model.MemberCollection;
import com.example.easysaccoapp.Pockdata.PocketPos;
import com.example.easysaccoapp.util.AppConstants;
import com.example.easysaccoapp.util.DateUtil;
import com.example.easysaccoapp.util.FontDefine;
import com.example.easysaccoapp.util.P25ConnectionException;
import com.example.easysaccoapp.util.P25Connector;
import com.example.easysaccoapp.util.Printer;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class DailyReportsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mEnableBtn, mPrintReceiptBtn, mConnectBtn;
    public static EditText Transsdate;
    private Spinner mDeviceSp;
    private ProgressDialog mProgressDlg, mConnectingDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private P25Connector mConnector;
    public String  tomorrow = "";
    public String yesterday="";
    static SQLiteDatabase db;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_reports);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Daily Collection Report");
        setSupportActionBar(myToolbar);

        mConnectBtn = (Button) findViewById(R.id.btn_connectd);
        mEnableBtn = (Button) findViewById(R.id.btn_enabled);
        mPrintReceiptBtn = (Button) findViewById(R.id.btn_print_receiptd);
        mDeviceSp = (Spinner) findViewById(R.id.sp_deviced);

        Transsdate = (EditText) findViewById(R.id.Transsdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Transsdate.setText(sdf.format(new Date()));

        db = openOrCreateDatabase("BosaDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS loanRepay(memberNo VARCHAR,amount VARCHAR, loanShareType VARCHAR, date DATETIME, auditId VARCHAR,status VARCHAR, transdate DATETIME, printed VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS loanTypes(type VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS sharesContrib(memberNo VARCHAR,amount VARCHAR, loanShareType VARCHAR, date DATETIME, auditId VARCHAR,status VARCHAR, transdate DATETIME, printed VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS shareTypes(type VARCHAR);");
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

            Transsdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    final Calendar calendar = Calendar.getInstance();
                    calendar.add(  DATE,1);
                    tomorrow = df.format(calendar.getTime());
                    calendar.add(DATE,-2);
                    yesterday = df.format(calendar.getTime());


                    int yy = calendar.get(Calendar.YEAR);
                    int mm = calendar.get(Calendar.MONTH)+1;
                    int dd = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePicker = new DatePickerDialog(DailyReportsActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            String monthString = String.valueOf((monthOfYear)+1);
                            if (monthString.length() == 1) {
                                monthString = "0" + monthString;
                            }
                            String dayOfMonthString = String.valueOf(dayOfMonth);
                            if (dayOfMonthString.length() == 1) {
                                dayOfMonthString = "0" + dayOfMonthString;
                            }

                            Transsdate.setText(new StringBuilder().append(year).append("-")
                                    .append(monthString).append("-").append(dayOfMonthString).append(" "));

                            Transsdate.setText(new StringBuilder().append(year).append("-")
                                    .append(monthString).append("-").append(dayOfMonthString).append(" "));
                        }
                    }, yy, mm, dd);
                    datePicker.show();
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

    @Override
    public void onClick(View view) {

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

    private void showDisabled() {
        showToast("Bluetooth disabled");
        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
        mDeviceSp.setVisibility(View.GONE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
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

    private void showConnected() {
        showToast("Connected");
        mConnectBtn.setText("Disconnect");
        mPrintReceiptBtn.setEnabled(true);
        mDeviceSp.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");
        mConnectBtn.setText("Connect");
        mPrintReceiptBtn.setEnabled(false);
        mDeviceSp.setEnabled(true);
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

    private void printStruk() {
        try {
            long milis1 = System.currentTimeMillis();
            String date = DateUtil.timeMilisToString(milis1, "yyyy-MM-dd");
            String time = DateUtil.timeMilisToString(milis1, "  HH:mm a");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String myDate =(Transsdate.getText().toString());
            myDate =  myDate.trim();

            Cursor l = db.rawQuery("SELECT type FROM loanTypes", null);
            ArrayList<String> loanTypeList = new ArrayList<>();
            while (l.moveToNext()) {
                String loanType = l.getString(0);
                loanTypeList.add(loanType);
            }

            Cursor s = db.rawQuery("SELECT type FROM shareTypes", null);
            ArrayList<String> sharesTypeList = new ArrayList<>();
            while (s.moveToNext()) {
                String sharesType = s.getString(0);
                sharesTypeList.add(sharesType);
            }

            ArrayList<DailyReport> dailyReport = new ArrayList<DailyReport>();
            String strAuditId = "";
            for(String loanType : loanTypeList){
                ArrayList<MemberCollection> collectionList = new ArrayList<MemberCollection>();
                Cursor lc = db.rawQuery("SELECT * FROM loanRepay WHERE  transdate ='"+myDate+"' AND loanShareType='"+loanType+"'", null);

                Double amount = Double.valueOf(0);
                while (lc.moveToNext()) {
                    String memberNo = lc.getString(0);
                    String str_amount = lc.getString(1);
                    if (str_amount == null)
                        str_amount = "0";
                    strAuditId = lc.getString(4);
                    amount += Double.parseDouble(str_amount);
                    collectionList.add(new MemberCollection(memberNo, str_amount));
                }

                dailyReport.add(new DailyReport(loanType, amount, collectionList));
            }

            for(String shareType : sharesTypeList){
                ArrayList<MemberCollection> collectionList = new ArrayList<MemberCollection>();
                Cursor sc = db.rawQuery("SELECT * FROM sharesContrib WHERE  transdate ='"+myDate+"' AND loanShareType='"+shareType+"'", null);

                Double amount = Double.valueOf(0);
                while (sc.moveToNext()) {
                    String memberNo = sc.getString(0);
                    String str_amount = sc.getString(1);
                    if (str_amount == null)
                        str_amount = "0";
                    strAuditId = sc.getString(4);
                    amount += Double.parseDouble(str_amount);
                    collectionList.add(new MemberCollection(memberNo, str_amount));
                }

                dailyReport.add(new DailyReport(shareType, amount, collectionList));
            }

            StringBuffer buffer = new StringBuffer();
            Double totalCollected = Double.valueOf(0);
            for(DailyReport report : dailyReport){
                if (report.getTotal() > 0){
                    totalCollected += report.getTotal();
                    buffer.append(report.getTransaction() + "\n");
                    for(MemberCollection collection : report.getMemberCollection()){
                        buffer.append("MemberNo. \t  Amount" + "\n");
                        buffer.append(collection.getMemberNo() + " \t  " + collection.getAmount() +"\n");
                        buffer.append("-----------------------------" + "\n");
                    }

                    buffer.append("Amount       : KES. " + report.getTotal() + "\n");
                    buffer.append("-----------------------------" + "\n");
                }
            }

            buffer.append("Total       : KES. " + totalCollected + "\n");
            buffer.append("Received By    :" + strAuditId + "\n");
            StringBuilder content2Sb = new StringBuilder();
            content2Sb.append("\n" + AppConstants.SACCO + "\n\n\t" + " RECEIPT" + "\n\n");
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
            sendData(senddata);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "No New Records Found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }
}