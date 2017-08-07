package com.textus.textus.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;

import com.textus.textus.R;
import com.textus.textus.adapter.ConversationAdapter;
import com.textus.textus.api.MessageApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/27/15.
 */
public class MessagesFragment extends BaseFragment
		implements
			OnItemClickListener {

	@Bind(R.id.home_recycler_view)
	RecyclerView recyclerView;

	private ConversationAdapter mAdapter;
	private List<ConversationEntity> mConversations;

	public static MessagesFragment getInstance() {

		return new MessagesFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_home_details;
	}

	@Override
	protected void initView() {
		super.initView();

		Utilities.setLayoutManager(getContext(), recyclerView, true, true);
		mConversations = new ArrayList<>();
		mAdapter = new ConversationAdapter(getContext(), this, mConversations);
		recyclerView.setAdapter(mAdapter);
		getConversations();
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

	/**
	 * Get conversations
	 */
	private void getConversations() {

		new MessageApi().getInterface().getConversations(1, 25)
				.enqueue(new Callback<List<ConversationEntity>>() {
					@Override
					public void onResponse(Call<List<ConversationEntity>> call,
							Response<List<ConversationEntity>> response) {

						mAdapter.setSearch(false);
						mConversations = response.body();
						mAdapter.setData(mConversations);
					}

					@Override
					public void onFailure(Call<List<ConversationEntity>> call,
							Throwable t) {

						mConversations = null;
						mAdapter.setSearch(false);
						mAdapter.setData(mConversations);
					}
				});
	}

	@Override
	public void onItemClick(BaseEntity model) {

		UserEntity user;
		if (model instanceof ConversationEntity) {

			user = new UserEntity();
			ConversationEntity conversation = (ConversationEntity) model;
			user.setBusinessName("");
			user.setLastName(conversation.getLastName());
			user.setFirstName(conversation.getFirstName());
			user.setPhone(conversation.getReceiverPhone());
			user.setId(conversation.getReceiverId());
		} else {

			user = (UserEntity) model;
		}

		addFragment(ChatFragment.getInstance(user), true);
	}

	@Override
	public void onSearch(String query) {

		if (query.equals("")) {

			mAdapter.setSearch(false);
			mAdapter.setData(mConversations);
			return;
		}

		mAdapter.setData(null);
		if (query.length() > 2) {

			searchConversation(query);
		}
	}

	/**
	 * Search conversation
	 */
	private void searchConversation(String query) {

		new MessageApi().getInterface().searchMessages(1, 25, query)
				.enqueue(new Callback<List<ConversationEntity>>() {
					@Override
					public void onResponse(Call<List<ConversationEntity>> call,
							Response<List<ConversationEntity>> response) {

						mAdapter.setSearch(true);
						mAdapter.setData(response.body());
					}

					@Override
					public void onFailure(Call<List<ConversationEntity>> call,
							Throwable t) {

						mAdapter.setSearch(true);
						mAdapter.setData(null);
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getContext()).registerReceiver(
				mUpdateMessageReceiver,
				new IntentFilter(Constants.INTENT_NEW_MESSAGE));
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
				mUpdateMessageReceiver);
	}

	private BroadcastReceiver mUpdateMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {

			ConversationEntity chat = (ConversationEntity) intent
					.getSerializableExtra(Constants.INTENT_NEW_MESSAGE_CONTENT);
			if (chat == null) {

				Utilities.playSound(getContext(), R.raw.received);
			}

			getConversations();
		}
	};
}
