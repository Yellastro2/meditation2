package com.yellastrodev.meditation;
 
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ReportFragment;

import com.yellastrodev.meditation.fragments.FrAffirm;
import com.yellastrodev.meditation.fragments.FrMain;
import com.yellastrodev.meditation.fragments.FrOnboadr;
import com.yellastrodev.meditation.fragments.FrPaywall;
import com.yellastrodev.meditation.fragments.iFragment;
import com.yellastrodev.meditation.yConst;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.money.meditation.affirmation.R;


public class MainActivity extends FragmentActivity {

	
     
	int mFrameId = R.id.activity_mainFrameLayout;

	private boolean isSaveInstState = false;
	
	public View mvBackBtn;

	private iFragment mFrag;

	private SharedPreferences mPreff;

	private Calendar mCal;

	private static Calendar sCal;

	public FbFiles mFB;
	private String kFirstLaunch = "firstlaunch";


	public static Calendar getDate() {
		if(sCal == null)
			sCal = Calendar.getInstance();
		return sCal;
	}

	public void forseBackPress() {
		super.onBackPressed();
	}
	
	public void sendException(final Exception e){
	    e.printStackTrace();
        Looper.prepare();
		final AlertDialog fDial = new AlertDialog.Builder(this)
			.setTitle("Ошибка")
			.setMessage("В приложении произошла ошибка. Пожалуйста, отправьте информацию об этом нам")
			.setPositiveButton("Отправить", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse("mailto:")); // only email apps should handle this
					intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yellastronaut2@gmail.com"});
					intent.putExtra(Intent.EXTRA_SUBJECT, "LogChat");
					String fTrase = "";
					for (StackTraceElement qTr : e.getStackTrace()) {
						fTrase += qTr.toString()+"\n";
					}
					intent.putExtra(Intent.EXTRA_TEXT, e.getMessage()+"\n"+
					fTrase);
					if (intent.resolveActivity(getPackageManager()) != null) {
						startActivity(intent);
					}
				}
				
				
			})
			.create();
			
		mvBackBtn.post(new Runnable(){
				@Override
				public void run() {
					fDial.getWindow().setBackgroundDrawableResource(R.drawable.bkg_dial);
					fDial.show();
				}
			});
		
		
	}
	ImageView mvImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		readLog();
		
		mFB = FbFiles.initFB(this);
		int fDim = getResources().getInteger(R.integer.dimen_type);
		Toast.makeText(this,"screen type: "+fDim,
		Toast.LENGTH_LONG).show();
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		
		if(metrics.densityDpi>430){

		}
		
        setContentView(R.layout.activity_main);
		mvImage = findViewById(R.id.main_image);
		AllOneReceiver.setRepeat(this);


		int fLaunchCount = getPreff().getInt("launch_count", 1);
		mFB.sendAnality("session_start_"+fLaunchCount);
		 getPreff().edit().putInt("launch_count",fLaunchCount+ 1).apply();
		mvBackBtn = findViewById(R.id.activity_mainBtnBack);
		mvBackBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onBackPressed();
				}
			});
        
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					init(0);
				}
			}, 1000);
			
		getSupportFragmentManager().addOnBackStackChangedListener(new
			FragmentManager.OnBackStackChangedListener(){
				@Override
				public void onBackStackChanged() {
					List<Fragment> fFraLs = getSupportFragmentManager().getFragments();
					if (getSupportFragmentManager().getBackStackEntryCount() < 1)
						disableBackBtn();
					Fragment fFrag = fFraLs.get(0);
					Log.i(yConst.TAG,fFrag.toString()+"; "+
							fFrag.getClass().getCanonicalName());
					//if(fFrag.getClass()!= ReportFragment.class)
					mFrag = (iFragment)fFraLs.get(0);
				}
			});
			
		// Navigation bar hiding:  Backwards compatible to ICS.
		

		createChannel(this);
		View decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener
        (new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					// Note that system bars will only be "visible" if none of the
					// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
					if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
						// TODO: The system bars are visible. Make any desired
						hideNavTab();
						// other navigational controls.
					} else {
						// TODO: The system bars are NOT visible. Make any desired
						// adjustments to your UI, such as hiding the action bar or
						// other navigational controls.
					}
				}
			});
		
    }
	
	
	

	public void sendAnality(String fMsg){
		mFB.sendAnality(fMsg);
	}
	
	public static void sendAnality(String fMsg,Context fCtx){
		Log.i(yConst.TAG,"analy "+fMsg);
		// Obtain the FirebaseAnalytics instance.
		FbFiles.initFB(fCtx).sendAnality(fMsg);
	}
	
	public void hideNavTab(){


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
			getWindow().getInsetsController().hide(
					WindowInsets.Type.navigationBars());
			getWindow().getInsetsController()
					.setSystemBarsBehavior(
							WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = getWindow();
			w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

			getWindow().getDecorView().setSystemUiVisibility(getWindowFlags());
		}
	}
	
	public int getWindowFlags(){
		//int uiOptions =
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
		newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
       	| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        return newUiOptions;
	}
	
	public SharedPreferences getPreff(){
		if(mPreff==null)
			mPreff = getSharedPreferences("",MODE_PRIVATE);
		return mPreff;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		int fArg = 0;
		Bundle fArgs = getIntent().getExtras();
		if(fArgs!=null){
			fArg = fArgs.getInt(yConst.kArg);
		}
		init(fArg);
	}
	
	
	
	void init(int fArg){
		Intent fIntent = getIntent();
		Bundle fExtra = fIntent.getExtras();
		String fAction = fIntent.getAction();
		despireView(
			mvImage);
		inspireView(
			findViewById(mFrameId));
			
		checkSubs();
		if(getPreff().getBoolean(kFirstLaunch,false)){

			if((fAction!=null&&fAction.equals(yConst.kAffirm))
				||(fArg==45))
				openFrame(new FrAffirm(),false);
			else{
				if(fAction!=null&&fAction.equals(yConst.kAffirm))
					sendAnality("main_open_from_refresh_push");
				openFrame(new FrMain(),false);
			}
		}

		else {

			openFrame(new FrOnboadr(),false);
			sendAnality("first_launch");
			getPreff().edit().putBoolean(kFirstLaunch,true).apply();
		}
	}

	public void checkSubs(){
		mFB.mAdapty.checkSubs(new yClb(){
			public void run(String fRes){
				if(fRes.equals("ok")){
					if(!getPreff().getBoolean(yConst.kSubs,false))
						openFrame(new FrMain(),false);
					getPreff().edit().putBoolean(yConst.kSubs,true)
					.apply();
				}else
				{
					if(getPreff().getBoolean(yConst.kSubs,false)) {
						openFrame(new FrPaywall(), false);
						mFB.sendAnality("subscription_remove");
					}
					getPreff().edit().putBoolean(yConst.kSubs,false)
						.apply();
					
				}
			}
		});
	}
	
	public void enableBackBtn(){
		/*if(mvBackBtn.getVisibility()!=View.VISIBLE){
			
			new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						mvBackBtn.setVisibility(View.VISIBLE);
					}},
			getResources().getInteger(R.integer.anim_durone));
			
		}*/
	}
	
	public void disableBackBtn(){
		/*if(mvBackBtn.getVisibility()!=View.INVISIBLE){

			new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						//despireView(mvBackBtn);
						mvBackBtn.setVisibility(View.INVISIBLE);
					}},
				getResources().getInteger(R.integer.anim_dursec));

		}*/
	}
	
	public void onBackToolbar(View fV){
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if(mFrag.onBackPress())
			super.onBackPressed();
	}
	
	public void openFrame(iFragment fFrag,boolean isBack)
    {
		/*List<Fragment> fList = getFragmentManager().getFragments();
		if(fList!=null&&fList.size()>0)
		despireView(fList.get(0).getView());*/
        FragmentTransaction fTrans
            = getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.animator.oneanim, R.animator.secanim,
                                 R.animator.oneanim, R.animator.secanim)
            .replace(mFrameId, fFrag);
        if(isBack){
            fTrans.addToBackStack("");
			enableBackBtn();
		}
		if(!isSaveInstState)
			fTrans
				.commit();
		mFrag = fFrag;
		//inspireView(fFrag.getView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		isSaveInstState = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		despireView(
				mvImage);

		isSaveInstState = false;
		hideNavTab();
	}
	
	
	void getDialog(){
		new AlertDialog.Builder(this).create().getWindow();
		
	}
	
	
	public void setListened(int fMedit){
		int fDatePreff = getPreff().getInt(yConst.isPlayToday, 0);
		int fDateToday = getDate().get(Calendar.DAY_OF_YEAR)
				+(getDate().get(Calendar.YEAR)*365);
		if(fDatePreff!=fDateToday){
			SharedPreferences.Editor fEdit = getPreff().edit();
			int fSexondLast = getPreff().getInt(yConst.ksecondLast,-1);
			Log.i(yConst.TAG,"last "+fDatePreff+
					"laet2 "+fSexondLast);
			if(fDatePreff!=0)
			    fEdit.putInt(yConst.ksecondLast,fDatePreff);
			fEdit.putInt(yConst.isPlayToday,
						 fDateToday)
				.putString(yConst.lastPlay, 
						   getPreff().getString(yConst.lastPlay, "")+yConst.kDiv+
						   fMedit).apply();
			AllOneReceiver.setRefresh(this);
		}
	}
	
	public void inspireView(View fView){
		inspireView(fView,null);
	}
	public void despireView(final View fView){
		despireView(fView, new Runnable(){
				@Override
				public void run() {
					fView.setVisibility(View.GONE);
				}
			});
	}
	public void respireView(final View fView,final Runnable... fSwitchRun){
		despireView(fView, new Runnable(){
				@Override
				public void run() {
					if(fSwitchRun!=null)
						fSwitchRun[0].run();
					if(fSwitchRun.length>1)
						inspireView(fView,fSwitchRun[1]);
					else
						inspireView(fView);
				}
			});
	}
	
	public void despireView(final View fView,final Runnable fRun){
		ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.secanim);
		set.setTarget(fView); // set the view you want to animate
		set.addListener(new Animator.AnimatorListener(){
				@Override
				public void onAnimationStart(Animator p1) {
				}
				@Override
				public void onAnimationEnd(Animator p1) {
					if(fRun!=null)
						fRun.run();
				}
				@Override
				public void onAnimationCancel(Animator p1) {
				}
				@Override
				public void onAnimationRepeat(Animator p1) {
				}
			});
		set.setDuration(200);
		set.start();
		fView.setActivated(false);
	}
	
	public void inspireView(final View fView,final Runnable fRun){
		
		ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.oneanim);
		set.setTarget(fView); // set the view you want to animate
		set.addListener(new Animator.AnimatorListener(){
				@Override
				public void onAnimationStart(Animator p1) {
				}
				@Override
				public void onAnimationEnd(Animator p1) {
					fView.setClickable(true);
					if(fRun!=null)
						fRun.run();
				}
				@Override
				public void onAnimationCancel(Animator p1) {
				}
				@Override
				public void onAnimationRepeat(Animator p1) {
				}
			});
		set.setDuration(200);
		set.start();
		fView.setVisibility(View.VISIBLE);
	}
	
	private String createChannel(Context ctx) {
		// Create a channel.
		NotificationManager notificationManager =
			(NotificationManager) 
			ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence channelName = "Playback channel";
		int importance = NotificationManager.IMPORTANCE_HIGH;
		NotificationChannel notificationChannel =
			new NotificationChannel(
			yConst.CHANNEL_ID, channelName, importance);

		notificationManager.createNotificationChannel(
			notificationChannel);
		return yConst.CHANNEL_ID;
	}

	void readLog()
	{
		new Thread(){public void run(){
			List<String> flsLog = new ArrayList<>();
			try {
				Runtime.getRuntime().exec("logcat -c");
				Process process = Runtime.getRuntime().exec("logcat");
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				File fFile = new File(getExternalCacheDir() , "log.txt");
				FileOutputStream fFileStr = new FileOutputStream(fFile);
				OutputStreamWriter fWriter= new OutputStreamWriter(fFileStr);
				StringBuilder log=new StringBuilder();
				String line = "";
				while ((line = bufferedReader.readLine()) != null) {
					//if(line.contains(yExlConst.TAG+":"))
					fWriter.write(line+"\n");
					flsLog.add(line);
					line = "";
					if(flsLog.size()>20)
						flsLog.remove(0);
					for(String qLine : flsLog)
					{
						log.append(qLine+"\n");
					}
					final String qFinalStr = log.toString();
					log = new StringBuilder();
						/*mvLogView.post(new Runnable(){

								@Override
								public void run() {
									mvLogView.setText(qFinalStr);}
							});*/
				}

			}
			catch (IOException e) {}
		}}.start();
	}
} 
