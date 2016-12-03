/* This file serves as the library for native transform functions that are being accessed from the
 useTransformService.java. It consists of two the transform functions which does the  transform and
 JNIEXPORT void JNICALL linking function for each and every native method mentioned in the
 useTransformService.java. Here we have implemented the color filter transform using native function
 */


#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#define  LOG_TAG    "image-transforms"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

static void colorfilter(AndroidBitmapInfo *ptr, void *pVoid, jint *pInt);
static void motionblur(AndroidBitmapInfo *ptr, void *pVoid, jint *pInt);

//color filter transform function implementation
static void colorfilter(AndroidBitmapInfo *info, void *pixels, jint *pInt) {

    int i, j, red, green, blue, alpha;
    uint32_t* row;

    for(j = 0; j < info->height; j++){
        row = (uint32_t*)pixels;
        for(i =0; i < info->width; i++){

            //Extracting the alpha red green blue values from the pixel
            red = (int) ((row[i] & 0x00FF0000) >> 16);
            green = (int)((row[i] & 0x0000FF00) >> 8);
            blue = (int) (row[i] & 0x000000FF);

            //if the ro value is equal to zero then output is equal to Ro
            if (*(pInt) == 0)   {
                red = *(pInt+1);
            }

            //if the r3 value is equal to 255 then output is equal to Ro
            if (*(pInt+6) == 255)   {
                red = *(pInt+7);
            }

            // Output color Mapping for input red color channel values between 0 and ro
            if (red >= 0 && red <= *pInt ) {
                red = (*(pInt+1) / *(pInt)) * red;
            }

                // Output color Mapping for input red color channel values between ro and r1
            else if (red > *(pInt) && red <= *(pInt+2) ) {
                red = (((*(pInt+3)-*(pInt+1)) / ((*(pInt+2)-*(pInt)))) * (red-*(pInt)))+*(pInt+1);
            }

                // Output color Mapping for input red color channel values between r1 and r2
            else if (red > *(pInt+2) && red <= *(pInt+4) ) {
                red = (((*(pInt+5)-*(pInt+3)) / ((*(pInt+4)-*(pInt+2)))) * (red-*(pInt+2)))+*(pInt+3);
            }

                // Output color Mapping for input red color channel values between r2 and r3
            else if (red > *(pInt+4) && red <= *(pInt+6) ) {
                red = (((*(pInt+7)-*(pInt+5)) / ((*(pInt+6)-*(pInt+4)))) * (red-*(pInt+4)))+*(pInt+5);
            }

                // Output color Mapping for input red color channel values between r3 and 255
            else if (red > *(pInt+6) && red <= 255 ) {
                red = (((255-*(pInt+7))/ (255-*(pInt+6))) * (red-*(pInt+6)))+*(pInt+7);
            }


            //if the go value is equal to zero then output is equal to Go
            if (*(pInt+8) == 0)   {
                green = *(pInt+9);
            }

            ///if the g3 value is equal to zero then output is equal to G3
            if (*(pInt+14) == 255)   {
                green = *(pInt+15);
            }

            // Output color Mapping for input green color channel values between  0 and go
            if (green >= 0 && green <= *(pInt+8) ) {
                green = (*(pInt+9) / *(pInt)) * green;
            }

                // Output color Mapping for input green color channel values between go and g1
            else if (green > *(pInt+8) && green <= *(pInt+10) ) {
                green = (((*(pInt+11)-*(pInt+9)) / ((*(pInt+10)-*(pInt+8)))) * (green-*(pInt+8)))+*(pInt+9);
            }

                // Output color Mapping for input green color channel values between g1 and g2
            else if (green > *(pInt+10) && green <= *(pInt+12) ) {
                green = (((*(pInt+13)-*(pInt+11)) / ((*(pInt+12)-*(pInt+10)))) * (green-*(pInt+10)))+*(pInt+11);
            }

                // Output color Mapping for input green color channel values between g2 and g3
            else if (green > *(pInt+12) && green <= *(pInt+14) ) {
                green = (((*(pInt+15)-*(pInt+13)) / ((*(pInt+14)-*(pInt+12)))) * (green-*(pInt+12)))+*(pInt+13);
            }

                // Output color Mapping for input green color channel values between g3 and 255
            else if (green > *(pInt+14) && green <= 255 ) {
                green = (((255-*(pInt+15))/ (255-*(pInt+14))) * (green-*(pInt+14)))+*(pInt+15);
            }


            //if the bo value is equal to zero then output is equal to Bo
            if (*(pInt+16) == 0)   {
                blue = *(pInt+17);
            }

            //if the b3 value is equal to zero then output is equal to B3
            if (*(pInt+22) == 255)   {
                blue = *(pInt+23);
            }


            // Output color Mapping for input blue color channel values between 0 and bo
            if (blue >= 0 && blue <= *(pInt+16) ) {
                blue = (*(pInt+17) / *(pInt+16)) * blue;
            }

                // Output color Mapping for input blue color channel values between bo and b1
            else if (blue > *(pInt+16) && blue <= *(pInt+18) ) {
                blue = (((*(pInt+19)-*(pInt+17)) / ((*(pInt+18)-*(pInt+16)))) * (blue-*(pInt+16)))+*(pInt+17);
            }

                // Output color Mapping for input blue color channel values between b1 and b2
            else if (blue > *(pInt+18) && blue <= *(pInt+20) ) {
                blue = (((*(pInt+21)-*(pInt+19)) / ((*(pInt+20)-*(pInt+18)))) * (blue-*(pInt+18)))+*(pInt+19);
            }

                // Output color Mapping for input blue color channel values between b2 and b3
            else if (blue > *(pInt+20) && blue <= *(pInt+22) ) {
                blue = (((*(pInt+23)-*(pInt+21)) / ((*(pInt+22)-*(pInt+20)))) * (blue-*(pInt+20)))+*(pInt+21);
            }

                // Output color Mapping for input blue color channel values between b3 and 255
            else if (blue > *(pInt+22) && blue <= 255 ) {
                blue = (((255-*(pInt+23))/ (255-*(pInt+22))) * (blue-*(pInt+22)))+*(pInt+23);
            }

            //setting alpha color channel to 255 in all the pixels
            alpha = 255;


            //Setting output red color channel to zero if red
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;


            // setting the transformed pixel back in its place
            row[i] = ((alpha << 24) & 0xFF000000) |
                    ((red << 16) & 0x00FF0000) |
                    ((green << 8) & 0x0000FF00) |
                    (blue & 0x000000FF);
        }

        //shifting the pixel array to next row
        pixels = (char*)pixels + info->stride;
    }

}

