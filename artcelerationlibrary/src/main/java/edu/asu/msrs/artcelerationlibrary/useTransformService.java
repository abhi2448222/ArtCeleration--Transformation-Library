package edu.asu.msrs.artcelerationlibrary;

/*
This Service Sends the Input Pixels to the Different Transforms classes depending on the Transform requested,
Appropriate class will be called. We have computed 2 Transforms i.e Motion Blur and Colour filter in NDk and have
 computed all 5 transforms in Java as well. The Different filters are implemented in different classes in Transforms
 Package.
 */
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import edu.asu.msrs.artcelerationlibrary.Transforms.ColorFilter;
import edu.asu.msrs.artcelerationlibrary.Transforms.GaussianBlur;
import edu.asu.msrs.artcelerationlibrary.Transforms.MotionBlur;
import edu.asu.msrs.artcelerationlibrary.Transforms.SobelEdgeFilter;
import edu.asu.msrs.artcelerationlibrary.Transforms.UnsharpMask;

public class useTransformService extends Service {
    private Handler mHandler = null;

    public useTransformService() {
    }

    public static final int addition = 1;
    int requestId;
    Bitmap outImg=null;
    public static final int sendingFileFromLibrary = 2;
    public static final int MSG_REGISTER_CLIENT = 3;
    public static final int requestFileFromService = 6;
    Messenger serviceMessenger = null;
    ParcelFileDescriptor sendFile = null;

    static {
        System.loadLibrary("image-transforms");
    }


    class messageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //Get arg1, arg2 values from library and send it back
                //Registering the Service with the client
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
                    /*
                   And, Once, the Parcelable file is received from Library, we are Sending the file back to the Library for now
                     without performing any Image Transforms.
                     */
                case sendingFileFromLibrary:
                    Log.d("Service", "HellofrmServ" + msg.what);
                    Bundle bundleOfData = msg.getData();
                    Log.d("Service", "AfterHellofrmServ" + String.valueOf(msg.getData()));
                   ParcelFileDescriptor test = (ParcelFileDescriptor) bundleOfData.get("testFile");

                    Log.d("ParcelIn library", String.valueOf(test));

                    //Read integer array and flaot aarray to service in bundle
                    int intArgs[]=bundleOfData.getIntArray("IntArray");
                    float floatArgs[]=bundleOfData.getFloatArray("FloatArray");



                    //converting back to bitmap
                    Log.d("Decoding bitmap ","Decoding bitmap");
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(test.getFileDescriptor());
                    if(bitmap!=null)Log.d("bitmap  in service ","bitmap full");
                    else{
                        Log.d("bitmap null","bitmap null");
                    }


                    int width= bitmap.getWidth();
                    int height= bitmap.getHeight();
                    int[][] inputPixelValues=new int[width][height];
                    int[][] outPixelValues=new int[width][height];

                    int index=msg.arg2;
                    Log.d("bitmap decoded ","Decoded bitmap");
                    Log.d("getting all pixels","getting all pixels");
                    for(int j=0;j<height;j++){
                        for(int i=0;i<width;i++){

                           inputPixelValues[i][j]=bitmap.getPixel(i,j);
                       }
                   }
                    Log.d("pixels got","got pixels");

                    //Call native code implementation for colorfilter Transform
                    if(index == 1) {
                        colorfilter(bitmap, intArgs);
                        outImg=bitmap;
                    }

                    /*call native code implementation for MotionBlur Transform
                     else if(index == 2) {
                       motionblur(bitmap, intArgs);
                        outImg=bitmap;
                    }*/
                    else{
                        //Calling the Java Implementation for all other filter Transforms
                        outPixelValues = callTransforms(index, inputPixelValues, intArgs, floatArgs, width, height);
                        Log.d("func Call transforms", "function Call transforms");


                        //converting pixels to bitmap
                        int outWidth = outPixelValues.length;
                        int outHeight = outPixelValues[0].length;
                        int[] pixels = new int[width * height];
                        int pixelsIndex = 0;
                        for (int j = 0; j < outHeight; j++)
                        {
                            for (int i = 0; i < outWidth; i++)
                            {
                                pixels[pixelsIndex] = outPixelValues[i][j];
                                pixelsIndex ++;
                            }
                        }
                         outImg=Bitmap.createBitmap(pixels, outWidth, outHeight, Bitmap.Config.ARGB_8888);
                    }





