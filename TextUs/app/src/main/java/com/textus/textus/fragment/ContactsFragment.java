package com.textus.textus.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.adapter.ContactAdapter;
import com.textus.textus.api.MessageApi;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/27/15.
 */
public class ContactsFragment extends BaseFragment
		implements
			OnItemClickListener {

	@Bind(R.id.home_recycler_view)
	RecyclerView recyclerView;

	@Bind(R.id.home_contact_message)
	TextView txtMessage;

	private ContactAdapter mAdapter;
	public static ContactsFragment getInstance() {

		return new ContactsFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_home_details;
	}

	@Override
	protected void initView() {
		super.initView();

		mAdapter = new ContactAdapter(getContext(), this, null);
		recyclerView.setAdapter(mAdapter);
		Utilities.setLayoutManager(getContext(), recyclerView, true, true);
		txtMessage.setVisibility(View.VISIBLE);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				Utilities.hideSoftKeyBoard(getActivity());
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}
		});
	}

	@Override
	public void onItemClick(BaseEntity model) {

		addFragment(ContactDetailFragment.getInstance((UserEntity) model), true);
	}

	@Override
	public void onSearch(String query) {

		if (query.length() < 3) {

			mAdapter.setData(null);
			txtMessage.setVisibility(View.VISIBLE);
			return;
		}

		txtMessage.setVisibility(View.INVISIBLE);
		searchContacts(query);
	}

	/**
	 * Search contact
	 * 
	 * @param query
	 *            query string
	 */
	private void searchContacts(final String query) {

		new MessageApi().getInterface().searchContacts(1, 25, query)
				.enqueue(new Callback<List<UserEntity>>() {
					@Override
					public void onResponse(Call<List<UserEntity>> call,
							Response<List<UserEntity>> response) {

						mAdapter.setData(response.body());
					}

					@Override
					public void onFailure(Call<List<UserEntity>> call,
							Throwable t) {
						mAdapter.setData(null);
					}
				});
	}
}
