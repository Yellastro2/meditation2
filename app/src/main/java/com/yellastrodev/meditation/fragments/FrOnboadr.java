package com.yellastrodev.meditation.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.money.meditation.affirmation.R;


public class FrOnboadr extends iFragment {
	
	int[] sHeads = {R.string.onboard1_head,R.string.onboard2_head,
		R.string.onboard3_head},
	sBodys = {R.string.onboard1_body,R.string.onboard2_body,
		R.string.onboard3_body},
	sImgs = {R.drawable.img_hum,R.drawable.img_money,
		R.drawable.img_time};

	TextView mvHeadText,mvQuestBody;
	ImageView mvImage;
	
	int mPage = 0;

	private View mView;
	private boolean isDisable=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_onboard, null);
		mvHeadText = fView.findViewById(R.id.fr_quest_textHead);
		mvQuestBody = fView.findViewById(R.id.fr_quest_bodytext);
		mvImage = fView.findViewById(R.id.fr_onboardImageView);
		
		
		
		fView.findViewById(R.id.fr_onboardButton)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mPage++;
					if(!isDisable)
					next(mPage);
					
				}
			});
		mView = fView;
		next(mPage);
		
		return fView;
	}
    
    void next(int fPage){
		if(fPage<sHeads.length){
			mMain.sendAnality("onbord_"+(fPage+1));
			mMain.respireView(mView, new Runnable(){
					@Override
					public void run() {
						if(mPage>=sHeads.length)
							return;
						mvHeadText.setText(sHeads[fPage]);
						mvQuestBody.setText(sBodys[fPage]);
						mvImage.setImageResource(sImgs[fPage]);
						if(mPage==2){
							mvImage.setScaleX(0.75f);
							mvImage.setScaleY(0.75f);
						}
					}
				});
		
		}else {
			isDisable = true;
			mMain.openFrame(new FrQuest(), false);
		}
	}
    
}
