package com.nandanu.halomama;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nandanu.halomama.roboto.RobotoTextView;

public class Desc2Fragment extends Fragment {

	public Desc2Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_desc2, container,
				false);
		RobotoTextView textView = (RobotoTextView) rootView
				.findViewById(R.id.section_label2);
		textView.setText(Html.fromHtml(getString(R.string.fragment2_desc)));
		return rootView;
	}
}
