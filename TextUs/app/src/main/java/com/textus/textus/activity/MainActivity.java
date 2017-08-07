package com.textus.textus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onesignal.OneSignal;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.util.HttpAuthorizer;
import com.textus.textus.R;
import com.textus.textus.api.MessageApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.fragment.BaseFragment;
import com.textus.textus.fragment.ChatFragment;
import com.textus.textus.fragment.HomeFragment;
import com.textus.textus.utility.CustomPreferences;
import com.textus.textus.utility.LogUtil;
import com.textus.textus.utility.Utilities;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 11/2/15.
 */
public class MainActivity extends BaseActivity {

	@Override
	protected int addView() {
		return R.layout.activity_main;
	}

	@Override
	protected void init(@Nullable Bundle savedInstanceState) {
		super.init(savedInstanceState);
		Context context = this;
		initImageLoader(context);
		if (savedInstanceState == null) {

			addFragment(HomeFragment.getInstance(), false);
		}

		setUpParse();
		setUpPusher();
		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {
					public void onBackStackChanged() {

						// BaseFragment fragment = (BaseFragment)
						// getSupportFragmentManager()
						// .findFragmentById(R.id.activity_container);
						// if (fragment != null) {
						//
						// fragment.setUpToolBar(toolBar);
						// }

						Utilities.hideSoftKeyBoard(MainActivity.this);
					}
				});
	}

	/**
	 * add fragment
	 *
	 * @param fragment
	 *            Fragment
	 * @param isAddToBackStack
	 *            Add fragment to back stack or not
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(500 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	public void addFragment(BaseFragment fragment, boolean isAddToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.setCustomAnimations(R.anim.trans_enter_from_left,
		// R.anim.trans_exit_to_right, R.anim.trans_enter_from_right,
		// R.anim.trans_exit_to_left);
		ft.setCustomAnimations(R.anim.trans_enter_from_right,
				R.anim.trans_exit_to_left, R.anim.trans_enter_from_left,
				R.anim.trans_exit_to_right);
		ft.add(R.id.activity_container, fragment, fragment.getClass().getName());
		if (isAddToBackStack) {

			ft.addToBackStack(null);
		}

		BaseFragment oldFragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		if (oldFragment != null) {

			ft.hide(oldFragment);
		}

		ft.commit();
	}

	/**
	 * add fragment
	 *
	 * @param fragment
	 *            Fragment
	 * @param isAddToBackStack
	 *            Add fragment to back stack or not
	 */
	public void addFragment(BaseFragment fragment, boolean isAddToBackStack,
			int enter, int exit, int popEnter, int popExit) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(enter, exit, popEnter, popExit);
		ft.add(R.id.activity_container, fragment, fragment.getClass().getName());
		if (isAddToBackStack) {

			ft.addToBackStack(null);
		}

		BaseFragment oldFragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		if (oldFragment != null) {

			ft.hide(oldFragment);
		}

		ft.commit();
	}

	/**
	 * Back to previous fragment
	 */
	public void backFragment(boolean backToHome) {

		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() <= 0) {

			finish();
			return;
		}

		if (backToHome) {

			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {

			fm.popBackStack();
		}
	}

	@Override
	public void onBackPressed() {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		if (fragment instanceof HomeFragment) {

			finish();
			return;
		}

		// backFragment(false);
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				updateContactReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();

		CustomPreferences.setPreferences(Constants.PREF_IS_APP_ACTIVE, false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		CustomPreferences.setPreferences(Constants.PREF_IS_APP_ACTIVE, true);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				updateContactReceiver,
				new IntentFilter(Constants.INTENT_SAVE_CONTACT));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// /**
	// * Request user permission
	// */
	// private void requestPermission() {
	//
	// String[] PERMISSIONS = {Manifest.permission.CALL_PHONE,
	// Manifest.permission.READ_CONTACTS};
	// if (!Utilities.hasPermissions(this, PERMISSIONS)) {
	//
	// ActivityCompat.requestPermissions(this, PERMISSIONS, 100);
	// }
	// }

	private BroadcastReceiver updateContactReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			boolean isNew = intent.getBooleanExtra(Constants.INTENT_IS_NEW,
					false);
			String msg;
			if (isNew) {

				msg = getString(R.string.contact_created);
			} else {

				msg = getString(R.string.contact_updated);
			}

			Utilities.showAlertDialog(MainActivity.this, "", msg,
					getString(R.string.ok), "", null, null, false);
		}
	};

	/**
	 * Set up parse
	 */
	private void setUpParse() {

		long myId = CustomPreferences
				.getPreferences(Constants.PREF_USER_ID, 0L);
		String strChanel = String.format("private-user_%s", myId);
		// List<String> channels = new ArrayList<>();
		// channels.add(strChanel);
		// installation.put("channels", channels);
		// LogUtil.e("Parse chanel", strChanel);
		// installation.saveInBackground();
		OneSignal.sendTag("channel", strChanel);
	}

	/**
	 * Set up pusher
	 */
	private void setUpPusher() {

		String strUserName = CustomPreferences.getPreferences(
				Constants.PREF_USERNAME, "");
		String strPassword = CustomPreferences.getPreferences(
				Constants.PREF_PASSWORD, "");

		Map<String, String> header = new HashMap<>();
		header.put("Authorization", Credentials.basic(strUserName, strPassword));
		header.put("Accept", "application/vnd.textus-v2+json");

		HttpAuthorizer authorizer = new HttpAuthorizer(
				"https://app.textus.com/api/channels/authenticate");
		authorizer.setHeaders(header);
		PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
		Pusher pusher = new Pusher("3c8939d731a24a81a6e6", options);

		long myId = CustomPreferences.getPreferences(Constants.PREF_ACC_ID, 0L);
		String strChannel = String.format("private-account_%s", myId);
		pusher.subscribePrivate(strChannel, new PrivateChannelEventListener() {
			@Override
			public void onAuthenticationFailure(String message, Exception e) {

				LogUtil.e("onAuthenticationFailure", message);
			}

			@Override
			public void onSubscriptionSucceeded(String channelName) {

				LogUtil.e("onSubscriptionSucceeded", channelName);
			}

			@Override
			public void onEvent(String channelName, String eventName,
					String data) {

				JsonObject jObject = new Gson()
						.fromJson(data, JsonObject.class);
				jObject = jObject.getAsJsonObject("content");
				jObject = jObject.getAsJsonObject("message");
				final ConversationEntity chat = new Gson().fromJson(
						jObject.toString(), ConversationEntity.class);

				final BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
						.findFragmentById(R.id.activity_container);
				if (fragment == null) {

					return;
				}

				new MessageApi().getInterface()
						.markMessageRead(chat.getId(), "1", chat.getId())
						.enqueue(new Callback<BaseEntity>() {
							@Override
							public void onResponse(Call<BaseEntity> call,
									Response<BaseEntity> response) {

								Intent intent = new Intent(
										Constants.INTENT_NEW_MESSAGE);
								if (fragment instanceof ChatFragment) {

									intent.putExtra(
											Constants.INTENT_NEW_MESSAGE_CONTENT,
											chat);
								}

								LocalBroadcastManager.getInstance(
										MainActivity.this)
										.sendBroadcast(intent);
							}

							@Override
							public void onFailure(Call<BaseEntity> call,
									Throwable t) {

							}
						});
			}
		}, "inbound-message");

		pusher.connect();
	}
}
