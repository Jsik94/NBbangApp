package com.kosmo.nbbangapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import lombok.val;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kosmo.nbbangapp.fragments.ChartFragment;
import com.kosmo.nbbangapp.fragments.HabitFragment;
import com.kosmo.nbbangapp.fragments.HomeFragment;
import com.kosmo.nbbangapp.fragments.MainFragment;
import com.kosmo.nbbangapp.fragments.ProfileFragment;
import com.kosmo.nbbangapp.info.MyUrl;
import com.kosmo.nbbangapp.models.MemberItem;
import com.kosmo.nbbangapp.retrofit.LoginService;

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
    private Toolbar toolbar;
    private TextView toolBarTitle,sidebarEmail,sidebarNickname;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private SharedPreferences sharedPreferences;
    private String autoLogin,email;
    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getHashKey();
        sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
        autoLogin = sharedPreferences.getString("autoLogin","No");
        loginCheck(autoLogin);
        //getData
//        user_account =  GoogleSignIn.getLastSignedInAccount(this);
//        Toast.makeText(getApplicationContext(),"account : " + user_account.getEmail(),Toast.LENGTH_SHORT).show();


        //init
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();


        //Binding
        bt_log  = findViewById(R.id.nav_footer_btn);
        toolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        toolBarTitle = findViewById(R.id.toolBarTitle);
        sidebarNickname = findViewById(R.id.nav_header_text_title);
        sidebarEmail = findViewById(R.id.nav_header_text_subtitle);


//        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.menuitem_background); //왼쪽 상단 버튼 아이콘 지정
//        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
//        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        retrofit = new Retrofit.Builder()
                .baseUrl(MyUrl.NBBANGBaseURL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        LoginService service = retrofit.create(LoginService.class);

        Call<MemberItem> memberInfo = service.getMember(getIntent().getStringExtra("email"));

        memberInfo.enqueue(new Callback<MemberItem>() {
            @Override
            public void onResponse(Call<MemberItem> call, Response<MemberItem> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "getMember Result"+response.body().getName());
                    MemberItem resp = response.body();
                    sidebarNickname.setText(resp.getNickname());
                    sidebarEmail.setText(resp.getEmail());
                }
            }

            @Override
            public void onFailure(Call<MemberItem> call, Throwable t) {
                Log.d(TAG, "getMember onFailure: "+t.getMessage());
                signOutProcess();
            }
        });




        initToolbar();
        // Handle toolbar actions
        handleToolbar(false);
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
                islogin=!islogin;
//                duoDrawerToggle.onDrawerClosed((DrawerLayout)findViewById(R.id.drawer));
                mViewHolder.mDuoDrawerLayout.closeDrawer();
                signOutProcess();

            }
        });

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {

            //i1이 인덱스임 i는 무슨값인지 확인 필요
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
//                Toast.makeText(getApplicationContext(),"이건뭐고"+i+"이건 뭘까"+i1,Toast.LENGTH_SHORT).show();
                switch (i1){
                    case 0:
                        mViewHolder.mDuoDrawerLayout.openDrawer();
                        bottomBar.selectTabAt(2,true);
                        break;

                    case 1:
                        break;

                    case 2:
                        goToFragment(new MainFragment(),false);
                        handleToolbar(false);
                        break;

                    case 3:
                        break;

                    case 4:
                        //프로필

                        Bundle bundle = new Bundle();
                        bundle.putString("email",getIntent().getStringExtra("email"));
                        goToFragment(ProfileFragment.newInstance(),false,bundle);
                        mTitles.get(1);
                        handleToolbar(true);
                        break;


                    default:
                        goToFragment(ProfileFragment.newInstance(),false);
                        handleToolbar(true);

                        break;


                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {
                //Not work
            }



        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("정말로 나가시겠습니까?")
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        signOut();
                    }
                });
    }

    private void signOutProcess(){
        if(getIntent().getStringExtra("type").equals("google")){
            Toast.makeText(getApplicationContext(),"구글 로그아웃",Toast.LENGTH_SHORT).show();
            //https://developers.google.com/identity/sign-in/android/disconnect
            signOut();
        }else{
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Toast.makeText(getApplicationContext(),"카카오 로그아웃",Toast.LENGTH_SHORT).show();
                }
            });
        }
        autoLogin = "No";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoLogin",autoLogin);
        editor.commit();
        loginCheck(autoLogin);
    }


    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail(getString(R.string.default_web_client_id_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"로그인이 해제되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginCheck(String autoLogin) {
        if(autoLogin.equals("No")){
            Intent intent = new Intent(this,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }

    }


    private void initToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() <1){
                    getOutApp();
                }else{
                    goToBeforeFragment();
                }
            }
        });
    }

    private void getOutApp() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }

    //프래그먼트가 2개이상 쌓였을 시, 앞에 프레그먼트를 제거하여 뒤로가기 구현
    private void goToBeforeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(getSupportFragmentManager().getFragments().get(0)).commit();


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

    private void handleToolbar(boolean toggle) {

        if(toggle){
            getSupportActionBar().show();

        }else{
            getSupportActionBar().hide();
        }
    }


    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {


        setCustomTitle(mTitles.get(position));
        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);
        // Navigate to the right fragment
        Log.d(TAG, "onOptionClicked: ");
        switch (position) {

            case 0:
                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(HomeFragment.newInstance(),true);
                handleToolbar(true);
                break;
            case 1:
                //프로필
                Log.d(TAG, "onOptionClicked: "+position);
                bottomBar.selectTabAt(4,true);
                Bundle bundle = new Bundle();
                bundle.putString("email",getIntent().getStringExtra("email"));
                goToFragment(ProfileFragment.newInstance(),false,bundle);
                handleToolbar(true);
                break;

            case 2:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(HabitFragment.newInstance(),false);
                handleToolbar(true);
                break;

            case 3:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(ChartFragment.newInstance(),false);
                handleToolbar(true);
                break;

            default:

                Log.d(TAG, "onOptionClicked: "+position);
                goToFragment(new MainFragment(), false);
                handleToolbar(false);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private void setCustomTitle(String s) {

        toolBarTitle.setText(s);
    }

    public void goToFragment(Fragment mainFragment, boolean backstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (backstack){
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container,mainFragment).commit();
    }

    public void goToFragment(Fragment mainFragment,boolean backstack, Bundle data){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (backstack){
            transaction.addToBackStack(null);
        }
        mainFragment.setArguments(data); //Name 변수 값 전달. 반드시 setArguments() 메소드를 사용하지 않으면, 받는 쪽에서 null 값으로 받음.
        transaction.replace(R.id.container, mainFragment).commit(); //프레임 레이아웃에서 프레그먼트 1로 변경(replace)해라

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