                    //create  ParcelFileDescriptor from BitMap
                    ByteArrayOutputStream BOS = new ByteArrayOutputStream();
                    outImg.compress(Bitmap.CompressFormat.PNG, 100, BOS);
                    byte[] array = BOS.toByteArray();
                    Log.d("inside here","stored bitmap into bytearrayoutStream");

                    try {
                        MemoryFile file  = new MemoryFile("test",array.length);
                        file.writeBytes(array,0,0,array.length);

                        Log.d("inside here","stored bitmap into memoryfile");
                        ParcelFileDescriptor outputFile= MemoryFileUtil.getParcelFileDescriptor(file);

                        //Storing the ParcelFileDescriptor to send it back to the Library
                        sendFile=outputFile;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Get the request ID of the Message and append it back to the Message
                    requestId=msg.arg1;
                    Log.d("reqIdin service", String.valueOf(requestId));


                    //Now sending the file back to the Library
                    Log.d("sending file", "onSrviceConneted");
                    serviceMessenger = (msg.replyTo);
                    Log.d("sending to library", "onSrviceConneted");
                    Message message3 = Message.obtain(null, ArtLib.sendingFileFromService,requestId,-1);
                    try {
                        Log.d("sending to lib", "message");
                        Bundle bundleOfData1 = new Bundle();
                        bundleOfData1.putParcelable("RecievedFile", sendFile);
                        Log.d("insid recieving file", "settng data");
                        message3.setData(bundleOfData1);
                        serviceMessenger.send(message3);
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


    //Native code Declaration
    public native void colorfilter(Bitmap bitmap, int[] intArgs);
    public native void motionblur(Bitmap bitmap, int[] intArgs);

    final Messenger messenger = new Messenger(new messageHandler());


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("insid here", "onbinder");
        return messenger.getBinder();
    }


    //Method to Compute the Transforms and Send the Output Pixel Array
    public static int[][] callTransforms(int index,int[][] InputpixelValues,int[] intArgs, float[] floatArgs,int width, int height){

        Log.d("calling Transform func", "calling Transform func");
        int[][] outputpixelValues=new int [width][height];

        switch(index) {

            case 0:
                Log.d("calling GaussianBlur", " Transform ");
                outputpixelValues= GaussianBlur.computeGaussianBlur(InputpixelValues,intArgs,floatArgs);

                Log.d("got output", "got output");

                break;


            case 1:
                Log.d("calling ColorFilter", " Transform ");
                outputpixelValues = ColorFilter.computeColorFilter(InputpixelValues, intArgs);
                Log.d("got output", "color filter");
                break;


            case 2 :
                Log.d("calling Motion Blur", " Transform ");
                outputpixelValues=MotionBlur.computeMotionBlur(InputpixelValues,intArgs);

                Log.d("got output", "Motion blur");
                break;

            case 3 :

                Log.d("calling Unsharp Mask", " Transform ");
                outputpixelValues= UnsharpMask.computeUnsharpMask(InputpixelValues,floatArgs);

                Log.d("got output", "Unsharp Mask");

                break;

            case 4 :

                Log.d("calling Sobel Edge", " Transform ");
                outputpixelValues= SobelEdgeFilter.computeSobelEdgeFilter(InputpixelValues,intArgs);

                Log.d("got output", "Sobel Edge");

                break;

            default :
                Log.d("Transform", "not in Application");

                break;
        }
        return outputpixelValues;
    }

}
