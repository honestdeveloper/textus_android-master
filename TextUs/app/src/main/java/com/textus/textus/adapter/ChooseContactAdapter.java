package com.textus.textus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.entity.ContactEntity;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 5/25/16.
 */
public class ChooseContactAdapter
		extends
			RecyclerView.Adapter<ChooseContactAdapter.ViewHolder> {

	private List<ContactEntity> mContacts;
	private OnItemClickListener mListener;

	public ChooseContactAdapter(List<ContactEntity> contacts,
			OnItemClickListener listener) {

		this.mContacts = contacts;
		this.mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.layout_item_choose_contact, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		final ContactEntity entity = mContacts.get(position);
		holder.txtNumber.setText(String.format("%s: %s", entity.getType(),
				entity.getNumber()));
		if (position == mContacts.size() - 1) {

			holder.sep.setVisibility(View.GONE);
		} else {

			holder.sep.setVisibility(View.VISIBLE);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(entity);
			}
		});
	}

	@Override
	public int getItemCount() {

		return mContacts == null ? 0 : mContacts.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.dialog_contact_txt)
		TextView txtNumber;

		@Bind(R.id.dialog_contact_sep)
		View sep;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
