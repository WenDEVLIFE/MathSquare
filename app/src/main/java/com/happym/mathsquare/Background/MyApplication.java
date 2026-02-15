package com.happym.mathsquare.Background;

import android.app.Application;
import android.util.Log;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        downloadInkModel();
    }

    private void downloadInkModel() {
        try {
            DigitalInkRecognitionModelIdentifier modelIdentifier =
                    DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");

            if (modelIdentifier == null) return;

            DigitalInkRecognitionModel model =
                    DigitalInkRecognitionModel.builder(modelIdentifier).build();

            RemoteModelManager modelManager = RemoteModelManager.getInstance();

            modelManager.isModelDownloaded(model)
                    .addOnSuccessListener(isDownloaded -> {
                        if (!isDownloaded) {
                            modelManager.download(
                                            model,
                                            new DownloadConditions.Builder()
                                                    .build()
                                    )
                                    .addOnSuccessListener(unused ->
                                            Log.d("MLKit", "Digital Ink model downloaded"))
                                    .addOnFailureListener(e ->
                                            Log.e("MLKit", "Model download failed", e));
                        } else {
                            Log.d("MLKit", "Model already downloaded");
                        }
                    });

        } catch (MlKitException e) {
            Log.e("MLKit", "Model init error", e);
        }
    }
}
