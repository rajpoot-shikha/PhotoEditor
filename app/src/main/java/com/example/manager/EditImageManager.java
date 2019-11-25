package com.example.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;

import com.example.utility.ApplicationUtils;
import com.fenchtose.nocropper.CropperView;

import java.io.IOException;

/**
 * This class manages the editing of image (Rotate, Crop, Grayscale)
 */
public class EditImageManager {

    private static EditImageManager edit = null;

    private EditImageManager() {
        // hidden constructor
    }

    public static EditImageManager getInstance() {
        if (edit == null) {
            edit = new EditImageManager();
        }
        return edit;
    }

    public Uri cropImage(Uri uri, Context context, CropperView cropperView) throws IOException {

        Bitmap mBitmap = cropperView.getCroppedBitmap();
        if (mBitmap != null) {
            uri = ApplicationUtils.convertBitmapToUri(mBitmap, context);
        }
        return uri;
    }

    public Uri grayScaleImage(Uri uri, Context context) throws IOException {
        Bitmap bitmap = ApplicationUtils.convertUriToBitmap(uri, context);
        Bitmap newBitmap = convertImage(bitmap);
        return ApplicationUtils.convertBitmapToUri(newBitmap, context);
    }

    public Bitmap convertImage(Bitmap bmpOriginal) {

        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Uri rotateImage(Uri uri, Context context) throws IOException {
        Bitmap bitmap = ApplicationUtils.convertUriToBitmap(uri, context);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return ApplicationUtils.convertBitmapToUri(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true), context);
    }
}

