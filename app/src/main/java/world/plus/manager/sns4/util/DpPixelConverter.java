package world.plus.manager.sns4.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
/**
 * dp를 픽셀로 변환 
 * @author user
 *
 */
public class DpPixelConverter {
    

    public static int toPixel(Context context, float value) {
        Resources r = context.getResources();
        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                r.getDisplayMetrics());
        return (int) pix;
    }
}
