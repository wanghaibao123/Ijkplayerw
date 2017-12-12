package whb.com.ijklibrary.widget;

import android.net.Uri;
import android.widget.MediaController;

import java.net.URI;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by whb on 2017/12/4.
 */

/**
 * 皮肤控制播放器
 * 播放器需要继承这个借口方便外部调用
 */
public interface MediaPlayerControl extends MediaController.MediaPlayerControl{
    /**
     * 设置播放路径 String
     * @param path
     */
    void setVideoPath(String path);

    /**
     * 设置间听过器
     * @param controller
     */
    void setMediaController(IMediaController controller);

    /**
     * 设置播放路径
     * @param uri
     */
    void setVideoURI(Uri uri);

    /**
     * 设置播放信息监听
     * @param l
     */
    void setOnInfoListener(IMediaPlayer.OnInfoListener l);

    /**
     * 释放资源
     */
    void release(boolean cleartargetstate);

    /**
     * 屏幕失去焦点
     */
    void onPause();

    /**
     * 重新获得焦点
     */
    void onResume();

    /**
     * 加速
     */
    void onSpeed(float speed);
}
