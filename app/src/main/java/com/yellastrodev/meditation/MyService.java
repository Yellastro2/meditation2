package com.yellastrodev.meditation;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.yellastrodev.meditation.fragments.FrMedit;
import java.io.File;
import android.net.Uri;


public class MyService extends Service implements MediaPlayer.OnPreparedListener 
{
	
	
	
	private final IBinder binder = new LocalBinder();

	public int mTotalDuration;

	private int mPgor;
	private FrMedit mAct;

	private int mPgorTime=0;
	
	public class LocalBinder extends Binder {
        public MyService getService(FrMedit fAct) {
			mAct = fAct;
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
	
    public static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) 
	{
		int fIndex = intent.getIntExtra(yConst.kCurentMedit, 0);

		// if (intent.getAction().equals(ACTION_PLAY)) {
		if(fIndex>yConst.sIdLocalLast){
			File fFile = new File(getExternalCacheDir(),
								  yConst.sFiles[fIndex] + ".mp3");
			mediaPlayer = mediaPlayer.create(this,Uri.fromFile(fFile));
		}else{
			int fMedit = yConst.kMeditations[fIndex];
			mediaPlayer = MediaPlayer.create(this, fMedit);

		}
			//mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);// initialize it here
        mediaPlayer.setOnPreparedListener(this);
            //mediaPlayer.prepareAsync(); // prepare async to not block main thread
        //}
		mTotalDuration = mediaPlayer.getDuration();
		//onPrepared(mediaPlayer);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer p1) {
					if(mAct!=null)
						mAct.onEndSong();
				}
			});
		return START_STICKY;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }
	
	public void play()
	{
		mediaPlayer.start();
	}
	
	public void pausePlayer()
	{
		mediaPlayer.pause();
	}
	
	public int getProgressPercs()
	{
		if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
			int fPos = mediaPlayer.getCurrentPosition();

			mPgor = fPos * 100 / mTotalDuration;
			
		}
		return mPgor;
	}
	
	public int getProgressTime(){
		if(mediaPlayer!=null){
			int fPos = mediaPlayer.getCurrentPosition();

			mPgorTime = fPos; //* 100 / mTotalDuration;

		}
		return mPgorTime;
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) mediaPlayer.release();
		mediaPlayer=null;
	}
}
