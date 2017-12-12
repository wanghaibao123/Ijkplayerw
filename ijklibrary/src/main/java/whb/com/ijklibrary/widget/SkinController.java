package whb.com.ijklibrary.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import whb.com.ijklibrary.R;

/**
 * Created by whb on 2017/12/4.
 * 默认的皮肤控制器
 */

public class SkinController extends FrameLayout implements View.OnClickListener, IMediaController {
    /**
     * 所有视图都隐藏
     */
    private static final int VIEW_ALL_SHOW = 1;
    /**
     * 所有视图都显示
     */
    private static final int VIEW_ALL_HIDE = 2;

    /**
     * 当先显示视图状态
     */
    private static int CURRENT_VIEW_TYPE;

    /**
     * 隐藏皮肤的视图时间
     */
    private static final long SKIN_HIDE_TIME = 3000;
    /**
     * 几次加多少
     */
    private static final float ONE_STEP_SPEED = 0.25f;
    /**
     * 提示语
     */
    private TextView tv_tip;
    /**
     * 等待进度的progressbar
     */
    private ProgressBar bufferProgressBar;
    /**
     * 中心的播放按钮
     */
    private ImageView iv_center_play;
    /**
     * 顶部的barlayout
     */
    private LinearLayout playerTopLayout;
    /**
     * 返回按钮
     */
    private ImageView backPlayList;
    /**
     * 标题
     */
    private TextView videoIdText;
    /**
     * 锁屏按键
     */
    private ImageView iv_lock;
    /**
     * 底部控制按钮
     */
    private LinearLayout playerBottomLayout;
    /**
     * 播放按钮
     */
    private ImageView iv_play;
    /**
     * 可调节的进度条
     */
    private SeekBar skbProgress;
    /**
     * 已经播放时长
     */
    private TextView playDuration;

    /**
     * 总时长
     */
    private TextView videoDuration;

    /**
     * 倍速按钮
     */
    private TextView tv_speed;
    /**
     * 全屏按钮
     */
    private ImageView iv_fullscreen;
    /**
     * 加速框
     **/
    private LinearLayout rl_speed;
    /**
     * 加速加
     **/
    private TextView tv_speed_add;
    /**
     * 倍速显示
     **/
    private TextView tv_speed_show;
    /**
     * 倍速减少
     **/
    private TextView tv_speed_reduce;

    /**
     * 播放器
     */
    private MediaPlayerControl mediaPlayer;

    /**
     * 播放路径
     */
    private String videoPath;

    /**
     * 标题
     */
    private String title;

    /**
     * 进度条最大值
     */
    private final int MAX_PROGRESS_VALUE = 1000;

    /**
     * 暂停位置
     */
    private int pausePosition;

    /**
     * 宿主activity
     */
    private Activity mActivity;

    /**
     * 记录是否为全屏
     */
    private boolean isOnlyFullScreen;

    /**
     * 手势监听对象
     */
    private GestureDetector detector;

    /**
     * 锁定屏幕
     */
    private boolean lockScreen;


    /**
     * 计时器
     */
    private Timer timer = new Timer();

    /**
     * 计时器任务
     */
    private TimerTask timerTask;

    /**
     * 触屏屏幕的时间，用于隐藏屏幕
     */
    private Long laseTime;

    /**
     * 总的快进距离
     */
    private float scrollTotalDistance;

    /**
     * 当前位置
     */
    private float scrollCurrentPosition;

    /**
     * 当前音量
     */
    private float scrollCurrentVolume;

    /**
     * winddow管理器
     */
    private WindowManager wm;

    /**
     * 当前音量
     */
    private int currentVolume;

    /**
     * 最大音量
     */
    private int maxVolume;

    /**
     * 声音管理器
     */
    private AudioManager audioManager;

    /**
     * 声音
     */
    private GestureVolumePopWindow mVolumePopWindow;

    /**
     * 亮度
     */
    private GestureBrightnessPopWindow mBrightnessPopWindow;

    /**
     * 当前的播放错误
     */
    private float currentSpeed = 1.0f;


    public SkinController(@NonNull Context context) {
        super(context);
        init();
    }

