package com.yellastrodev.meditation.fragments;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.money.meditation.affirmation.R;
import com.yellastrodev.meditation.MyAnimations;
import com.yellastrodev.meditation.yConst;


public class FrFaq extends iFragment {

    int[] sTitles = {R.string.faq1_title, R.string.faq2_title, R.string.faq3_title,R.string.faq4_title},
	sBodys = {R.string.faq1_body, R.string.faq2_body, R.string.faq3_body,R.string.faq4_body};

    ScrollView mScrollView;
	LinearLayout mvLinear;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fView = inflater.inflate(R.layout.fr_faq, null);
			mvLinear = fView.findViewById(R.id.fr_faqLinearLayout);
        mScrollView = fView.findViewById(R.id.fr_faqScrollView);
        LayoutTransition qTrans = new LayoutTransition();
        qTrans.enableTransitionType(LayoutTransition.CHANGING);
        ((ViewGroup) mScrollView.getChildAt(0)).setLayoutTransition(qTrans);
        init();
		TextView fvPolicy = fView.findViewById(R.id.fr_faqTextPolicy);
			/*fvPolicy.setText(Html.fromHtml(sPolicyAndTerms, Html.FROM_HTML_MODE_COMPACT));
			Linkify.addLinks(fvPolicy, Linkify.WEB_URLS);
			fvPolicy.setMovementMethod(LinkMovementMethod.getInstance());
			*/
        fvPolicy.setVisibility(View.GONE);
		

        return fView;
    }

    void init() {
        android.widget.LinearLayout.LayoutParams fLayParam =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        fLayParam.setMarginStart(30);
        for (int i = 0; i < sTitles.length; i++) {
            View qvItem = LayoutInflater.from(mMain).inflate(R.layout.it_faq, null);
            LayoutTransition qTrans = new LayoutTransition();
            qTrans.enableTransitionType(LayoutTransition.CHANGING);

            ((ViewGroup) qvItem).setLayoutTransition(qTrans);
			
            onOpenedListClick qOnclick =
                    new onOpenedListClick(qvItem.findViewById(R.id.it_faqTextBody));
            
			TextView qvTitle = 
				((TextView) qvItem.findViewById(R.id.it_faqTextTitle));
			qvTitle.setText(getString(sTitles[i]));
			((View)qvTitle.getParent()).setOnClickListener(qOnclick);
			
			TextView qvBody = 
				((TextView) qvItem.findViewById(R.id.it_faqTextBody));
			if(i<3)
				qvBody.setText(getString(sBodys[i]));
			else{
				qvBody.setText(Html.fromHtml(sProvideBy, Html.FROM_HTML_MODE_COMPACT));
				Linkify.addLinks(qvBody, Linkify.WEB_URLS);
				qvBody.setMovementMethod(LinkMovementMethod.getInstance());
				
			}
			
            ((ImageView) qvItem.findViewById(R.id.it_faqImageArrow))
                    .setImageResource(R.drawable.ic3);
            mvLinear.addView(qvItem);

            /*((ViewGroup)mScrollView.getChildAt(0)).addView(;
            qvItem.setLayoutParams(fLayParam);*/
        }
    }

    void openAndScroll(final View fView) {
        MyAnimations.expand(
                fView,
                new Runnable() {
                    @Override
                    public void run() {
                        //mScrollView.smoothScrollTo(0, fView.getTop());
                    }
                });
        // fView.postDelayed(, fDelay);

    }

    class onOpenedListClick implements OnClickListener {
        public boolean isShortHide = true;

        private View mView;

        public onOpenedListClick(View fView) {
            mView = fView;
            isShortHide = mView.getVisibility() != View.VISIBLE;
        }

        @Override
        public void onClick(View p1) {
            ImageView fImg = p1.findViewById(R.id.it_faqImageArrow);
            if (isShortHide) {
                openAndScroll(mView);
            } else MyAnimations.collapse(mView);
            isShortHide = !isShortHide;
            spinImage(fImg, !isShortHide);
        }
    }

    void spinImage(final ImageView fView, boolean isBt) {
        final Matrix fMatrix = new Matrix();
        fView.setScaleType(ScaleType.MATRIX);

        final int fCenterY = fView.getHeight() / 2;
        final int fCenterX = fView.getWidth() / 2;

        float fStart, fEnd;
        if (isBt) {
            fStart = 0;
            fEnd = 180;
        } else {
            fStart = 180;
            fEnd = 360;
        }

        ValueAnimator fAnim = ValueAnimator.ofFloat(fStart, fEnd).setDuration(300);
        fAnim.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator p1) {
                        fMatrix.setRotate((float) p1.getAnimatedValue(), fCenterX, fCenterY);
                        fView.setImageMatrix(fMatrix);
                    }
                });
        fAnim.start();
    }


	String sProvideBy = "Использованная в медитациях музыка:"+
            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/128173194-magic-forest-meditation'>Stock music</a></font> provided by wavetune, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/109517984-yeshua-30m-track-piano-cinematic-soundtrack-emotional-beauti'>Stock music</a></font> provided by heavenlysound, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/134211239-wellness-massage-chill-zen-meditation-yoga-432-hz'>Stock music</a></font> provided by TopSounds, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/129051016-meditation-calming-busy-mind'>Stock music</a></font> provided by AudiouslyPro, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/129050878-peaceful-mindfulness-meditation'>Stock music</a></font> provided by AudiouslyPro, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/142138427-10-min-meditation-abundant-and-prosperous-morning-inspiring'>Stock music</a></font> provided by ThinkrootRecords, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/126526256-meditation'>Stock music</a></font> provided by wavetune, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/125426899-its-meditation'>Stock music</a></font> provided by Ashot_Danielyan_Composer, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/129505696-bright-meditation'>Stock music</a></font> provided by wavetune, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"+

            "<br>- <font color='gray' ><a href='https://www.pond5.com/ru/royalty-free-music/item/138091911-healing-meditationinspirationyogaspamantracalmsleepnature1-h'>Stock music</a></font> provided by AG_Music, from <font color='gray' ><a href='https://www.pond5.com/'>Pond5</a></font>"
+"<br><br><font color='gray' ><a href='"+yConst.sLinkPrivacy+"'>Политика конфиденциальности</a></font>"+
            " и <font color='gray' ><a href='"+yConst.sLinkTems+"'>Условия использования</a></font>.";

}
