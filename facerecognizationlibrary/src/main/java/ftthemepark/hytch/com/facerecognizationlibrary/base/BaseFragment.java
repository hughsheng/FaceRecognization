package ftthemepark.hytch.com.facerecognizationlibrary.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ftthemepark.hytch.com.facerecognizationlibrary.R;

public abstract class BaseFragment extends Fragment {

  protected View rootView;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    if (null != rootView) {
      ViewGroup parent = (ViewGroup) rootView.getParent();
      if (null != parent) {
        parent.removeView(rootView);
      }
    } else {
      rootView = inflater.inflate(getLayoutResId(), container, false);
    }
    return rootView;
  }


  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    onLogicPresenter();
  }

  public abstract int getLayoutResId();

  /**
   * view视图创建时调用
   */
  public abstract void onLogicPresenter();
}
