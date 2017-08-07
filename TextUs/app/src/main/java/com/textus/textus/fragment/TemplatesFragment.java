package com.textus.textus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.textus.textus.R;
import com.textus.textus.adapter.TemplateAdapter;
import com.textus.textus.api.MessageApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.MessageEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.LoadingDialog;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 5/24/16.
 */
public class TemplatesFragment extends BaseFragment
		implements
			OnItemClickListener {

	@Bind(R.id.template_recycler_view)
	RecyclerView recyclerView;

	public static TemplatesFragment getInstance() {

		return new TemplatesFragment();
	}

	@Override
	protected int addView() {

		return R.layout.fragment_templates;
	}

	@Override
	protected void initView() {
		super.initView();

		Utilities.setLayoutManager(getContext(), recyclerView, false, false);
		getTemplates();
	}

	@Override
	public void setUpToolBar() {
		super.setUpToolBar();

		txtRight.setVisibility(View.VISIBLE);
		txtTitle.setVisibility(View.VISIBLE);
		// txtLeft.setVisibility(View.INVISIBLE);
		imvTitle.setVisibility(View.INVISIBLE);
		// btnRight.setVisibility(View.INVISIBLE);
		txtRight.setEnabled(true);

		txtRight.setText(getString(R.string.done));
		txtTitle.setText(R.string.message_template);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_txt_right :

				backFragment(false);
				break;
		}
	}

	/**
	 * Get template
	 */
	private void getTemplates() {

		final Dialog dialog = LoadingDialog.show(mActivity);
		new MessageApi().getInterface().getTemplates(1, 25)
				.enqueue(new Callback<List<MessageEntity>>() {
					@Override
					public void onResponse(Call<List<MessageEntity>> call,
							Response<List<MessageEntity>> response) {

						displayTemplates(response.body());
						Utilities.dismissDialog(dialog);
					}

					@Override
					public void onFailure(Call<List<MessageEntity>> call,
							Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	/**
	 * Display templates
	 */
	private void displayTemplates(List<MessageEntity> templates) {

		TemplateAdapter adapter = new TemplateAdapter(templates, this);
		recyclerView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(BaseEntity model) {

		MessageEntity template = (MessageEntity) model;
		Intent intent = new Intent(Constants.INTENT_TEMPLATE_SELECTED);
		intent.putExtra(Constants.INTENT_TEMPLATE_CONTENT,
				template.getContent());
		LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
		backFragment(false);
	}

	@Override
	public void onSearch(String query) {

	}
}
