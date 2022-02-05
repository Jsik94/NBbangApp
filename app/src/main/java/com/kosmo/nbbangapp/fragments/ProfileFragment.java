package com.kosmo.nbbangapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kosmo.nbbangapp.R;
import com.kosmo.nbbangapp.info.MyUrl;
import com.kosmo.nbbangapp.models.MemberItem;
import com.kosmo.nbbangapp.retrofit.LoginService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ProfileFragment extends Fragment {

    private static final String TAG = "CHECKER-Profile";
    private View view;
    private Retrofit retrofit;
    private TextView txt_email,txt_name,txt_nickname,txt_phone,txt_gender;
    private ImageButton ibtn_account;
    private Intent myIntent;
    private String email;


    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,container,false);
        Bundle bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.

        if(bundle != null){
            email = bundle.getString("email"); //Name 받기.
        }

        txt_email = view.findViewById(R.id.tv_profile_email);
        txt_name = view.findViewById(R.id.tv_profile_name);
        txt_nickname = view.findViewById(R.id.tv_profile_nickname);
        txt_phone = view.findViewById(R.id.tv_profile_phone);
        txt_gender = view.findViewById(R.id.tv_profile_gender);
        ibtn_account =view.findViewById(R.id.imgbtn_profile_account);

        retrofit = new Retrofit.Builder()
                .baseUrl(MyUrl.NBBANGBaseURL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        LoginService service = retrofit.create(LoginService.class);

        Call<MemberItem> call =service.getMember(email);

        call.enqueue(new Callback<MemberItem>() {
            @Override
            public void onResponse(Call<MemberItem> call, Response<MemberItem> response) {
                if(response.isSuccessful()){
                    MemberItem resp = response.body();

                    txt_email.setText(resp.getEmail());
                    txt_name.setText(resp.getName());
                    txt_nickname.setText(resp.getNickname());
                    txt_phone.setText(resp.getTel());
                    txt_gender.setText(resp.getGender().equals("man")? "남자":"여자");

                }else{

                    Log.i(TAG, "onResponse: fail : "+ response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<MemberItem> call, Throwable t) {
                Log.i(TAG, "onFailure: ");
            }
        });


        ibtn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }


}
