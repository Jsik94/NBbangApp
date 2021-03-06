package com.kosmo.nbbangapp;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kosmo.nbbangapp.info.MyUrl;
import com.kosmo.nbbangapp.models.MemberItem;
import com.kosmo.nbbangapp.models.RespItem;
import com.kosmo.nbbangapp.retrofit.LoginService;
import com.kosmo.nbbangapp.sign.SignUpActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import com.kakao.usermgmt.UserManagement;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "CHECKER";
    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private Button btn_signup, btn_signin;
    private EditText tv_email, tv_pw;
    private SessionCallback sessionCallback;
    Session session;
    private boolean result;
    private AlertDialog dialog;


    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "" + e.getStatusCode());
            updateUI(null);
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInButton = findViewById(R.id.sign_in_button);
        btn_signup = findViewById(R.id.btn_signup);
        btn_signin = findViewById(R.id.btn_signin);
        tv_email = findViewById(R.id.edt_signin_email);
        tv_pw = findViewById(R.id.edt_signin_password);
        sessionCallback = new SessionCallback();
        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        session.checkAndImplicitOpen();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail(getString(R.string.default_web_client_id_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("type", "general");
                startActivity(intent);
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MyUrl.NBBANGBaseURL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();
                LoginService loginService = retrofit.create(LoginService.class);
                MemberItem param = new MemberItem();
                param.setEmail(tv_email.getText().toString());
                param.setPassword(tv_pw.getText().toString());
                Call<RespItem> call = loginService.isGeneralMember(param);
                call.enqueue(new Callback<RespItem>() {
                    @Override
                    public void onResponse(Call<RespItem> call, Response<RespItem> response) {

                        if (response.isSuccessful()) {
                            Log.d(TAG, response.body().getResp_code() + "");
                            if (response.body().getResp_code().equals("success")) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("type", "general");
                                intent.putExtra("email", param.getEmail());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplication(), "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<RespItem> call, Throwable t) {
                        Toast.makeText(getApplication(), "??????????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {

        Toast.makeText(getApplicationContext(), "??????????????? UI???" + account, Toast.LENGTH_SHORT).show();
        //??????????????? ?????? ??????
        if (account != null) {
            Toast.makeText(getApplicationContext(), "" + account.getEmail(), Toast.LENGTH_SHORT).show();
//            isMember(account.getEmail());
            //????????? ????????? googleSignInAccount ????????? ?????????
            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("autoLogin", "Yes");
            editor.commit();

//            Intent intent = new Intent(this, MainActivity.class);
////            intent.putExtra("account",account);
//            intent.putExtra("type","google");
//            startActivity(intent);
//            finish();

            socialProcess(account.getEmail(), account.getDisplayName(), "google");
        }
    }

    private void updateUI(Intent intent, MeV2Response result) {
//        isMember(result.getKakaoAccount().getEmail());
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoLogin", "Yes");
        editor.commit();

        socialProcess(result.getKakaoAccount().getEmail(), result.getKakaoAccount().getLegalName(), "kakao");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);


    }


    private void socialProcess(String email, String name, String type) {

        //?????? ???????????? ????????? ??????
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyUrl.NBBANGBaseURL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        LoginService loginService = retrofit.create(LoginService.class);
        Call<RespItem> call = loginService.isMember(email);

        dialog = new AlertDialog.Builder(SignInActivity.this)
                .setCancelable(false)
                .setView(R.layout.progress_layout)
                .create();

        dialog.show();

        call.enqueue(new Callback<RespItem>() {
            @Override
            public void onResponse(Call<RespItem> call, Response<RespItem> response) {

                dialog.dismiss();
                if (response.isSuccessful()) {

                    RespItem data = response.body();
                    Log.d(TAG, "onResponse: " + data.getResp_code());
                    if (data.getResp_code().equals("success")) {
                        result = true;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        dialog = new AlertDialog.Builder(SignInActivity.this)
                                .setTitle("????????? ???????????????!")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage("??????????????? ???????????????????????? ? ")
                                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //--> ok -> ?????? ????????? type google or kakao ?????? ????????????
                                        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                        intent.putExtra("type", type);
                                        intent.putExtra("email", email);
                                        intent.putExtra("name", name);

                                        startActivity(intent);

                                    }
                                }).setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (type.equals("kakao")) {
                                            signOutKakao();
                                        } else {
                                            //?????? ????????? ??????
                                            signOutGoogle();
                                        }
                                    }
                                }).create();

                        dialog.show();


                    }

//                    Toast.makeText(getApplicationContext(),"??????????????? : "+data.getResp_code() , Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Fail  :" + response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<RespItem> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    private void signOutKakao() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

//                Toast.makeText(getApplicationContext(),"????????? ????????????",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOutGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail(getString(R.string.default_web_client_id_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(getApplicationContext(),"???????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class SessionCallback implements ISessionCallback {

        // ???????????? ????????? ??????
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // ???????????? ????????? ??????
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // ????????? ?????? ??????
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "????????? ?????? ??????: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            int result = errorResult.getErrorCode();

                            if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "????????? ?????? ????????? ??????????????????: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onSuccess(MeV2Response result) {
                            Log.i("KAKAO_API", "????????? ?????????: " + result.getId());
//                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//                            intent.putExtra("email", result.getKakaoAccount().getEmail());
//                            intent.putExtra("name", result.getKakaoAccount().getLegalName());

                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {

                                // ?????????
                                String email = kakaoAccount.getEmail();

                                if (email != null) {
                                    Log.i("KAKAO_API", "email: " + email);

                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ??????
                                    // ???, ?????? ????????? ???????????? ????????? ????????? ?????? ???????????? ????????? ????????? ????????? ???????????? ???????????? ?????????.

                                } else {
                                    // ????????? ?????? ??????
                                }

                                // ?????????
                                Profile profile = kakaoAccount.getProfile();

                                if (profile != null) {
                                    Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                    Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ?????? ??????

                                } else {
                                    // ????????? ?????? ??????
                                }
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                ;
                                intent.putExtra("email", kakaoAccount.getEmail());
                                updateUI(intent, result);

                            }


                        }
                    });
        }

    }

}
