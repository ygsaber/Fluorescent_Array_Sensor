package com.sunshine.Fluorescent_Array_Sensor;

public class Threshold {
    // Function for Otsu thresholding
    static int[] OtsuThreshold(int[] a){

        // Total number of pixels
        int total = a.length;

        // Calculate histogram
        int p = 0;
        int[] hist = new int[total];
        while (p < total) {
            int h = a[p];
            hist[h]++;
            p++;
        }

        int sum = 0;
        for (int t = 0 ; t < 256 ; t++){
            sum += t * hist[t];
        }

        int sumB = 0;
        int wB = 0;
        int wF;

        float varMax = 0.0f;
        int threshold = 0;
        int finalSumBackground = 0;
        int finalSumForeground = 0;
        int weightBackground = 0;
        int weightForeground = 0;

        for (int t=0 ; t < 256 ; t++) {
            wB += hist[t];               // Weight Background
            if (wB == 0) continue;

            wF = total - wB;                 // Weight Foreground
            if (wF == 0) break;

            sumB += (float) (t * hist[t]);

            float mB = (float) sumB / (float) wB;            // Mean Background
            float mF = (float)(sum - sumB) / (float) wF;    // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
                finalSumBackground = sumB;
                finalSumForeground = sum - sumB;
                weightBackground = wB;
                weightForeground = wF;

            }
        }
        int[] result = new int[5];
        result[0] = threshold;
        result[1] = finalSumBackground;
        result[2] = finalSumForeground;
        result[3] = weightBackground;
        result[4] = weightForeground;

        return result;
    }
}
