package com.example.gg_user.createservice;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gg_user.createservice.adapter_ggs.UploadMediaRecyclerViewAdapterGgs;
import com.example.gg_user.createservice.media_ggs.MediaGgs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceDetailsGgs extends Fragment implements UploadMediaRecyclerViewAdapterGgs.MediaUploadOnClickCallBack {

    private static final int CAPTURE_IMAGE = 1;
    private static final int FROM_GALLERY = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };
    RecyclerView uploadMediaRecyclerView;

    ArrayList<MediaGgs> mediaList;
    UploadMediaRecyclerViewAdapterGgs adapter;


    TextView rateTv;


    public ServiceDetailsGgs() {
        // Required empty public constructor
    }
    Uri photoURI;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ggs_fragment_service_details, container, false);
        initializeMediaList();
        initializeView(view);
        return view;
    }

    private void initializeMediaList() {
        mediaList = new ArrayList<>();
        MediaGgs mediaGgs = new MediaGgs(1);
        mediaList.add(mediaGgs);


    }

    private void initializeView(View view) {
        uploadMediaRecyclerView = (RecyclerView) view.findViewById(R.id.upload_media_recycler_view);
        adapter = new UploadMediaRecyclerViewAdapterGgs(mediaList, getContext(), this);

        rateTv = (TextView) view.findViewById(R.id.rate_value);

        rateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        uploadMediaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        uploadMediaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        uploadMediaRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClipClicked() {
        showGalleryDialogue();
    }

    private void showGalleryDialogue() {
        final Dialog dialog = new Dialog(getContext());
        //  View view = inflater
        dialog.setContentView(R.layout.ggs_dialogue_layout_view);

        TextView camera = (TextView) dialog.findViewById(R.id.camera);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCameraPermissions();
                dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions();
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("currentphptopathj",mCurrentPhotoPath);
        return image;
    }

    private void showCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                if(!photoFile.exists()) photoFile.mkdirs();
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.gg_user.createservice.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }

        }

    }

    private void showGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    Log.d("uri", String.valueOf(photoURI == null));
                    if (photoURI != null) {
                        MediaGgs mediaGgs = new MediaGgs(photoURI, 2);
                        adapter.addData(mediaGgs);
                        uploadMediaRecyclerView.scrollToPosition(mediaList.size() - 1);

                    }


                }

                break;
            case FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Log.d("data", data.getDataString());
                    MediaGgs mediaGgs = new MediaGgs(selectedImage, 2);
                    adapter.addData(mediaGgs);
                    uploadMediaRecyclerView.scrollToPosition(mediaList.size() - 1);

                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            showGallery();
        }

    }

    public void verifyCameraPermissions() {
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA
            );
        } else {
            showCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permitted","gallery");
                    showGallery();

                } else {
                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permitted","camera");
                    showCamera();

                } else {
                    Toast.makeText(getContext(), "Permission denied to capture image", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