//motion blur transform function implementation
static void motionblur(AndroidBitmapInfo *info, void *pixels, jint *pInt) {

    LOGI("Inside Motion Filer");
    int alpha, red, green, blue, r;

    uint32_t *row;

    r = *(pInt + 1);

    LOGI("native code");


    //if first argument in intArgs array is zero then horizontal motion blur
    if (*(pInt) == 0) {

        for (int j = 0; j < info->height; j++) {
            row = (uint32_t *) pixels;

            for (int i = 0; i < info->width; i++) {

                //Splitting each pixel into separate color channels alpha red green blue
                red = (int) ((row[i] & 0x00FF0000) >> 16);
                green = (int) ((row[i] & 0x0000FF00) >> 8);
                blue = (int) (row[i] & 0x000000FF);
            }
        }

        for (int j = 0; j < info->height; j++) {
            row = (uint32_t *) pixels;

            for (int i = 0; i < info->width; i++) {
                for (int k = i - r; k < i + r+1 ; k++) {

                    if (k < 0 || k > (info->width) - 1) {
                        continue;
                    }

                    red += row[k];
                    green += row[k];
                    blue += row[k];

                }

                red = red / ((2 * r) + 1);
                green = green / ((2 * r) + 1);
                blue = blue / ((2 * r) + 1);
                alpha = 255;

                // setting the new pixel back in its place
                row[i] = ((alpha << 24) & 0xFF000000) |
                         ((red << 16) & 0x00FF0000) |
                         ((green << 8) & 0x0000FF00) |
                         (blue & 0x000000FF);
                LOGI("Row val = %d, i val=%d ",row[i],i);

            }

            pixels = (char *) pixels + info->stride;

        }
    }

    //if first argument in intArgs array is zero then vertical motion blur
    if (*(pInt) == 1) {

        for (int j = 0; j < info->width; j++) {
            row = (uint32_t *) pixels;
            for (int i = 0; i < info->height; i++) {

                red = (int) ((row[i] & 0x00FF0000) >> 16);
                green = (int) ((row[i] & 0x0000FF00) >> 8);
                blue = (int) (row[i] & 0x000000FF);


                //transformation takes place here
                for (int k = i - r; k < i + r+1 ; k++) {
                    if (k < 0 || k > (info->height) - 1) {
                        continue;
                    }
                    red += row[k];
                    green += row[k];
                    blue += row[k];
                }

                red = red / ((2 * r) + 1);
                green = green / ((2 * r) + 1);
                blue = blue / ((2 * r) + 1);
                alpha = 255;

                // setting the transformed pixel back in its place
                row[i] = ((alpha << 24) & 0xFF000000) |
                         ((red << 16) & 0x00FF0000) |
                         ((green << 8) & 0x0000FF00) |
                         (blue & 0x000000FF);

            }

            pixels = (char *) pixels + info->stride;

        }
    }
}

/* This section contains the linking functions that link when the respective image transform functions are
    called from useTransformService in native mode
 */
extern "C"
{
JNIEXPORT void JNICALL
Java_edu_asu_msrs_artcelerationlibrary_useTransformService_colorfilter(JNIEnv *env,
                                                                       jobject instance,
                                                                       jobject bitmap,
                                                                       jintArray intArgs_) {
    AndroidBitmapInfo info;
    int ret;
    void *pixels;
    jint *intArgs = env->GetIntArrayElements(intArgs_, NULL);

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    //calling color filter transform function
    colorfilter(&info, pixels, intArgs);

    AndroidBitmap_unlockPixels(env, bitmap);

    env->ReleaseIntArrayElements(intArgs_, intArgs, 0);

}


//This function is called from the useTransformService when a native function is called
JNIEXPORT void JNICALL
Java_edu_asu_msrs_artcelerationlibrary_useTransformService_motionblur(JNIEnv *env, jobject instance,
                                                                      jobject bitmap,
                                                                      jintArray intArgs_) {
    AndroidBitmapInfo info;
    int ret;
    void *pixels;
    jint *intArgs = env->GetIntArrayElements(intArgs_, NULL);

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    //callin motion blur transform function
    motionblur(&info, pixels, intArgs);


    AndroidBitmap_unlockPixels(env, bitmap);

    env->ReleaseIntArrayElements(intArgs_, intArgs, 0);
}

}