package edu.asu.msrs.artcelerationlibrary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rlikamwa on 10/2/2016.
 */

public class ArtLib {
    private TransformHandler artlistener;
    private Activity setActivity;

    private Messenger activeMessenger;
    private boolean isServiceBound;
    private useTransformService mService = null;
    public static final int gotResultFromService=4;
    public static final int  sendingFileFromService=8;


    public ArtLib(Activity activity){
        setActivity=activity;
        //Bind service to the library
        bindService();
    }

    /*
    Message Handler to handle the messages from the service
     */
    class messageHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //Recieve a Response Back from the Service
                case gotResultFromService:
                    Log.d("Got answer", String.valueOf(msg.arg1));
                    Log.d("Got answer", String.valueOf(msg.arg2));
                    break;
                //Recieve  the file from the Service
                case sendingFileFromService:
                    Bundle bundleOfData= msg.getData();
                    Log.d("Service","AfterHellofrmServ"+ String.valueOf(msg.getData()));
                    ParcelFileDescriptor test= (ParcelFileDescriptor) bundleOfData.get("RecievedFile");
                    Log.d("ParcelIn client", String.valueOf(test));
                    int content;
                    try {
                        FileInputStream file= new FileInputStream(test.getFileDescriptor());
                        Log.d("file in client", String.valueOf(file));
                        while((content=file.read())!=-1){
                            Log.d("output", String.valueOf(content));
                        }
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("error", "erorro");
                    }
                    break;
                default:
                    break;
            }

        }
    }
   final Messenger messenger= new Messenger(new ArtLib.messageHandler());
     //Messenger activeMessenger= new Messenger(new ArtLib.messageHandler());


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("insid here","onSrviceConneted");
            activeMessenger=new Messenger(service);
            isServiceBound=true;

            try {
                //Send a Register Client to the Service
                Log.d("trying to send service","onSrviceConneted");
                Message msg = Message.obtain(null,
                        useTransformService.MSG_REGISTER_CLIENT,40,80);
                msg.replyTo = messenger;
                Log.d("sending","onSrviceConneted");
                activeMessenger.send(msg);
                Log.d("messagesent","onSrviceConneted");
            }
            catch (RemoteException e) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("insid here","disonnect");
            activeMessenger=null;
            isServiceBound=false;

        }
    };
    //Bind the service to the library

    public void bindService(){
    Intent intent = new Intent(setActivity,useTransformService.class);
        Log.d("insid here","calling bind servie");
    setActivity.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
}
    public String[] getTransformsArray(){
        String[] transforms = {"Gaussian Blur", "Neon edges", "Color Filter"};
        return transforms;
    }

    public TransformTest[] getTestsArray(){
        TransformTest[] transforms = new TransformTest[3];
        transforms[0]=new TransformTest(0, new int[]{1,2,3}, new float[]{0.1f, 0.2f, 0.3f});
        transforms[1]=new TransformTest(1, new int[]{11,22,33}, new float[]{0.3f, 0.2f, 0.3f});
        transforms[2]=new TransformTest(2, new int[]{51,42,33}, new float[]{0.5f, 0.6f, 0.3f});

        return transforms;
    }

    public void registerHandler(TransformHandler artlistener){
        this.artlistener=artlistener;
    }


    public boolean requestTransform(Bitmap img, int index, int[] intArgs, float[] floatArgs){
        Log.d("in reqTra","reTrans");
        if(activeMessenger!=null){
            Log.d("insid here","reTrans");
            int what=0;
            try {

                //Sending the Bitmap to the service in terms of a parcelable File descriptor

                ByteArrayOutputStream BOS = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, BOS);
                byte[] array = BOS.toByteArray();
                Log.d("inside here","stored bitmap into bytearrayoutStream");
                MemoryFile file = new MemoryFile("test",2000);
                file.writeBytes(array,0,500,1000);
                Log.d("inside here","stored bitmap into memoryfile");
                ParcelFileDescriptor testFile= MemoryFileUtil.getParcelFileDescriptor(file);
                Bundle bundleOfData= new Bundle();
                bundleOfData.putParcelable("testFile",testFile);
                Log.d("insid here","settng data");
                Message msg = Message.obtain(null, useTransformService.sendingFileFromLibrary,1,2);
                msg.setData(bundleOfData);
                Log.d("Service", String.valueOf(msg.getData()));

            try {
                Log.d("insid here","message");
                activeMessenger.send(msg);
            } catch (RemoteException e) {
                Log.d("insid here","Faliled");
                e.printStackTrace();
            }
            } catch (IOException e) {
                Log.d("insid here","why failed");
                e.printStackTrace();
            }
        }

        requestFileFromService();
        return true;


    }
    //Requesting the file Back from service after Image Transform

public void requestFileFromService(){
    try {
        Log.d("trying to send msg","onSrviceConneted");
        Message msg = Message.obtain(null,
                useTransformService.requestFileFromService);
        msg.replyTo = messenger;
        Log.d("replytoset","onSrviceConneted");
        activeMessenger.send(msg);
        Log.d("messagesent","onSrviceConneted");
    }
    catch (RemoteException e) {
        e.printStackTrace();
    }

}
}
