package com.textus.textus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.textus.textus.R;
import com.textus.textus.api.UserApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.CustomPreferences;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.LoadingDialog;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/27/15.
 */
public class LoginActivity extends BaseActivity implements TextWatcher {

	@Bind(R.id.login_btn_login)
	TextView btnLogin;

	@Bind(R.id.login_edt_email)
	EditText edtEmail;

	@Bind(R.id.login_edt_password)
	EditText edtPassword;

	@Override
	protected int addView() {
		return R.layout.activity_login;
	}

	@Override
	protected void init(@Nullable Bundle savedInstanceState) {
		super.init(savedInstanceState);

		btnLogin.setOnClickListener(this);
		edtEmail.addTextChangedListener(this);
		edtPassword.addTextChangedListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.login_btn_login :

				Utilities.hideSoftKeyBoard(LoginActivity.this);
				doLogin();
				break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		String strEmail = edtEmail.getText().toString().trim();
		String strPassword = edtPassword.getText().toString().trim();
		if (strEmail.equals("") || strPassword.equals("")) {

			btnLogin.setEnabled(false);
			return;
		}

		btnLogin.setEnabled(true);
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	/**
	 * Do login
	 */
	private void doLogin() {

		String strPassword = edtPassword.getText().toString();
		String strEmail = edtEmail.getText().toString().toLowerCase();
		CustomPreferences.setPreferences(Constants.PREF_USERNAME, strEmail);
		CustomPreferences.setPreferences(Constants.PREF_PASSWORD, strPassword);
		final LoadingDialog dialog = LoadingDialog.show(this);
		new UserApi().getInterface().doLogin()
				.enqueue(new Callback<UserEntity>() {
					@Override
					public void onResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						finishLogin(response);
						Utilities.dismissDialog(dialog);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						finishLogin(null);
						Utilities.dismissDialog(dialog);
					}
				});
	}

	/**
	 * Finish login
	 * 
	 * @param response
	 *            response
	 */
	private void finishLogin(Response<UserEntity> response) {

		if (response == null || response.body() == null) {

			CustomPreferences.setPreferences(Constants.PREF_USERNAME, "");
			CustomPreferences.setPreferences(Constants.PREF_PASSWORD, "");
			CustomPreferences.setPreferences(Constants.PREF_USER_ID, 0L);
			CustomPreferences.setPreferences(Constants.PREF_ACC_ID, 0L);
			Utilities.showAlertDialog(LoginActivity.this,
					getString(R.string.login_fail),
					getString(R.string.try_again), getString(R.string.ok), "",
					null, null, false);
			edtPassword.setText("");
			edtPassword.requestFocus();
			return;
		}

		CustomPreferences.setPreferences(Constants.PREF_USER_ID, response
				.body().getId());
		CustomPreferences.setPreferences(Constants.PREF_ACC_ID, response.body()
				.getAccountId());
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
