/**
 * 
 */
package com.textus.textus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.textus.textus.fragment.ContactsFragment;
import com.textus.textus.fragment.MessagesFragment;

/**
 * @author nvhaiwork
 *
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

	private String[] mTitles;

	/**
	 * @param fm
	 *            Fragment manager
	 */
	public TabPagerAdapter(FragmentManager fm, String[] titles) {
		super(fm);

		this.mTitles = titles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {

		switch (position) {

			case 1 :

				return ContactsFragment.getInstance();
			default :

				return MessagesFragment.getInstance();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {

		return mTitles == null ? 0 : mTitles.length;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	// */
	// @Override
	// public int getItemPosition(Object object) {
	// return POSITION_NONE;
	// }

	@Override
	public CharSequence getPageTitle(int position) {

		return mTitles[position];
	}
}
