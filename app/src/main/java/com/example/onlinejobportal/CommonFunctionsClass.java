package com.example.onlinejobportal;

import android.util.Patterns;

public class CommonFunctionsClass {


    public static boolean isEmailValid(CharSequence charSequence){
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }

}
