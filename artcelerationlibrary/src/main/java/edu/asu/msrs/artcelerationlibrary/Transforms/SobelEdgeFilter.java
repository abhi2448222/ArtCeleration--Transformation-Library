package edu.asu.msrs.artcelerationlibrary.Transforms;

import android.util.Log;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/* This class takes in the two dimensional array containing the pixels of the input image and then
applies sobel edge filter transform to the two dimensional array and returns the transformed array.
For each pixel,the algorithm multiplies the adjacent nearest adjacent elements in all directions with
the coefficients provided in the Sx and Sy matrices.We split each and every pixel into separate
color channels, apply the transform and combine the color channels to form the output pixel.
*/

public class SobelEdgeFilter {

    public static int[][] computeSobelEdgeFilter(int[][] inputPixelArray, int[] intArgs) {

        int noOfRows = inputPixelArray.length;
        int noOfCols = inputPixelArray[0].length;

        int[][] Sx ={{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] Sy ={{-1,2,1},{0,0,0},{1,2,1}};

        int [][] outputPixelArray=new int [noOfRows][noOfCols];

        int [][] Q_Array = new int [noOfRows][noOfCols];
        int [][] Grx = new int [noOfRows][noOfCols];
        int [][] Gry = new int [noOfRows][noOfCols];
        int [][] Gr = new int [noOfRows][noOfCols];

        int[][] pixel_R = new int[noOfRows][noOfCols];
        int[][] pixel_G = new int[noOfRows][noOfCols];
        int[][] pixel_B = new int[noOfRows][noOfCols];
        int[][] pixel_Alpha = new int[noOfRows][noOfCols];

        int[][] Output_R = new int[noOfRows][noOfCols];
        int[][] Output_G = new int[noOfRows][noOfCols];
        int[][] Output_B = new int[noOfRows][noOfCols];
        int[][] Output_Alpha = new int[noOfRows][noOfCols];

        //Splitting each pixel into separate color channels red, green, alpha and blue
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {

                pixel_B[i][j] = inputPixelArray[i][j] & 0x000000FF;
                pixel_G[i][j] = (inputPixelArray[i][j] & 0x0000FF00) >> 8;
                pixel_R[i][j] = (inputPixelArray[i][j] & 0x00FF0000) >> 16;
                pixel_Alpha[i][j] = (inputPixelArray[i][j] & 0xFF000000) >> 24;

                Q_Array[i][j] = (int)(0.2989*pixel_R[i][j]+0.5870*pixel_G[i][j]+0.1140*pixel_B[i][j]);

            }
        }


        // Transformation takes place here
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfCols; j++) {


                for (int k = i - 1; k < i + 2  ; k++) {

                    for (int l = j - 1; l < j + 2; l++) {
                        if (k < 0 || k > noOfRows - 1|| l < 0 || l > noOfCols - 1) {
                            continue;
                        }

                        Grx[i][j] += (Sx[k-(i-1)][l-(j-1)]*Q_Array[k][l]) ;
                        Gry[i][j] += (Sy[k-(i-1)][l-(j-1)]*Q_Array[k][l]) ;

                    }
                }


                Gr[i][j] = (int) (sqrt(pow(Grx[i][j], 2) + pow(Gry[i][j], 2)));


                //if argument in intArgs array is 0, all the color channels will have Grx
                if(intArgs[0]==0){
                    Output_R[i][j] = Grx[i][j];
                    Output_G[i][j] = Grx[i][j];
                    Output_B[i][j] = Grx[i][j];
                }

                //if argument in intArgs array is 1, all the color channels will have Gry
                if(intArgs[0]==1){
                    Output_R[i][j] = Gry[i][j];
                    Output_G[i][j] = Gry[i][j];
                    Output_B[i][j] = Gry[i][j];
                }

                //if argument in intArgs array is 2, all the color channels will have Gr
                if(intArgs[0]==2){
                    Output_R[i][j] = Gr[i][j];
                    Output_G[i][j] = Gr[i][j];
                    Output_B[i][j] = Gr[i][j];
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

        Log.d("in Sobel Edge","returning Sobel Edge output array");
        return outputPixelArray;
    }
}
