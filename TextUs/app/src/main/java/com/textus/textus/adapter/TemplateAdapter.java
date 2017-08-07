package com.textus.textus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.entity.MessageEntity;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 5/24/16.
 */
public class TemplateAdapter
		extends
			RecyclerView.Adapter<TemplateAdapter.ViewHolder> {

	private List<MessageEntity> mTemplates;
	private OnItemClickListener mListener;

	public TemplateAdapter(List<MessageEntity> templates,
			OnItemClickListener listener) {

		this.mListener = listener;
		this.mTemplates = templates;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.layout_item_template, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		final MessageEntity template = mTemplates.get(position);
		holder.txtContent.setText(template.getContent());
		holder.txtTitle.setText(template.getTitle());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(template);
			}
		});
	}

	@Override
	public int getItemCount() {

		return mTemplates == null ? 0 : mTemplates.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.template_item_txt_content)
		TextView txtContent;

		@Bind(R.id.template_item_txt_title)
		TextView txtTitle;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
