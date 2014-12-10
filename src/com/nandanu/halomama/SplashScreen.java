package com.nandanu.halomama;

import com.nandanu.halomama.roboto.RobotoTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;

public class SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;

	private RobotoTextView splashText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_done_upload);

		splashText = (RobotoTextView) findViewById(R.id.textViewSplashLogo);
		splashText.setText(Html.fromHtml(getString(R.string.splash)));
		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreen.this, DescActivity.class);
				startActivity(i);

				// close this activity
				SplashScreen.this.finish();
			}
		}, SPLASH_TIME_OUT);

	}
}
