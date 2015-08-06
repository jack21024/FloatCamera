package com.jack.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jack.Debug;
import com.jack.FCVStorage;
import com.jack.R;
import com.jack.util.Setting;
import com.jack.util.SettingManager;
import com.jack.view.CameraView;
import com.jack.view.FloatWindow;

import java.util.List;

/**
 * Created by jacktseng on 2015/6/19.
 */
public class FloatCameraService extends Service {
    public static final String TAG = FloatCameraService.class.getSimpleName();

    public static final String ACTION = "com.jack.float.camera";

    public static final int PAGE_FLOAT_CAMERA   = 0x01;
    public static final int PAGE_CAMERA_SETTING = 0x02;

    public static final String WEB_URL_HOME = "http://www.google.com";

    private int mOnPage;

    private View mRootView;

    private FloatWindow mFloatView;

    private CameraView mCameraView;

    private Dialog mDialog;

    private boolean mIsFloatable = false;

    @Override
    public void onCreate() {
        super.onCreate();

//        loadFloatCameraPage();
        switchPage(PAGE_FLOAT_CAMERA);
    }

    private void switchPage(int toPage) {
        Debug.dumpLog(TAG, "switch page from " + mOnPage + " to " + toPage);

        if(mFloatView != null)
            mFloatView.dismiss();

        switch(mOnPage) {
            case PAGE_FLOAT_CAMERA:
                if(toPage == PAGE_CAMERA_SETTING) {
                    if(mCameraView != null) {
                        List<Camera.Size> previewSizes = mCameraView.getCameraSupportedPreviewSizes();
                        List<Camera.Size> snapshotSizes = mCameraView.getCameraSupportedSnapshotSizes();
                        mCameraView.release();
                        loadCameraSettingPage(previewSizes, snapshotSizes);
                    }
                }

                break;
            case PAGE_CAMERA_SETTING:
                loadFloatCameraPage();

                break;
            default:
                loadFloatCameraPage();
        }

        mOnPage = toPage;
    }

