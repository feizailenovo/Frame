package com.lenovo.frame.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lenovo.frame.R;
import com.lenovo.frame.listeren.RequsetPermissionResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author feizai
 * @date 12/21/2020 021 9:44:14 PM
 */
public abstract class BaseFragment extends Fragment implements IGetPageName {

    int resLayout;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RequsetPermissionResultListener mRequsetPermissionResultListener;

    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    public BaseFragment(int resLayout){
        this.resLayout=resLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(resLayout,container,false);
        initView(view);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        ButterKnife.bind(this,view);
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            activityResultCallback(result);
        });
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        });
        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        });
//        sendDateToFragment();
        return view;
    }

    protected abstract void initView(View view);

    //activity??????????????????
    protected abstract void activityResultCallback(ActivityResult result);

    /**
     * ???????????????????????????
     *
     * @param activityclass ????????????????????????class
     */
    protected void startActivity(Class activityclass) {
        startActivity(new Intent(getActivity(), activityclass));
    }

    /**
     * ???????????????????????????
     *
     * @param activityclass ????????????????????????class
     * @param requsetCode   ???????????????id ?????????????????????
     */
    @Deprecated
    protected void startActivityForResult(Class activityclass, int requsetCode) {
        startActivityForResult(new Intent(getActivity(), activityclass), requsetCode);
    }

    /**
     * ???????????????????????????
     *
     * @param activityclass ????????????????????????class
     */
    protected void registerForActivityResult(Class activityclass) {
        intentActivityResultLauncher.launch(new Intent(getActivity(), activityclass));
    }

    /**
     * ???????????????????????????
     *
     * @param intent ????????????????????????intent
     */
    protected void registerForActivityResult(Intent intent) {
        intentActivityResultLauncher.launch(intent);
    }

    /**
     * ??????????????????
     * ????????????
     * @param id                                ???????????????id ??????????????????
     * @param permission                        ???????????????
     * @param requsetPermissionResultListener   ??????????????????????????????
     */
    @Deprecated
    public void myRequestPermission(int id, String permission, RequsetPermissionResultListener requsetPermissionResultListener) {
        mRequsetPermissionResultListener = requsetPermissionResultListener;
        //????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //????????????????????????
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //????????????
               ActivityCompat.requestPermissions(getActivity(),new String[]{permission},id);
            } else {
                requsetPermissionResultListener.onAllowable(permission);
            }
        } else {
            requsetPermissionResultListener.onAllowable(permission);
        }
    }

    /**
     * ??????????????????
     * ??????ActivityResultAPI
     *
     * @param permission ???????????????
     * @param requsetPermissionResultListener ??????????????????????????????
     */
    public void myRequestPermission(String permission, RequsetPermissionResultListener requsetPermissionResultListener) {
        mRequsetPermissionResultListener = requsetPermissionResultListener;
        //????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //????????????????????????
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //????????????
                requestPermissionLauncher.launch(permission);
            } else {
                requsetPermissionResultListener.onAllowable(permission);
            }
        } else {
            requsetPermissionResultListener.onAllowable(permission);
        }
    }

    /**
     * ???????????????
     * ????????????
     *
     * @param id                              ???????????????id ??????????????????
     * @param permissions                     ??????????????????
     * @param requsetPermissionResultListener ??????????????????????????????
     */
    @Deprecated
    public void requestPermissions(int id, String[] permissions, RequsetPermissionResultListener requsetPermissionResultListener) {
        mRequsetPermissionResultListener = requsetPermissionResultListener;
        //????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> result = new ArrayList<>();
            int count = 0;
            //????????????????????????
            for (String permission : permissions) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permission);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    result.add(permission);
                    count++;
                }
            }
            if (count == 0) {
                requsetPermissionResultListener.onAllAllowable();
            } else {
                String[] strings = new String[count];
                result.toArray(strings);
                ActivityCompat.requestPermissions(getActivity(), strings, id);
            }
        } else {
            requsetPermissionResultListener.onAllAllowable();
        }
    }

    /**
     * ???????????????
     *
     * @param permissions ??????????????????
     * @param requsetPermissionResultListener ??????????????????????????????
     */
    public void requestPermissions(String[] permissions, RequsetPermissionResultListener requsetPermissionResultListener) {
        mRequsetPermissionResultListener = requsetPermissionResultListener;
        //????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> result = new ArrayList<>();
            int count = 0;
            //????????????????????????
            for (String permission : permissions) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permission);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    result.add(permission);
                    count++;
                }
            }
            if (count == 0) {
                requsetPermissionResultListener.onAllAllowable();
            } else {

                String[] strings = new String[count];
                result.toArray(strings);
                requestPermissionsLauncher.launch(strings);
            }
        } else {
            requsetPermissionResultListener.onAllAllowable();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequsetPermissionResultListener == null) {
            throw new NullPointerException("RequsetPermissionResultListener is not allowed to be empty");
        }
        for (int i = 0; i < grantResults.length; i++) {
            boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (isTip) {//????????????????????????????????????????????????
                    mRequsetPermissionResultListener.onDisallowable(permissions[i]);
                } else {//????????????????????????????????????????????????
                    //???????????????????????????????????????????????????
                    mRequsetPermissionResultListener.onCompleteban(permissions[i]);
                }
                return;
            } else {
                mRequsetPermissionResultListener.onAllowable(permissions[i]);
            }
        }
    }

    /*????????????????????????intent??????????????????????????????????????????????????????????????????????????????????????????*/
    protected Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getActivity().getPackageName());
        }
        return localIntent;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    /**
     * ??????Disposable
     */
    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void sendDateToFragment(String requestKey, FragmentResultListener listener) {
        getParentFragmentManager().setFragmentResultListener(requestKey,this,listener);
    }
}
