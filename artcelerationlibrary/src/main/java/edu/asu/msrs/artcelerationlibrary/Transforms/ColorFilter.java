package edu.asu.msrs.artcelerationlibrary.Transforms;

import android.util.Log;


/* This class takes in the two dimensional array containing the pixels of the input image and then applies color filter transform
        to the two dimensional array and returns the transformed array. The algorithm used is piece wise linear mapping of every
        input pixel to an output pixel based on the values {ro,Ro,r1,R1,r2,R2,r3,R3,go,Go,g1,G1,g2,G2,g3,G3,bo,Bo,b1,B1,b2,B2,b3,B3}
        given in the intArgs array. We split each and every pixel into separate color channels, apply the transform and combine the
        color channels to form the output pixel.
        **/

public class ColorFilter {
    public static int[][] computeColorFilter(int[][] inputPixelArray, int[] intArgs) {
        int noOfRows = inputPixelArray.length;
        int noOfCols = inputPixelArray[0].length;

        Log.d("ColorFilter", String.valueOf(noOfRows));
        Log.d("ColorFilter", String.valueOf(noOfCols));

        int[][] pixel_R = new int[noOfRows][noOfCols];
        int[][] pixel_G = new int[noOfRows][noOfCols];
        int[][] pixel_B = new int[noOfRows][noOfCols];
        int[][] pixel_Alpha = new int[noOfRows][noOfCols];

        int[][] Output_R = new int[noOfRows][noOfCols];
        int[][] Output_G = new int[noOfRows][noOfCols];
        int[][] Output_B = new int[noOfRows][noOfCols];
        int[][] Output_Alpha = new int[noOfRows][noOfCols];

        int [][] outputPixelArray=new int [noOfRows][noOfCols];


        // Splitting the input pixels into color channels Alpha, Red, Green and Blue
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                pixel_B[i][j] = inputPixelArray[i][j] & 0x000000FF;
                pixel_G[i][j] = (inputPixelArray[i][j] & 0x0000FF00) >> 8;
                pixel_R[i][j] = (inputPixelArray[i][j] & 0x00FF0000) >> 16;
                pixel_Alpha[i][j] = (inputPixelArray[i][j] & 0xFF000000) >> 24;

            }
        }

        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                //if the input color channel is zero then output color channel is also zero
                if (intArgs[0] == 0)   {
                    Output_R[i][j] = intArgs[1];
                }

                if (intArgs[6] == 255)   {
                    Output_R[i][j] = intArgs[7];
                }


                //if the input color channel is 255 then output color channel is also 255
                if (pixel_R[i][j] == 255) {
                    Output_R[i][j] = 255;
                }

                // Output color Mapping for input green color channel values between 0 and ro
                if (pixel_R[i][j] >= 0 && pixel_R[i][j] <= intArgs[0] ) {
                    Output_R[i][j] = (intArgs[1] / intArgs[0]) * pixel_R[i][j];
                }

                // Output color Mapping for input green color channel values between ro and r1
                else if (pixel_R[i][j] > intArgs[0] && pixel_R[i][j] <= intArgs[2] ) {
                    Output_R[i][j] = (((intArgs[3]-intArgs[1]) / (intArgs[2]-intArgs[0])) * (pixel_R[i][j]-intArgs[0]))+intArgs[1];
                }

                // Output color Mapping for input green color channel values between r1 and r2
                else if (pixel_R[i][j] > intArgs[2] && pixel_R[i][j] <= intArgs[4] ) {
                    Output_R[i][j] = (((intArgs[5]-intArgs[3]) / (intArgs[4]-intArgs[2])) * (pixel_R[i][j]-intArgs[2]))+intArgs[3];
                }

                // Output color Mapping for input green color channel values between r2 and r3
                else if (pixel_R[i][j] > intArgs[4] && pixel_R[i][j] <= intArgs[6] ) {
                    Output_R[i][j] = (((intArgs[7]-intArgs[5]) / (intArgs[6]-intArgs[4])) * (pixel_R[i][j]-intArgs[4]))+intArgs[5];
                }

                // Output color Mapping for input green color channel values between r3 and 255
                else if (pixel_R[i][j] > intArgs[6] && pixel_R[i][j] <= 255 ) {
                    Output_R[i][j] = (((255-intArgs[7])/ (255-intArgs[6])) * (pixel_R[i][j]-intArgs[6]))+intArgs[7];
                }


                //if the input color channel is zero then output color channel is also zero
               /* if (pixel_G[i][j] == 0)   {
                    Output_G[i][j] = 0;
                }


                //if the input color channel is 255 then output color channel is also 255
                if (pixel_G[i][j] == 255) {
                    Output_G[i][j] = 255;
                }*/

                if (intArgs[8] == 0)   {
                    Output_R[i][j] = intArgs[9];
                }

                if (intArgs[14] == 255)   {
                    Output_R[i][j] = intArgs[15];
                }

                // Output color Mapping for input green color channel values between 0 and go
                if (pixel_G[i][j] >= 0 && pixel_G[i][j] <= intArgs[8] ) {
                    Output_G[i][j] = (intArgs[9] / intArgs[8]) * pixel_G[i][j];
                }

                // Output color Mapping for input green color channel values between go and g1
                else if (pixel_G[i][j] > intArgs[8] && pixel_G[i][j] <= intArgs[10] ) {
                    Output_G[i][j] = (((intArgs[11]-intArgs[9]) / (intArgs[10]-intArgs[8])) * (pixel_G[i][j]-intArgs[8]))+intArgs[9];
                }

                // Output color Mapping for input green color channel values between g1 and g2
                else if (pixel_G[i][j] > intArgs[10] && pixel_G[i][j] <= intArgs[12] ) {
                    Output_G[i][j] = (((intArgs[13]-intArgs[11]) / (intArgs[12]-intArgs[10])) * (pixel_G[i][j]-intArgs[10]))+intArgs[11];
                }

                // Output color Mapping for input green color channel values between g2 and g3
                else if (pixel_G[i][j] > intArgs[12] && pixel_G[i][j] <= intArgs[14] ) {
                    Output_G[i][j] = (((intArgs[15]-intArgs[13]) / (intArgs[14]-intArgs[12])) * (pixel_G[i][j]-intArgs[12]))+intArgs[13];
                }

                // Output color Mapping for input green color channel values between g3 and 255
                else if (pixel_G[i][j] > intArgs[14] && pixel_G[i][j] <= 255 ) {
                    Output_G[i][j] = (((255-intArgs[15])/ (255-intArgs[14])) * (pixel_G[i][j]-intArgs[14]))+intArgs[15];
                }



                //if the input color channel is zero then output color channel is also zero
               /* if (pixel_B[i][j] == 0)   {
                    Output_B[i][j] = 0;
                }

                //if the input color channel is 255 then output color channel is also 255
                if (pixel_B[i][j] == 255) {
                    Output_B[i][j] = 255;
                }*/

                if (intArgs[16] == 0)   {
                    Output_R[i][j] = intArgs[17];
                }

                if (intArgs[22] == 255)   {
                    Output_R[i][j] = intArgs[23];
                }

                // Output color Mapping for input green color channel values between 0 and bo
                if (pixel_B[i][j] >= 0 && pixel_B[i][j] <= intArgs[16] ) {
                    Output_B[i][j] = (intArgs[17] / intArgs[16]) * pixel_B[i][j];
                }

                // Output color Mapping for input green color channel values between bo and b1
                else if (pixel_B[i][j] > intArgs[16] && pixel_B[i][j] <= intArgs[18] ) {
                    Output_B[i][j] = (((intArgs[19]-intArgs[17]) / (intArgs[18]-intArgs[16])) * (pixel_B[i][j]-intArgs[16]))+intArgs[17];
                }

                // Output color Mapping for input green color channel values between b1 and b2
                else if (pixel_B[i][j] > intArgs[18] && pixel_B[i][j] <= intArgs[20] ) {
                    Output_B[i][j] = (((intArgs[21]-intArgs[19]) / (intArgs[20]-intArgs[18])) * (pixel_B[i][j]-intArgs[18]))+intArgs[19];
                }

                // Output color Mapping for input green color channel values between b2 and b3
                else if (pixel_B[i][j] > intArgs[20] && pixel_B[i][j] <= intArgs[22] ) {
                    Output_B[i][j] = (((intArgs[23]-intArgs[21]) / (intArgs[22]-intArgs[20])) * (pixel_B[i][j]-intArgs[20]))+intArgs[21];
                }

                // Output color Mapping for input green color channel values between b3 and 255
                else if (pixel_B[i][j] > intArgs[22] && pixel_B[i][j] <= 255 ) {
                    Output_B[i][j] = (((255-intArgs[23])/ (255-intArgs[22])) * (pixel_B[i][j]-intArgs[22]))+intArgs[23];
                }

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

                // Combining the transformed color channels Alpha, Red, Green and Blue into output pixel values
                outputPixelArray[i][j] = ((Output_Alpha[i][j] << 24) & 0xFF000000) | ((Output_R[i][j]<< 16) & 0x00FF0000 ) | ((Output_G[i][j] << 8) & 0x0000FF00 ) |  (Output_B[i][j] & 0x000000FF) ;

            }
        }

        Log.d("in ColorFilter","returning outputColor array");
        return outputPixelArray;
    }

}
