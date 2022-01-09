package com.yellastrodev.meditation.fragments;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.icu.number.Scale;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.money.meditation.affirmation.R;
import com.yellastrodev.meditation.AllOneReceiver;
import com.yellastrodev.meditation.yConst;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FrAffirm extends iFragment {

		
		int[] sImages = {R.drawable.image_1,R.drawable.image_2};

		ScrollView mScrollView;
	private String kAffirmRandom = "affirmrandom";
	private String kIndex = "indexfromstr";

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
		
		loadImg(fView.findViewById(R.id.fr_affirmimg));
		return fView;
	}
    
    
    void loadImg(ImageView fView){
		String [] list;
		try {
			list = mMain.getAssets().list("");
			String fStr = mMain.getPreff().getString(kAffirmRandom,"");
			int fIndexFromStr =0;
			int fSize = list.length;
			if(fStr.isEmpty()){

				List<Integer> fArr = new ArrayList<>();
				for (int i= 0;i<fSize;i++){
					fArr.add(i);
				}
				for (int i= 0;i<fSize;i++){
					int qInd = (int)(Math.random()*fArr.size());
					fStr += fArr.get(qInd)+",";
					fArr.remove(qInd);
				}
				fStr.substring(0,fStr.length()-2);
				Log.i(yConst.TAG,"affirm set: "+fStr);
				mMain.getPreff().edit().putString(kAffirmRandom,fStr).apply();
			}else{
				fIndexFromStr = mMain.getPreff().getInt(kIndex,0);
			}
			String[] fArray = fStr.split(",");
			String fIndStr = fArray[fIndexFromStr];
			int fIndRandom = Integer.parseInt(fIndStr);
			fIndexFromStr++;
			if(fIndexFromStr>=fSize)
				fIndexFromStr = 0;
			mMain.getPreff().edit().putInt(kIndex,fIndexFromStr).apply();
			Log.i(yConst.TAG,"asset size - "+list.length);
			if (list.length > 0) {

				// This is a folder
				int fImg = fIndRandom;
				
				InputStream ims = mMain.getAssets().open(list[fImg]);
				// load image as Drawable
				Drawable d = Drawable.createFromStream(ims, null);
				// set image to ImageView

				fView.setImageDrawable(d);
				
				//fView.setBackgroundResource(sImages[fImg]);
				
			} 
		} catch (IOException e) {
			e.printStackTrace();
			return ;
	}}
}
