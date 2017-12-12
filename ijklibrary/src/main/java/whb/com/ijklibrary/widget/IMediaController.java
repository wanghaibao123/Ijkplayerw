/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package whb.com.ijklibrary.widget;

import android.view.View;
import android.widget.MediaController;

/**
 * 视频播放控制皮肤接口
 * 外部皮肤需要继承这个接口，rang'kong'q
 */

public interface IMediaController {

    /**
     * 隐藏标题栏
     */
    void hide();

    /**
     * 判断是否显示
     */
    boolean isShowing();

    /**
     * 设置主播界面
     */
    void setAnchorView(View view);

    /**
     * 设置是否可用
     */
    void setEnabled(boolean enabled);

    /**
     * 设置媒体播放器
     */
    //void setMediaPlayer(MediaPlayerControl player);

    /**
     * 显示带超时时间
     */
    void show(int timeout);

    /**
     * 显示标题栏
     */
    void show();

    //----------
    // Extends
    //----------
    void showOnce(View view);

    /**
     * 开始播放回调
     */
    void startPlay();

    /**
     * 暂停播放回调
     */
    void pausePlay();
}
