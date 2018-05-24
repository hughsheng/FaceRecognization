package ftthemepark.hytch.com.facerecognization;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ftthemepark.hytch.com.facerecognizationlibrary.FaceFragment;

import static android.support.v4.util.Preconditions.checkNotNull;

public class MainActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (getActionBar() != null) {
      getActionBar().hide();
    }
    addContent(FaceFragment.newInstance());
  }

  private void addContent(Fragment fragment) {
    addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.container, FaceFragment
        .TAG, 0, 0);
  }


  @SuppressLint("RestrictedApi")
  private static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                            @NonNull Fragment fragment, int frameId, String tag,
                                            int animStar, int animEnd) {
    checkNotNull(fragmentManager);
    checkNotNull(fragment);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(frameId, fragment, tag)
        .commitAllowingStateLoss();
  }

}
