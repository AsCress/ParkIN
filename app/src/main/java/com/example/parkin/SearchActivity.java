package com.example.parkin;

import androidx.appcompat.app.AppCompatActivity;
import android.transition.Fade;

import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Fade fade = new Fade();

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

    }
}