package whb.com.ijklibrary.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * PopupWindow基类
 */
public abstract class BasePopupWindow {
    public Context context;

    public BasePopupWindow(Context context) {
        this.context = context;
        init();
    }

    protected PopupWindow popupWindow;

    protected abstract void init();

    protected abstract int getPopHeight(View anchor);

    protected abstract int getPopWidth(View anchor);

    protected abstract View getPopContentView();

    /**
     * 隐藏
     */
    public void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 是否正在显示
     *
     * @return true 正在显示
     * false 未显示
     */
    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    /**
     * 展示
     *
     * @param anchor 绑定view
     */
    public void show(View anchor) {
        if (popupWindow == null) {
            int width = getPopWidth(anchor);
            if (width <= 0) {
                width = anchor.getWidth();
            }
            popupWindow = new PopupWindow(getPopContentView(), width, getPopHeight(anchor));
            popupWindow.setBackgroundDrawable(null);
            popupWindow.setOutsideTouchable(true); //点击外部隐藏切换码率菜单
            popupWindow.setFocusable(true);
        }
        int[] location = new int[2];
        anchor.getLocationInWindow(location);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());
    }

}
