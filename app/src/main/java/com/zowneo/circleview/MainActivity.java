package com.zowneo.circleview;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private ViewPager viewPager;
    private int[] imageResIds;
    private ArrayList<ImageView> imageViewList;
    private LinearLayout ll_point_container;
    private String[] contentDescs;
    private TextView tv_desc;
    private int previousSelectedPositon = 0;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初识化布局 View 视图
        initViews();

        // Model 数据
        initData();

        // Controller 控制器
        initApdater();

        // 开启轮询
        new Thread() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 往下跳一位
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("设置当前位置： " + viewPager.getCurrentItem());
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    // 初始化视图
    private void initViews() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(1); // 左右各保留几个对象
        ll_point_container = findViewById(R.id.ll_point_container);
        tv_desc = findViewById(R.id.tv_desc);
    }

    // 初识化要显示的数据
    private void initData() {
        // 图片资源要显示的数组
        imageResIds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};

        // 文本描述
        contentDescs = new String[]{
                "第一个图片",
                "第二个图片",
                "第三个图片",
                "第四个图片",
                "第五个图片"
        };

        // 初始化要显示的 ImageView
        imageViewList = new ArrayList<ImageView>();

        ImageView imageView;
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < imageResIds.length; i++) {
            // 初始化要显示的图片对象
            imageView = new ImageView(this);
            imageView.setBackgroundResource(imageResIds[i]);
            imageViewList.add(imageView);

            // 加小白点指示器
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.selector_bg_point);
            layoutParams = new LinearLayout.LayoutParams(25, 25);
            if ((i+1) != 0) {
                Log.e("MainActivity", "i: " + i);
                layoutParams.leftMargin = 20;
            // 设置默认所有都不可用
                pointView.setEnabled(false);
                ll_point_container.addView(pointView, layoutParams);
            }
        }
    }

    // 控制器
    private void initApdater() {
        ll_point_container.getChildAt(0).setEnabled(true);
        tv_desc.setText(contentDescs[0]);
        previousSelectedPositon = 0;

        // 设置适配器
        viewPager.setAdapter(new MyAdapter());

        // 默认设置到中间的某个位置
        int pos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageViewList.size());
//        System.out.println("默认设置到中间的某个位置" + (1073741823 - (1073741823 % 3)));
        // 2147483647 / 2 = 1073741823 - (1073741823 % 3)
        viewPager.setCurrentItem(5000000);
    }

    // 自定义适配器
    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        // 指定复用的判断逻辑
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            System.out.println("isViewFromObject: " + (view == object));
            // 当划到新的条目，又返回来，view 是否可以被复用
            // 返回判断规则
            return view == object;
        }

        // 返回要显示的条目内容，创建条目
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            System.out.println("instantiateItem: " + position);
            // container: 容器：ViewPager
            // position：当前条目要显示的位置 0 -> 2

            // newPosition = position % 3
            int newPosition = position  % imageViewList.size();

            ImageView imageView = imageViewList.get(newPosition);
            // a. 把 View 对象添加到 container 中
            container.addView(imageView);
            // b. 把 View 对象返回给框架，适配器
            return imageView; // 必须重写，否则报错
        }

        // 销毁条目
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // object 要销毁的对象
            System.out.println("destroyItem: " + position);
            container.removeView((View) object);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 滚动时调用
    }

    @Override
    public void onPageSelected(int position) {
        // 新的条目被选中时调用
//        Log.e("MainActivity", "position: " + position);
        int newPosition = position % imageViewList.size();

        // 设置文本
        tv_desc.setText(contentDescs[newPosition]);


        // 记录之前的位置
        // 把之前的禁用，把之前的启用，更新指示器
//        Log.e("MainActivity", "previousSelectedPositon: " + previousSelectedPositon);
        ll_point_container.getChildAt(previousSelectedPositon).setEnabled(false);
        ll_point_container.getChildAt(newPosition).setEnabled(true);
        previousSelectedPositon = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 滚动状态变化时调用
    }
}
