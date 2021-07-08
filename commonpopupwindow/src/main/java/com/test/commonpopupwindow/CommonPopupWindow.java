package com.test.commonpopupwindow;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.widget.PopupWindowCompat;


/*
 *
 * time:2021-07-08
 *
 * Builder设计模式
 * 使用步骤：1.new CommonPopupWindow
 *          2.获取Builder getBuilder()
 *          3.builder.setAnim()...设置参数
 *          4.builder.setAnim().. .create().show()显示弹窗( create()返回CommonPopupWindow )
 *
 * 补充：CommonPopupWindow.setListeners()与findViewById()是绑定监听与绑定View
 *
 * */
public class CommonPopupWindow extends PopupWindow{

    private Builder P;

    private View mView;

    public enum HorizontalPosition {LEFT,RIGHT,ALIGN_LEFT,ALIGN_RIGHT,CENTER}//左，右，左对齐，右对齐，居中
    public enum VerticalPosition{ABOVE,BELOW,ALIGN_TOP, ALIGN_BOTTOM,CENTER}//上，下，上对齐，下对齐，居中


    /**
     * 弹出窗口的构造方法,宽高参数默认为WRAP_CONTENT
     * @param layout:自定义的弹出布局
     * @param view:弹出所依附的View
     */
    public CommonPopupWindow(int layout, View view){
        this(layout,view,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 弹出窗口的构造方法,高的参数默认为WRAP_CONTENT
     * @param layout:自定义的弹出布局
     * @param view:弹出所依附的View
     * @param width:弹出布局的宽度(ViewGroup.LayoutParams.WRAP_CONTENT/MATCH_PARENT 或是 一个正值)
     */
    public CommonPopupWindow(int layout, View view, int width){
        this(layout,view,width,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 自行指定宽高
     * @param height:弹出布局的高度(ViewGroup.LayoutParams.WRAP_CONTENT/MATCH_PARENT 或是 一个正值)
     */
    public CommonPopupWindow(int layout, View view, int width, int height){
        super(LayoutInflater.from(view.getContext()).inflate(layout, null),
                dip2px(view.getContext(),width), dip2px(view.getContext(),height), true);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        getContentView().measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        this.mView = view;
    }



    public Builder getBuilder(){
        P = new Builder();
        return P;
    }

    public void show(){
        PopupWindowCompat.showAsDropDown(this, mView, P.offsetX+P.horizontalExcursion,
                P.offsetY+P.verticalExcursion, Gravity.START);
    }


    public <T extends View> T findViewById(int view){//绑定View
        if (getContentView() != null)
            return getContentView().findViewById(view);
        else
            return null;
    }

    public void setListeners(View.OnClickListener listener,int...ids){//设置View的按钮监听
        if (getContentView() == null) {
            throw new RuntimeException("错误：mContentView 为 null,即所依附View不存在...");
        }
        for (int id : ids) {
            View view = getContentView().findViewById(id);
            view.setOnClickListener(listener);
        }
    }

    /**
     * 给整个弹出窗口里所有的View设置点击事件监听
     * @param listener 设置View的点击事件监听回调接口
     */
    public void setItemClickListener(View.OnClickListener listener){
        setItemClickListener(getContentView(),listener);
    }

    /**
     * 指定设置ViewGroup下所有view的点击事件监听
     * @param view 本质是ViewGroup layout，下面包含多个View或ViewGroup
     * @param listener 点击事件监听回调
     */
    public void setItemClickListener(View view, View.OnClickListener listener) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i=0;i<childCount;i++){
                //不断的递归给里面所有的View设置OnClickListener
                View childView = viewGroup.getChildAt(i);
                setItemClickListener(childView,listener);
            }
        }else if (view instanceof TextView){
            view.setOnClickListener(listener);
        }
    }


    /**
     * Builder设计模式
     */
    public class Builder{

        private Builder(){}

        int horizontalExcursion = 0,verticalExcursion = 0;//水平偏移，垂直偏移
        int anim = -1;//-1是默认动画，0是无动画
        int offsetX = -1;
        int offsetY = -1;
        float alpha = 0f;//1透明，0不透明
        boolean shadowState = false;//背景灰色状态
        boolean touchable = true;//点击窗口外，是否可以取消
        Activity activity;
        OnDismissListener listener = null;//窗口销毁回调监听
        HorizontalPosition horizontalPosition = HorizontalPosition.CENTER;
        VerticalPosition verticalPosition = VerticalPosition.CENTER;


        /**
         * 设置水平垂直的偏移量（平移）
         * 单位都是像素，必须配合dip2px（）使用
         * @param horizontalExcursion：窗口水平的偏移量 正数右偏
         * @param verticalExcursion：窗口垂直的偏移量 正数下移
         */
        public Builder setExcursion(int horizontalExcursion,int verticalExcursion){
            this.horizontalExcursion = horizontalExcursion;
            this.verticalExcursion = verticalExcursion;
            return this;
        }


        /**
         * 设置水平垂直的偏移量（平移）
         * 单位都是dp，不必须配合dip2px（）使用
         * @param horizontalExcursion：窗口水平的偏移量 正数右偏
         * @param verticalExcursion：窗口垂直的偏移量 正数下移
         */
        public Builder setExcursionDip(int horizontalExcursion,int verticalExcursion){
            this.horizontalExcursion = dip2px(mView.getContext(),horizontalExcursion);
            this.verticalExcursion = dip2px(mView.getContext(),verticalExcursion);
            return this;
        }

        /**
         * 设置动画，默认是-1，默认动画
         * @param styles：设置为0是无动画
         */
        public Builder setAnim(int styles){
            this.anim = styles;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener){//弹窗退出销毁
            this.listener = listener;
            return this;
        }

        /**
         * 基于依附View的位置状态 （比如：弹窗在依附View水平方向的左边，垂直方向的上边）
         * @param horizontalState：水平的位置状态 LEFT,RIGHT,ALIGN_LEFT,ALIGN_RIGHT,CENTER 左，右，左对齐，右对齐，居中
         * @param verticalState：垂直的位置状态 ABOVE,BELOW,ALIGN_TOP, ALIGN_BOTTOM,CENTER 上，下，上对齐，下对齐，居中
         */
        public Builder setPopupWindowsPosition(HorizontalPosition horizontalState,VerticalPosition verticalState){
            this.horizontalPosition = horizontalState;
            this.verticalPosition = verticalState;
            return this;
        }

        /**
         * 点击窗口外，是否可以取消
         * @param touchable：true 可以取消，false 不可以
         */
        public Builder setTouchable(boolean touchable){
            this.touchable = touchable;
            return this;
        }

        /**
         * 设置背景透明度
         * @param activity：必须是activity
         * @param alpha：透明度，1透明，0不透明
         */
        public Builder setShadow(Activity activity,float alpha){
            this.shadowState = true;
            this.activity = activity;
            this.alpha = alpha;
            return this;
        }

        /**
         *最后的组装，必须调用（使用完成后，调用show（））
         */
        public CommonPopupWindow create(){
            switch (horizontalPosition){
                case RIGHT:
                    offsetX = mView.getWidth();
                    break;
                case LEFT:
                    offsetX = -getPopupWindow().getContentView().getMeasuredWidth();
                    break;
                case CENTER:
                    offsetX = getCoefficient(mView.getWidth(),getPopupWindow().getContentView().getMeasuredWidth())*Math.abs(getPopupWindow().getContentView().getMeasuredWidth()-mView.getWidth()) / 2;
                    break;
                case ALIGN_LEFT:
                    offsetX = 0;
                    break;
                case ALIGN_RIGHT:
                    offsetX = getCoefficient(mView.getWidth(),getPopupWindow().getContentView().getMeasuredWidth())*Math.abs(getPopupWindow().getContentView().getMeasuredWidth()-mView.getWidth());
                    break;
                    default:
                        break;
            }

            switch (verticalPosition){
                case CENTER:
                    offsetY = -mView.getHeight()-(getPopupWindow().getContentView().getMeasuredHeight()-mView.getHeight())/2;
                    break;
                case ABOVE:
                    offsetY = -(getPopupWindow().getContentView().getMeasuredHeight()+mView.getHeight());
                    break;
                case BELOW:
                    offsetY = 0;
                    break;
                case ALIGN_TOP:
                    offsetY = -mView.getHeight();
                    break;
                case ALIGN_BOTTOM:
                    offsetY = - getPopupWindow().getContentView().getMeasuredHeight();
                    break;
                    default:
                        break;
            }

            getPopupWindow().setTouchable(touchable);

            getPopupWindow().setOnDismissListener(listener);

            getPopupWindow().setAnimationStyle(anim);

            if (shadowState && activity != null){
                Window window = activity.getWindow();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.alpha = this.alpha;
                window.setAttributes(layoutParams);
            }


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                //小于Android 8.0的 需要再次设置WRAP_CONTENT，否则部分手机会抛出异常
                getPopupWindow().setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            return getPopupWindow();
        }

        private int getCoefficient(int num1,int num2){
            return num1>num2?1:-1;
        }

    }


    private CommonPopupWindow getPopupWindow(){
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (P.activity != null) {
            final Window window = P.activity.getWindow();
            final WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = 1f;
            window.setAttributes(layoutParams);
        }
    }

    @SuppressWarnings("ResourceType")
    private int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (dpValue < 0) return (int) dpValue;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
