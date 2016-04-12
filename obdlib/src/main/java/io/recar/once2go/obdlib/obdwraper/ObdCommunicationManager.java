//package io.recar.once2go.obdlib.obdwraper;
//
//import android.app.AlertDialog;
//import android.app.Application;
//import android.app.Dialog;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.hardware.SensorManager;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.ViewGroup;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import com.github.pires.obd.commands.ObdCommand;
//import com.github.pires.obd.enums.AvailableCommandNames;
//
//import java.io.FileNotFoundException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import io.recar.once2go.obdlib.obdcomponents.ConfigActivity;
//import io.recar.once2go.obdlib.obdcomponents.ObdCommandJob;
//import io.recar.once2go.obdlib.obdcomponents.ObdProgressListener;
//
///**
// * Created by once2go on 11.04.16.
// */
//public class ObdCommunicationManager implements ObdProgressListener{
//
//    private Context mContext;
//    private static final String TAG = ObdCommunicationManager.class.getName();
//
//    private static final int NO_BLUETOOTH_ID = 0;
//    private static final int BLUETOOTH_DISABLED = 1;
//    private static final int START_LIVE_DATA = 2;
//    private static final int STOP_LIVE_DATA = 3;
//    private static final int SETTINGS = 4;
//    private static final int GET_DTC = 5;
//    private static final int TABLE_ROW_MARGIN = 7;
//    private static final int NO_ORIENTATION_SENSOR = 8;
//    private static final int NO_GPS_SUPPORT = 9;
//    private static final int TRIPS_LIST = 10;
//    private static final int SAVE_TRIP_NOT_AVAILABLE = 11;
//    private static final int REQUEST_ENABLE_BT = 1234;
//    private static boolean bluetoothDefaultIsEnable = false;
//
//    private SharedPreferences prefs;
//    private boolean isServiceBound;
//    private AbstractGatewayService service;
//    private final Runnable mQueueCommands = new Runnable() {
//        public void run() {
//            if (service != null && service.isRunning() && service.queueEmpty()) {
//                queueCommands();
//
//                double lat = 0;
//                double lon = 0;
//                double alt = 0;
//                final int posLen = 7;
//                if (mGpsIsStarted && mLastLocation != null) {
//                    lat = mLastLocation.getLatitude();
//                    lon = mLastLocation.getLongitude();
//                    alt = mLastLocation.getAltitude();
//
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("Lat: ");
//                    sb.append(String.valueOf(mLastLocation.getLatitude()).substring(0, posLen));
//                    sb.append(" Lon: ");
//                    sb.append(String.valueOf(mLastLocation.getLongitude()).substring(0, posLen));
//                    sb.append(" Alt: ");
//                    sb.append(String.valueOf(mLastLocation.getAltitude()));
//                    gpsStatusTextView.setText(sb.toString());
//                }
//                if (prefs.getBoolean(ConfigActivity.UPLOAD_DATA_KEY, false)) {
//                    // Upload the current reading by http
//                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
//                    Map<String, String> temp = new HashMap<String, String>();
//                    temp.putAll(commandResult);
//                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
//                    new UploadAsyncTask().execute(reading);
//
//                } else if (prefs.getBoolean(ConfigActivity.ENABLE_FULL_LOGGING_KEY, false)) {
//                    // Write the current reading to CSV
//                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
//                    Map<String, String> temp = new HashMap<String, String>();
//                    temp.putAll(commandResult);
//                }
//                commandResult.clear();
//            }
//            // run again in period defined in preferences
//            new Handler().postDelayed(mQueueCommands, ConfigActivity.getObdUpdatePeriod(prefs));
//        }
//    };
//
//    private boolean preRequisites = true;
////    private ServiceConnection serviceConn = new ServiceConnection() {
////        @Override
////        public void onServiceConnected(ComponentName className, IBinder binder) {
////            Log.d(TAG, className.toString() + " service is bound");
////            isServiceBound = true;
////            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
////            service.setContext(MainActivity.this);
////            Log.d(TAG, "Starting live data");
////            try {
////                service.startService();
////                if (preRequisites)
////                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
////            } catch (IOException ioe) {
////                Log.e(TAG, "Failure Starting live data");
////                btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
////                doUnbindService();
////            }
////        }
////
////        @Override
////        protected Object clone() throws CloneNotSupportedException {
////            return super.clone();
////        }
////
////        // This method is *only* called when the connection to the service is lost unexpectedly
////        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
////        // So the isServiceBound attribute should also be set to false when we unbind from the service.
////        @Override
////        public void onServiceDisconnected(ComponentName className) {
////            Log.d(TAG, className.toString() + " service is unbound");
////            isServiceBound = false;
////        }
////    };
//
//    public static String LookUpCommand(String txt) {
//        for (AvailableCommandNames item : AvailableCommandNames.values()) {
//            if (item.getValue().equals(txt)) return item.name();
//        }
//        return txt;
//    }
//
//    public void updateTextView(final TextView view, final String txt) {
//        new Handler().post(new Runnable() {
//            public void run() {
//                view.setText(txt);
//            }
//        });
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        releaseWakeLockIfHeld();
//        if (isServiceBound) {
//            doUnbindService();
//        }
//
//        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter != null && btAdapter.isEnabled() && !bluetoothDefaultIsEnable)
//            btAdapter.disable();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "Pausing..");
//        releaseWakeLockIfHeld();
//    }
//
//    /**
//     * If lock is held, release. Lock will be held when the service is running.
//     */
//    private void releaseWakeLockIfHeld() {
//        if (wakeLock.isHeld())
//            wakeLock.release();
//    }
//
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "Resuming..");
//        sensorManager.registerListener(orientListener, orientSensor,
//                SensorManager.SENSOR_DELAY_UI);
//        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
//                "ObdReader");
//
//        // get Bluetooth device
//        final BluetoothAdapter btAdapter = BluetoothAdapter
//                .getDefaultAdapter();
//
//        preRequisites = btAdapter != null && btAdapter.isEnabled();
//        if (!preRequisites && prefs.getBoolean(ConfigActivity.ENABLE_BT_KEY, false)) {
//            preRequisites = btAdapter != null && btAdapter.enable();
//        }
//
//        gpsInit();
//
//        if (!preRequisites) {
//            showDialog(BLUETOOTH_DISABLED);
//            btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
//        } else {
//            btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
//        }
//    }
//
//    private void updateConfig() {
//        startActivity(new Intent(this, ConfigActivity.class));
//    }
//
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, START_LIVE_DATA, 0, getString(R.string.menu_start_live_data));
//        menu.add(0, STOP_LIVE_DATA, 0, getString(R.string.menu_stop_live_data));
//        menu.add(0, GET_DTC, 0, getString(R.string.menu_get_dtc));
//        menu.add(0, TRIPS_LIST, 0, getString(R.string.menu_trip_list));
//        menu.add(0, SETTINGS, 0, getString(R.string.menu_settings));
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case START_LIVE_DATA:
//                startLiveData();
//                return true;
//            case STOP_LIVE_DATA:
//                stopLiveData();
//                return true;
//            case SETTINGS:
//                updateConfig();
//                return true;
//            case GET_DTC:
//                getTroubleCodes();
//                return true;
//            case TRIPS_LIST:
//                startActivity(new Intent(this, TripListActivity.class));
//                return true;
//        }
//        return false;
//    }
//
//    private void getTroubleCodes() {
//        startActivity(new Intent(this, TroubleCodesActivity.class));
//    }
//
//    private void startLiveData() {
//        Log.d(TAG, "Starting live data..");
//
//        tl.removeAllViews(); //start fresh
//        doBindService();
//
//        currentTrip = triplog.startTrip();
//        if (currentTrip == null)
//            showDialog(SAVE_TRIP_NOT_AVAILABLE);
//
//        // start command execution
//        new Handler().post(mQueueCommands);
//
//        if (prefs.getBoolean(ConfigActivity.ENABLE_GPS_KEY, false))
//            gpsStart();
//        else
//            gpsStatusTextView.setText(getString(R.string.status_gps_not_used));
//
//        // screen won't turn off until wakeLock.release()
//        wakeLock.acquire();
//
//        if (prefs.getBoolean(ConfigActivity.ENABLE_FULL_LOGGING_KEY, false)) {
//
//            // Create the CSV Logger
//            long mils = System.currentTimeMillis();
//            SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");
//
//            try {
//                myCSVWriter = new LogCSVWriter("Log" + sdf.format(new Date(mils)).toString() + ".csv",
//                        prefs.getString(ConfigActivity.DIRECTORY_FULL_LOGGING_KEY,
//                                getString(R.string.default_dirname_full_logging))
//                );
//            } catch (FileNotFoundException | RuntimeException e) {
//                Log.e(TAG, "Can't enable logging to file.", e);
//            }
//        }
//    }
//
//
//    protected Dialog onCreateDialog(int id) {
//        AlertDialog.Builder build = new AlertDialog.Builder(this);
//        switch (id) {
//            case NO_BLUETOOTH_ID:
//                build.setMessage(getString(R.string.text_no_bluetooth_id));
//                return build.create();
//            case BLUETOOTH_DISABLED:
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                return build.create();
//            case NO_ORIENTATION_SENSOR:
//                build.setMessage(getString(R.string.text_no_orientation_sensor));
//                return build.create();
//            case NO_GPS_SUPPORT:
//                build.setMessage(getString(R.string.text_no_gps_support));
//                return build.create();
//            case SAVE_TRIP_NOT_AVAILABLE:
//                build.setMessage(getString(R.string.text_save_trip_not_available));
//                return build.create();
//        }
//        return null;
//    }
//
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem startItem = menu.findItem(START_LIVE_DATA);
//        MenuItem stopItem = menu.findItem(STOP_LIVE_DATA);
//        MenuItem settingsItem = menu.findItem(SETTINGS);
//        MenuItem getDTCItem = menu.findItem(GET_DTC);
//
//        if (service != null && service.isRunning()) {
//            getDTCItem.setEnabled(false);
//            startItem.setEnabled(false);
//            stopItem.setEnabled(true);
//            settingsItem.setEnabled(false);
//        } else {
//            getDTCItem.setEnabled(true);
//            stopItem.setEnabled(false);
//            startItem.setEnabled(true);
//            settingsItem.setEnabled(true);
//        }
//
//        return true;
//    }
//
//    private void addTableRow(String id, String key, String val) {
//
//        TableRow tr = new TableRow(this);
//        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN,
//                TABLE_ROW_MARGIN);
//        tr.setLayoutParams(params);
//
//        TextView name = new TextView(this);
//        name.setGravity(Gravity.RIGHT);
//        name.setText(key + ": ");
//        TextView value = new TextView(this);
//        value.setGravity(Gravity.LEFT);
//        value.setText(val);
//        value.setTag(id);
//        tr.addView(name);
//        tr.addView(value);
//        tl.addView(tr, params);
//    }
//
//    /**
//     *
//     */
//    private void queueCommands() {
//        if (isServiceBound) {
//            for (ObdCommand Command : ObdConfig.getCommands()) {
//                if (prefs.getBoolean(Command.getName(), true))
//                    service.queueJob(new ObdCommandJob(Command));
//            }
//        }
//    }
//
//    private void doBindService() {
//        if (!isServiceBound) {
//            Log.d(TAG, "Binding OBD service..");
//            if (preRequisites) {
//                btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
//                Intent serviceIntent = new Intent(this, ObdGatewayService.class);
//                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//            } else {
//                btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
//                Intent serviceIntent = new Intent(this, MockObdGatewayService.class);
//                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//            }
//        }
//    }
//
//    private void doUnbindService() {
//        if (isServiceBound) {
//            if (service.isRunning()) {
//                service.stopService();
//                if (preRequisites)
//                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
//            }
//            Log.d(TAG, "Unbinding OBD service..");
//            unbindService(serviceConn);
//            isServiceBound = false;
//            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
//        }
//    }
//
//
//
//
//    @Override
//    public void stateUpdate(ObdCommandJob job) {
//        final String cmdName = job.getCommand().getName();
//        String cmdResult = "";
//        final String cmdID = LookUpCommand(cmdName);
//
//        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
//            cmdResult = job.getCommand().getResult();
//            if (cmdResult != null) {
//                obdStatusTextView.setText(cmdResult.toLowerCase());
//            }
//        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
////            cmdResult = getString(R.string.status_obd_no_support);
//        } else {
//            cmdResult = job.getCommand().getFormattedResult();
////            obdStatusTextView.setText(getString(R.string.status_obd_data));
//        }
//
//        if (vv.findViewWithTag(cmdID) != null) {
//            TextView existingTV = (TextView) vv.findViewWithTag(cmdID);
//            existingTV.setText(cmdResult);
//        } else addTableRow(cmdID, cmdName, cmdResult);
////        commandResult.put(cmdID, cmdResult);
////        updateTripStatistic(job, cmdID);
//    }
//
//
//    public ObdCommunicationManager(Application context) {
//        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter != null)
//            bluetoothDefaultIsEnable = btAdapter.isEnabled();
//    }
//}
