package com.yellastrodev.meditation.fragments;

import static com.yellastrodev.meditation.yConst.kTimePause;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yellastrodev.meditation.FbFiles;
import com.yellastrodev.meditation.MainActivity;
import com.yellastrodev.meditation.MyService;

import com.yellastrodev.meditation.yConst;
import java.util.Calendar;
import com.money.meditation.affirmation.R;


public class FrMedit extends iFragment {
	
	ImageView mvCloud1,mvCloud2;
	View mvPlayBtn,mvPauseBtn;
	TextView mvTime,mvCompl1,mvCompl2;
	ProgressBar mvProgr;

	private Matrix mMatrix1;

	private Matrix mMatrix2;

	private float mCenter;

	private boolean isServiceStart;

	private Thread mThread;

	private boolean mIsplay;

	private boolean mBound;
	private MyService mService;

	private SharedPreferences mPreff;

	private static final String isFirst = "ismeditfirst";

	private int mMedit;

	private boolean isFinish = false;
	private boolean isListened;
	
	private boolean onClb;
	Runnable mClb = new Runnable(){
	@Override
	public void run() {
		onClb = true;
		mProgressDial.dismiss();
	}
	};

	private AlertDialog mProgressDial;

    private String kPausedMedit = "pausedmedit";

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_medit, null);
		try{
		isSaveTime = true;
		mPreff = mMain.getPreff();
		mMedit = mMain.getPreff().getInt(yConst.kCurentMedit, 0);
		int fPlayDay = mMain.getPreff().getInt(yConst.isPlayToday, -2);
		int fToday = mMain.getDate().get(Calendar.DAY_OF_YEAR);
		if(fPlayDay !=fToday){
			isListened = false;
		}
		int fState = mMain.mFB.sStates[mMedit];
		if(fState!=yConst.STATE_READY){
			if (mMain.mFB.sStates[mMedit] != yConst.STATE_LOADING) {
				mMain.mFB.getAudio(mMain, mMedit,mClb );
			}else{
				mMain.mFB.addClb(mMedit,mClb);
			}
			
			showLoadDialog();
		}
		
		/*Calendar fCal = MainActivity.getDate();
		int fFirstDay= mMain.getPreff().getInt(yConst.kFirstDay,-1);
		int fToday2 = fCal.get(fCal.DAY_OF_YEAR)+
				(fCal.get(fCal.YEAR)*365);
		if(fFirstDay==-1) {
			mMain.getPreff().edit()
					.putInt(yConst.kFirstDay,fToday2);
			fToday2 = 1;
		}else{
			fToday2 = fToday2-fFirstDay+1;
		}*/
			mMain.sendAnality("meditation_screen");
		mvPauseBtn = fView.findViewById(R.id.fr_meditImagePause);
		mvPlayBtn = fView.findViewById(R.id.fr_meditImagePlay);
		mvTime = fView.findViewById(R.id.fr_meditTextTime);
		mvProgr = fView.findViewById(R.id.fr_meditSeekPrg);
		mvCloud1 = fView.findViewById(R.id.fr_meditImageCloud1);
		mvCloud2 = fView.findViewById(R.id.fr_meditImageCloud2);
		mvCompl1 = fView.findViewById(R.id.fr_meditTextComplHead);
		mvCompl2 = fView.findViewById(R.id.fr_meditTextComlBody);

		
		
		OnClickListener fOnPlay = new OnClickListener(){
			@Override
			public void onClick(View p1) {
				onPlayClick();
			}
		};
		
		mvPlayBtn.setOnClickListener(fOnPlay);
		mvPauseBtn.setOnClickListener(fOnPlay);
		mvPauseBtn.setVisibility(View.GONE);
		
		mMatrix1 = new Matrix();
		mMatrix2 = new Matrix();
		int fMargin = 50;
		DisplayMetrics metrics = new DisplayMetrics();
		mMain.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float fW;
		fW = metrics.widthPixels;
		float fBtmWidth = fW - (fMargin*2);
		int fDrawWidth = getResources().getDrawable(R.drawable.cloud1).getIntrinsicWidth();
		fMargin = (int)((fW - fDrawWidth)/2);
		//getResources().getDimensionPixelSize(R.dimen.imgmain_size);
		//fMargin = (int)((fW-fBtmWidth)/2);
		ViewGroup.LayoutParams fparam= ((RelativeLayout)mvCloud1.getParent()).getLayoutParams();
		fparam.width = (int)fW;
		fparam.height = (int)fW+100;
		mvCloud1.setLayoutParams(fparam);
		mvCloud2.setLayoutParams(fparam);
		
		float fScale = fW / fBtmWidth;
		//mMatrix1.setScale(fScale, fScale);
		mMatrix1.setTranslate(fMargin,fMargin);
		//mMatrix2.setScale(fScale,fScale);
		mMatrix2.setTranslate(fMargin,fMargin);
		
		mvCloud1.setImageMatrix(mMatrix1);
		mvCloud2.setImageMatrix(mMatrix2);
		
		mCenter = fW/2;

		
		if(mPreff.getBoolean(isFirst,true))
			showDial();
		mPreff.edit().putBoolean(isFirst,false).apply();
		
	}catch(Exception e){mMain.sendException(e);}
		return fView;
	}

	@Override
	public boolean onBackPress() {
		try{
		if(isServiceStart||!isFinish){
			showExitDial();
			return false;
		}
		}catch(Exception e){mMain.sendException(e);}
		return super.onBackPress();
		
	}

	boolean isSaveTime = true;

	@Override
	public void onPause() {
		try{
			Toast.makeText(mMain,"onpause",Toast.LENGTH_LONG);
		super.onPause();
		if(isServiceStart)
		{
			Toast.makeText(mMain,"isservice",Toast.LENGTH_LONG);
			if(mService!=null){
				Toast.makeText(mMain,"isserv2",Toast.LENGTH_LONG);
				int fPerc= mService.getProgressPercs();
				if(isSaveTime){
					Toast.makeText(mMain,"savetime",Toast.LENGTH_LONG);
				    long fProg = mService.getProgressTime();
				    mPreff.edit().putLong(kTimePause,fProg)
                            .putInt(kPausedMedit,mMedit).apply();
                }else {
                    mPreff.edit().putLong(kTimePause,0)
                            .putInt(kPausedMedit,-1).apply();

					Toast.makeText(mMain,"break",Toast.LENGTH_LONG);
                    fPerc = ((int) (fPerc / 20)) * 20;
					Toast.makeText(mMain,"break at "+fPerc,Toast.LENGTH_LONG);
                    mMain.sendAnality("meditation_listen_" +
							(mMedit+1) + "_" +
                            fPerc);
                }
			}
			totalPause();
		}
		}catch(Exception e){mMain.sendException(e);}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isServiceStart)
		{
		totalPause();
		}
	}
	
	

	private void totalPause() {
		try{
		if(isServiceStart)
		{
			mMain.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		isSpin = false;
		pauseRotate();
		mThread.interrupt();
		//mThread.stop();
		Intent fIntent = new Intent(mMain, MyService.class);
		mMain.stopService(fIntent);
		mMain.unbindService( connection);
		mBound = false;
		mIsplay = false;
			mvPlayBtn.setVisibility(View.VISIBLE);
			mvPauseBtn.setVisibility(View.GONE);
		isServiceStart = false;
		}
		}catch(Exception e){mMain.sendException(e);}
	}
	
	void onPlayClick()
	{
		try{
			
		if(mIsplay)
		{
			mService.pausePlayer();
			mIsplay = false;
			isSpin = false;
			pauseRotate();
			mvPlayBtn.setVisibility(View.VISIBLE);
			mvPauseBtn.setVisibility(View.GONE);
		}else
		{
			if(mBound) 
			{
				mService.play();
			}else
			{initPlayer();
				int fLastSec = mMain.getPreff().getInt(yConst.lastSeq, 0)+1;
				mMain.sendAnality("meditation_play_day" + fLastSec);
			}
			mIsplay = true;
			startRotate();
			mvPlayBtn.setVisibility(View.GONE);
			mvPauseBtn.setVisibility(View.VISIBLE);
		}
			/*List fLs = null;
			fLs.get(5);*/
		}catch(Exception e){mMain.sendException(e);}
	}

	void initPlayer()
	{
		try{
					Intent fIntent = new Intent(mMain, MyService.class);
					fIntent.putExtra(yConst.kCurentMedit,mMedit);
					if(mPreff.getInt(kPausedMedit,-1)==mMedit){
					    fIntent.putExtra(kTimePause,mPreff.getLong(kTimePause,0));
                    }
					mMain.startService(fIntent);
					mMain.bindService(fIntent, connection, Context.BIND_AUTO_CREATE);
					isServiceStart=true;

			mMain.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mThread = new Thread(){

				public void run()  
						{
							try{
							int fProg = 0;
							while(fProg<100)
							{
								if(fProg>yConst.kFirstProcent&&!isListened)
								{
									mMain.setListened(mMedit);
									isListened = true;
								}
								try {
									Thread.currentThread().sleep(1000);
								} catch (InterruptedException e) {}
								if(interrupted())
									return;
								fProg = mService.getProgressPercs();
								int fTimeMils = mService.getProgressTime()/1000;
								final Calendar fTimeCal = Calendar.getInstance();
								fTimeCal.setTimeInMillis(fTimeMils);
								final String fTimeStr = String.format("%02d:%02d", 
																(fTimeMils % 3600) / 60, (fTimeMils % 60));

								final int fFinProg = fProg;
								mvPlayBtn.post(new Runnable(){
										@Override
										public void run() {
											if(isFinish)
												return;
											mvTime.setText(fTimeStr);
											//mvProgr.setProgress(fFinProg);
											setProgress(fFinProg);
										}
									});
							}
							final int fFinProg = 100;
							}catch(Exception e){mMain.sendException(e);}
						}};
					mThread.start();
		}catch(Exception e){mMain.sendException(e);}
	}

	
	void setProgress(int fProg){
		mvProgr.setProgress(fProg);
	}


	public void onEndSong() {
		try{
            mPreff.edit().putLong(kTimePause,0)
                    .putInt(kPausedMedit,-1).apply();

            mvCompl1.setVisibility(View.VISIBLE);
		mvCompl2.setVisibility(View.VISIBLE);
		mvPlayBtn.setEnabled(false);
		totalPause();
		isFinish = true;
		//mvTime.setText(fTimeStr);
		setProgress(100);

		mThread.interrupt();
		}catch(Exception e){mMain.sendException(e);}
	}

	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
		
        @Override
        public void onServiceConnected(ComponentName className,
									   IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService(FrMedit.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

	boolean isSpin = true;
	ObjectAnimator mRotate1,mRotate2;

	void pauseRotate(){
		mRotate1.pause();
		mRotate2.pause();
	}

	void startRotate(){
		isSpin = true;
		int fDur = 20000;
		if(mRotate1!= null){
			mRotate1.resume();
			mRotate2.resume();
		}else {
			mRotate1 = ObjectAnimator.ofFloat(mvCloud1, "rotation", 0, 360);
			mRotate1.setDuration(fDur);
			mRotate1.setInterpolator(new LinearInterpolator());
			mRotate1.setRepeatCount(ObjectAnimator.INFINITE);
			mRotate1.setRepeatMode(ObjectAnimator.RESTART);
			mRotate2 = ObjectAnimator.ofFloat(mvCloud2, "rotation", 360, 0);
			mRotate2.setDuration(fDur);
			mRotate2.setInterpolator(new LinearInterpolator());
			mRotate2.setRepeatCount(ObjectAnimator.INFINITE);
			mRotate2.setRepeatMode(ObjectAnimator.RESTART);
			mRotate2.start();
			mRotate1.start();
		}
			/*}else{
			mRotate2.start();
			mRotate1.start();
		}
		/*new Thread(){public void run(){
			while(isSpin){
				//mMatrix1.postRotate((float) 0.1,mCenter,mCenter);
				//mMatrix2.postRotate((float) -0.1,mCenter,mCenter);
				rotateImage(mvCloud1,mMatrix1);
				rotateImage(mvCloud2,mMatrix2);
				try {
					sleep(10);
				} catch (InterruptedException e) {}
			}
		}}.start();*/
	}
    
    static void rotateImage(ImageView fView, Matrix fMtr){
		
		 //required
		
		fView.setImageMatrix(fMtr);
	}
	
	void showLoadDialog(){
		AlertDialog fDial = new AlertDialog.Builder(mMain)
			.setTitle(R.string.meditLoadHead)
			.setMessage(R.string.meditLoadBody)
			.setOnDismissListener(new DialogInterface.OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface p1) {
					if(!onClb)
						mMain.forseBackPress();
					mProgressDial = null;
					onClb = false;
				}
			})
			.create();


		fDial.getWindow().setBackgroundDrawableResource(R.drawable.bkg_dial);
		fDial.show();
		mProgressDial = fDial;
	}
	
	void showExitDial(){
	    String fMsg = getString(R.string.medit_esc_body)+
            " " +   FrMain.getTimeToEnd(mMain.getDate(),mMain);
		AlertDialog fDial = new AlertDialog.Builder(mMain)
			.setTitle(R.string.medit_esc_title)
			.setMessage(fMsg)
			.setPositiveButton(R.string.medit_esc_btnyes, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					isSaveTime = false;
					mMain.forseBackPress();
				}
			})
			.setNegativeButton(R.string.medit_esc_btnni, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					p1.dismiss();
				}
			})
		.create();
		
		
		fDial.getWindow().setBackgroundDrawableResource(R.drawable.bkg_dial);
		fDial.show();
	}
	
	void showDial(){
		AlertDialog.Builder fBld = new AlertDialog.Builder(mMain);
		
		View fView = getLayoutInflater().inflate(
			R.layout.dial_text, null);
		
		fBld.setView(fView);
		final AlertDialog fDial = fBld.create();
		
		fDial.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		fView.findViewById(R.id.dialtextImageView1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					fDial.dismiss();
				}
			});
		((TextView)fView.findViewById(R.id.dial_textTextView))
		.setText(R.string.medit_desc_head);
		((TextView)fView.findViewById(R.id.dial_textTextView2))
			.setText(R.string.medit_desc_body);
		fDial.show();
	}
}
