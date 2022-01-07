package com.yellastrodev.meditation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.yellastrodev.meditation.MainActivity;

public class iFragment extends Fragment {

	public MainActivity mMain;

	public boolean onBackPress() {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMain = (MainActivity) getActivity();
	}
    
    
    
}
