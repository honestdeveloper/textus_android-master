package com.textus.textus.adapter;

import java.util.List;

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

import butterknife.Bind;
import butterknife.ButterKnife;

import com.textus.textus.R;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.view.viewinterface.OnItemClickListener;

/**
 * Hai Nguyen - 12/27/15.
 */
public class ContactAdapter
		extends
			RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int TYPE_HEADER = 1;

	// private Context mCtx;
	private String mQuery;
	private LayoutInflater mInflater;
	private List<UserEntity> mContacts;
	private OnItemClickListener mListener;
	public ContactAdapter(Context ctx, OnItemClickListener listener,
			List<UserEntity> contacts) {

		// this.mCtx = ctx;
		this.mListener = listener;
		this.mContacts = contacts;
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

			View view = mInflater.inflate(R.layout.layout_item_contact, parent,
					false);
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

				headerHolder.btnClose.setVisibility(View.VISIBLE);
				headerHolder.edtSearch.setSelection(mQuery.length());
				headerHolder.edtSearch.requestFocus();
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

		final UserEntity contact = mContacts.get(position - 1);
		ViewHolder itemHolder = (ViewHolder) holder;
		itemHolder.txtLastName.setText(contact.getLastName());
		itemHolder.txtFirstName.setText(contact.getFirstName());
		itemHolder.btnDetail.setVisibility(View.VISIBLE);
		itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(contact);
			}
		});
	}

	@Override
	public int getItemCount() {

		return mContacts == null ? 1 : mContacts.size() + 1;
	}

	@Override
	public int getItemViewType(int position) {

		if (position == 0) {

			return TYPE_HEADER;
		}

		return 0;
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.contact_item_first_name)
		TextView txtFirstName;

		@Bind(R.id.contact_item_last_name)
		TextView txtLastName;

		@Bind(R.id.contact_item_detail)
		ImageView btnDetail;

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

		private ContactAdapter.HeaderHolder mHolder;

		public void setHolder(ContactAdapter.HeaderHolder holder) {
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
	 * @param contacts
	 *            contact
	 */
	public void setData(List<UserEntity> contacts) {

		mContacts = contacts;
		notifyDataSetChanged();
	}
}
