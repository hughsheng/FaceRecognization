package ftthemepark.hytch.com.facerecognizationlibrary.utils;

public class ConstantValue {
  // Camera nv21格式预览帧的尺寸，默认设置640*480
  public static final int PREVIEW_WIDTH = 640;
  public static final int PREVIEW_HEIGHT = 480;
  //限制人脸在屏幕中的位置
  public static final int POSITION_LIMIT = 20;
  //允许人脸的最小面积
  public static final int FACE_MIN_AREA = 8100;
  //允许人脸偏移量
  public static final int MOVELIMIT = 50;
  //设置抓取照片数
  public static final int FACENUM = 2;
  //抓拍的照片不清晰
  public static final int HANDLER_FACE_UNCLEAR = 710;
  //人脸照片不完整
  public static final int HANDLER_FACE_IMPERFECT = 711;
  //显示人脸
  public static final int HANDLER_FACE_SUCCESS = 200;
  //人脸照片
  public static byte[] FACE_IMAGE;
  //未裁剪人物照片
  public static byte[] FULL_IMAGE;
}
