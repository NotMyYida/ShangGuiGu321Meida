package com.huake.huakemedia.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huake.huakemedia.R;
import com.huake.huakemedia.base.BasePager;
import com.huake.huakemedia.pager.AudioPager;
import com.huake.huakemedia.pager.NetAudioPager;
import com.huake.huakemedia.pager.NetVideoPager;
import com.huake.huakemedia.pager.VideoPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.activity_main_framelayout)
    FrameLayout mActivityMainFramelayout;
    @InjectView(R.id.activity_main_radionGroup)
    RadioGroup mActivityMainRadionGroup;
    @InjectView(R.id.titlebar_tv_search)
    TextView mTitlebarTvSearch;

    private List<BasePager> mBasePagers;
    public int checkPosition;
    //储存点击的时间
    private long preTime;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //针对6.0适配权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
//            增加权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }else{
            //有权限当然就不需要管理啦
        }

        mBasePagers = new ArrayList<BasePager>();
        mBasePagers.add(new VideoPager(this));//添加本地视频页面-0
        mBasePagers.add(new AudioPager(this));//添加本地音乐页面-1
        mBasePagers.add(new NetVideoPager(this));//添加网络视频页面-2
        mBasePagers.add(new NetAudioPager(this));//添加网络音频页面-3

        //设置监听，默认选择第一个
        mActivityMainRadionGroup.setOnCheckedChangeListener(new RadioGroupListener());
        mActivityMainRadionGroup.check(R.id.rb_video);
        //点击事件
        mTitlebarTvSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titlebar_tv_search:
                //跳转到搜索界面
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_audio:
                    checkPosition = 1;
                    break;
                case R.id.rb_netvideo:
                    checkPosition = 2;
                    break;
                case R.id.rb_netaudio:
                    checkPosition = 3;
                    break;
                default:
                    checkPosition = 0;
                    break;
            }
            setFrament();
        }
    }

    //添加碎片的方法
    public void setFrament() {

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.activity_main_framelayout, new Fragment(
        ) {
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                if (basePager != null) {
                    return basePager.rootView;
                }
                return null;
            }


        });

        fragmentTransaction.commit();
    }

    public BasePager getBasePager() {
        BasePager basePager = mBasePagers.get(checkPosition);
        if (basePager != null && !basePager.isInitData) {
            basePager.isInitData = true;
            basePager.initData();
        }
        return basePager;
    }

    @Override
    public void onBackPressed() {
        if (checkPosition != 0) {
            checkPosition = 0;
            setFrament();
            mActivityMainRadionGroup.check(R.id.rb_video);
            return;
        }
        if (System.currentTimeMillis() - preTime > 2000) {
            preTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission success", Toast.LENGTH_SHORT).show();

            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
