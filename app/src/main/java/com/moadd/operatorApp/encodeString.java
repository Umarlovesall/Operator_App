package com.moadd.operatorApp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.moaddi.operatorApp.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by moadd on 28-Dec-17.
 */

public class encodeString {
    public static String encodedBarcode(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b ,Base64.DEFAULT);
        return encodedImage;
    }
}
