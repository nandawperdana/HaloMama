package com.nandanu.halomama;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;

import com.nandanu.halomama.roboto.RobotoEditText;

/**
 * A placeholder fragment containing a first description.
 */
@SuppressLint("NewApi")
public class MamaFragment extends Fragment {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * widgets
	 */
	private String namaMama, mentionTeman, fileVideoPath;
	private RobotoEditText etNamaMama, etMentionTeman;
	private ImageButton buttonLanjut;
	private ImageButton btnBatalkan;

	/*
	 * vars
	 */
	private SharedPreferences pref;
	private Uri uriPath;

	public MamaFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upload_mama,
				container, false);
		pref = getActivity().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);

		Bundle bundle = this.getArguments();
		fileVideoPath = bundle.getString("VIDEO_PATH", "");
		uriPath = bundle.getParcelable("VIDEO_URI");

		/*
		 * widgets init
		 */
		etNamaMama = (RobotoEditText) rootView
				.findViewById(R.id.editTextNamaMama);
		etMentionTeman = (RobotoEditText) rootView
				.findViewById(R.id.editTextMention);
		btnBatalkan = (ImageButton) rootView
				.findViewById(R.id.imageButtonBatalkan2);
		buttonLanjut = (ImageButton) rootView.findViewById(R.id.buttonLanjut);
		
		/*
		 * listeners
		 */
		btnBatalkan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), DescActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			}
		});

		buttonLanjut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub
				namaMama = etNamaMama.getText().toString();
				mentionTeman = etMentionTeman.getText().toString();

				Fragment feeling = new FeelingFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable("VIDEO_URI", uriPath);
				bundle.putString("VIDEO_PATH", fileVideoPath);
				bundle.putString("NAMA_MAMA", namaMama);
				bundle.putString("NAMA_TEMAN", mentionTeman);
				feeling.setArguments(bundle);

				FragmentTransaction ft = getActivity().getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.pager_upload, feeling);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		return rootView;
	}
}
