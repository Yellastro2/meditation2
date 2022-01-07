package com.yellastrodev.meditation;

import android.view.*;
import android.view.animation.*;

public class MyAnimations
{

	//private static long mDurat;
	
	public static void expand(final View view,final Runnable fClb) {
        //view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		//view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		view.setVisibility(View.VISIBLE);
		final int actualheightpost = view.getMeasuredHeight();
		int fDbgheigh = view.getMeasuredHeight();
		long mDurat = (long) (actualheightpost / view.getContext().getResources().getDisplayMetrics().density)
			+ 200;
		view.postDelayed(fClb,mDurat);
		/*view.post(new Runnable(){

				@Override
				public void run() {
					//view.setVisibility(View.VISIBLE);
					
					int dbg = 5;
					
					view.getLayoutParams().height = 0;
					//view.requestLayout();
					final Animation animation;
					animation = new Animation() {
						@Override
						protected void applyTransformation(float interpolatedTime, Transformation t) {

							view.getLayoutParams().height = interpolatedTime == 1
								? ViewGroup.LayoutParams.WRAP_CONTENT
								: (int) (actualheightpost * interpolatedTime);
							view.requestLayout();
						}
					};
					
					//animation.setDuration(mDurat);
					//view.startAnimation(animation);
					
				}
			});*/
		
		//return mDurat;
    }
	/*
	public static long expand(View view) {
        Animation animation = expandAction(view);
        view.startAnimation(animation);
		return mDurat;
    }

    private static Animation expandAction(final View view) {

        
       // view.startAnimation(animation);

        return animation;


    }*/

    public static void collapse(final View view) {

        final int actualHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    
                } else {
                    view.getLayoutParams().height = actualHeight - (int) (actualHeight * interpolatedTime);
                    view.requestLayout();

                }
            }
        };
		view.setVisibility(View.GONE);
        animation.setDuration((long) (actualHeight/ (view.getContext().getResources().getDisplayMetrics().density*2))
			+200);
		//view.startAnimation(animation);
    }
}
