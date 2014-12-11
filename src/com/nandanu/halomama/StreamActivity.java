package com.nandanu.halomama;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nandanu.halomama.roboto.RobotoTextView;

public class StreamActivity extends Activity {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);
	private ImageButton btnProfile, btnPlayVid;
	private RobotoTextView tvUsername, tvRetweet, tvSeen;
	private ImageView imageUser, imageShare;
	private Dialog dialog;
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stream);
		pref = getApplicationContext().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);

		tvUsername = (RobotoTextView) findViewById(R.id.textViewUsername);
		tvRetweet = (RobotoTextView) findViewById(R.id.textViewRetweet);
		tvSeen = (RobotoTextView) findViewById(R.id.textViewSeen);
		imageShare = (ImageView) findViewById(R.id.imageViewShare);
		imageUser = (ImageView) findViewById(R.id.imageViewUserImage);
		btnPlayVid = (ImageButton) findViewById(R.id.buttonPlayVideo);
		btnProfile = (ImageButton) findViewById(R.id.buttonUserImage);

		tvUsername.setText("@" + pref.getString("USERNAME", ""));

		btnPlayVid.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		btnProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.video_dialog);

				RobotoTextView tvRekamUlang = (RobotoTextView) findViewById(R.id.textViewRekamUlang);
				RobotoTextView tvGantiAkun = (RobotoTextView) findViewById(R.id.textViewGantiAkun);
				RobotoTextView tvShareMedia = (RobotoTextView) findViewById(R.id.textViewShareMedia);

				tvRekamUlang.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								RecordActivity.class);
						startActivity(i);

					}
				});

				tvGantiAkun.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								DescActivity.class);
						startActivity(i);

					}
				});

				tvShareMedia.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								RecordActivity.class);
						startActivity(i);
					}
				});
			}
		});
	}
}
