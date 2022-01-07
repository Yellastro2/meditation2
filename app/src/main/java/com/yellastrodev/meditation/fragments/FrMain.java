package com.yellastrodev.meditation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.yellastrodev.meditation.AllOneReceiver;
import com.yellastrodev.meditation.MainActivity;

import com.yellastrodev.meditation.yConst;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.money.meditation.affirmation.R;


public class FrMain extends iFragment {
	int LOW=0,MIDLE=1,HIGHT=2;
	
	int[] sImages = {R.drawable.img_red,R.drawable.ic_yellow,R.drawable.img_green},
	stext = {R.string.level_low,R.string.level_mid,R.string.level_height},
	sColors = {R.color.red,R.color.yellow,R.color.green},
	sTextColors = {R.color.redText,R.color.yellowText,R.color.greenText};

	private Calendar mCal = MainActivity.getDate();

	private TextView mvTestText,mvStatus;
	ImageView mvImageStatus,mvImageS;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View fView = inflater.inflate(R.layout.fr_main, null);
		mMain.sendAnality("main_screen");
		//FbFiles.initFB(getContext());
		mMain.disableBackBtn();
		mCal = mMain.getDate();
		
		mvStatus = fView.findViewById(R.id.fr_mainTextStatus);
		mvImageStatus = fView.findViewById(R.id.fr_mainImageMain);
		mvImageS = fView.findViewById(R.id.fr_mainImageDollar);

