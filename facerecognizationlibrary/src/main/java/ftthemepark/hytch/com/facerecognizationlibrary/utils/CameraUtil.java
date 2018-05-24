package ftthemepark.hytch.com.facerecognizationlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.tzutalin.dlib.VisionDetRet;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ftthemepark.hytch.com.facerecognizationlibrary.bean.FaceRectBean;
import ftthemepark.hytch.com.facerecognizationlibrary.widges.CameraPreview;
import ftthemepark.hytch.com.facerecognizationlibrary.widges.FaceOverlayView;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.getNumberOfCameras;
import static android.hardware.Camera.open;
import static ftthemepark.hytch.com.facerecognizationlibrary.utils.ConstantValue.PREVIEW_HEIGHT;
import static ftthemepark.hytch.com.facerecognizationlibrary.utils.ConstantValue.PREVIEW_WIDTH;

public class CameraUtil {
  /**
   * 获取图像中最大的人脸
   *
   * @param faceDetRets
   * @return
   */
  public static VisionDetRet getMaxFace(List<VisionDetRet> faceDetRets) {
    if (faceDetRets.size() == 1) {
      return faceDetRets.get(0);
    }
    List<FaceRectBean> rectBeanList = new ArrayList<>();
    int position = 0;
    for (int i = 0; i < faceDetRets.size(); i++) {
      VisionDetRet faceDetRet = faceDetRets.get(i);
      FaceRectBean rectBean = new FaceRectBean();
      rectBean.setOrder(i);
      rectBean.setAcreage(Math.abs(faceDetRet.getBottom() - faceDetRet.getTop()) *
          Math.abs(faceDetRet.getLeft() - faceDetRet.getRight()));
      rectBeanList.add(rectBean);
    }
    float max = rectBeanList.get(0).getAcreage();
    for (int j = 0; j < rectBeanList.size(); j++) {
      if (rectBeanList.get(j).getAcreage() > max) {
        max = rectBeanList.get(j).getAcreage();
        position = j;
      }
    }
    return faceDetRets.get(position);
  }

  /**
   * 判断获取的人脸是否符合要求
   * （人脸是否在指定的区域）
   *
   * @param faceBean
   * @return
   */
  public static boolean isGetfitFace(VisionDetRet faceBean, float scaleX, float scaleY, int
      screenWidth, int screenHight) {

    int faceTop = faceBean.getTop();
    int faceBottom = faceBean.getBottom();
    int faceRight = faceBean.getRight();
    int faceLeft = faceBean.getLeft();

    int acreage = Math.abs(faceTop - faceBottom) * Math.abs(faceRight - faceLeft);

    float height = screenHight - (faceBottom * scaleY);
    float width = screenWidth - faceRight * scaleX;

    return (faceLeft * scaleX > ConstantValue.POSITION_LIMIT && faceTop * scaleY > ConstantValue
        .POSITION_LIMIT && width > ConstantValue.POSITION_LIMIT &&
        height > ConstantValue.POSITION_LIMIT && acreage > ConstantValue.FACE_MIN_AREA);
  }

  /**
   * 判断获取的图片是否清晰
   * （人脸的晃动距离是否过大----40）
   *
   * @param faceBeanList
   * @return
   */
  public static boolean isFaceClear(List<VisionDetRet> faceBeanList) {
    int size = faceBeanList.size();
    if (size == 1) {//当只识别一张则直接返回
      return true;
    }
    int firstTop = faceBeanList.get(0).getTop();
    int firstBottom = faceBeanList.get(0).getBottom();
    int firstRight = faceBeanList.get(0).getRight();
    int firstLeft = faceBeanList.get(0).getLeft();

    int lastTop = faceBeanList.get(size - 1).getTop();
    int lastBottom = faceBeanList.get(size - 1).getBottom();
    int lastRight = faceBeanList.get(size - 1).getRight();
    int lastLeft = faceBeanList.get(size - 1).getLeft();

    int moveTop = Math.abs(firstTop - lastTop);
    int moveBottom = Math.abs(firstBottom - lastBottom);
    int moveRight = Math.abs(firstRight - lastRight);
    int moveLeft = Math.abs(firstLeft - lastLeft);

    return (moveTop < ConstantValue.MOVELIMIT && moveBottom < ConstantValue.MOVELIMIT && moveLeft
        < ConstantValue.MOVELIMIT && moveRight < ConstantValue.MOVELIMIT);
  }

