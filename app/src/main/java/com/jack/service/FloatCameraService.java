package com.jack.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jack.Debug;
import com.jack.R;
import com.jack.Version;
import com.jack.library.camera.CameraHolder;
import com.jack.library.camera.CameraRecord;
import com.jack.library.camera.CameraStorage;
import com.jack.library.camera.impl.CameraManagerFactory;
import com.jack.library.camera.listener.OnSnapshotListener;
import com.jack.library.util.DimenUtils;
import com.jack.library.view.CameraView;
import com.jack.library.view.FloatWindow;
import com.jack.sys.AppConfig;
import com.jack.sys.AppConfigFactory;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.HoverBubble;
import com.txusballesteros.bubbles.OnInitializedCallback;

import java.util.List;

/**
 * Note: This application is not support api level 21 upper.
 * <p/>
 * This class is a real main function of this application.<br/>
 * In order to use float window without any pages, we uses the service instead of the activity to
 * create views dynamically.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class FloatCameraService extends Service {
    public static final String TAG = FloatCameraService.class.getSimpleName();

    /**
     * Action name for launching this service
     */
    public static final String ACTION = "com.jack.float.camera";

    /**
     * Code of pages
     */
    private static final int PAGE_FLOAT_CAMERA   = 0x01;
    private static final int PAGE_CAMERA_SETTING = 0x02;

    private static final String WEB_URL_HOME = "http://www.google.com";

    /** Application storage folder */
    public static final String APP_FOLDER_NAME = "FCV";

    /** It's a flag which enabling view to float or not  */
    private boolean mIsFloatable = false;

    /** Indicates current page  */
    private int mOnPage;

    /**
     * A storage use to read or write the record of camera
     */
    private CameraStorage mStorage;

    /** The path where the record storing */
    private String mStorageRootPath;

    /** Just a cache to indicate current view */
    private View mRootView;

    private FloatWindow mFloatView;

    private CameraView mCameraView;

    private Dialog mDialog;

    /**
     * This listener is used to get snapshot which is a callback from CameraHolder, and saves the
     * snapshot to external storage with CameraStorage which giving from CameraManager.
     */
    private OnSnapshotListener mOnSnapshotListener = new OnSnapshotListener() {
        @Override
        public void onSnapshot(int imageDataType, CameraRecord record) {
            do {
                if(record == null) break;
                if(mStorage == null) break;
                if(!mStorage.save(record)) break;

                Toast.makeText(FloatCameraService.this,
                        "saved image at " + record.getResourcePath(), Toast.LENGTH_SHORT).show();

                //Notifies media scanner to update immediately
                MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[]{record.getResourcePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.v(TAG, "file " + path + " was scanned successfully");
                            }
                        });
            } while(false);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * init
         */
        mStorageRootPath = Environment.getExternalStorageDirectory().getPath() + "/" + APP_FOLDER_NAME;
        mStorage = CameraManagerFactory.getInstance()
                .getCameraStorage(CameraStorage.STORAGE_TYPE.LOCAL_DEFAULT);
        mStorage.setRoot(mStorageRootPath);

