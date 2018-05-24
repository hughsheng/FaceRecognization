package ftthemepark.hytch.com.facerecognizationlibrary.widges;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ftthemepark.hytch.com.facerecognizationlibrary.utils.CameraUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera
    .PreviewCallback {

  private static final String TAG = "CameraPreview";

  private int mCameraId = CAMERA_FACING_FRONT;

  private SurfaceHolder mHolder;

  private Camera mCamera;

  private FragmentActivity fragmentActivity;

  private byte[] nv21;

  public CameraPreview(FragmentActivity fragmentActivity,byte[] nv21) {

    super(fragmentActivity);
    this.fragmentActivity = fragmentActivity;
    this.nv21=nv21;
    mHolder = getHolder();

    mHolder.addCallback(this);

    // 已过期的设置，但版本低于3.0的Android还需要
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }


  public void surfaceCreated(SurfaceHolder holder) {
    mCamera = CameraUtil.openCamera(fragmentActivity, holder, this);
  }


  public void surfaceDestroyed(SurfaceHolder holder) {
    CameraUtil.closeCamera(mCamera);
  }


  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

  }

  public Camera getmCamera() {
    return mCamera;
  }

  /**
   * 赋值获取到的图像数据
   *
   * @param data
   * @param camera
   */
  @Override
  public void onPreviewFrame(final byte[] data, Camera camera) {
    System.arraycopy(data, 0, nv21, 0, data.length);
  }


}
