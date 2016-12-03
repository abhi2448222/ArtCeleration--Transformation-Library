package edu.asu.msrs.artcelerationlibrary.Transforms;

import android.util.Log;

import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/* This class takes in the two dimensional array containing the pixels of the input image and then
applies gaussian blur transform to the two dimensional array and returns the transformed array. The
algorithm calculates gaussian weight vector between [-r,r]."r" is the argument received from
intArgs array.For each pixel, it multiplies the gaussian weight vector with the adjacent "r" number
of pixels both in its left and right directions.Then it multiplies the adjacent "r" number of pixels
both in its top and bottom directions.The standard deviation is provided as argument in floatArgs
array which is used to calculate the gaussian vector. We split each and every pixel into separate
color channels, apply the transform and combine the color channels to form the output pixel.
*/

public class GaussianBlur {
    public static int[][] computeGaussianBlur(int[][] inputPixelArray, int[] intArgs, float[] floatArgs) {
        int noOfRows = inputPixelArray.length;
        int noOfCols = inputPixelArray[0].length;
        int r = intArgs[0];
        float sigma = floatArgs[0];
        double G[] = new double[2*r+1];

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

        //Splitting each pixel into separate color channels red, green, alpha and blue
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {
                pixel_B[i][j] = inputPixelArray[i][j] & 0x000000FF;
                pixel_G[i][j] = (inputPixelArray[i][j] & 0x0000FF00) >> 8;
                pixel_R[i][j] = (inputPixelArray[i][j] & 0x00FF0000) >> 16;
                pixel_Alpha[i][j] = (inputPixelArray[i][j] & 0xFF000000) >> 24;
            }
        }

        //Calling the gaussian weight function
        for (int k = -r; k < r+1 ; k++) {

            G[k+r] = ((1/sqrt(2*(22/7)*pow(sigma,2)))*(exp(-pow(k,2)/(2*pow(sigma,2)))));

        }


        //Transformation takes place here
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                //q(x,y) calculation
                for (int k = i - r; k < i + r +1  ; k++) {

                    if (k < 0 || k > noOfRows-1) {
                        continue;
                    }

                    Q_R[i][j] += G[k -(i-r)] * pixel_R[k][j];
                    Q_G[i][j] += G[k - (i-r)] * pixel_G[k][j];
                    Q_B[i][j] += G[k -(i-r)] * pixel_B[k][j];

                }
            }
        }

        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                //P(x,y) calculation
                for (int k = j-r; k < j + r+1 ; k++) {

                    if (k < 0 || k > noOfCols-1) {
                        continue;
                    }

                    Output_R[i][j] += G[k-(j-r)] * Q_R[i][k];
                    Output_G[i][j] += G[k-(j-r)] * Q_G[i][k];
                    Output_B[i][j] += G[k-(j-r)] * Q_B[i][k];


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

                //Combining  separate color channels red, green, alpha and blue to form transformed pixel
                outputPixelArray[i][j] = ((Output_Alpha[i][j] << 24) & 0xFF000000) | ((Output_R[i][j]<< 16) & 0x00FF0000 ) |
                                         ((Output_G[i][j] << 8) & 0x0000FF00 ) |  (Output_B[i][j] & 0x000000FF) ;

            }
        }

        Log.d("in GaussianBlur","returning Gaussian output array");
        return outputPixelArray;

    }
}
