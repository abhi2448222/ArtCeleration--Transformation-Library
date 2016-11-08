package edu.asu.msrs.artcelerationlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

public class useTransformService extends Service {
    private Handler mHandler = null;

    public useTransformService() {
    }

    public static final int addition = 1;
    public static final int sendingFileFromLibrary = 2;
    public static final int MSG_REGISTER_CLIENT = 3;
    public static final int requestFileFromService = 6;
    Messenger serviceMessenger = null;
    ParcelFileDescriptor sendFile = null;


    class messageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //Get arg1, arg2 values from library and send it back
                case MSG_REGISTER_CLIENT:
                    Log.d("registering the client", "onSrviceConneted");
                    serviceMessenger = (msg.replyTo);
                    Log.d("serviceMessengerSet", "onSrviceConneted");
                    Message message = Message.obtain(null, ArtLib.gotResultFromService, msg.arg1, msg.arg2);

                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.d("sent to Register Client", "client");


                    break;

                //Collect the File from Library
                case sendingFileFromLibrary:
                    Log.d("Service", "HellofrmServ" + msg.what);
                    Bundle bundleOfData = msg.getData();
                    Log.d("Service", "AfterHellofrmServ" + String.valueOf(msg.getData()));
                    ParcelFileDescriptor test = (ParcelFileDescriptor) bundleOfData.get("testFile");
                    Log.d("ParcelIn lIbraed", String.valueOf(test));

                    //Storing the ParcelFileDescriptor to send it back to the Library

                    sendFile = test;
                    int content;
                    try {
                        //Reading the file using FileInputStream
                        FileInputStream file = new FileInputStream(test.getFileDescriptor());
                        Log.d("file in lib", String.valueOf(file));
                        while ((content = file.read()) != -1) {
                            //Log.d("output", String.valueOf(content));
                        }
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("insid here", "service");
                    Log.d("Answer", String.valueOf(msg.arg1 * msg.arg2));

                    break;


            /*
            Once, the Parcelable file is received from Library, we are Sending the file back to the Library for now
             without performing any Image Transforms.
                     */
                case requestFileFromService:
                    Log.d("sending file", "onSrviceConneted");
                    serviceMessenger = (msg.replyTo);
                    Log.d("sending to library", "onSrviceConneted");
                    Message message2 = Message.obtain(null, ArtLib.sendingFileFromService);
                    try {
                        Log.d("sending to lib", "message");
                        Bundle bundleOfData1 = new Bundle();
                        bundleOfData1.putParcelable("RecievedFile", sendFile);
                        Log.d("insid recieving file", "settng data");
                        message2.setData(bundleOfData1);
                        serviceMessenger.send(message2);
                        Log.d("sent file to libarr", "message");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        }


    }

    ;

    final Messenger messenger = new Messenger(new messageHandler());
    //private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("insid here", "onbinder");
        return messenger.getBinder();
        // return mBinder;
    }

}
