package com.ninebythree.nutriscanph.detection.tflite;

import android.content.res.AssetManager;

import java.io.IOException;

public class DetectorFactory
{
    public static TFLiteClassifier getDetector(final AssetManager assetManager, final String modelFilename) throws IOException
    {
        String labelFilename = "file:///android_asset/bento_train_n-fp16.txt";;
        boolean isQuantized = false;
        int inputSize = 416;

        return TFLiteClassifier.create(assetManager, modelFilename, labelFilename, isQuantized, inputSize);
    }

}
