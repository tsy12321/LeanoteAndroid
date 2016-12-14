package com.tsy.leanote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tsy.leanote.base.BaseActivity;
import com.tsy.leanote.feature.user.interactor.UserInteractor;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInteractor userInteractor = new UserInteractor();

        TextView txt_hello = (TextView) findViewById(R.id.txt_hello);
        txt_hello.setText("Hello " + userInteractor.getCurUser());
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
