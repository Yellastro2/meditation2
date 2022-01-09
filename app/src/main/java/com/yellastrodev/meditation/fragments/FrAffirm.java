package com.yellastrodev.meditation.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.money.meditation.affirmation.R;
import com.yellastrodev.meditation.AllOneReceiver;
import com.yellastrodev.meditation.yConst;

import java.io.IOException;
import java.io.InputStream;

public class FrAffirm extends iFragment {

		
		int[] sImages = {R.drawable.image_1,R.drawable.image_2};

		ScrollView mScrollView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_affirm, null);

		mMain.mvBackBtn.setVisibility(View.GONE);
		
		mMain.sendAnality("push_affirmation_open"+
		(AllOneReceiver.isTestpus?"_test":""));
		
		fView.findViewById(R.id.fr_affirmImageBack)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FrMain(),false);
				}
			});
			
		fView.findViewById(R.id.fr_affirmImageSett)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FrSettAffirm(),false);
				}
			});
		String fData = mMain.getString(R.string.affirm_list);
		String[] fAffList = fData.split("\\.");

		String fBody = fAffList[AllOneReceiver.sAffirm];
		
		((TextView)fView.findViewById(R.id.fr_affirmTextView))
		.setText(fBody);
		
		loadImg(fView);
		return fView;
	}
    
    
    void loadImg(View fView){
		String [] list;
		try {
			list = mMain.getAssets().list("");
			Log.i(yConst.TAG,"asset size - "+list.length);
			if (list.length > 0) {

				// This is a folder
				int fImg = (int)(Math.random() * list.length);
				
				InputStream ims = mMain.getAssets().open(list[fImg]);
				// load image as Drawable
				Drawable d = Drawable.createFromStream(ims, null);
				// set image to ImageView
				fView.setBackground(d);
				
				//fView.setBackgroundResource(sImages[fImg]);
				
			} 
		} catch (IOException e) {
			e.printStackTrace();
			return ;
	}}
}
