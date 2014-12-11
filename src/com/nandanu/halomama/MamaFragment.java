package com.nandanu.halomama;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.nandanu.halomama.roboto.RobotoEditText;

/**
 * A placeholder fragment containing a first description.
 */
public class MamaFragment extends Fragment {

	// widgets
	private String namaMama, mentionTeman;
	private RobotoEditText etNamaMama, etMentionTeman;
	private Button buttonLanjut;
	private ImageButton btnBatalkan;

	private SharedPreferences pref;

	public MamaFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upload_mama,
				container, false);
		pref = getActivity().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);

		etNamaMama = (RobotoEditText) rootView
				.findViewById(R.id.editTextNamaMama);
		etMentionTeman = (RobotoEditText) rootView
				.findViewById(R.id.editTextMention);
		btnBatalkan = (ImageButton) rootView
				.findViewById(R.id.imageButtonBatalkan3);
		btnBatalkan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
		buttonLanjut = (Button) rootView.findViewById(R.id.buttonLanjut);

		buttonLanjut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				namaMama = etNamaMama.getText().toString();
				mentionTeman = etMentionTeman.getText().toString();

				Fragment feeling = new FeelingFragment();
				Bundle bundle = new Bundle();
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
