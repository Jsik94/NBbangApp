package com.kosmo.nbbangapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kosmo.nbbangapp.fragments.ChartFragment;
import com.kosmo.nbbangapp.fragments.HabitFragment;
import com.kosmo.nbbangapp.fragments.HomeFragment;
import com.kosmo.nbbangapp.fragments.MainFragment;
import com.kosmo.nbbangapp.fragments.ProfileFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements  DuoMenuView.OnMenuClickListener {

    private static final String TAG = "CHECKER";
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    private ArrayList<String> mTitles = new ArrayList<>();
    private DuoDrawerToggle duoDrawerToggle;
    private AnimatedBottomBar bottomBar;
    private boolean islogin =false;
    private Button bt_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getHashKey();
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String autoLogin = sharedPreferences.getString("autoLogin","No");
        if(autoLogin.equals("No")){
            Intent intent = new Intent(this,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }


        bt_log  = findViewById(R.id.nav_footer_btn);
//        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
//        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.menuitem_background); //왼쪽 상단 버튼 아이콘 지정
//
//        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();


        // Handle toolbar actions
        handleToolbar();
        // Handle menu actions
        handleMenu();
        // Handle drawer actions
        handleDrawer();



        goToFragment(new MainFragment(),false);
        mMenuAdapter.setViewSelected(0,true);
        setTitle(mTitles.get(0));

        bt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.nav_footer_btn)).setText(!islogin ?"Log IN" : "Log Out");
                islogin=!islogin;
                Toast.makeText(getApplicationContext(),"로그인 아웃 이걸로",Toast.LENGTH_SHORT).show();
//                duoDrawerToggle.onDrawerClosed((DrawerLayout)findViewById(R.id.drawer));
                mViewHolder.mDuoDrawerLayout.closeDrawer();

                //전면수정할것
                //https://developers.google.com/identity/sign-in/android/disconnect


            }
        });

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {

            //i1이 인덱스임 i는 무슨값인지 확인 필요
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                Toast.makeText(getApplicationContext(),"이건뭐고"+i+"이건 뭘까"+i1,Toast.LENGTH_SHORT).show();
                switch (i1){
                    case 0:
                        mViewHolder.mDuoDrawerLayout.openDrawer();
                        bottomBar.selectTabAt(2,true);
                        break;

                    case 1:
//                        goToFragment();
                        break;

                    case 2:
                        goToFragment(HomeFragment.newInstance(),false);
                        break;

                    case 3:
                        break;

                    case 4:
                        goToFragment(ProfileFragment.newInstance(),false);
                        break;


                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {
                //Not work
            }



        });

    }

    private void handleDrawer() {
        duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }


    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);
        // Navigate to the right fragment
        Log.d(TAG, "onOptionClicked: ");
        switch (position) {

            case 0:
                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(HomeFragment.newInstance(),true);
                break;
            case 1:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(ProfileFragment.newInstance(),false);
                break;

            case 2:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(HabitFragment.newInstance(),false);
                break;

            case 3:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(ChartFragment.newInstance(),false);
                break;

            default:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(new MainFragment(), false);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private void goToFragment(Fragment mainFragment, boolean backstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (backstack){
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container,mainFragment).commit();
    }


    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
//            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }


    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }




}