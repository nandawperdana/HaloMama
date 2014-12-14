package it.fhab.halomama;

import it.fhab.halomama.roboto.RobotoTextView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Desc1Fragment extends Fragment {

	public Desc1Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_desc1, container,
				false);
		RobotoTextView textView = (RobotoTextView) rootView
				.findViewById(R.id.section_label1);
		textView.setText(Html.fromHtml(getString(R.string.fragment1_desc)));
		return rootView;
	}
}
