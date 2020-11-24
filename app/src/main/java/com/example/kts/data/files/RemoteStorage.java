package com.example.kts.data.files;

import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Collections;

public class RemoteStorage {

    private final FirebaseStorage storage;
    private StorageReference noteImageRef;

    public RemoteStorage() {
        storage = FirebaseStorage.getInstance();
    }

    public void deleteImageFile(String imageTitle) {
        String path = "Note images/" + imageTitle;
        noteImageRef = storage.getReference(path);
        noteImageRef.delete();
    }

    public File getFileImage() {
        return null;
    }

//    public void getImageBytes(String imageTitle, OnLoadDataListCallback<byte[]> callback) {
//        String path = "Note images/" + imageTitle;
//        final long ONE_MEGABYTE = 1024 * 1024;
//        noteImageRef = storage.getReference(path);
//        noteImageRef.getBytes(ONE_MEGABYTE * 2)
//                .addOnSuccessListener(bytes ->
//                        callback.onDataListLoad(Collections.singletonList(bytes)))
//                .addOnFailureListener(e -> Log.w("lol", "onFailure: " + imageTitle, e));
//    }
//
//    public void createImageFile(String imageTitle, byte[] imageByte) {
//        String path = "Note images/" + imageTitle;
//        noteImageRef = storage.getReference(path);
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType(null)
//                .build();
//        noteImageRef.putBytes(imageByte, metadata);
//    }
}
