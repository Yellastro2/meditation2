package com.yellastrodev.meditation;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yellastrodev.meditation.yConst;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 import com.adapty.Adapty;

public class FbFiles {
	
	
	
	public int[] sStates;
	
	public Runnable[] sClbs;
	
	public yAdapty mAdapty;
	
	public static FbFiles initFB(Context fCtx){
		return new FbFiles(fCtx);
	}
    
    public FbFiles(Context fCtx){
		sStates = new int[yConst.sFiles.length];
		int fDefState = yConst.STATE_NONE;
		/*if(BuildConfig.DEBUG)
			fDefState = yConst.STATE_READY;*/
		for(int qst : sStates)
			qst = fDefState;
			
		
			
		sClbs = new Runnable[yConst.sFiles.length];
		
		String fUserId = "";
		
		Account[] accounts = AccountManager.get(fCtx).getAccounts();
		Log.e("", "Size: " + accounts.length);
		for (Account account : accounts) {

			String possibleEmail = account.name;
			String type = account.type;

			//if (type.equals("com.google")) {
				fUserId = possibleEmail;
				break;
			//}
		}

		//fUserId = Math.random()*10000+"id";
		Log.e("", "Emails: " + fUserId);

		mAdapty = new yAdapty(fCtx,fUserId);
		
		
			
		

		int fLoadet = 0;
		try {
			for (int i = 0; i < yConst.sFiles.length&&
				fLoadet<yConst.sMaxOneztimeLoad; i++) {
			   if(i<=yConst.sIdLocalLast){
				   sStates[i]=yConst.STATE_READY;
				   continue;
			   }
			   String fName = yConst.sFiles[i] + yConst.sFileEnd;
				File fFile = new File(fCtx.getExternalCacheDir(), fName);

				if (!fFile.exists()){
					Log.i(yConst.TAG,"file "+fFile.getAbsolutePath()+" not exist");
					loadFile(fCtx,i ,fName,new Runnable(){public void run(){}});
					fLoadet++;
				}else{
					if(!checkFile(fCtx,i)){
						Log.i(yConst.TAG,"file "+i+" broken");
						loadFile(fCtx,i ,fName,new Runnable(){public void run(){}});
						fLoadet++;
						continue;
					}else {
						sStates[i] = yConst.STATE_READY;
						Log.i(yConst.TAG, "file " + fFile.getAbsolutePath() + " exist!!!");
					}
				}
					
			}

			if(mFirebaseAnalytics==null)
				mFirebaseAnalytics = FirebaseAnalytics.getInstance(fCtx);

			// Creating an extended library configuration.
			YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(yConst.YaApi).build();
			// Initializing the AppMetrica SDK.
			YandexMetrica.activate(fCtx.getApplicationContext(), config);
			Application fApp;
			if(fCtx instanceof Activity){
				fApp = ((Activity)fCtx).getApplication();
				YandexMetrica.enableActivityAutoTracking(fApp);

			}else {//
				fApp = (Application) fCtx.getApplicationContext();
				YandexMetrica.enableActivityAutoTracking(fApp);

			}

			// Automatic tracking of user activity.

			
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void addClb(int fId,Runnable fClb){
		sClbs[fId]=fClb;
	}
	
	 void sendClb(int fId){
		if(sClbs[fId]!=null)
			sClbs[fId].run();
		sClbs[fId] = null;
	}
	
	static StorageReference getStorageRef(){
		if(sStorageRef==null){
			FirebaseStorage storage = FirebaseStorage.getInstance();
		// Create a storage reference from our app
			sStorageRef = storage.getReference();
		}
		return sStorageRef;
	}

	boolean checkFile(final Context fCtx,final int fId){
		try {
			final String fName = yConst.sFiles[fId]  + yConst.sFileEnd;
			final File fFile = new File(fCtx.getExternalCacheDir(),
					fName);
			Log.i(yConst.TAG,"check file "+fFile.getName());
			MediaPlayer mediaPlayer = MediaPlayer.create(fCtx, Uri.fromFile(fFile));

					int fDur = mediaPlayer.getDuration();
			Log.i(yConst.TAG,"dur: "+ fDur);
					mediaPlayer.setOnPreparedListener(null);

			StorageReference reference = sStorageRef.child(fName);
			reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
				@Override
				public void onSuccess(StorageMetadata storageMetadata) {
					long fFvSize =  storageMetadata.getSizeBytes();
					long fLocalSize = fFile.length();
					Log.i(yConst.TAG,"The size of the fb file is:"+fFvSize+
							" localv- "+fLocalSize);

					if(fFvSize!=fLocalSize) {

						fFile.delete();
						try {
							loadFile(fCtx, fId, fName, new Runnable() {
								@Override
								public void run() {

								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {

				}
			});
			//mediaPlayer.prepare();
			return mediaPlayer != null;
		}catch(Exception e){
			e.printStackTrace();

			return false;
		}
	}
	
	 static StorageReference sStorageRef;
	
	public void loadFile(final Context fCtx,final int fId,final String fName,final Runnable fClb) throws IOException{

		File localFile = new File(fCtx.getExternalCacheDir(), fName);
		localFile.createNewFile();
		sStates[fId]=yConst.STATE_LOADING;
		getStorageRef().child(fName).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
					// Local temp file has been created
					Log.i(yConst.TAG,"file "+fName+" was loaded");
					sStates[fId]=yConst.STATE_READY;
					fClb.run();
					sendClb(fId);
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					// Handle any errors
					Log.e(yConst.TAG,"file "+fName+" error load");
					exception.printStackTrace();
					File fFile = new File(fCtx.getExternalCacheDir(), fName );
					fFile.delete();
					sStates[fId]=yConst.STATE_NONE;
					fClb.run();
					sendClb(fId);
				}
			});
	}
	private static FirebaseAnalytics mFirebaseAnalytics;

	

	public String getAudio(Context fCtx,int fId,Runnable fClb){
		String fName = fId + yConst.sFileEnd;
		File fFile = new File(fCtx.getExternalCacheDir(), fName);
		if (!fFile.exists()){
			Log.i(yConst.TAG,"file "+fFile.getAbsolutePath()+" not exist");
			try {
				loadFile(fCtx,fId, fName,fClb);
			} catch (IOException e) {e.printStackTrace();}
		} else
			Log.i(yConst.TAG,"file "+fFile.getAbsolutePath()+" exist!!!");
		return fFile.getAbsolutePath();
	}
    
	
	public void sendAnality(String fMsg){
		
		 Map<String, Object> eventParameters = new HashMap<String, Object>();
		 eventParameters.put("name", fMsg);
		 

		 YandexMetrica.reportEvent(fMsg, eventParameters);
		
		


		 Bundle bundle = new Bundle();
		 bundle.putString("event", fMsg);
		 mFirebaseAnalytics.logEvent(fMsg, bundle);
		 
	}
}
