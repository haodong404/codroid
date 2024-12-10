package org.codroid.interfaces.env;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageResource extends Resource {

    private Bitmap bitmap;

    public ImageResource(AddonEnv addonEnv, String path) {
        super(addonEnv, path);
        bitmap = BitmapFactory.decodeFile(toPath().toString());
    }

    public Bitmap toBitmap() {
        return this.bitmap;
    }

}
