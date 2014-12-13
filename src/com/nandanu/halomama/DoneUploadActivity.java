package com.nandanu.halomama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class DoneUploadActivity extends Activity {
	/*
	 * widgets
	 */
	private ImageButton btnDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_done_upload);

		btnDone = (ImageButton) findViewById(R.id.buttonDone);
		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DoneUploadActivity.this,
						StreamActivity.class);
				startActivity(i);
			}
		});
	}

}
