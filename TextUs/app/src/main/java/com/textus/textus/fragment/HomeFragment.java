package com.textus.textus.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.textus.textus.R;
import com.textus.textus.activity.LoginActivity;
import com.textus.textus.adapter.ChooseContactAdapter;
import com.textus.textus.adapter.TabPagerAdapter;
import com.textus.textus.api.UserApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ContactEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.CustomPreferences;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 12/27/15.
 */
public class HomeFragment extends BaseFragment {

	@Bind(R.id.home_view_pager)
	ViewPager viewPager;

	@Bind(R.id.home_tab_layout)
	TabLayout tabLayout;

	public static HomeFragment getInstance() {

		return new HomeFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_home;
	}

	@Override
	protected void initView() {
		super.initView();

		TabPagerAdapter tabAdapter = new TabPagerAdapter(
				getChildFragmentManager(), getResources().getStringArray(
						R.array.home_title));
		viewPager.setAdapter(tabAdapter);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

				setUpIcons(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_btn_right :

				int page = viewPager.getCurrentItem();
				if (page == 1) {

					contactDialog();
					return;
				}

				logOut();
				break;
		}
	}

	@Override
	public void setUpToolBar() {
		super.setUpToolBar();

		int page = 0;
		if (viewPager != null) {

			page = viewPager.getCurrentItem();
		}

		btnRight.setVisibility(View.VISIBLE);
		imvTitle.setVisibility(View.VISIBLE);
		// txtLeft.setVisibility(View.INVISIBLE);
		txtTitle.setVisibility(View.INVISIBLE);
		// txtRight.setVisibility(View.INVISIBLE);
		setUpIcons(page);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {

			Uri contactData = data.getData();
			Cursor queryCursor = getActivity().getContentResolver().query(
					contactData, null, null, null, null);
			queryCursor.moveToFirst();
			String id = queryCursor.getString(queryCursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			ContactEntity contact = new ContactEntity();
			if (Integer
					.parseInt(queryCursor.getString(queryCursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

				Cursor pName = getActivity()
						.getContentResolver()
						.query(ContactsContract.Data.CONTENT_URI,
								null,
								ContactsContract.Data.MIMETYPE
										+ " = '"
										+ ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
										+ "' AND "
										+ ContactsContract.Data.CONTACT_ID
										+ " = ?", new String[]{id}, null);
				while (pName.moveToNext()) {

					String lastName = pName
							.getString(pName
									.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
					String firstName = pName
							.getString(pName
									.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
					contact.setFirstName(firstName);
					contact.setLastName(lastName);
				}

				pName.close();

				// Phone number
				Cursor pCur = getActivity().getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[]{id}, null);
				List<ContactEntity> numbers = new ArrayList<>();
				while (pCur.moveToNext()) {

					String number = pCur
							.getString(pCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					ContactEntity contactEntity = new ContactEntity();
					contactEntity.setNumber(number);

					int type = pCur
							.getInt(pCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
					switch (type) {

						case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :

							contactEntity.setType("Home");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :

							contactEntity.setType("Mobile");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :

							contactEntity.setType("Work");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN :

							contactEntity.setType("Main");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK :

							contactEntity.setType("Work Fax");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME :

							contactEntity.setType("Home Fax");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER :

							contactEntity.setType("Pager");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER :

							contactEntity.setType("Other");
							break;
						case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM :

							contactEntity.setType("Custom");
							break;
					}

					numbers.add(contactEntity);
				}

				contact.setNumbers(numbers);
				pCur.close();
			}

			queryCursor.close();
			checkContact(contact);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == Constants.INTENT_REQUEST_PERMISSION
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			openContact();
		}
	}

	/**
	 * Set up icon
	 */
	private void setUpIcons(int page) {

		switch (page) {

			case 0 :

				btnRight.setImageResource(R.drawable.ic_action_logout);
				break;

			case 1 :

				btnRight.setImageResource(R.drawable.ic_action_add);
				break;
		}
	}

	/**
	 * Log out
	 */
	private void logOut() {

        OneSignal.deleteTag("channel");
		CustomPreferences.setPreferences(Constants.PREF_PASSWORD, "");
		CustomPreferences.setPreferences(Constants.PREF_USERNAME, "");
		CustomPreferences.setPreferences(Constants.PREF_USER_ID, 0L);
		CustomPreferences.setPreferences(Constants.PREF_ACC_ID, 0L);
		startActivity(new Intent(getActivity(), LoginActivity.class));
		mActivity.finish();
	}

	/**
	 * Contact dialog
	 */
	private void contactDialog() {

		// View view = LayoutInflater.from(mActivity).inflate(
		// R.layout.dialog_contact, null);
		// AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		// builder.setView(view);
		final Dialog dialog = new Dialog(mActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_contact);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LinearLayout view = ButterKnife.findById(dialog, R.id.dialog_ll_root);
		TextView btnCancel = ButterKnife.findById(dialog,
				R.id.dialog_contact_btn_cancel);
		TextView btnImport = ButterKnife.findById(dialog,
				R.id.dialog_contact_btn_import);
		TextView btnAdd = ButterKnife.findById(dialog,
				R.id.dialog_contact_btn_add);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				dialog.dismiss();
			}
		});

		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				addFragment(NewContactFragment.getInstance(null), true);
				dialog.dismiss();
			}
		});

		btnImport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				dialog.dismiss();
				String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
				if (!Utilities.hasPermissions(mActivity, PERMISSIONS)) {

					requestPermissions(PERMISSIONS,
							Constants.INTENT_REQUEST_PERMISSION);
					return;
				}

				openContact();
			}
		});

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * Choose number to import
	 * 
	 * @param contact
	 *            Contact
	 */
	private void chooseNumberDialog(final ContactEntity contact) {

		final Dialog dialog = new Dialog(mActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_choose_number);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LinearLayout view = ButterKnife.findById(dialog, R.id.dialog_ll_root);
		TextView btnCancel = ButterKnife.findById(dialog,
				R.id.dialog_contact_btn_cancel);
		RecyclerView recyclerView = ButterKnife.findById(dialog,
				R.id.dialog_contact_recycle_view);
		Utilities.setLayoutManager(getContext(), recyclerView, true, false);
		ChooseContactAdapter adapter = new ChooseContactAdapter(
				contact.getNumbers(), new OnItemClickListener() {
					@Override
					public void onItemClick(BaseEntity model) {

						ContactEntity contactEntity = (ContactEntity) model;
						contact.getNumbers().add(0, contactEntity);
						importContact(contact);
						dialog.dismiss();
					}

					@Override
					public void onSearch(String query) {

					}
				});
		recyclerView.setAdapter(adapter);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				dialog.dismiss();
			}
		});

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * Open contact
	 */
	private void openContact() {

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		startActivityForResult(intent, 10);
	}

	/**
	 * Import contact
	 * 
	 * @param contact
	 *            contact info
	 */
	private void checkContact(ContactEntity contact) {

		if (contact == null) {

			return;
		}

		if (contact.getNumbers() == null || contact.getNumbers().size() == 0) {

			Utilities.showAlertDialog(mActivity, "",
					getString(R.string.no_phone_number),
					getString(R.string.cancel), "", null, null, false);
			return;
		}

		if (contact.getNumbers().size() > 1) {

			chooseNumberDialog(contact);
			return;
		}

		importContact(contact);
	}

	/**
	 * Import contact
	 */
	private void importContact(final ContactEntity contact) {

		ContactEntity number = contact.getNumbers().get(0);
		String strNumber = number.getNumber();
		strNumber = strNumber.replaceAll("\\D+", "");

		// only add the "1" if it's not already there
		if (strNumber.charAt(0) != '1') {

			strNumber = String.format("1%s", strNumber);
		}

		// if the new number string length does not equal 11 (10 digit number
		// with "1" country code)
		// then alert the user and return
		if (strNumber.length() != 11) {

			Utilities.showAlertDialog(mActivity, "",
					getString(R.string.number_must_be_10),
					getString(R.string.ok), "", null, null, false);
			return;
		}

		// number = "15555648265";
		number.setNumber(strNumber);
		contact.getNumbers().set(0, number);
		new UserApi().getInterface().importContact(strNumber)
				.enqueue(new Callback<List<UserEntity>>() {
					@Override
					public void onResponse(Call<List<UserEntity>> call,
							Response<List<UserEntity>> response) {

						boolean isExists = false;
						if (response.body() != null
								&& response.body().size() > 0) {

							for (UserEntity user : response.body()) {

								if (user.getPhone()
										.equals(contact.getNumbers().get(0)
												.getNumber())) {

									isExists = true;
									break;
								}
							}
						}

						if (isExists) {

							Utilities.showAlertDialog(mActivity, "",
									getString(R.string.contact_exists),
									getString(R.string.ok), "", null, null,
									false);
							return;
						}

						updateContact(contact);
					}

					@Override
					public void onFailure(Call<List<UserEntity>> call,
							Throwable t) {

					}
				});
	}

	/**
	 * Update contact
	 * 
	 * @param contact
	 *            Contact
	 */
	private void updateContact(ContactEntity contact) {

		new UserApi()
				.getInterface()
				.importContact(contact.getNumbers().get(0).getNumber(),
						contact.getFirstName(), contact.getLastName(),
						contact.getBusinessName())
				.enqueue(new Callback<UserEntity>() {
					@Override
					public void onResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.showAlertDialog(mActivity, "",
								getString(R.string.contact_imported),
								getString(R.string.ok), "", null, null, false);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

					}
				});
	}
}
