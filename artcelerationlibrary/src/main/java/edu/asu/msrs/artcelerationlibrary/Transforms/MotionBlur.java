package edu.asu.msrs.artcelerationlibrary.Transforms;

import android.util.Log;

/* This class takes in the two dimensional array containing the pixels of the input image and then
applies motion blur transform to the two dimensional array and returns the transformed array. The
algorithm decides to perform either the horizontal or vertical motion blur based on the first
argument of the intArgs array. If the horizontal blur is selected, then it averages the adjacent
radius number of pixels both in its left and right directions. If the vertical blur is selected,
then it averages the adjacent radius number of pixels both in its top and bottom directions.The
radius is provided as second argument in intArgs array. We split each and every pixel into separate
color channels, apply the transform and combine the color channels to form
the output pixel.
*/

public class MotionBlur {
    public static int[][] computeMotionBlur(int[][] inputPixelArray, int[] intArgs) {
        int noOfRows = inputPixelArray.length;
        int noOfCols = inputPixelArray[0].length;
        int r = intArgs[1];

        int[][] pixel_R = new int[noOfRows][noOfCols];
        int[][] pixel_G = new int[noOfRows][noOfCols];
        int[][] pixel_B = new int[noOfRows][noOfCols];
        int[][] pixel_Alpha = new int[noOfRows][noOfCols];

        int[][] Output_R = new int[noOfRows][noOfCols];
        int[][] Output_G = new int[noOfRows][noOfCols];
        int[][] Output_B = new int[noOfRows][noOfCols];
        int[][] Output_Alpha = new int[noOfRows][noOfCols];

       int [][] outputPixelArray=new int [noOfRows][noOfCols];


        //Splitting each pixel into separate color channels red, green, alpha and blue
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                pixel_B[i][j] = inputPixelArray[i][j] & 0x000000FF;
                pixel_G[i][j] = (inputPixelArray[i][j] & 0x0000FF00) >> 8;
                pixel_R[i][j] = (inputPixelArray[i][j] & 0x00FF0000) >> 16;
                pixel_Alpha[i][j] = (inputPixelArray[i][j] & 0xFF000000) >> 24;

            }
        }


        // Transformation takes place here
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {


                // if intArgs[0]==0 then horizontal motion blur is selected
                if (intArgs[0] == 0) {

                    for (int k = i - r; k < i + r+1; k++) {
                        if (k < 0 || k > noOfRows-1) {
                            continue;
                        }

                        Output_R[i][j] += pixel_R[k][j];
                        Output_G[i][j] += pixel_G[k][j];
                        Output_B[i][j] += pixel_B[k][j];

                    }

                    Output_R[i][j] = Output_R[i][j] / ((2 * r) + 1);
                    Output_G[i][j] = Output_G[i][j] / ((2 * r) + 1);
                    Output_B[i][j] = Output_B[i][j] / ((2 * r) + 1);

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

                // if intArgs[0]==1 then vertical motion blur is selected
                else if (intArgs[0] == 1)
                {
                    for (int k = j - r; k < j + r+1; k++) {

                        if (k < 0 || k>noOfCols-1) {
                            continue;
                        }

                        Output_R[i][j] += pixel_R[i][k];
                        Output_G[i][j] += pixel_G[i][k];
                        Output_B[i][j] += pixel_B[i][k];

                    }

                    Output_R[i][j] = Output_R[i][j] / ((2 * r) + 1);
                    Output_G[i][j] = Output_G[i][j] / ((2 * r) + 1);
                    Output_B[i][j] = Output_B[i][j] / ((2 * r) + 1);

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
                    outputPixelArray[i][j] = ((Output_Alpha[i][j] << 24) & 0xFF000000) | ((Output_R[i][j]<< 16) & 0x00FF0000 ) |
                                              ((Output_G[i][j] << 8) & 0x0000FF00 ) |  (Output_B[i][j] & 0x000000FF) ;

                }

            }
        }

        Log.d("in motionBlur","returning output Motion Blur array");
        return outputPixelArray;
    }
}