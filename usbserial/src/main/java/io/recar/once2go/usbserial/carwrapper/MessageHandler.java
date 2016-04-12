package io.recar.once2go.usbserial.carwrapper;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import io.recar.once2go.usbserial.carwrapper.interfaces.MessageHandlerListener;
import io.recar.once2go.usbserial.carwrapper.services.UsbService;

/**
 * Created by once2go on 07.04.16.
 */
public class MessageHandler extends Handler {

    private MessageHandlerListener mMessageHandlerListener;

    public MessageHandler(@NonNull MessageHandlerListener listener) {
        mMessageHandlerListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UsbService.MESSAGE_FROM_SERIAL_PORT:
                String data = (String) msg.obj;
                if (data != null && !data.isEmpty()) {
                    mMessageHandlerListener.onMessageReceived(data);
                }
                break;
            case UsbService.CTS_CHANGE:
                break;
            case UsbService.DSR_CHANGE:
                break;
        }
    }
}

