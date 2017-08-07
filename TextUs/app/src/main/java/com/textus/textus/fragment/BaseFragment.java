package com.textus.textus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 8/28/15.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

	protected MainActivity mActivity;

	@Nullable
	@Bind(R.id.toolbar_btn_right)
	ImageView btnRight;

	@Nullable
	@Bind(R.id.toolbar_imv_title)
	ImageView imvTitle;

	@Nullable
	@Bind(R.id.toolbar_txt_left)
	TextView txtLeft;

	@Nullable
	@Bind(R.id.toolbar_txt_right)
	TextView txtRight;

	@Nullable
	@Bind(R.id.toolbar_txt_title)
	TextView txtTitle;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		int viewId = addView();
		View view = inflater.inflate(viewId, container, false);
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		ButterKnife.bind(this, view);
		initView();
		setUpToolBar();
		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		mActivity = (MainActivity) context;
	}

	protected void addFragment(BaseFragment fragment, boolean isAddToBackStack) {

		mActivity.addFragment(fragment, isAddToBackStack);
	}

	protected void addFragment(BaseFragment fragment, boolean isAddToBackStack,
			int enter, int exit, int popEnter, int popExit) {

		mActivity.addFragment(fragment, isAddToBackStack, enter, exit,
				popEnter, popExit);
	}

	protected void backFragment(boolean backToHome) {

		mActivity.backFragment(backToHome);
	}

	/**
	 * 
	 * Set up toolbar
	 */
	public void setUpToolBar() {

		if (txtLeft != null) {

			txtLeft.setOnClickListener(this);
			txtRight.setOnClickListener(this);
			btnRight.setOnClickListener(this);
		}
	}

	/**
	 * Add view
	 */
	protected int addView() {

		return 0;
	}

	/**
	 * Init child view
	 */
	protected void initView() {

	}

	@Override
	public void onClick(View view) {

	}
}
