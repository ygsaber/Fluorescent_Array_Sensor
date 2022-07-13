package com.sunshine.Fluorescent_Array_Sensor;

import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.Arrays;

public class GetDotIntensity {
    static SparseArray <float[][]> getIntensity(Bitmap bm, int row, int col){
        int width = bm.getWidth();
        int height = bm.getHeight();
        int x = (int) ((float) width / (float) col);
        int y = (int) ((float) height / (float) row);
        int z = x * y;

        int[] gray = new int[z];
        int[] result;
        float[][] meanB = new float[row][col];
        float[][] meanF = new float[row][col];
        float[][] medianB = new float[row][col];
        float[][] medianF = new float[row][col];
        float[][] totalF = new float[row][col];
        float[][] totalF_medianB = new float[row][col];
        float[][] threshold = new float[row][col];

        SparseArray <float[][]> m = new SparseArray<>();


        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                int[] rgb = new int[z];
                bm.getPixels(rgb, 0, x, x * j, y * i, x, y);

                for (int k = 0; k < z; k++){
                    gray[k] = Math.round((float) (rgb[k] >> 16 & 0x000000FF) * 0.3f +
                            (float) (rgb[k] >> 8 & 0x000000FF) * 0.59f +
                            (float) (rgb[k] & 0x000000FF) * 0.11f);
                }

                result = Threshold.OtsuThreshold(gray);

                meanB[i][j] = (float) result[1] / (float) result[3];
                meanF[i][j] = (float) result[2] / (float) result[4];
                Arrays.sort(gray);
                int[] B = Arrays.copyOfRange(gray, 0, result[3]-1);
                int[] F = Arrays.copyOfRange(gray, result[3], z);
                medianB[i][j] = findMedian(B);
                medianF[i][j] = findMedian(F);

                totalF[i][j] = (float) result[2];
                totalF_medianB[i][j] = (float) result[2] - ((float) result[4]) * medianB[i][j];
                threshold[i][j] = (float) result[0];
            }
        }
        m.append(1, meanB);
        m.append(2, meanF);
        m.append(3, medianB);
        m.append(4, medianF);
        m.append(5, totalF);
        m.append(6, totalF_medianB);
        m.append(7, threshold);

        return m;
    }

    // Function for calculating median
    private static float findMedian(int[] b){
        int n = b.length;

        // check for even case
        if (n % 2 != 0)
            return b[(n - 1)/ 2];

        return (b[n / 2 - 1] + b[n / 2]) / 2.0f;
    }

}
