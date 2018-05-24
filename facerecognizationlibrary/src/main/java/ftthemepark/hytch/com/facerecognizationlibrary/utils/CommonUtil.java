package ftthemepark.hytch.com.facerecognizationlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.tzutalin.dlib.VisionDetRet;

import java.io.ByteArrayOutputStream;

public class CommonUtil {


  public static byte[] resultBitmap(VisionDetRet faceBean, Bitmap bitmap) {
    int left = faceBean.getLeft();
    int top = faceBean.getTop();
    int right = faceBean.getRight();
    int bottom = faceBean.getBottom();

    Bitmap curBitmap = cutBitmap(bitmap, left, right, top, bottom);
    Bitmap mirrorBitmap = mirrorImg(curBitmap);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mirrorBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
    mirrorBitmap.recycle();
    curBitmap.recycle();
    return os.toByteArray();
  }

  public static Bitmap cutBitmap(Bitmap bitmap, int left, int right, int top, int bottom) {
    if (bitmap == null) {

      return null;
    }
    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();
    // 定义矩阵对象
    int widthFace = Math.abs(right - left);
    int heightFace = Math.abs(bottom - top);
    Matrix matrix = new Matrix();
    // 缩放图像
    matrix.postScale(1.5f, 1.5f);

    int x;//截图X起点
    int xx;//截图X起点
    int y;//截图Y起点
    int yy;//截图Y起点
    if (left < widthFace * 0.2) {
      x = 0;
      xx = left * 2;
    } else {
      x = (int) (left - (widthFace * 0.2));
      xx = (int) (widthFace * 0.4);
    }

    if (top < heightFace * 0.33) {
      y = 0;
      yy = top * 2;
    } else {
      y = (int) (top - (heightFace * 0.33));
      yy = (int) (heightFace * 0.66);
    }

    int width;
    int height;

    width = Math.abs(right - left) + xx;
    height = Math.abs(bottom - top) + yy;

    if ((x + width) > bitmapWidth) {
      width = bitmapWidth - x;
    }

    if ((y + height) > bitmapHeight) {
      height = bitmapHeight - y;
    }

    return Bitmap.createBitmap(bitmap, x, y, width, height);
  }

  public static Bitmap mirrorImg(Bitmap bitmap) {
    Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
        bitmap.getConfig());
    Canvas canvas = new Canvas(bitmap2);

    Matrix orig = new Matrix();
    orig.setScale(-1, 1);                     //翻转X
    orig.postTranslate(bitmap.getWidth(), 0);//平移
    canvas.drawBitmap(bitmap, orig, null);
    return bitmap2;
  }

  public static byte[] bitmapToByte(Bitmap bitmap) {
    Bitmap bitmap1 = mirrorImg(bitmap);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, os);
    bitmap1.recycle();
    return os.toByteArray();
  }
}
