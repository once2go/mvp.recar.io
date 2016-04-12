package io.recar.once2go.usbserial.carwrapper;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

import io.recar.once2go.usbserial.carwrapper.interfaces.MessageHandlerListener;
import io.recar.once2go.usbserial.carwrapper.services.UsbService;

public class CarMediaCommunicationManager implements MessageHandlerListener{

    private Application mContext;
    private UsbService mUsbService;
    private MessageHandler mHandler;

    public CarMediaCommunicationManager(Application context) {
        mContext = context;
        mHandler = new MessageHandler(this);
        setFilters();
        startService(UsbService.class, usbConnection, null);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "Media Center Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "Media Center not connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "Media Center disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    // run before app will be destroyed
    public void destroyCarCommunicationManager( ) {
        mContext.unregisterReceiver(mUsbReceiver);
        mContext.unbindService(usbConnection);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mUsbService = ((UsbService.UsbBinder) arg1).getService();
            mUsbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mUsbService = null;
        }
    };


    private void sendDataToUart(String data) {
        if (mUsbService != null) { // if UsbService was correctly binded, Send data
            mUsbService.write(data.getBytes());
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(mContext, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            mContext.startService(startService);
        }
        Intent bindingIntent = new Intent(mContext, service);
        mContext.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void onMessageReceived(String message) {

    }
}