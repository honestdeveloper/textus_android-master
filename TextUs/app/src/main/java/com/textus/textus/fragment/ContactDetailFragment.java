package com.textus.textus.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.api.UserApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.Utilities;

import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/29/15.
 */
public class ContactDetailFragment extends BaseFragment
		implements
			CompoundButton.OnCheckedChangeListener {

	private long mOptId;
	private UserEntity mContact;

	@Bind(R.id.detail_txt_last_name)
	TextView txtLastName;

	@Bind(R.id.detail_txt_first_name)
	TextView txtFirstName;

	@Bind(R.id.detail_txt_phone)
	TextView txtPhone;

	@Bind(R.id.detail_txt_business)
	TextView txtBusiness;

	@Bind(R.id.detail_btn_call)
	ImageView btnCall;

	@Bind(R.id.detail_btn_msg)
	ImageView btnMsg;

	@Bind(R.id.detail_sw_opt)
	Switch mSwitch;

	public static ContactDetailFragment getInstance(UserEntity contact) {

		ContactDetailFragment fragment = new ContactDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contact", contact);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_contact_detail;
	}

	@Override
	protected void initView() {
		super.initView();

		mContact = (UserEntity) getArguments().getSerializable("contact");
		if (mContact == null) {

			backFragment(false);
			return;
		}

		txtPhone.setText(mContact.getPhone());
		txtLastName.setText(mContact.getLastName());
		txtFirstName.setText(mContact.getFirstName());
		txtBusiness.setText(mContact.getBusinessName());

		btnMsg.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		mSwitch.setOnCheckedChangeListener(this);
		getOptOutRecord();
	}

	@Override
	public void setUpToolBar() {
		super.setUpToolBar();

		// txtLeft.setVisibility(View.VISIBLE);
		txtRight.setVisibility(View.VISIBLE);
		// txtLeft.setVisibility(View.INVISIBLE);
		txtTitle.setVisibility(View.INVISIBLE);
		// btnRight.setVisibility(View.INVISIBLE);
		imvTitle.setVisibility(View.INVISIBLE);
		txtRight.setEnabled(true);

		txtRight.setText(R.string.edit);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == Constants.INTENT_REQUEST_PERMISSION
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			showCallDialog();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_txt_right :

				addFragment(NewContactFragment.getInstance(mContact), true);
				break;
			case R.id.detail_btn_call :

				String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
				if (!Utilities.hasPermissions(mActivity, PERMISSIONS)) {

					requestPermissions(PERMISSIONS,
							Constants.INTENT_REQUEST_PERMISSION);
					return;
				}

				showCallDialog();
				break;

			case R.id.detail_btn_msg :

				addFragment(ChatFragment.getInstance(mContact), true);
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton,
			final boolean checked) {

		mContact.setOptedOut(checked);
		if (!checked && mOptId != 0) {

			new UserApi().getInterface().deleteOpt(mOptId, mOptId)
					.enqueue(new Callback<BaseEntity>() {
						@Override
						public void onResponse(Call<BaseEntity> call,
								Response<BaseEntity> response) {

						}

						@Override
						public void onFailure(Call<BaseEntity> call, Throwable t) {

						}
					});
			return;
		}

		new UserApi().getInterface().optOutUser(mContact.getPhone())
				.enqueue(new Callback<BaseEntity>() {
					@Override
					public void onResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

					}
				});
	}

	/**
	 * Show call dialog
	 */
	private void showCallDialog() {

		Utilities.showAlertDialog(getContext(), "", mContact.getPhone(),
				getString(R.string.ok), getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"
								+ mContact.getPhone()));
						startActivity(callIntent);
					}
				}, null, false);
	}

	/**
	 *
	 * Get opt out record
	 */
	private void getOptOutRecord() {

		new UserApi().getInterface().getOptOut(mContact.getPhone())
				.enqueue(new Callback<List<BaseEntity>>() {
					@Override
					public void onResponse(Call<List<BaseEntity>> call,
							Response<List<BaseEntity>> response) {

						List<BaseEntity> opts = response.body();
						if (opts == null || opts.size() == 0) {

							return;
						}

						mOptId = opts.get(0).getId();
						mSwitch.setChecked(true);
					}

					@Override
					public void onFailure(Call<List<BaseEntity>> call,
							Throwable t) {

						mOptId = 0;
					}
				});
	}
}
