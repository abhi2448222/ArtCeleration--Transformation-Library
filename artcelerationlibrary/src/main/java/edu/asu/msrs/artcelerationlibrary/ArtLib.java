package edu.asu.msrs.artcelerationlibrary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.concurrent.ConcurrentLinkedQueue;

/*
This Library is called from the Application for processing of the Image Transforms.
The Request transform method is called by the app and it processes the request and connects
with UseTransformService to compute the Image transform and receives the Transformed Image from the Service
and sets in the Transform Handler.
 */
public class ArtLib {
    ConcurrentLinkedQueue<Integer> globalQueue = new ConcurrentLinkedQueue<Integer>();
    Integer requestId=0;
    private TransformHandler artlistener;
    private Activity setActivity;

    boolean flag=false;
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

                    break;
                //Recieve  the file from the Service
                case sendingFileFromService:

                    Log.d("in Library from service", String.valueOf(msg.arg1));
                    int i=msg.arg1;

                    /*
                    If the Head of the Queue is not equal to the Request ID of the message,
                     then busy wait in a loop until the requested Transform is at the Head.
                     If it is equal, then break the loop and Remove the item from the head of the queue.
                     */

                    while(true){
                        if(globalQueue.peek()!=i){
                            Log.d("Value is notequaltohead", String.valueOf(globalQueue.peek()));
                        }
                        else{
                            flag=true;
                            break;
                        }
                    }

                    Log.d("head is same", String.valueOf(globalQueue.peek()));

                    //Removing the head element
                    globalQueue.poll();

                    Bundle bundleOfData= msg.getData();
                    Log.d("Service","AfterHellofrmServ"+ String.valueOf(msg.getData()));
                    ParcelFileDescriptor test= (ParcelFileDescriptor) bundleOfData.get("RecievedFile");
                    Log.d("ParcelIn client", String.valueOf(test));


                    //Get BitMap from ParcelFileDescriptor
                    Log.d("Decoding bitmap ","Decoding bitmap");
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(test.getFileDescriptor());
                    if(bitmap!=null){
                        Log.d("bitmap  in Library ","bitmap full");


                        //setting Bitmap in Listener
                        Log.d("bitmap st in handler ","bitmap st in handlr");

                        artlistener.onTransformProcessed(bitmap);
                    }
                    else{
                        Log.d("bitmap null","bitmap null");

                    }

