package com.nandanu.halomama;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ShareTwitterActivity extends Activity {
	private Button btnShareTwitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_twitter);

		btnShareTwitter = (Button) findViewById(R.id.buttonShareTwitter);
		btnShareTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// share to twitter
			}
		});
	}

}
