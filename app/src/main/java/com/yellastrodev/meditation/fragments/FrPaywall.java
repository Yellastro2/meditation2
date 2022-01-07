package com.yellastrodev.meditation.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.money.meditation.affirmation.R;
import com.yellastrodev.meditation.yClb;
import com.yellastrodev.meditation.yConst;

public class FrPaywall extends iFragment {
	
	TextView mvTextPrice;
	Button mvBtnPursh;
	
	String sPolicyAndTerms = "Купив подписку, вы соглашаетесь с нашими "+
	"<font color='gray' ><a href='"+yConst.sLinkTems+"'>Условиями</a></font> и "+
	"<font color='gray' ><a href='"+yConst.sLinkPrivacy+"'>Политикой конфиденциальности</a></font>.";
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_paywall, null);

		fView.findViewById(R.id.fr_paywall_esc)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mMain.openFrame(new FrMain(),false);
					}
				});

		mvBtnPursh = fView.findViewById(R.id.fr_paywallButton);
		mvBtnPursh.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					purshase();
				}
			});
		mvTextPrice = fView.findViewById(R.id.fr_paywallTextPrice);
			
		mMain.sendAnality("subscription_screen");
		mMain.mvBackBtn.setVisibility(View.GONE);
		if(mMain.getPreff().getBoolean(yConst.kSubs,false))
			mMain.openFrame(new FrMain(),false);
			
		TextView fvPolicy = fView.findViewById(R.id.fr_paywallTextTerms);
			
		fvPolicy.setText(Html.fromHtml(sPolicyAndTerms, Html.FROM_HTML_MODE_COMPACT));
		//Linkify.addLinks(fvPolicy, Linkify.WEB_URLS);
		fvPolicy.setMovementMethod(LinkMovementMethod.getInstance());
		
		/*.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity( new Intent(Intent.ACTION_VIEW, 
											  Uri.parse(yConst.sLinkTems)));

				}
			});*/
		mvBtnPursh.setEnabled(false);
		mvTextPrice.setText("Загрузка..");
		mMain.mFB.mAdapty.getSubsc(new yClb(){
				public void run(String fRes){
					if(fRes.contains("error")){
						loadError(fRes);
						mvTextPrice.setText("Ошибка подключения");
						mvBtnPursh.setEnabled(false);
					}else
					{
						Toast.makeText(mMain,fRes,Toast.LENGTH_LONG).show();
						mvTextPrice.setText(getString(R.string.paywall_pricetext)+
						fRes);
						mvBtnPursh.setEnabled(true);
					}
				}
			});
		fView.findViewById(R.id.fr_pawalltest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPurshais();
            }
        });
		return fView;
	}
	
	void loadError(String fErr){
		Toast.makeText(mMain,"Ошибка "+fErr,Toast.LENGTH_LONG).show();
	}
	
	void purshase(){
		mMain.sendAnality("subscription_screen_button");
		mMain.mFB.mAdapty.makePursh(mMain,new yClb(){
				public void run(String fRes){
					if(fRes.equals("ok")){
						onPurshais();
						mMain.mFB.sendAnality("subscription_success");
					}else
					{
						Toast.makeText(mMain,"Не удалось оформить подписку",Toast.LENGTH_LONG).show();
						mMain.mFB.sendAnality("subscription_abort");
					}
				}
			});
	}
    
	public void onPurshais(){
		mMain.getPreff().edit().putBoolean(yConst.kSubs,true)
			.commit();
		Toast.makeText(mMain,"Подписка оформлена",Toast.LENGTH_LONG).show();
		mMain.openFrame(new FrMain(),false);
		mMain.mvBackBtn.setVisibility(View.INVISIBLE);
	}
}
