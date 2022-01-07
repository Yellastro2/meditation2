package com.yellastrodev.meditation.fragments;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.money.meditation.affirmation.R;

public class FrQuest extends iFragment {
	
	int[] sQuests = {R.string.qw1_h,R.string.qw2_h};
	int[] sDescs = {R.string.qw1_d,R.string.qw2_d};
	int[][] sVars = {{R.string.qw1_q1,R.string.qw1_q2,R.string.qw1_q3},
		{R.string.qw2_q1,R.string.qw2_q2,R.string.qw2_q3,
			R.string.qw2_q4,R.string.qw2_q5,R.string.qw2_q6}
	},
	sAnsws = {{R.string.qw1_a1,R.string.qw1_a2,R.string.qw1_a3},
		{R.string.qw2_a1,R.string.qw2_a2,R.string.qw2_a3,
			R.string.qw2_a4,R.string.qw2_a4,R.string.qw2_a4}
	};
	

	ListView mvList;
	View mvAnswLay;
	View mvAnswBtn;
	TextView mvHeadText,mvAnswVariant,mvAnswText,mvDescText;

	private FrQuest.BoxAdapter mAdapter;
	
	List<Quest> mlsQuests = new ArrayList<>();
	int mStep = 0;

	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_questions, null);
		
		mAdapter = new BoxAdapter(getContext());

		// настраиваем список
		mvList = (ListView) fView.findViewById(R.id.fr_quest_list);
		mvList.setAdapter(mAdapter);
		
		mvHeadText = fView.findViewById(R.id.fr_quest_textHead);
		mvDescText = fView.findViewById(R.id.fr_quest_textDesc);
		//mvQuestBody = fView.findViewById(R.id.fr_quest_bodytext);
		mvAnswVariant = fView.findViewById(R.id.fr_quest_answbtntext);
		mvAnswText = fView.findViewById(R.id.fr_quest_answtext);
		
		mvAnswLay = fView.findViewById(R.id.fr_quest_answlay);
		fView.findViewById(R.id.fr_quest_answbtnnext)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mStep>=mlsQuests.size())
						return;
					mStep++;
					if(mStep>=mlsQuests.size())
						endQuest();
					else
						mMain.respireView(mView, new Runnable(){
								@Override
								public void run() {
									setQuest();
								}
							},new Runnable(){
								@Override
								public void run() {
									mMain.inspireView(mvList);
								}
							});
				}

				
			});
		
		/*ArrayList<String> fVars = new ArrayList<String>();
		ArrayList<String> fAnswrs = new ArrayList<String>();
		fVars.add("one");
		fVars.add("one2");
		fVars.add("one4");
		fVars.add("one3");
		
		fAnswrs.add("answer");
		fAnswrs.add("answer2");
		fAnswrs.add("answer4");
		fAnswrs.add("answer3");

		mlsQuests.add(new Quest("Какой то важный вопрос",
			"описание этого важнлго вопроса",fVars,fAnswrs));
		mlsQuests.add(new Quest("Какой то важный вопрос no 2",
								"описание этого важнлго вопроса 222",fVars,fAnswrs));
		mlsQuests.add(new Quest("Какой то важный вопрос nomer 3",
								"описание этого важнлго вопроса 3333",fVars,fAnswrs));
		*/
		for (int i = 0; i < sQuests.length; i++) {
			ArrayList<String> qVars = new ArrayList<String>();
			for (int j = 0; j < sVars[i].length; j++) {
				qVars.add(getString(sVars[i][j]));
			}
			ArrayList<String> qAnsw = new ArrayList<String>();
			for (int j = 0; j < sAnsws[i].length; j++) {
				qAnsw.add(getString(sAnsws[i][j]));
			}
			mlsQuests.add(new Quest(getString(sQuests[i]),
			getString(sDescs[i]),qVars,qAnsw));
		}
		setQuest();
		mView = fView;
		return fView;
	}
	
	private void endQuest() {
		mMain.openFrame(new FrPaywall(),false);
	}
	
	void setQuest(){
		if(mStep>=mlsQuests.size())
			return;
		mMain.sendAnality("onbord_"+(mStep+4));
		FrQuest.Quest fQuest = mlsQuests.get(mStep);
		ArrayList<String>  fLs = fQuest.getAnswers();
			//= new ArrayList<String>();
		
		/*fLs.add("Чото");
		fLs.add("Чото");
		fLs.add("Чото");
		fLs.add("Чото");*/
		
		mvHeadText.setText(fQuest.getHead());
		mvDescText.setText(fQuest.getBody());
		
		mAdapter.setList(fLs);
		mvList.setVisibility(View.VISIBLE);
		//mvList.setAlpha(1);
		mvAnswLay.setVisibility(View.GONE);
	}
	
	void setAnsw(final int fId){
		mMain.sendAnality("onbord_quest_"+(mStep+4)+
		"_"+(fId+1));
		mMain.despireView(mvList, new Runnable(){
				@Override
				public void run() {
					FrQuest.Quest fQuest = mlsQuests.get(mStep);

					
					String fText = fQuest.getAnswers().get(fId);
					int fTextSize = R.dimen.text_medium;
					if(fText.length()>28){
						fTextSize= R.dimen.text_medium2;
					}
					mvAnswVariant.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMain.getResources().getDimension(fTextSize));
					
					mvAnswVariant.setText(fText);
					mvAnswText.setText(fQuest.getAnswerText(fId));
					mvList.setVisibility(View.GONE);
					mMain.inspireView(mvAnswLay);
				}
			});
		
		
		
	}
    
    
    public class BoxAdapter extends BaseAdapter {
		Context mCtx;
		LayoutInflater lInflater;
		ArrayList<String> mList = new ArrayList();

		BoxAdapter(Context context) {
			mCtx = context;
			
			lInflater = (LayoutInflater) mCtx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setList(ArrayList<String> fLs)
		{
			mList = fLs;
			//notify();
			//notifyAll();
			notifyDataSetChanged();
		}

		// кол-во элементов
		@Override
		public int getCount() {
			return mList.size();
		}

		// элемент по позиции
		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		// id по позиции
		@Override
		public long getItemId(int position) {
			return position;
		}

		// пункт списка
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// используем созданные, но не используемые view
			LinearLayout fView = (LinearLayout)convertView;
			if (fView == null) {
				fView = (LinearLayout)lInflater.inflate(R.layout.it_quest, parent, false);
			}
			Button fBtn = (Button)fView.getChildAt(0);
			
			String fText = (String)getItem(position);
			int fTextSize = R.dimen.text_medium;
			if(fText.length()>28){
				fTextSize= R.dimen.text_medium2;
			}
			fBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMain.getResources().getDimension(fTextSize));
			fBtn.setText(fText);
			fBtn.setBackgroundResource(
				R.drawable.bkg_stroked_lilround);
			fBtn.setTextColor(
				getResources().getColor(R.color.PrimaryLight));
			fBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
							setAnsw(position);
							((Button)view).setBackgroundResource(
							R.drawable.bkg_solid_lilcorn_ligth);
							((Button)view).setTextColor(
								Color.WHITE);
                        }
					});
			
			return fView;
		}
	}
	
	public static class Quest {

		private String mHead;

		private String mBody;

		private ArrayList<String> mlsVar;

		private ArrayList<String> mlsAnsw;
		
		public Quest(String fH,String fB,ArrayList<String> fVar,
		ArrayList<String> fAnswes){
			mHead = fH;
			mBody = fB;
			mlsVar = fVar;
			mlsAnsw = fAnswes;
		}

		public String getAnswerText(int fId) {
			return mlsAnsw.get(fId);
		}

		public String getBody() {
			return mBody;
		}

		public String getHead() {
			return mHead;
		}
		

		public ArrayList<String> getAnswers() {
			return mlsVar;
		}}
}
