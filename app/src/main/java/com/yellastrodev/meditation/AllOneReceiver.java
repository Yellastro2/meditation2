package com.yellastrodev.meditation;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.yellastrodev.meditation.yConst;
import java.util.Calendar;
import android.app.Activity;
import android.content.SharedPreferences;
import com.money.meditation.affirmation.R;

public class AllOneReceiver extends BroadcastReceiver {

	static int kAffirm = 25;
	static int kRefresh = 46;

	private static final int NOTIFICATION_ID = 0;

	private static String kType = "type";

	private String kAffirmToday = "affirmtoday";

	private String kLastAffirm = "lastaff";

	public static int sAffirm = 0;

	public static boolean isTestpus = false;
	@Override
	public void onReceive(Context fCtx, Intent intent) {
		if(intent.getAction()!=null&&
		//if (
		intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // on device boot compelete, reset the alarm
			setRepeat(fCtx);
			Toast.makeText(fCtx, "Alarm running", Toast.LENGTH_SHORT).show();
			
        }else{
			int fIntentType = intent.getExtras().getInt(kType);
			if(fIntentType==kAffirm){
			Calendar calendar = MainActivity.getDate();
			Log.i(yConst.TAG, "recieve alarm, showing notify");
			int fHour = calendar.get(calendar.HOUR_OF_DAY);
			SharedPreferences fPreff = fCtx.getSharedPreferences("", Activity.MODE_PRIVATE);
			if((fHour>9 && fHour<21)){
				Log.i(yConst.TAG, "check for affirm notify");
				if(fPreff.getBoolean(yConst.isAffirm,true)){
					long fLastMils = fPreff.getLong(kLastAffirm,0);
					Calendar fCalLast = calendar.getInstance();
					fCalLast.setTimeInMillis(fLastMils);
					
					int fFreq = fPreff.getInt(yConst.kFreq, 3);
					int fHasAff = fPreff.getInt(kAffirmToday, 0);
					if(fCalLast.get(calendar.DAY_OF_YEAR)
							+(fCalLast.get(calendar.YEAR)*1000)<
						calendar.get(calendar.DAY_OF_YEAR)
								+(calendar.get(calendar.YEAR)*1000))
						fHasAff = 0;
					Log.i(yConst.TAG, "has affir: "+fHasAff);
					if(fHasAff<fFreq){
						float fTimeSpend = (calendar.getTimeInMillis() - fLastMils);
						float fLastInHour = fTimeSpend / (1000 * 60 * 60);
						float fInterval = 10f / (float)fFreq;
						Log.i(yConst.TAG, "last hour: "+fLastInHour+
						" interval: "+fInterval+
						" frequency: "+fFreq);
						if(fLastInHour>fInterval-0.05){
							showAffirmNoty(fCtx);
							fPreff.edit().putInt(kAffirmToday,fHasAff+1)
								.putLong(kLastAffirm,calendar.getTimeInMillis())
								.apply();
							Log.i(yConst.TAG, "notify of affirm");
						}
						
					}
				}
					
				
			}
			}else if(fIntentType==kRefresh){
				Log.i(yConst.TAG, "notify of refresh");
				showRefreshNoty(fCtx);
				
			}
		}
		
		// show toast
		
		
		//showNotification(context);
	}