    private void loadFloatCameraPage() {

        View rootView = View.inflate(this, R.layout.float_camera, null);
        CameraView cameraView = (CameraView) rootView.findViewById(R.id.main_cv_cameraView);
        /**
         * set snapshot listener for catch snapshot picture
         */
        cameraView.getCameraController().setSnapshotListener(new FCVStorage(this));

        /**
         * mask of WebView which is used to hide camera preview
         */
        final WebView wvMaskView = (WebView) rootView.findViewById(R.id.main_wv_webView);
        wvMaskView.setFocusable(true);
        wvMaskView.requestFocus();
        wvMaskView.setEnabled(true);
        wvMaskView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvMaskView.loadUrl(WEB_URL_HOME);

        /**
         * maskSeeker setting for the mask view's alpha controlling
         */
        SeekBar maskSeeker = (SeekBar) rootView.findViewById(R.id.main_sb_cvVisibleSeek);
        maskSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (wvMaskView == null) return;

                float alpha = (float) i / 100f;
                wvMaskView.setAlpha(alpha);
                wvMaskView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Log.e(TAG, "onStartTrackingTouch()");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.e(TAG, "onStopTrackingTouch()");
            }
        });
        maskSeeker.setProgress(90);

        /**
         * resize camera view and it's relation views according to camera setting
         */
        Setting setting = SettingManager.getInstance(this);
        if(setting != null) {
            Debug.dumpLog(TAG, "setting CameraView preview size - w="
                    + setting.getPreviewWidth() + " h=" + setting.getPreviewHeight());
            cameraView.setCameraPreviewSize(setting.getPreviewWidth(), setting.getPreviewHeight());
            cameraView.setCameraSnapshotSize(setting.getSnapshotWidth(), setting.getSnapshotHeight());

            //resize CameraView
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(setting.getPreviewHeight(), setting.getPreviewWidth());
            cameraView.setLayoutParams(lp);

            //resize mask for hiding CameraView
            lp = new FrameLayout.LayoutParams(lp.width, lp.height);
            wvMaskView.setLayoutParams(lp);

            //reset SeekBar for CameraView
            int w = (int) (setting.getPreviewHeight());
            lp = new FrameLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            lp.bottomMargin = 20;
            maskSeeker.setLayoutParams(lp);

            //resize wrapper of CameraView & it's mask view
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(setting.getPreviewHeight(), setting.getPreviewWidth());
            rootView.findViewById(R.id.main_fl_cameraViewWrapper).setLayoutParams(llp);
        }

        /**
         * Button click setting
         */

        //camera setting button
        rootView.findViewById(R.id.main_btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FloatCameraService.this, "Setting", Toast.LENGTH_SHORT).show();

                switchPage(PAGE_CAMERA_SETTING);
            }
        });
        //float window floatable button
        rootView.findViewById(R.id.main_btn_touchLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * if mIsFloatable is true and let FloatWindow can be move by touch, otherwise if
                 * mIsFloatable is false, the WebView will handle touch event
                 *
                 */
                if(mIsFloatable) {
                    //let soft keyboard can show, need to set WebView focus
                    wvMaskView.setFocusable(true);
                    wvMaskView.requestFocus();
                    //reset OnTouchListener to default to origin touch behavior
                    wvMaskView.setOnTouchListener(null);
                }
                else {
                    //clean focus
                    wvMaskView.setFocusable(false);
                    wvMaskView.clearFocus();
                    /**
                     * cause the WebView interrupt touch event, so the FloatWindow can't trigger
                     * onTouch to control window to move when window be touched. At this case, we
                     * implements OnTouchListener to redefine WebViews touch event, and pass event
                     * to FloatWindow manually.
                     */
                    wvMaskView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            mFloatView.onTouch(view, motionEvent);
                            return true;
                        }
                    });
                }
                mIsFloatable = !mIsFloatable;

                Button btn = (Button) view;
                String text = (mIsFloatable) ? "FLOAT" : "LOCK";
                btn.setText(text);
                btn.invalidate();
            }
        });
        //program close button
        rootView.findViewById(R.id.main_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FloatCameraService.this, "Close", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
        //camera snapshot button
        rootView.findViewById(R.id.main_btn_snapshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(FloatCameraService.this, "Snapshot", Toast.LENGTH_SHORT).show();

                if(mCameraView != null)
                    mCameraView.snapshot();

                //notify media database to show newly
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }
        });

        /**
         * cache views
         */
        mRootView = rootView;
        mCameraView = cameraView;

        /**
         * create a float window which contain the given view to float popup on screen
         */
        mFloatView = new FloatWindow(rootView);
        mFloatView.layout();
    }

    public void loadCameraSettingPage(List<Camera.Size> preview,List<Camera.Size> snapshot) {
        if(mDialog != null)
            mDialog.dismiss();

        View rootView = View.inflate(this, R.layout.camera_setting, null);
        final RadioGroup rgPreviewSizes = (RadioGroup) rootView.findViewById(R.id.setting_rg_previewSizes);
        final RadioGroup rgSnapshotSizes = (RadioGroup) rootView.findViewById(R.id.setting_rg_snapshotSizes);

        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

        Setting setting = SettingManager.getInstance(this);
        for(Camera.Size size : preview) {
            Debug.dumpLog(TAG, "preview: w=" + size.width + " h=" + size.height);

            /**
             * filtering over size of screen
             */
            if(size.width >= dm.heightPixels)
                continue;

            RadioGroup.LayoutParams lp =
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RadioButton rb = new RadioButton(this);
            rb.setText(size.width + "x" + size.height);
            rb.setTag(size);
            rb.setLayoutParams(lp);
            rgPreviewSizes.addView(rb);


            /**
             * select using now
             */
            int w = setting.getPreviewWidth();
            int h = setting.getPreviewHeight();
            if(w == size.width && h == size.height)
                rb.setChecked(true);
        }
        for(Camera.Size size : snapshot) {
            RadioGroup.LayoutParams lp =
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RadioButton rb = new RadioButton(this);
            rb.setText(size.width + "x" + size.height);
            rb.setTag(size);
            rb.setLayoutParams(lp);
            rgSnapshotSizes.addView(rb);

            /**
             * select using now
             */
            int w = setting.getSnapshotWidth();
            int h = setting.getSnapshotHeight();
            if(w == size.width && h == size.height)
                rb.setChecked(true);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(rootView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int previewCheckId = rgPreviewSizes.getCheckedRadioButtonId();
                int snapshotCheckId = rgSnapshotSizes.getCheckedRadioButtonId();
                Camera.Size previewSize = (Camera.Size) rgPreviewSizes.findViewById(previewCheckId).getTag();
                Camera.Size snapshotSize = (Camera.Size) rgSnapshotSizes.findViewById(snapshotCheckId).getTag();

                Log.v(TAG, "preview: w=" + previewSize.width + " h=" + previewSize.height);
                Log.v(TAG, "snapshot: w=" + snapshotSize.width + " h=" + snapshotSize.height);

                Setting setting = SettingManager.getInstance(FloatCameraService.this);
                if(setting != null) {
                    setting.setPreviewWidth(previewSize.width);
                    setting.setPreviewHeight(previewSize.height);
                    setting.setSnapshotWidth(snapshotSize.width);
                    setting.setSnapshotHeight(snapshotSize.height);
                    setting.commit();

                    switchPage(PAGE_FLOAT_CAMERA);
                }
            }
        });

        mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mFloatView != null)
            mFloatView.dismiss();
        if(mCameraView != null)
            mCameraView.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
