package com.yellastrodev.meditation.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.yellastrodev.meditation.AllOneReceiver;

import com.yellastrodev.meditation.yConst;
import android.content.DialogInterface;
import android.os.Handler;
import com.money.meditation.affirmation.R;

public class FrSettAffirm extends iFragment {

	LinearLayout mvSeekLegend;
	SeekBar mvSeekBar;
	View mvOnPush;

	private SharedPreferences mPreff;

	private String isFirstAffirm;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_settaffirm, null);
		
		mPreff = mMain.getPreff();
		
		boolean fEnabled = mPreff.getBoolean(yConst.isAffirm, true);
		int fFreq = mPreff.getInt(yConst.kFreq, yConst.sDefaultFreq);

		//mScrollView = fView.findViewById(R.id.fr_faqScrollView);
		mvSeekLegend = fView.findViewById(R.id.fr_affirmLinSeekbarLegend);
		mvSeekBar = fView.findViewById(R.id.fr_affirmSeekPrg);
    
    	initSeekLegend();
		mvSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

				private View mvItemSelect;

				private int GRAY;
				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
					View fVItem = mvSeekLegend.getChildAt(p2);
					
					GRAY = getResources().getColor(R.color.greyText);
					
					((TextView)fVItem.findViewById(R.id.it_seeklegendText))
						.setTextColor(Color.BLACK);
					fVItem.findViewById(R.id.it_seekLegView).setVisibility(View.INVISIBLE);
					if(mvItemSelect!=null){
					((TextView)mvItemSelect.findViewById(R.id.it_seeklegendText))
						.setTextColor(GRAY);
					mvItemSelect.findViewById(R.id.it_seekLegView).setVisibility(View.VISIBLE);
					}
					mvItemSelect = fVItem;
					
					mPreff.edit().putInt(yConst.kFreq,p2+1).apply();
						mMain.sendAnality("affirmation_frequency_"+(p2+1));
				}
				@Override
				public void onStartTrackingTouch(SeekBar p1) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar p1) {
				}
			});
		mvSeekBar.setProgress(fFreq-1);
		
		mvOnPush= fView.findViewById(R.id.fr_settaffirmTextOnPush);
		
		fView.findViewById(R.id.fraffirmButton1)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AllOneReceiver.showAffirmNoty(mMain,true);
					//mMain.sendAnality("send_test_push");
					mMain.inspireView(mvOnPush);
					new Handler().postDelayed(new Runnable(){
							@Override
							public void run() {
								mMain.despireView(mvOnPush, new Runnable(){
										@Override
										public void run() {
										}
									});
							}
						}, 5000);
				}
			});
			
		Switch fvSwitch = fView.findViewById(R.id.fr_settaffirmSwitch);
		fvSwitch.setChecked(fEnabled);
		fvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2) {
					mPreff.edit().putBoolean(yConst.isAffirm,p2).apply();
					mMain.sendAnality("affirmation_push_"+(p2?"enabled":"disabled"));
				}
			});
			
		fView.findViewById(R.id.fr_affirmTextAsk)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					showDial();
				}
			});
			
		if(mPreff.getBoolean(isFirstAffirm,true))
			showDial();
		mPreff.edit().putBoolean(isFirstAffirm,false).apply();
		mMain.sendAnality("affirmation_settings_screen");
		
    	return fView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onBackPress() {
		getFragmentManager().popBackStack();
		mMain.openFrame(new FrMain(),false);
		return false;
	}
	
	
	
	
	void initSeekLegend(){
		
		mvSeekLegend.removeAllViews();
		int fMax = mvSeekBar.getMax()+1;
		LinearLayout.LayoutParams fParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
																		 LinearLayout.LayoutParams.WRAP_CONTENT,
																		 1);
		for (int i = 0; i < fMax; i++) {
			View qView = LayoutInflater.from(mMain)
				.inflate(R.layout.it_seekbarlegend, null);
			qView.setLayoutParams(fParam);
			((TextView)qView.findViewById(R.id.it_seeklegendText))
			.setText((i+1)+"");
			mvSeekLegend.addView(qView);
		}
	}
	
	void showDial(){
		AlertDialog.Builder fBld = new AlertDialog.Builder(mMain);

		View fView = getLayoutInflater().inflate(
			R.layout.dial_text, null);

		fBld.setView(fView);
		final AlertDialog fDial = fBld.create();

		
        fDial.getWindow().getDecorView().setSystemUiVisibility(
			mMain.getWindowFlags());
		fDial.setOnDismissListener(new DialogInterface.OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface p1) {
					
					mMain.hideNavTab();
				}
			});
		fDial.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		fView.findViewById(R.id.dialtextImageView1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					fDial.dismiss();
				}
			});
		((TextView)fView.findViewById(R.id.dial_textTextView))
			.setText(R.string.affir_dial_head);
		((TextView)fView.findViewById(R.id.dial_textTextView2))
			.setText(getString(R.string.affir_dial_text));
		fDial.show();
		
		
	}
}
