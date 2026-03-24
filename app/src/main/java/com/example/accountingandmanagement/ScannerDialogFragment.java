package com.example.accountingandmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.common.util.concurrent.ListenableFuture;

public class ScannerDialogFragment extends DialogFragment {

    private PreviewView previewView;
    public Button btnFlashlight;
    private ProcessCameraProvider cameraProvider;
    private androidx.camera.core.Camera camera;
    private boolean isFlashlightOn = false;
    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_scanner, container, false);

        btnFlashlight = view.findViewById(R.id.btnFlashlight);
        previewView = view.findViewById(R.id.dialogPreviewView);
        btnFlashlight.setOnClickListener(v -> {
            if (camera == null) {
                Toast.makeText(requireContext(), "Camera still loading...", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Make sure this specific camera lens actually has a flash unit
            if (!camera.getCameraInfo().hasFlashUnit()) {
                Toast.makeText(requireContext(), "No flashlight available on this camera!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Toggle the state
            isFlashlightOn = !isFlashlightOn;

            // 4. Turn it on and listen for any errors!
            camera.getCameraControl().enableTorch(isFlashlightOn)
                    .addListener(() -> {
                        // This runs if it successfully turned on/off
                        if (isFlashlightOn) {
                            btnFlashlight.setBackgroundResource(R.drawable.ic_action_flashlight_on);
                        } else {
                            btnFlashlight.setBackgroundResource(R.drawable.ic_action_flashlight_off);
                        }
                    }, ContextCompat.getMainExecutor(requireContext()));
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This is what you are missing!
        startCamera();
    }
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                // 1. The Preview (What the user sees)
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // 2. The Image Analysis (What ML Kit sees)
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // Attach our new BarcodeAnalyzer
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()),
                        new BarcodeAnalyzer(barcode -> {

                            Bundle result = new Bundle();
                            Log.i("imageAnalysis barcode: ",barcode);
                            result.putString("barcode_data", barcode);

                            getParentFragmentManager().setFragmentResult("scanner_request", result);

                            dismiss();
                        })
                );

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        selector,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    /*private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector selector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        selector,
                        preview
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}