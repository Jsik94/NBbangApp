package com.kosmo.nbbangapp.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespItem {
    @SerializedName("resp_code")
    String resp_code;



    public RespItem() {
    }
}
