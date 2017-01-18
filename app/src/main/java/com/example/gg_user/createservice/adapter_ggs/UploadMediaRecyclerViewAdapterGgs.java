package com.example.gg_user.createservice.adapter_ggs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gg_user.createservice.R;
import com.example.gg_user.createservice.media_ggs.MediaGgs;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by gg-user on 1/15/17.
 */

public class UploadMediaRecyclerViewAdapterGgs extends RecyclerView.Adapter<UploadMediaRecyclerViewAdapterGgs.Media> {


    ArrayList<MediaGgs> mediaList;
    Context context;
    MediaUploadOnClickCallBack mediaUploadOnClickCallBack;

    public UploadMediaRecyclerViewAdapterGgs(ArrayList<MediaGgs> mediaList, Context context, MediaUploadOnClickCallBack mediaUploadOnClickCallBack) {
        this.mediaList = mediaList;
        this.context = context;
        this.mediaUploadOnClickCallBack = mediaUploadOnClickCallBack;
    }

    @Override
    public Media onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ggs_upload_media_layout, parent, false);
        return new Media(itemView);
    }


    @Override
    public void onBindViewHolder(Media holder, final int position) {
        final MediaGgs media = mediaList.get(position);
        final int mediaType = media.getType();

        if (mediaType != 1) {
            try {

                //checkPermission();
                Log.d("mediauri",media.getUri().toString());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), media.getUri());
                Log.d("bitmap", String.valueOf(bitmap==null));
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                // Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap("Your_Image_Path_Here");

                // bitmap.re
                // Picasso.with(context).load(bitmap).into(holder.imageView);
                holder.imageView.setImageBitmap(scaled);
                //    holder.imageView.setBackgroundResource(R.drawable.ggs_dashed_border);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.removeMedia.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaType == 1) {
                    if (mediaList.size() < 6) {
                        showMediaDialog();

                    } else {
                        Toast.makeText(context, "You are allowed to upload 5 photos max", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.removeMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(media);
            }
        });


    }

    private void removeData(MediaGgs mediaGgs) {
        int position = mediaList.indexOf(mediaGgs);
        Log.d("position", "" + position);
        mediaList.remove(position);
        notifyItemRemoved(position);

    }


    public void addData(MediaGgs mediaGgs) {
        int currentSize = mediaList.size();
        mediaList.add(mediaGgs);

        notifyItemInserted(currentSize);
    }

    private void showMediaDialog() {
        mediaUploadOnClickCallBack.onClipClicked();
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


    public static class Media extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView removeMedia;


        public Media(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            removeMedia = (ImageView) itemView.findViewById(R.id.remove_media);
        }
    }

    public interface MediaUploadOnClickCallBack {
        void onClipClicked();
    }
}
