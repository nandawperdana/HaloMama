package it.fhab.halomama;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class UploadActivity extends FragmentActivity {

	// shared preferences
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload);

		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
		Intent i = getIntent();
		String fileVideoPath = i.getStringExtra("VIDEO_PATH");
		String fileImagePath = i.getStringExtra("IMAGE_PATH");
		Uri uriPath = i.getParcelableExtra("VIDEO_URI");

		Fragment mama = new MamaFragment();
		Bundle bundle = new Bundle();
		bundle.putString("VIDEO_PATH", fileVideoPath);
		bundle.putString("IMAGE_PATH", fileImagePath);
		bundle.putParcelable("VIDEO_URI", uriPath);
		mama.setArguments(bundle);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.pager_upload, mama);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent i = new Intent(getApplicationContext(), RecordActivity.class);

		i.addCategory(Intent.CATEGORY_HOME);
		// closing all the activity
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// add new flag to start new activity
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
	}
}