	public static void setRefresh(Context fCtx){
		Intent alarmIntent = new Intent(fCtx, AllOneReceiver.class);
		alarmIntent.putExtra(kType,kRefresh);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(fCtx, 0, alarmIntent, 0);

		AlarmManager manager = (AlarmManager) fCtx.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		/*calendar.set(calendar.DAY_OF_YEAR,
			calendar.get(calendar.DAY_OF_YEAR)+1);
		calendar.set(calendar.HOUR_OF_DAY,10);*/

		calendar.set(calendar.MINUTE,
					 calendar.get(calendar.MINUTE)+1);
		
		manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
			, pendingIntent);
	}
    
	public static void setRepeat(Context fCtx){
		
		SharedPreferences fPreff = fCtx.getSharedPreferences("", Activity.MODE_PRIVATE);
		

		Intent alarmIntent = new Intent(fCtx, AllOneReceiver.class);
		alarmIntent.putExtra(kType,kAffirm);
		//alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(fCtx, 0, alarmIntent, 0);

		AlarmManager manager = (AlarmManager) fCtx.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		
	
		if(fPreff.getBoolean(yConst.isAffirm,true)){
			
			manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
								 AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
			
		}else{
			manager.cancel(pendingIntent);
		}
		
	}
	
	public static void showRefreshNoty(Context fCtx){
		Intent fIntent = new Intent(fCtx, MainActivity.class);
		fIntent.putExtra(yConst.kArg,yConst.kMain);
		fIntent.setAction(yConst.kMain);
		fIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		MainActivity.sendAnality("push_meditation_send",fCtx);
		

		PendingIntent contentIntent = PendingIntent.getActivity(fCtx, 0, fIntent
																, 0);
		showNoty(fCtx,kRefresh,fCtx.getString(R.string.noty_refresh_head),
				 fCtx.getString(R.string.noty_text_refresh)
		,contentIntent);
	}
    
    public static void showAffirmNoty(Context fCtx,boolean is){
		Intent fIntent = new Intent(fCtx, MainActivity.class);
		fIntent.putExtra(yConst.kArg,yConst.kAffirm);
		fIntent.putExtra(yConst.kIsTest,true);
		isTestpus = true;
		MainActivity.sendAnality("push_affirmation_send_test",fCtx);
		fIntent.setAction(yConst.kAffirm);
		fIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK 
						 |Intent.FLAG_ACTIVITY_SINGLE_TOP);
		showAffirmNoty(fCtx,fIntent);
	}
	
	public static void showAffirmNoty(Context fCtx){
		isTestpus = false;
		MainActivity.sendAnality("push_affirmation_send",fCtx);
		Intent fIntent = new Intent(fCtx, MainActivity.class);
		fIntent.putExtra(yConst.kArg,yConst.kAffirm);
		fIntent.setAction(yConst.kAffirm);
		fIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK 
						 |Intent.FLAG_ACTIVITY_SINGLE_TOP);
		showAffirmNoty(fCtx,fIntent);
	}
	
	public static void showAffirmNoty(Context fCtx,Intent fIntent){
		PendingIntent contentIntent = PendingIntent.getActivity(fCtx, 0,fIntent
																, 0);

		String[] fAffList = fCtx.getString(R.string.affirm_list).split("\\.");

		sAffirm = (int)(Math.random() * (fAffList.length-2));

		String fBody = fAffList[sAffirm];
		if(fBody.substring(0,1).equals(" ")){
			fBody = fBody.substring(1,fBody.length());
		}
		if(fBody.length()>30)
			fBody = fBody.substring(0,30)+"...";
		showNoty(fCtx,kAffirm,fCtx.getString(R.string.noty_affirm_head),
				 fBody,contentIntent);
	}
	
	static void showNoty(Context fCtx,int fId,String fTitle,String fText,PendingIntent fPend){
	
		Notification notification= new Notification.Builder(fCtx, yConst.CHANNEL_ID)
			.setSmallIcon(R.drawable.img_hum)  // the status icon
			//.setTicker(text)// the status text
			//.setWhen(System.currentTimeMillis())  // the time stamp
			.setContentTitle(fTitle)  // the label of the entry
			.setContentText(fText)  // the contents of the entry
			//.setLargeIcon(fBtm)
			//.setStyle(new NotificationCompat.BigPictureStyle()
			// .bigPicture(fBtm))
			//.setStyle(new NotificationCompat.BigTextStyle()
			// .bigText(fMsg))
			.setAutoCancel(true)
			.setContentIntent(fPend)
			.build();
		//.setContentIntent(contentIntent);;
		NotificationManager notificationManager = (NotificationManager) fCtx.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(fId, notification);

	}
}