//        switchPage(PAGE_FLOAT_CAMERA);
        initBubble();
    }

    private void release() {
        if(mCameraView != null)
            mCameraView.release();
        if(mFloatView != null)
            mFloatView.dismiss();
        if(mDialog != null)
            mDialog.dismiss();

        mStorage = null;
        mRootView = null;
        mFloatView = null;
        mCameraView = null;
        mDialog = null;
        mOnSnapshotListener = null;

        releaseBubble();
    }

    private void switchPage(int toPage) {
        Debug.dumpLog(TAG, "switch page from " + mOnPage + " to " + toPage);

        if(mFloatView != null)
            mFloatView.dismiss();

        switch(mOnPage) {
            case PAGE_FLOAT_CAMERA:
                if(toPage == PAGE_CAMERA_SETTING) {
                    if(mCameraView == null) return;
                    if(mCameraView.getCameraHolder() == null) return;

                    List<int[]> previewSizes = mCameraView.getCameraHolder().getSupportedPreviewSizes();
                    List<int[]> snapshotSizes = mCameraView.getCameraHolder().getSupportedPictureSizes();
                    mCameraView.release();
                    loadCameraSettingPage(previewSizes, snapshotSizes);
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

    /**
     * Loads the main page to display
     */
    private void loadFloatCameraPage() {
        View rootView = View.inflate(this, R.layout.float_camera, null);
        CameraView cameraView = (CameraView) rootView.findViewById(R.id.main_cv_cameraView);

        /**
         * Sets snapshot listener for catch snapshot picture
         */
        if(cameraView.getCameraHolder() != null)
            cameraView.getCameraHolder().addSnapshotListener(mOnSnapshotListener);

        /**
         * WebView is a mask which is used to hide the camera preview
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
         * Sets seeker for alpha setting of the WebView
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
         * Resize views according to setting
         */
        AppConfig appConfig = AppConfigFactory.getInstance(this);
        if(appConfig != null) {
            Debug.dumpLog(TAG, "setting CameraView preview size - w="
                    + appConfig.getPreviewWidth() + " h=" + appConfig.getPreviewHeight());
            cameraView.setCameraPreviewSize(appConfig.getPreviewWidth(), appConfig.getPreviewHeight());
            cameraView.setCameraSnapshotSize(appConfig.getSnapshotWidth(), appConfig.getSnapshotHeight());

            //Resize CameraView
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(appConfig.getPreviewHeight(), appConfig.getPreviewWidth());
            cameraView.setLayoutParams(lp);

            //Resize WebView for hiding CameraView
            lp = new FrameLayout.LayoutParams(lp.width, lp.height);
            wvMaskView.setLayoutParams(lp);

            //Resets SeekBar for CameraView
            int w = (int) (appConfig.getPreviewHeight());
            lp = new FrameLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            lp.bottomMargin = 20;
            maskSeeker.setLayoutParams(lp);

            //Resize wrapper of CameraView & and it's sub views
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(appConfig.getPreviewHeight(), appConfig.getPreviewWidth());
            rootView.findViewById(R.id.main_fl_cameraViewWrapper).setLayoutParams(llp);
        }

        /**
         * Button click setting
         */
        //Camera setting button
        rootView.findViewById(R.id.main_btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FloatCameraService.this, "Setting", Toast.LENGTH_SHORT).show();

                switchPage(PAGE_CAMERA_SETTING);
            }
        });
        //Enable float button
        rootView.findViewById(R.id.main_btn_touchLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * If mIsFloatable is true, enables FloatWindow to be moved by touch, otherwise if
                 * mIsFloatable is false, the WebView will handle touch event
                 *
                 */
                if(mIsFloatable) {
                    //Lets soft keyboard can show, need to set WebView focus
                    wvMaskView.setFocusable(true);
                    wvMaskView.requestFocus();
                    //Resets OnTouchListener to default to origin touch behavior
                    wvMaskView.setOnTouchListener(null);
                }
                else {
                    //Clean focus
                    wvMaskView.setFocusable(false);
                    wvMaskView.clearFocus();
                    /**
                     * Cause the WebView interrupt touch event, so the FloatWindow can't trigger
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
        //Close app button
        rootView.findViewById(R.id.main_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FloatCameraService.this, "Close", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
        //Camera snapshot button
        rootView.findViewById(R.id.main_btn_snapshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraHolder ch = mCameraView.getCameraHolder();
                if(ch != null)
                    ch.snapshot();

                Toast.makeText(FloatCameraService.this, "Snapshot!", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Starts the camera to preview
         */
        cameraView.start();

        /**
         * Creates a float window which contain the given view to float popup on screen
         */
        FloatWindow floatWindow = new FloatWindow(rootView);
        floatWindow.layout();

        /**
         * Sets views caching
         */
        mRootView = rootView;
        mCameraView = cameraView;
        mFloatView = floatWindow;

    }

    /**
     * Loads the setting page with given lists of preview size and snapshot size
     *
     * @param preview
     * @param snapshot
     */
    public void loadCameraSettingPage(List<int[]> preview,List<int[]> snapshot) {
        if(mDialog != null)
            mDialog.dismiss();

        View rootView = View.inflate(this, R.layout.camera_setting, null);
        final RadioGroup rgPreviewSizes = (RadioGroup) rootView.findViewById(R.id.setting_rg_previewSizes);
        final RadioGroup rgSnapshotSizes = (RadioGroup) rootView.findViewById(R.id.setting_rg_snapshotSizes);

        /**
         * Shows the version of this application
         */
        ((TextView) rootView.findViewById(R.id.setting_tv_appVersion)).setText(Version.getVersionWithoutDate());

        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

        /**
         * Gets a config of app
         */
        AppConfig appConfig = AppConfigFactory.getInstance(this);

        /**
         * Builds the group of camera preview setting
         */
        for(int[] size : preview) {
            Debug.dumpLog(TAG, "preview: w=" + size[0] + " h=" + size[1]);

            /**
             * Filters the size which is small than screen
             */
            if(size[0] >= dm.heightPixels)
                continue;

            /**
             * Creates a radio button to show camera size
             */
            RadioGroup.LayoutParams lp =
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            RadioButton rb = new RadioButton(this);
            rb.setText(size[0] + "x" + size[1]);
            rb.setTag(size);
            rb.setLayoutParams(lp);
            rgPreviewSizes.addView(rb);

            /**
             * Selects which one of setting is used now according to app config
             */
            int w = appConfig.getPreviewWidth();
            int h = appConfig.getPreviewHeight();
            if(w == size[0] && h == size[1])
                rb.setChecked(true);
        }

        /**
         * Builds the group of camera snapshot setting
         */
        for(int[] size : snapshot) {
            /**
             * Creates a radio button to show camera size
             */
            RadioGroup.LayoutParams lp =
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            RadioButton rb = new RadioButton(this);
            rb.setText(size[0] + "x" + size[1]);
            rb.setTag(size);
            rb.setLayoutParams(lp);
            rgSnapshotSizes.addView(rb);

            /**
             * Selects which one of setting is used now according to app config
             */
            int w = appConfig.getSnapshotWidth();
            int h = appConfig.getSnapshotHeight();
            if(w == size[0] && h == size[1])
                rb.setChecked(true);
        }

        /**
         * Prepares to show the setting page on screen
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(rootView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int previewCheckId = rgPreviewSizes.getCheckedRadioButtonId();
                int snapshotCheckId = rgSnapshotSizes.getCheckedRadioButtonId();
                int[] previewSize = (int[]) rgPreviewSizes.findViewById(previewCheckId).getTag();
                int[] snapshotSize = (int[]) rgSnapshotSizes.findViewById(snapshotCheckId).getTag();

                Log.v(TAG, "save preview: w=" + previewSize[0] + " h=" + previewSize[1]);
                Log.v(TAG, "save snapshot: w=" + snapshotSize[0] + " h=" + snapshotSize[1]);

                /**
                 * To save the camera setting
                 */
                AppConfig appConfig = AppConfigFactory.getInstance(FloatCameraService.this);
                if(appConfig != null) {
                    appConfig.setPreviewWidth(previewSize[0]);
                    appConfig.setPreviewHeight(previewSize[1]);
                    appConfig.setSnapshotWidth(snapshotSize[0]);
                    appConfig.setSnapshotHeight(snapshotSize[1]);
                    appConfig.commit();

                    /**
                     * Changes page to the float camera page
                     */
                    switchPage(PAGE_FLOAT_CAMERA);
                }
            }
        });
        mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    private BubblesManager mBubbleManager;
    private HoverBubble mBubble;
    private void initBubble() {
        mBubbleManager = new BubblesManager.Builder(this).setTrashLayout(R.layout.pb_bubble_trash)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {

                        mBubble = (HoverBubble) LayoutInflater.from(FloatCameraService.this).inflate(R.layout.pb_bubble_main, null);
                        float hoverRadius = DimenUtils.dp2px(60); // 60dp
                        int[] colors = {Color.parseColor("#FFFFFFFF"), Color.parseColor("#FFFFFFFF"), Color.parseColor("#00FFFFFF")};
                        GradientDrawable hoverDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                        hoverDrawable.setShape(GradientDrawable.OVAL);
                        hoverDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                        hoverDrawable.setGradientRadius(hoverRadius);
                        mBubble.setHoverResource(hoverDrawable, (ViewGroup) mBubble.findViewById(R.id.pb_bubble_main_layout_wrapper));

                        Log.e(TAG, "2");

                        mBubble.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
                            @Override
                            public void onBubbleClick(BubbleLayout bubble) {
                                toast("bubble click!");
                            }
                        });
                        mBubble.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
                            @Override
                            public void onBubbleRemoved(BubbleLayout bubble) {
                                toast("bubble removed");
                            }

                            @Override
                            public void onBubbleRemovedWithActionFired(BubbleLayout bubble, int actionId) {
                                toast("bubble remove with action fired");
                            }
                        });


                        mBubbleManager.addBubble(mBubble, 60, 20);
                        mBubble.addHoverListener();

                    }
                }).build();

        if(mBubbleManager != null) {
            mBubbleManager.initialize();
        }
    }

    private void releaseBubble() {
        if(mBubbleManager != null) {
            mBubbleManager.recycle();
        }
    }

    private void toast(String toast) {
        Toast.makeText(FloatCameraService.this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