  /**
   * 打开相机
   */
  public static Camera openCamera(FragmentActivity fragmentActivity, SurfaceHolder
      holder, CameraPreview cameraPreview) {
    int mCameraId = CAMERA_FACING_FRONT;
    Camera mCamera = null;
    if (getNumberOfCameras() == 1) {//如果只有一个摄像头打开后置摄像头
      mCameraId = CAMERA_FACING_BACK;
    }
    try {
      mCamera = open(mCameraId);
      //设置相机预览格式和尺寸
      Camera.Parameters params = mCamera.getParameters();
      params.setPreviewFormat(ImageFormat.NV21);
      params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
      setAutoFocus(params);//自动对焦设置
      mCamera.setParameters(params);//camera参数设置
      mCamera.setPreviewCallback(cameraPreview);//获取实时图像数据
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();//开启预览
      if (!"Freescale".endsWith(Build.BOARD)) {
        mCamera.setDisplayOrientation(getOrientation(fragmentActivity));
      }
    } catch (Exception e) {
      e.printStackTrace();
      closeCamera(mCamera);
    }
    return mCamera;
  }


  /**
   * 关闭摄像头
   */
  public static void closeCamera(Camera mCamera) {
    if (null != mCamera) {
      mCamera.setPreviewCallback(null);
      mCamera.stopPreview();
      mCamera.release();
      mCamera = null;
    }
  }

  /**
   * 判断是否可以设置自动对焦
   *
   * @param cameraParameters
   */
  public static void setAutoFocus(Camera.Parameters cameraParameters) {
    List<String> focusModes = cameraParameters.getSupportedFocusModes();
    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
      cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
  }

  /**
   * 获取摄像头与手持设备拍摄的角度
   *
   * @param context
   * @return
   */
  public static int getOrientation(Context context) {
    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
        .getDefaultDisplay();
    int rotation = display.getRotation();
    int orientation;
    boolean expectPortrait;
    switch (rotation) {
      case Surface.ROTATION_0:
      default:
        orientation = 90;
        expectPortrait = true;
        break;
      case Surface.ROTATION_90:
        orientation = 0;
        expectPortrait = false;
        break;
      case Surface.ROTATION_180:
        orientation = 270;
        expectPortrait = true;
        break;
      case Surface.ROTATION_270:
        orientation = 180;
        expectPortrait = false;
        break;
    }
    boolean isPortrait = display.getHeight() > display.getWidth();
    if (isPortrait != expectPortrait) {
      orientation = (orientation + 270) % 360;
    }
    return orientation;
  }

  public static Bitmap getProperBitmap(byte[] data, FragmentActivity fragmentActivity) {
    Bitmap bmp = decodeToBitMap(data);
    // 定义矩阵对象
    Matrix matrix = new Matrix();
    // 缩放原图
    matrix.postScale(1f, 1f);
    matrix.postRotate(-getOrientation(fragmentActivity));
    if (bmp != null) {
      return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
          matrix, true);
    } else {
      return null;
    }
  }


  /**
   * 图像数组转化为bitmap
   *
   * @param data
   * @return
   */
  public static Bitmap decodeToBitMap(byte[] data) {
    try {
      YuvImage image = new YuvImage(data, ImageFormat.NV21, PREVIEW_WIDTH,
          PREVIEW_HEIGHT, null);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      image.compressToJpeg(new Rect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT),
          100, stream);
      Bitmap bmp = BitmapFactory.decodeByteArray(
          stream.toByteArray(), 0, stream.size());
      stream.close();
      return bmp;
    } catch (Exception ex) {

    }
    return null;
  }
}
