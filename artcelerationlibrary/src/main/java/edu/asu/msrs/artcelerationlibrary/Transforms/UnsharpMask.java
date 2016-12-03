package edu.asu.msrs.artcelerationlibrary.Transforms;

import android.provider.Settings;
import android.util.Log;

/* This class takes in the two dimensional array containing the pixels of the input image and then
applies unsharp mask transform to the two dimensional array and returns the transformed array. The
algorithm performs the gaussian blur with first argument from the floatArgs array as standard
deviation for the gaussian transform and radius as 6 times standard deviation.For each pixel, it
finds the difference between original pixel and blurred pixel. It scales the difference with a factor
provided as second argument in the floatArgs array. We split each and every pixel into separate
color channels, apply the transform and combine the color channels to form the output pixel.
*/

public class UnsharpMask {

    public static int[][] computeUnsharpMask(int[][] inputPixelArray, float[] floatArgs) {

        int noOfRows = inputPixelArray.length;
        int noOfCols = inputPixelArray[0].length;

        Log.d("input rows",String.valueOf(noOfRows));
        Log.d("input cols",String.valueOf(noOfCols));

        float fo = floatArgs[0];
        int f1 = (int)floatArgs[1];
        int r = (int)(6*fo);

        Log.d("r value",String.valueOf(r));
        Log.d("f1 value",String.valueOf(f1));



        int[] intArgtoGaussian = {r};
        float[] floatArgtoGaussian = {fo};


        int[][] pixel_R = new int[noOfRows][noOfCols];
        int[][] pixel_G = new int[noOfRows][noOfCols];
        int[][] pixel_B = new int[noOfRows][noOfCols];
        int[][] pixel_Alpha = new int[noOfRows][noOfCols];

        int[][] Q_R = new int[noOfRows][noOfCols];
        int[][] Q_G = new int[noOfRows][noOfCols];
        int[][] Q_B = new int[noOfRows][noOfCols];

        int[][] Output_R = new int[noOfRows][noOfCols];
        int[][] Output_G = new int[noOfRows][noOfCols];
        int[][] Output_B = new int[noOfRows][noOfCols];
        int[][] Output_Alpha = new int[noOfRows][noOfCols];

        int [][] outputPixelArray=new int [noOfRows][noOfCols];


       // Performing the gaussian blur transform with standard deviation fo and radius 6*f0
        int [][] Q_Array = GaussianBlur.computeGaussianBlur(inputPixelArray, intArgtoGaussian, floatArgtoGaussian);




        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                //Splitting each pixel into separate color channels red, green, alpha and blue
                pixel_B[i][j] = inputPixelArray[i][j] & 0x000000FF;
                pixel_G[i][j] = (inputPixelArray[i][j] & 0x0000FF00) >> 8;
                pixel_R[i][j] = (inputPixelArray[i][j] & 0x00FF0000) >> 16;
                pixel_Alpha[i][j] = (inputPixelArray[i][j] & 0xFF000000) >> 24;


                //Splitting color channels from the array obtained from gaussian blur with fo and radius 6*fo
                Q_B[i][j] = Q_Array[i][j] & 0x000000FF;
                Q_G[i][j] = (Q_Array[i][j] & 0x0000FF00) >> 8;
                Q_R[i][j] = (Q_Array[i][j] & 0x00FF0000) >> 16;


                // Transformation takes place here
                Output_R[i][j] = ((pixel_R[i][j]- Q_R[i][j])*f1);
                Output_G[i][j] = ((pixel_G[i][j]- Q_G[i][j])*f1);
                Output_B[i][j] = ((pixel_B[i][j]- Q_B[i][j])*f1);

                Output_R[i][j] += pixel_R[i][j];
                Output_G[i][j] += pixel_G[i][j];
                Output_B[i][j] += pixel_B[i][j];

                //setting alpha channel to 255
                Output_Alpha[i][j] = 255;

                //Verifying each color channel value is between 0 and 255
                if (Output_R[i][j] < 0)
                    Output_R[i][j] = 0;
                if (Output_R[i][j] > 255)
                    Output_R[i][j] = 255;
                if (Output_G[i][j] < 0)
                    Output_G[i][j] = 0;
                if (Output_G[i][j] > 255)
                    Output_G[i][j] = 255;
                if (Output_B[i][j] < 0)
                    Output_B[i][j] = 0;
                if (Output_B[i][j] > 255)
                    Output_B[i][j] = 255;


                //Combining  separate color channels red, green, alpha and blue to form transformed pixel
                outputPixelArray[i][j] = ((Output_Alpha[i][j] << 24) & 0xFF000000) | ((Output_R[i][j]<< 16) & 0x00FF0000 ) | ((Output_G[i][j] << 8) & 0x0000FF00 ) |  (Output_B[i][j] & 0x000000FF) ;

            }
        }

        Log.d("in UnsharpMask","returning UnsharpMask output array");
        return outputPixelArray;

    }

}
