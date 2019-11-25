package com.example.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class contains the common utility functions of application
 */

public class ApplicationUtils {

    /**
     * This method convert uri to bitmap
     *
     * @param uri
     * @param context
     * @return
     * @throws IOException
     */
    public static Bitmap convertUriToBitmap(Uri uri, Context context) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    /**
     * This method converts bitmap to uri
     *
     * @param bitmap
     * @param context
     * @return
     */
    public static Uri convertBitmapToUri(Bitmap bitmap, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    /**
     * This method performs the password encryption.
     *
     * @param password
     * @return
     */
    public static String encrypt(String password) {
        String cipherText = "";
        for (int i = 0; i < password.length(); i++) {
            char alpha = password.charAt(i);
            int c = (char) alpha + 2;
            char ch = (char) c;
            cipherText = cipherText + ch;
        }
        return cipherText;
    }

    /**
     * This method performs the password decryption
     *
     * @param cipherText
     * @return
     */
    public static String decrypt(String cipherText) {
        String plainText = "";
        for (int i = 0; i < cipherText.length(); i++) {
            char alpha = cipherText.charAt(i);
            int c1 = (char) alpha - 2;
            char ch1 = (char) c1;
            plainText = plainText + ch1;

        }
        return plainText;
    }
}