    public SkinController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SkinController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SkinController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        initData();
        initView();
    }

    public void initData() {
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        this.mVolumePopWindow = new GestureVolumePopWindow(getContext());
        this.mBrightnessPopWindow = new GestureBrightnessPopWindow(getContext());
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumePopWindow.setProgress(currentVolume * 100 / maxVolume);
        detector = new GestureDetector(mActivity, new MyGesture());

    }

    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_skin_video, null);

        bufferProgressBar = view.findViewById(R.id.bufferProgressBar);
        iv_center_play = view.findViewById(R.id.iv_center_play);
        playerTopLayout = view.findViewById(R.id.playerTopLayout);
        backPlayList = view.findViewById(R.id.backPlayList);
        videoIdText = view.findViewById(R.id.videoIdText);
        iv_lock = view.findViewById(R.id.iv_lock);
        playerBottomLayout = view.findViewById(R.id.playerBottomLayout);
        iv_play = view.findViewById(R.id.iv_play);
        skbProgress = view.findViewById(R.id.skbProgress);
        playDuration = view.findViewById(R.id.playDuration);
        videoDuration = view.findViewById(R.id.videoDuration);
        tv_speed = view.findViewById(R.id.tv_speed);
        iv_fullscreen = view.findViewById(R.id.iv_fullscreen);
        rl_speed = view.findViewById(R.id.rl_speed);
        tv_speed_add = view.findViewById(R.id.tv_speed_add);
        tv_speed_show = view.findViewById(R.id.tv_speed_show);
        tv_speed_reduce = view.findViewById(R.id.tv_speed_reduce);

        iv_center_play.setOnClickListener(this);
        backPlayList.setOnClickListener(this);
        iv_lock.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        tv_speed.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        tv_speed_add.setOnClickListener(this);
        tv_speed_reduce.setOnClickListener(this);


        initSkbProgress();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        addView(view);

    }

    @Override
    public void onClick(View view) {
        laseTime = System.currentTimeMillis();//赋值
        int id = view.getId();
        if (id == R.id.iv_center_play) {
            if (mediaPlayer != null) mediaPlayer.start();
        } else if (id == R.id.backPlayList) {
            iv_fullscreen.performClick();
        } else if (id == R.id.iv_lock) {
            lockScreen = !lockScreen;
        } else if (id == R.id.iv_play) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } else if (id == R.id.iv_fullscreen) {
            setOnlyFullScreen(isPortrait());
        } else if (id == R.id.tv_speed_reduce) {
            addVideoSpeed(-ONE_STEP_SPEED);
        } else if (id == R.id.tv_speed_add) {
            addVideoSpeed(ONE_STEP_SPEED);
        }
    }

    /**
     * 初始化播放进度条
     * 控制进度条的调度
     */
    public void initSkbProgress() {
        skbProgress.setMax(MAX_PROGRESS_VALUE);
        skbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = (int) (progress * getDuration() / seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(progress);
            }
        });
    }

    @Override
    public void hide() {
    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void setAnchorView(View view) {

    }

//    @Override
//    public void setMediaPlayer(MediaPlayerControl player) {
//        this.mediaPlayer=player;
//    }

    @Override
    public void show(int timeout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                show();
            }
        }, timeout);
    }

    @Override
    public void show() {

    }

    @Override
    public void showOnce(View view) {

    }

    @Override
    public void startPlay() {
        iv_play.setImageResource(R.drawable.smallstop_ic);
        iv_center_play.setVisibility(INVISIBLE);
    }

    @Override
    public void pausePlay() {
        iv_play.setImageResource(R.drawable.smallbegin_ic);
        iv_center_play.setVisibility(VISIBLE);
    }

    /**
     * 设置进度条
     *
     * @param progress
     * @param duration
     */
    public void progressBarSeek(long progress, long duration) {
        int progressvalue = (int) (progress * MAX_PROGRESS_VALUE * 1.0 / duration);
        String positiontime = generateTime(progress);
        skbProgress.setProgress(progressvalue);
        playDuration.setText(positiontime);
    }

    /**
     * 设置媒体播放器
     *
     * @param player
     * @return
     */
    public SkinController setMediaPlayer(MediaPlayerControl player) {
        this.mediaPlayer = player;
        mediaPlayer.setMediaController(this);//控制监听
        return this;
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            bufferProgressBar.setVisibility(GONE);
            long duration = getDuration();
            long progress = getCurrentPosition();
            int position = (int) ((duration * progress * 1.0) / MAX_PROGRESS_VALUE);
            String durationtime = generateTime(duration);
            String positiontime = generateTime(position);
            videoDuration.setText(durationtime);
            playDuration.setText(positiontime);
            initTimerTask();
        }
    }

    /**
     * 设置路径
     *
     * @param path
     * @return
     */
    public SkinController setPath(String path) {
        this.videoPath = path;
        return this;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public SkinController setTitle(String title) {
        this.title = title;
        videoIdText.setText(title);
        return this;
    }

    /**
     * 设置宿主activity
     */
    public SkinController withActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }


    /**
     * 开始播放
     */
    public void startPlayer() {
        if (mediaPlayer == null) return;
        mediaPlayer.setVideoPath(videoPath);
        mediaPlayer.start();
    }


    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        long duration = mediaPlayer.getDuration();
        return duration;
    }

    /**
     * 时长格式化显示
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }


    /**
     * 消息处理机制
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                long progress = getCurrentPosition();
                long duration = getDuration();
                progressBarSeek(progress, duration);

                //判断有么有超过隐藏屏幕的时间
                if (System.currentTimeMillis() - laseTime > SKIN_HIDE_TIME) {
                    if (CURRENT_VIEW_TYPE != VIEW_ALL_HIDE) showSkinViewBytype(VIEW_ALL_HIDE);
                }
            }
        }
    };


    /**
     * 初始化更新进度条的时间循环任务
     */
    private void initTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        laseTime = System.currentTimeMillis();//赋值
        timer.schedule(timerTask, 0, 1000);
    }

    public void onPause() {
        mediaPlayer.pause();
        pausePosition = getCurrentPosition();
    }

    public void onResume() {
        mediaPlayer.onResume();
        mediaPlayer.seekTo(pausePosition);
    }


    /**
     * 销毁
     */
    public void onDestory() {
        if (timerTask != null) {
            timerTask.cancel();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        mediaPlayer.release(true);
    }

    /**
     * 设置全屏隐藏状态栏状态栏
     */
    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }


    public void setOnlyFullScreen(boolean isFull) {
        this.isOnlyFullScreen = isFull;
        setFullScreen(isOnlyFullScreen);
        if (isOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    /**
     * 获取屏幕方向
     * true是竖屏
     * false是横屏
     *
     * @return
     */
    private boolean isPortrait() {
        int mOrientation = getContext().getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取界面方向
     */
    public int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    /**
     * 监听屏幕方向状态
     *
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        showSkinViewBytype(VIEW_ALL_HIDE);
        iv_fullscreen.setImageResource(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? R.drawable.chgscreen_large : R.drawable.chgscreen_small);
    }

    /**
     * 根据状态，显示View的显示状态
     *
     * @param type
     */
    public void showSkinViewBytype(int type) {
        CURRENT_VIEW_TYPE = type;
        //任何模式下面都可以隐藏
        if (type == VIEW_ALL_HIDE) {
            playerBottomLayout.setVisibility(INVISIBLE);
            playerTopLayout.setVisibility(INVISIBLE);
            iv_lock.setVisibility(INVISIBLE);
            tv_speed.setVisibility(INVISIBLE);
        }

        if (lockScreen) {//如果是锁定屏幕，那么就显示
            iv_lock.setVisibility(type == VIEW_ALL_HIDE ? INVISIBLE : VISIBLE);
            return;
        }

        if (type == VIEW_ALL_SHOW) {
            if (!isPortrait()) {
                playerTopLayout.setVisibility(VISIBLE);
                rl_speed.setVisibility(VISIBLE);
            }
            iv_lock.setVisibility(VISIBLE);
            playerBottomLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mVolumePopWindow != null)
                    mVolumePopWindow.dismiss();
                if (mBrightnessPopWindow != null) {
                    mBrightnessPopWindow.dismiss();
                }
                break;
        }
        laseTime = System.currentTimeMillis();//赋值
        detector.onTouchEvent(event);//没有锁屏交给手势控制处理
        return true;
    }

    /**
     * 手势监听的一个内部类
     */
    private class MyGesture extends GestureDetector.SimpleOnGestureListener {
        private float mYDown;
        private float mXDown;
        private float mYMove;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isPortrait() && !lockScreen) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    parseVideoScroll(distanceX);
                } else {
                    if (ScreenUtils.isInRight(mActivity, (int) e1.getX())) {
                        parseAudioScroll(distanceY);
                    } else if (ScreenUtils.isInLeft(mActivity, (int) e1.getX())) {
                        this.mYMove = e2.getY();
                        int addtion = (int) (this.mYDown - this.mYMove) * 100 / ScreenUtils.getHeight(getContext());
                        parseBrighScroll(addtion);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            showSkinViewBytype(CURRENT_VIEW_TYPE == VIEW_ALL_HIDE ? VIEW_ALL_SHOW : VIEW_ALL_HIDE);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!lockScreen) iv_play.performClick();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            scrollTotalDistance = 0f;
            scrollCurrentPosition = (float) mediaPlayer.getCurrentPosition();
            scrollCurrentVolume = currentVolume;
            this.mXDown = e.getX();
            this.mYDown = e.getY();
            return super.onDown(e);
        }


    }

    /**
     * 加速装置
     *
     * @param speed
     */
    public void addVideoSpeed(float speed) {
        currentSpeed += speed;
        mediaPlayer.onSpeed(currentSpeed);
        tv_speed_show.setText(String.valueOf(currentSpeed));
    }


    //------------------------------------快进，快退-----------------------------------------

    /**
     * 调节播放进度
     *
     * @param distanceX
     */
    private void parseVideoScroll(float distanceX) {

        scrollTotalDistance += distanceX;

        float duration = (float) getDuration();

        float width = wm.getDefaultDisplay().getWidth() * 0.75f; // 设定总长度是多少，此处根据实际调整

        //右滑distanceX为负
        float currentPosition = scrollCurrentPosition - (float) duration * scrollTotalDistance / width;

        if (currentPosition < 0) {
            currentPosition = 0;
        } else if (currentPosition > duration) {
            currentPosition = duration;
        }

        mediaPlayer.seekTo((int) currentPosition);

        playDuration.setText(generateTime((long) currentPosition));
        int pos = (int) (skbProgress.getMax() * currentPosition / duration);
        skbProgress.setProgress(pos);
    }
    //------------------------------------快进，快退-----------------------------------------


    //------------------------------------音量，调节-----------------------------------------

    /**
     * 声音调节
     *
     * @param distanceY
     */
    private void parseAudioScroll(float distanceY) {
        if (isPortrait()) return;

        scrollTotalDistance += distanceY;

        float height = wm.getDefaultDisplay().getHeight() * 0.75f;
        // 上滑distanceY为正
        currentVolume = (int) (scrollCurrentVolume + maxVolume * scrollTotalDistance / height);

        if (currentVolume < 0) {
            currentVolume = 0;
        } else if (currentVolume > maxVolume) {
            currentVolume = maxVolume;
        }

        int flag = currentVolume * 100 / maxVolume;
        mVolumePopWindow.setProgress(flag);
        setVolume(flag);
        if (!mVolumePopWindow.isShowing() && !isPortrait()) {//没有显示且是横屏装太
            mVolumePopWindow.showPopWindow(this);
        }
    }

    public void setVolume(int percentage) {
        if (null == this.audioManager) {
            return;
        }

        if (percentage < 0 || percentage > 100) {
            return;
        }

        int maxValue = this.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentage * maxValue / 100, 0);
    }


    //------------------------------------音量，调节-----------------------------------------


    //------------------------------------亮度，调节-----------------------------------------

    /**
     * 使用的乐视亮度调节
     *
     * @param addtion
     */
    private int level = 0;// 记录popupwindow每次显示时候的初始值

    private int mCurrentBrightness = -1;

    private void parseBrighScroll(int addtion) {
        if (isPortrait()) return;
        if (!mBrightnessPopWindow.isShowing()) {
            mBrightnessPopWindow.showPopWindow(this);
            this.level = (getScreenBrightness(mActivity) * 100) / 255;
            mBrightnessPopWindow.setProgress(this.level);
            setScreenBrightness(mActivity, this.level);
        }
        int brightness = this.level + addtion;
        brightness = (brightness > 100 ? 100 : (brightness < 0 ? 0 : brightness));
        setScreenBrightness(mActivity, brightness * 255 / 100);
        mBrightnessPopWindow.setProgress(brightness);
    }

    /**
     * 设置亮度
     *
     * @param paramInt 取值0-255
     */
    public void setScreenBrightness(Activity activity, int paramInt) {
        this.mCurrentBrightness = paramInt;
        ScreenBrightnessManager.setScreenBrightness(activity, paramInt);

    }

    /**
     * 获取当前亮度(取值0-255)
     */
    public int getScreenBrightness(Activity activity) {
        if (this.mCurrentBrightness != -1) {
            return this.mCurrentBrightness;
        }
        return ScreenBrightnessManager.getScreenBrightness(activity);
    }

    //------------------------------------亮度，调节-----------------------------------------


}