                    break;
                default:
                    break;
            }

        }
    }
   final Messenger messenger= new Messenger(new ArtLib.messageHandler());



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
        Log.d("inside here","calling bind servie");
    setActivity.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
}
    //Getting the Available Filters in the App
    public String[] getTransformsArray(){
        String[] transforms = {"Gaussian Blur", "Color Filter","Motion Blur","Unsharp Mask","Sobel Edge Filter"};
        return transforms;
    }

    //Test Input Array values provided for eah transforms
    public TransformTest[] getTestsArray(){
        TransformTest[] transforms = new TransformTest[5];
        transforms[0]=new TransformTest(0, new int[]{10}, new float[]{15f});
        transforms[1]=new TransformTest(1, new int[]{32,48,60,60,85,190,195,255,0,16,32,48,80,90,200,255,0,16,64,80,86,92,98,255}, new float[]{0.5f, 0.6f, 0.3f});
        transforms[2]=new TransformTest(2, new int[]{1,5}, new float[]{0.1f, 0.2f, 0.3f});
        transforms[3]=new TransformTest(3, new int[]{5,15}, new float[]{10f,2f});
        transforms[4]=new TransformTest(4, new int[]{0}, new float[]{4f});

        return transforms;
    }

    //Registering the TransformHandler Interface to the Library
    public void registerHandler(TransformHandler artlistener){
        this.artlistener=artlistener;
    }


    public boolean requestTransform(Bitmap img, int index, int[] intArgs, float[] floatArgs){
        Log.d("in reqTra","requestTrans");
        switch(index){
            case 0:
                //If the length of the intArgs array or floatArgs array is not equal to 1 then return false
                if((intArgs.length>1) || (floatArgs.length>1))
                    return false;
                //If the argument in the intArs array and floatArgs is not positive, then return false
                if ((intArgs[0]<0) || (floatArgs[0]<0 )){
                    return false;
                }
                break;



            case 1:

                //If the length of the intArgs array is not equal to 24 then return false
                if(!(intArgs.length==24))
                    return false;

                //If the condition 0 <= ro < r1 < r2 < r3 <= 255  does not satisfy then return false
                if(!(intArgs[0] >=0 || intArgs[0] < intArgs[2] || intArgs[0] < intArgs[4] || intArgs[0] < intArgs[6] || intArgs[0] < 255 ))
                    return false;
                if(!(intArgs[2] >0 || intArgs[2] > intArgs[0] || intArgs[2] < intArgs[4] || intArgs[2] < intArgs[6] || intArgs[2] < 255 ))
                    return false;
                if(!(intArgs[4] >0 || intArgs[4] > intArgs[0] || intArgs[4] > intArgs[2] || intArgs[4] < intArgs[6] || intArgs[4] < 255 ))
                    return false;
                if(!(intArgs[6] >0 || intArgs[6] > intArgs[0] || intArgs[6] > intArgs[2] || intArgs[6] > intArgs[4] || intArgs[6] <= 255 ))
                    return false;

                //If the condition 0 <= go < g1 < g2 < g3 <= 255  does not satisfy then return false
                if(!(intArgs[8] >=0 || intArgs[8] < intArgs[10] || intArgs[8] < intArgs[12] || intArgs[8] < intArgs[14] || intArgs[8] < 255 ))
                    return false;
                if(!(intArgs[10] >0 || intArgs[10] > intArgs[8] || intArgs[10] < intArgs[12] || intArgs[10] < intArgs[14] || intArgs[10] < 255 ))
                    return false;
                if(!(intArgs[12] >0 || intArgs[12] > intArgs[8] || intArgs[4] > intArgs[10] || intArgs[12] < intArgs[14] || intArgs[12] < 255 ))
                    return false;
                if(!(intArgs[14] >0 || intArgs[14] > intArgs[8] || intArgs[14] > intArgs[10] || intArgs[14] > intArgs[12] || intArgs[14] <= 255 ))
                    return false;

                //If the condition  0 <= bo < b1 < b2 < b3 <= 255  does not satisfy then return false
                if(!(intArgs[16] >=0 || intArgs[16] < intArgs[18] || intArgs[16] < intArgs[20] || intArgs[16] < intArgs[22] || intArgs[16] < 255 ))
                    return false;
                if(!(intArgs[18] >0 || intArgs[18] > intArgs[16] || intArgs[18] < intArgs[20] || intArgs[18] < intArgs[22] || intArgs[18] < 255 ))
                    return false;
                if(!(intArgs[20] >0 || intArgs[20] > intArgs[16] || intArgs[20] > intArgs[18] || intArgs[20] < intArgs[22] || intArgs[20] < 255 ))
                    return false;
                if(!(intArgs[22] >0 || intArgs[22] > intArgs[16] || intArgs[22] > intArgs[18] || intArgs[22] > intArgs[20] || intArgs[22] <= 255 ))
                    return false;

               break;

            case 2:
                //If the Length of IntArgs array is not equal to 2, then return false
                if(!(intArgs.length==2))
                    return false;

                /*Two available options for Motion Blur :0 is Horizontal Motion Blur ; 1 is Vertical Motion Blur
                Any Other Invalid input is received, then return false
                */
                if (!(intArgs[0] == 0 || intArgs[0] == 1)){
                    return false;
                }

                // If the radius of Motion Blur is negative, then return false
                if ((intArgs[1]<= 0)){
                    return false;
                }

                break;

            case 3:
                //If the Length of FloatArgs array is not equal to 2, then return false
                if(!(floatArgs.length==2))
                    return false;
                //If the Standard Deviation and Factor for scaling Difference is negative , thn return false
                if ((floatArgs[0]<= 0) || (floatArgs[1]<= 0)){
                    return false;
                }


                break;

            case 4:
                //If the Length of IntArgs array is not equal to 1, then return false
                if(!(intArgs.length==1))
                    return false;
                //Three options are available to determine the Output, all other options Apart from 0,1,2 are invalid
                if (!((intArgs[0]== 0) || (intArgs[0]== 1) || (intArgs[0]== 2))){
                    return false;
                }

                break;

            default:
                break;

        }
        if(activeMessenger!=null){
            Log.d("inside here","requestTrans");
            int what=0;
            try {

                //Sending the Bitmap to the service in terms of a parcelable File descriptor

                ByteArrayOutputStream BOS = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, BOS);
                byte[] array = BOS.toByteArray();
                Log.d("inside here","stored bitmap into bytearrayoutStream");
                MemoryFile file = new MemoryFile("test",array.length);
                file.writeBytes(array,0,0,array.length);

                Log.d("inside here","stored bitmap into memoryfile");
                ParcelFileDescriptor testFile= MemoryFileUtil.getParcelFileDescriptor(file);



                Bundle bundleOfData= new Bundle();
                bundleOfData.putParcelable("testFile",testFile);

                //send integer array and flaot array to service in bundle
                bundleOfData.putIntArray("IntArray",intArgs);
                bundleOfData.putFloatArray("FloatArray",floatArgs);

                file.close();
                Log.d("insid here","settng data");
                //Incrementing the Request ID for Next Request
                requestId++;

                Message msg = Message.obtain(null, useTransformService.sendingFileFromLibrary,requestId,index);
                msg.setData(bundleOfData);
                Log.d("Service", String.valueOf(msg.getData()));

            try {
                //Adding the Request ID in the Global Queue and send the message through messenger to service
                globalQueue.add(requestId);
                Log.d("requestId in library", String.valueOf(requestId));
                msg.replyTo = messenger;
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

        Log.d("Sending to app","before return true");

        return true;


    }

}
