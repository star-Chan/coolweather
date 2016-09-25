package com.coolweather.app.activity;

import com.coolweather.app.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;


public class WelcomeActivity extends Activity {
	
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);
		imageView=(ImageView)findViewById(R.id.image_view);
		ObjectAnimator animator=ObjectAnimator.ofFloat(imageView, "alpha", 0f,1f);
		animator.setDuration(3000);
		animator.start();
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				Intent intent=new Intent(WelcomeActivity.this, ChooseAreaActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
