package com.textus.textus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.textus.textus.R;
import com.textus.textus.api.UserApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.LoadingDialog;

import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/28/15.
 */
public class NewContactFragment extends BaseFragment {

	@Bind(R.id.add_contact_edt_last)
	EditText edtLast;

	@Bind(R.id.add_contact_edt_first)
	EditText edtFirst;

	@Bind(R.id.add_contact_edt_phone)
	EditText edtPhone;

	@Bind(R.id.add_contact_edt_name)
	EditText edtName;

	private UserEntity mUser;

	public static NewContactFragment getInstance(UserEntity user) {

		NewContactFragment fragment = new NewContactFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("user_model", user);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {

		return R.layout.fragment_new_contact;
	}

	@Override
	protected void initView() {
		super.initView();

		mUser = (UserEntity) getArguments().getSerializable("user_model");
		edtPhone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {

				if (txtRight != null) {

					txtRight.setEnabled(charSequence.length() == 10
							|| charSequence.length() == 11);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		if (mUser == null) {

			return;
		}

		edtPhone.setText(mUser.getPhone());
		edtLast.setText(mUser.getLastName());
		edtFirst.setText(mUser.getFirstName());
		edtName.setText(mUser.getBusinessName());
	}

	@Override
	public void setUpToolBar() {
		super.setUpToolBar();

		txtLeft.setVisibility(View.VISIBLE);
		txtRight.setVisibility(View.VISIBLE);
		txtTitle.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.INVISIBLE);
		imvTitle.setVisibility(View.INVISIBLE);

		txtRight.setText(R.string.save);
		txtLeft.setText(R.string.cancel);
		if (mUser != null) {

			txtTitle.setText(String.format("%s %s", mUser.getFirstName(),
					mUser.getLastName()));
			txtRight.setEnabled(mUser.getPhone().length() == 10
					|| mUser.getPhone().length() == 11);
			return;
		} else {

			txtRight.setEnabled(false);
		}

		txtTitle.setText(R.string.new_contact);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_txt_left :

				backFragment(false);
				break;
			case R.id.toolbar_txt_right :

				saveUser();
				break;
		}
	}

	/**
	 * 
	 * Update or create user
	 */
	private void saveUser() {

		Utilities.hideSoftKeyBoard(getActivity());
		final String strName = edtName.getText().toString().trim();
		String strPhone = edtPhone.getText().toString().trim();
		final String strLastName = edtLast.getText().toString().trim();
		final String strFirstName = edtFirst.getText().toString().trim();
		final LoadingDialog dialog = LoadingDialog.show(getContext());
		if (mUser == null) {

			// only add the "1" if it's not already there
			if (strPhone.charAt(0) != '1') {

				strPhone = String.format("1%s", strPhone);
			}

			final String finalStrPhone = strPhone;
			new UserApi().getInterface().importContact(strPhone)
					.enqueue(new Callback<List<UserEntity>>() {
						@Override
						public void onResponse(Call<List<UserEntity>> call,
								Response<List<UserEntity>> response) {

							boolean isExists = false;
							if (response.body() != null
									&& response.body().size() > 0) {

								for (UserEntity user : response.body()) {

									if (user.getPhone().equals(finalStrPhone)) {

										isExists = true;
										break;
									}
								}
							}

							if (isExists) {

								dialog.dismiss();
								Utilities.showAlertDialog(mActivity, "",
										getString(R.string.contact_exists),
										getString(R.string.ok), "", null, null,
										false);
								return;
							}

							addContact(dialog, finalStrPhone, strFirstName,
									strLastName, strName);
						}

						@Override
						public void onFailure(Call<List<UserEntity>> call,
								Throwable t) {

							dialog.dismiss();
						}
					});
			return;
		}

		new UserApi()
				.getInterface()
				.editContact(mUser.getId(), strPhone, strFirstName,
						strLastName, strName)
				.enqueue(new Callback<UserEntity>() {
					@Override
					public void onResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						finishSave(response.body() != null);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						finishSave(false);
						dialog.dismiss();
					}
				});
	}

	private void addContact(final Dialog dialog, String... values) {

		new UserApi().getInterface()
				.addContact(values[0], values[1], values[2], values[3])
				.enqueue(new Callback<UserEntity>() {
					@Override
					public void onResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						finishSave(response.body() != null);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						finishSave(false);
						dialog.dismiss();
					}
				});
	}

	/**
	 * 
	 * Finish save
	 * 
	 * @param success
	 *            is success
	 */
	private void finishSave(boolean success) {

		if (success) {

			Intent intent = new Intent(Constants.INTENT_SAVE_CONTACT);
			intent.putExtra(Constants.INTENT_IS_NEW, mUser == null);
			LocalBroadcastManager.getInstance(getContext()).sendBroadcast(
					intent);
			backFragment(false);
			return;
		}

		Utilities.showAlertDialog(getContext(), "",
				getString(R.string.invalid_response), getString(R.string.ok),
				"", null, null, false);
	}
}
