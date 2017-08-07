package com.textus.textus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.api.MessageApi;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.entity.MessageEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/27/15.
 */
public class ConversationAdapter
		extends
			RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int TYPE_HEADER = 1;

	private Context mCtx;
	private String mQuery;
	private boolean isSearch;
	private LayoutInflater mInflater;
	private OnItemClickListener mListener;
	private List<ConversationEntity> mConversations;
	public ConversationAdapter(Context ctx, OnItemClickListener listener,
			List<ConversationEntity> conversations) {

		this.mCtx = ctx;
		this.mListener = listener;
		this.mConversations = conversations;
		this.mInflater = LayoutInflater.from(ctx);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {

		if (viewType == TYPE_HEADER) {

			View view = mInflater.inflate(R.layout.layout_header_search,
					parent, false);
			return new HeaderHolder(view);
		} else {

			View view = mInflater.inflate(R.layout.layout_item_conversation,
					parent, false);
			return new ViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		if (getItemViewType(position) == TYPE_HEADER) {

			final HeaderHolder headerHolder = (HeaderHolder) holder;
			headerHolder.edtSearch.removeTextChangedListener(myTextWatcher);
			if (mQuery == null || mQuery.equals("")) {

				headerHolder.btnClose.setVisibility(View.INVISIBLE);
			} else {

				headerHolder.edtSearch.requestFocus();
				headerHolder.btnClose.setVisibility(View.VISIBLE);
				headerHolder.edtSearch.setSelection(mQuery.length());
			}

			headerHolder.itemView.setVisibility(View.VISIBLE);
			headerHolder.btnClose
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {

							headerHolder.edtSearch.setText("");
						}
					});

			myTextWatcher.setHolder(headerHolder);
			headerHolder.edtSearch.addTextChangedListener(myTextWatcher);
			return;
		}

		ViewHolder itemHolder = (ViewHolder) holder;
		final ConversationEntity conversation = mConversations
				.get(position - 1);
		final UserEntity contact = conversation.getContact();
		String strLastName = "", strFirstName = "", strTime, strMessage;
		if (isSearch) {

			String name = conversation.getReceiverName();
			String[] separated = name.split(" ");
			strLastName = separated[1];
			strFirstName = separated[0];
			conversation.setLastName(strLastName);
			strMessage = conversation.getContent();
			conversation.setFirstName(strFirstName);
			strTime = Utilities.getDeliveredTime(conversation.getDeliverAt());
		} else {

			MessageEntity message = conversation.getMessages().get(0);
			if (contact != null) {

				strLastName = contact.getLastName();
				strFirstName = contact.getFirstName();
			}

			strMessage = message.getContent();
			strTime = Utilities.getDeliveredTime(message.getDeliverAt());
			if (!message.isRead()) {

				holder.itemView.setBackgroundColor(mCtx.getResources()
						.getColor(R.color.red));
			} else {

				holder.itemView.setBackgroundColor(mCtx.getResources()
						.getColor(R.color.white));
			}
		}

		itemHolder.txtTime.setText(strTime);
		itemHolder.txtMessage.setText(strMessage);
		itemHolder.txtLastName.setText(strLastName);
		itemHolder.txtFirstName.setText(strFirstName);
		itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (isSearch) {

					mListener.onItemClick(conversation);
				} else {

					MessageEntity message = conversation.getMessages().get(0);
					if (!message.isRead()) {

						message.setRead(true);
						notifyDataSetChanged();
						new MessageApi()
								.getInterface()
								.markMessageRead(message.getId(), "1",
										message.getId())
								.enqueue(new Callback<BaseEntity>() {
									@Override
									public void onResponse(
											Call<BaseEntity> call,
											Response<BaseEntity> response) {

									}

									@Override
									public void onFailure(
											Call<BaseEntity> call, Throwable t) {

									}
								});
					}

					mListener.onItemClick(contact);
				}
			}
		});
	}
	@Override
	public int getItemCount() {

		return mConversations == null ? 1 : mConversations.size() + 1;
	}

	@Override
	public int getItemViewType(int position) {

		if (position == 0) {

			return TYPE_HEADER;
		}

		return 0;
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.conver_item_first_name)
		TextView txtFirstName;

		@Bind(R.id.conver_item_last_name)
		TextView txtLastName;

		@Bind(R.id.conver_item_message)
		TextView txtMessage;

		@Bind(R.id.conver_item_time)
		TextView txtTime;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}

	class HeaderHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.header_edt_search)
		EditText edtSearch;

		@Bind(R.id.header_btn_close)
		ImageView btnClose;

		public HeaderHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}

	private MyTextWatcher myTextWatcher = new MyTextWatcher();
	private class MyTextWatcher implements TextWatcher {

		private HeaderHolder mHolder;

		public void setHolder(HeaderHolder holder) {
			this.mHolder = holder;
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1,
				int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1,
				int i2) {

			mQuery = charSequence.toString();
			if (charSequence.length() > 0) {

				mListener.onSearch(mQuery);
				mHolder.btnClose.setVisibility(View.VISIBLE);
				return;
			}

			mListener.onSearch("");
			mHolder.btnClose.setVisibility(View.INVISIBLE);
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	}

	/**
	 * Set data
	 *
	 * @param conversations
	 *            contact
	 */
	public void setData(List<ConversationEntity> conversations) {

		mConversations = conversations;
		notifyDataSetChanged();
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}
}