		fView.findViewById(R.id.fr_mainButAffirm)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FrSettAffirm(),true);
				}
			});
		
		fView.findViewById(R.id.fr_mainImageFaq)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FrFaq(),true);
				}
			});
		fView.findViewById(R.id.fr_mainButtonMedit)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mMain.getPreff().getBoolean(yConst.kSubs,false))
						mMain.openFrame(new FrMedit(),true);
					else
						mMain.openFrame(new FrPaywall(),true);
				}
			});
		fView.findViewById(R.id.fr_mainTextPolicy)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity( new Intent(Intent.ACTION_VIEW, 
					Uri.parse(yConst.sLinkPrivacy)));
					
				}
			});
		//if(BuildConfig.DEBUG)
		{
			fView.findViewById(R.id.fr_mainLinearTest)
			.setVisibility(View.VISIBLE);
			fView.findViewById(R.id.fr_mainButtestDefalt)
				.setOnClickListener(new View.OnClickListener() {
					@SuppressLint("WrongConstant")
					@Override
					public void onClick(View view) {
						mMain.getPreff().edit().clear()
						.apply();
						Toast.makeText(mMain,"Перезапустите прилку", Toast.LENGTH_LONG).show();
					}
				});
			fView.findViewById(R.id.fr_mainButtestplus)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mCal.set(Calendar.HOUR_OF_DAY,
								 mCal.get(Calendar.HOUR_OF_DAY)+1);
						setMeditDesc((TextView)fView.findViewById(R.id.fr_mainMeditDesc));
						Intent fBr  = new Intent(mMain, AllOneReceiver.class);
						fBr.putExtra("type",25);
						mMain.sendBroadcast(fBr);
					}
				});
			fView.findViewById(R.id.fr_mainButSetPlayed)
				.setOnClickListener(new View.OnClickListener() {
					@SuppressLint("WrongConstant")
					@Override
					public void onClick(View view) {
						mMain.setListened(getMeditation());
						setMeditDesc((TextView)fView.findViewById(R.id.fr_mainMeditDesc));
						Toast.makeText(mMain,"медитация отмечена прослушанной",Toast.LENGTH_LONG).show();
					}
				});
		}
		mvTestText = (TextView)fView.findViewById(R.id.fr_mainTestText);
		setMeditDesc((TextView)fView.findViewById(R.id.fr_mainMeditDesc));
		getMeditation();
		getStatus();

		return fView;
	}
	
	void getStatus(){
		int fToday = mCal.get(Calendar.DAY_OF_YEAR)+(mCal.get(Calendar.YEAR)*365);
		int fLastPlay = mMain.getPreff().getInt(yConst.isPlayToday, -5);
		int fLastSec = mMain.getPreff().getInt(yConst.ksecondLast, -5);
		Log.i(yConst.TAG,"today "+fToday+
				" last "+fLastPlay+" last2 "+fLastSec);
		if(fLastPlay==-5)
			setStatus(MIDLE);
		else if(fToday-fLastPlay>2)
			setStatus(LOW);
		else if(fToday-fLastSec>2)
			setStatus(MIDLE);
		else
			setStatus(HIGHT);
	}

	private void setStatus(int fSt) {
		mvStatus.setText(stext[fSt]);
		mvStatus.setTextColor(getResources().getColor(sTextColors[fSt]));
		mvImageStatus.setImageResource(
		sImages[fSt]);
		if(fSt==HIGHT)
			mvImageS.setVisibility(View.VISIBLE);
		else
			mvImageS.setVisibility(View.GONE);
	}

	private void setMeditDesc(TextView fView) {
		SimpleDateFormat fFormat= new SimpleDateFormat("dd.MM,hh:mm");
		int fPlayDay = mMain.getPreff().getInt(yConst.isPlayToday, -2);
		int fToday = mCal.get(Calendar.DAY_OF_YEAR)
				+(mCal.get(Calendar.YEAR)*365);
		Log.e(yConst.TAG,"playday: "+fPlayDay+", today: "+fToday);
		if(fPlayDay !=fToday){
		 String fLastPlay = mMain.getPreff().getString(yConst.lastPlay, "");
		 if(!fLastPlay.isEmpty()){
			 Log.e(yConst.TAG,"last play: "+fLastPlay);
			 fView.setText(R.string.main_btn_medit_refcompl);
			 String[] fData = fLastPlay.split(yConst.kDiv);
			 if(fData[fData.length-1].equals(""+
					mMain.getPreff().getInt(yConst.kCurentMedit, 0))){
				 generMedit();
			 }
		 } else
			 fView.setVisibility(View.GONE);
		} else {
			int fHour = mCal.get(Calendar.HOUR_OF_DAY);
			String fMsg;
			fMsg = getString(R.string.main_btn_medit_refresh);
			fMsg += " "+getTimeToEnd(mCal,mMain);

			fView.setText(fMsg);
		}
		mvTestText.setText(
			fFormat.format(mCal.getTime())+"\nAudio no "+
					(mMain.getPreff().getInt(yConst.kCurentMedit, 0)+1));
		getStatus();
	}

	public static String getTimeToEnd(Calendar fCal, Context fCtx){
		int fHour = fCal.get(Calendar.HOUR_OF_DAY);
		String fMsg ="";
		if(fHour<23){
			Log.e(yConst.TAG,"hour: "+fHour);
			fHour = 24-fHour;
			fMsg = fHour +" ";
			String fEnd = fCtx.getString(R.string.hours);
			if (fHour == 1 || fHour == 21)
				fEnd = fCtx.getString(R.string.hours1);
			else if(fHour>21||fHour<5)
				fEnd = fCtx.getString(R.string.hours2);
			fMsg += fEnd;
		}else {
			int fMin = fCal.get(Calendar.MINUTE);
			fMin = 60 - fMin;
			fMsg = fMin + " ";
			String fEnd = fCtx.getString(R.string.minutes);
			int fMinDigit = fMin - ((int) (fMin / 10) * 10);
			if (fMinDigit == 1)
				fEnd = fCtx.getString(R.string.minutes1);
			else if (fMinDigit > 0 && fMinDigit < 5 && !(fMin > 10 && fMin < 15))
				fEnd = fCtx.getString(R.string.minutes2);
			fMsg += fEnd;
		}
		return fMsg;
	}
	
	int getMeditation(){
		
		
		int fMedit = mMain.getPreff().getInt(yConst.kCurentMedit, -1);
		if(fMedit <0){
			fMedit = generMedit();
		}
		
		return fMedit;
	}
	
	int generMedit(){
		int fMedit = 0;
		String fLastPlay = mMain.getPreff().getString(yConst.lastPlay, "");
		
		int fLastSec = mMain.getPreff().getInt(yConst.lastSeq, -1);
		
		fLastSec++;
		if(fLastSec>=yConst.sMainSequence.length)
			fLastSec=0;
		fMedit = yConst.sMainSequence[fLastSec]-1;
		
		mMain.getPreff().edit()
			.putInt(yConst.lastSeq,fLastSec)
			.putInt(yConst.kCurentMedit,fMedit)
			.apply();

		/*
		if(!fLastPlay.isEmpty()) {
			String[] fData = fLastPlay.split(yConst.kDiv);
			if(fData.length-1<yConst.kMeditations.length)
				fMedit = fData.length-1;
			else{
				fMedit = ((int)
					(Math.random() * yConst.kMeditations.length));
				boolean is = true;
				while(is){
					is = false;
					for (int i = 1; i < yConst.kUnique+1; i++) {
						if(fData[fData.length-i].equals(""+fMedit))
							is = true;
					}
					fMedit++;
					if(fMedit>=fData.length)
						fMedit = 0;
				}
			}
		}
		mMain.getPreff().edit().putInt(yConst.kCurentMedit,fMedit)
			.apply();*/
		return fMedit;
	}
}
