package ftthemepark.hytch.com.facerecognizationlibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;
import java.util.List;

import ftthemepark.hytch.com.facerecognizationlibrary.base.BaseFragment;
import ftthemepark.hytch.com.facerecognizationlibrary.utils.CameraUtil;
import ftthemepark.hytch.com.facerecognizationlibrary.utils.CommonUtil;
import ftthemepark.hytch.com.facerecognizationlibrary.utils.ConstantValue;
import ftthemepark.hytch.com.facerecognizationlibrary.utils.ThreadManagerUtil;
import ftthemepark.hytch.com.facerecognizationlibrary.utils.WeakHandler;
import ftthemepark.hytch.com.facerecognizationlibrary.widges.CameraPreview;
import ftthemepark.hytch.com.facerecognizationlibrary.widges.FaceOverlayView;



public class FaceFragment extends BaseFragment implements Handler.Callback {

  public static final String TAG = "FaceFragment";
  private int screenWidth;
  private int screenHeight;
  private float scaleX;
  private float scaleY;
  private CameraPreview cameraPreview;
  private byte[] nv21 = new byte[640 * 480 * 2];
  private FrameLayout frameLayout;
  private FragmentActivity fragmentActivity;
  private FaceOverlayView mFaceView;//绘制人脸检测框
  private FrameLayout face_Preview_Layout;
  private ImageView face_Preview;
  private Runnable faceRunnable;
  private List<VisionDetRet> faceBeanList;//一次连拍照片集合
  private boolean detecting = true;
  private boolean facedata = false;
  private FaceDet mFaceDet;
  private WeakHandler handler;
  private List<VisionDetRet> detect;

  public static FaceFragment newInstance() {

    Bundle args = new Bundle();

    FaceFragment fragment = new FaceFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public int getLayoutResId() {
    return R.layout.fragment_face;
  }

  @Override
  public void onLogicPresenter() {
    initView();
    getPhoneWidthAndHeight();
    setDetecteRunnable();
  }

  private void initView() {
    fragmentActivity = getActivity();
    frameLayout = fragmentActivity.findViewById(R.id.face_container);
    cameraPreview = new CameraPreview(getActivity(), nv21);
    frameLayout.addView(cameraPreview);
    mFaceView = new FaceOverlayView(fragmentActivity);
    mFaceView.setFront(true);
    mFaceView.setDisplayOrientation(CameraUtil.getOrientation(fragmentActivity));
    fragmentActivity.addContentView(mFaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    face_Preview_Layout = fragmentActivity.findViewById(R.id.face_preview_layout);
    face_Preview = fragmentActivity.findViewById(R.id.face_preview);
    mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
    faceBeanList = new ArrayList<>();//一次连拍照片集合
    handler = new WeakHandler(this);

  }

  private void getPhoneWidthAndHeight() {
    DisplayMetrics dm = new DisplayMetrics();
    dm = new DisplayMetrics();
    fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    float densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
    int screenWidthDip = dm.widthPixels; // 屏幕宽（dip，如：320dip）
    int screenHeightDip = dm.heightPixels; // 屏幕高（dip，如：533dip）
    screenWidth = (int) (screenWidthDip * density + 0.5f); // 屏幕宽（px，如：720px）
    screenHeight = (int) (screenHeightDip * density + 0.5f); // 屏幕高（px，如：1280px）
  }

  @Override
  public void onResume() {
    super.onResume();
    detecting = true;
    ThreadManagerUtil.getSingleThreadPool().execute(faceRunnable);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    detecting = false;
    CameraUtil.closeCamera(cameraPreview.getmCamera());
  }

  private void setDetecteRunnable() {
    faceRunnable = new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(800);
          while (true) {

            if (null == nv21) {
              continue;//未获取到图像
            }


            byte[] tmp = new byte[nv21.length];
            synchronized (nv21) {
              System.arraycopy(nv21, 0, tmp, 0, nv21.length);// 拷贝到临时数据中
            }

            Bitmap bitmap = CameraUtil.getProperBitmap(tmp, fragmentActivity);
            detect = mFaceDet.detect(bitmap);//检测人脸

            if (detect == null || detect.size() == 0) {
              continue;//未检测到人脸
            }

            handler.post(new Runnable() {
              @Override
              public void run() {
                mFaceView.setFaces(detect);//发送人脸数据，绘制人脸
                scaleX = mFaceView.getScaleX();
                scaleY = mFaceView.getScaleY();
              }
            });


            VisionDetRet faceBean = CameraUtil.getMaxFace(detect);
            faceBeanList.add(faceBean);//保存获取到的人脸数据
            if (faceBeanList.size() <= ConstantValue.FACENUM) {
              continue;
            }


            if (!CameraUtil.isFaceClear(faceBeanList)) {//判断人脸是否清晰
              faceBeanList.clear();
              handler.sendEmptyMessage(ConstantValue.HANDLER_FACE_UNCLEAR);
              continue;
            }

            VisionDetRet faceBeanEnd = faceBeanList.get(faceBeanList.size() - 1);//判断人脸是否完整
            if (!CameraUtil.isGetfitFace(faceBeanEnd, scaleX, scaleY, screenWidth, screenHeight)) {
              faceBeanList.clear();
              handler.sendEmptyMessage(ConstantValue.HANDLER_FACE_IMPERFECT);
              continue;
            }
            byte[] padImgBytes = CommonUtil.resultBitmap(faceBeanEnd, bitmap);//获取人脸照片流
            faceBeanList.clear();
            ConstantValue.FACE_IMAGE = padImgBytes;
            ConstantValue.FULL_IMAGE = CommonUtil.bitmapToByte(bitmap);
            handler.sendEmptyMessage(ConstantValue.HANDLER_FACE_SUCCESS);

            bitmap.recycle();
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
  }

  @Override
  public boolean handleMessage(Message msg) {
    int msgType = msg.what;
    switch (msgType) {
      case ConstantValue.HANDLER_FACE_UNCLEAR:
        Toast.makeText(fragmentActivity, getString(R.string.unclear), Toast.LENGTH_LONG).show();
        break;

      case ConstantValue.HANDLER_FACE_IMPERFECT:
        Toast.makeText(fragmentActivity, getString(R.string.imperfect), Toast.LENGTH_LONG).show();
        break;

      case ConstantValue.HANDLER_FACE_SUCCESS:
        face_Preview.setImageBitmap(BitmapFactory.decodeByteArray(ConstantValue.FACE_IMAGE, 0,
            ConstantValue.FACE_IMAGE.length));
        mFaceView.setFaces(null);
        break;
    }
    return false;
  }
}
