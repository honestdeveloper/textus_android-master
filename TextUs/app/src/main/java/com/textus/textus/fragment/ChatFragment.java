package com.textus.textus.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.textus.textus.R;
import com.textus.textus.activity.MainActivity;
import com.textus.textus.adapter.ChatAdapter;
import com.textus.textus.api.MessageApi;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.entity.UserEntity;
import com.textus.textus.utility.CustomPreferences;
import com.textus.textus.utility.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.loopj.android.http.*;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import cz.msebera.android.httpclient.*;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.mode;
import static com.google.android.gms.analytics.internal.zzy.s;
import static com.textus.textus.constant.Constants.imgView;

/**
 * Hai Nguyen - 12/29/15.
 */
public class ChatFragment extends BaseFragment implements
		OnItemClickListener {

	@Bind(R.id.chat_recycler_view)
	RecyclerView recyclerView;

	@Bind(R.id.chat_btn_send)
	ImageView btnSend;

	@Bind(R.id.chat_btn_template)
	ImageView btnTemplate;

	@Bind(R.id.chat_btn_camera)
	ImageView btnCamera;

	@Bind(R.id.chat_btn_expand)
	ImageView btnExpand;

	@Bind(R.id.chat_edt_msg)
	EditText edtMsg;

	@Bind(R.id.image_view)
	ImageView imageView;

	private static final int CAMERA_REQUEST = 1888;
	private static final int SELECT_FILE = 1999;

	private final String LAST_NAME_KEY = "{{contact.last_name}}";
	private final String FIRST_NAME_KEY = "{{contact.first_name}}";
	private final String BUSINESS_NAME_KEY = "{{contact.business_name}}";

	private UserEntity mContact;
	private ChatAdapter mAdapter;

	private AsyncHttpClient client;
	private String imgPath = "";
	Context context;
	String credential;
	public static ChatFragment getInstance(UserEntity user) {

		ChatFragment fragment = new ChatFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contacts", user);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	protected int addView() {

		return R.layout.fragment_chat;
	}

	@Override
	protected void initView() {
		super.initView();
		context = getActivity();
		mContact = (UserEntity) getArguments().getSerializable("contacts");
		if (mContact == null) {

			return;
		}

		String strUserName = CustomPreferences.getPreferences(
				Constants.PREF_USERNAME, "");
		String strPassword = CustomPreferences.getPreferences(
				Constants.PREF_PASSWORD, "");
		credential = Credentials.basic(strUserName, strPassword);

		LinearLayoutManager manager = (LinearLayoutManager) Utilities
				.setLayoutManager(getContext(), recyclerView, false, false);
		mAdapter = new ChatAdapter(getContext(), this, mContact.getId());
		recyclerView.setAdapter(mAdapter);
		manager.setReverseLayout(true);
		btnSend.setOnClickListener(this);
		btnTemplate.setOnClickListener(this);
		btnCamera.setOnClickListener(this);
		btnExpand.setOnClickListener(this);

		imageView.setOnClickListener(this);
		getChatHistory();
	}

	@Override
	public void setUpToolBar() {
		super.setUpToolBar();

		txtRight.setVisibility(View.VISIBLE);
		txtRight.setEnabled(true);

		txtRight.setText(getString(R.string.detail));
		txtTitle.setText(String.format("%s %s", mContact.getFirstName(),
				mContact.getLastName()));
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_txt_right :
				addFragment(ContactDetailFragment.getInstance(mContact), true);
				break;
			case R.id.chat_btn_send :
				postImage();
				break;
			case R.id.chat_btn_expand:
				btnCamera.setVisibility(View.VISIBLE);
				btnTemplate.setVisibility(View.VISIBLE);
				btnExpand.setVisibility(View.GONE);
				break;
			case R.id.chat_btn_camera:
				selectImage();
				break;
			case R.id.chat_btn_template :
				addFragment(TemplatesFragment.getInstance(), true,
						R.anim.trans_enter_from_bottom, R.anim.trans_stand,
						R.anim.trans_stand, R.anim.trans_exit_to_bottom);
				break;
			case R.id.image_view:
				imageView.setVisibility(View.INVISIBLE);
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(mActivity).registerReceiver(
				mUpdateMessageReceiver,
				new IntentFilter(Constants.INTENT_NEW_MESSAGE));
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(
				mTemplateReceiver,
				new IntentFilter(Constants.INTENT_TEMPLATE_SELECTED));
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(
				mUpdateMessageReceiver);
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(
				mTemplateReceiver);
	}

	/**
	 * Get chat history
	 */
	private void getChatHistory() {
		new MessageApi().getInterface()
			.getChatHistory(mContact.getId(), 1, 25, mContact.getId())
			.enqueue(new Callback<List<ConversationEntity>>() {
				@Override
				public void onResponse(Call<List<ConversationEntity>> call,
						Response<List<ConversationEntity>> response) {

					mAdapter.setChats(response.body());
				}

				@Override
				public void onFailure(Call<List<ConversationEntity>> call,
						Throwable t) {

				}
			});
	}

	/**
	 * Send message
	 */
	private void sendMessage() {

		String strMsg = edtMsg.getText().toString().trim();
		if (strMsg.equals("")) {
			return;
		}

		edtMsg.setText("");
		long myId = CustomPreferences
				.getPreferences(Constants.PREF_USER_ID, 0L);
		long cId = mContact.getId();
		new MessageApi().getInterface()
				.sendMessage(strMsg, myId, mContact.getId())
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

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				boolean result=Utilities.checkPermission(getActivity());
				if (items[item].equals("Take Photo")) {
//                    userChoosenTask ="Take Photo";
//                    if(result)
					cameraIntent();

				} else if (items[item].equals("Choose from Library")) {
//                    userChoosenTask ="Choose from Library";
//                    if(result)
					galleryIntent();

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void galleryIntent()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
	}

	public void cameraIntent() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	public void postImage()
	{
		RequestParams params = new RequestParams();
		long myId = CustomPreferences
				.getPreferences(Constants.PREF_USER_ID, 0L);
		long cId = mContact.getId();
		String strMsg = edtMsg.getText().toString().trim();
		if (strMsg.equals("")) {
			if(imgPath.equals("")) return;
			else strMsg = "[Image Attached]";
		}
		if(!imgPath.equals("")) {
			try {
				params.put("file", new File(imgPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		edtMsg.setText("");
		imgPath = "";
		params.put("sender", myId);
		params.put("receiver", cId);
		params.put("content",strMsg);

		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", credential);
		client.post("https://app.textus.com/api/messages", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("State", "Send Message");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				System.out.println("statusCode "+statusCode);//statusCode 200
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				System.out.println("Failure");
			}
		});
	}

	private Bitmap getScaledBitmap(Bitmap bmp)
	{
		edtMsg.measure(0, 0);
		int imageWidth = bmp.getWidth();
		int imageHeight = bmp.getHeight();
		int width = edtMsg.getMeasuredWidth();

		if(width >= imageWidth)
			return bmp;

		float scaleFactor = (float)width/(float)imageWidth;
		int newHeight = (int)(imageHeight * scaleFactor);

		Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, width, newHeight, true);
		return scaledBmp;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				Bitmap bm=null;
				String strMessage = " ";
				if(!edtMsg.getText().equals(""))
					strMessage = edtMsg.getText().toString();
				if (data != null) {
					try {
						bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				ImageSpan span = new ImageSpan(getActivity(), bm);
				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append("").append(" ");
				builder.setSpan(new ImageSpan(getActivity(), getScaledBitmap(bm)),
						builder.length() - 1, builder.length(), 0);
				builder.append("\r\n" + strMessage);
				edtMsg.setText(builder);

				File f = new File(context.getCacheDir(), "capture.jpg");
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, 20, bos);
				byte[] bitmapdata = bos.toByteArray();

				FileOutputStream fos;
				try {
					fos = new FileOutputStream(f);
					try {
						fos.write(bitmapdata);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				imgPath = context.getCacheDir() + "/" + "capture.jpg";
				//text.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.check_box), null);
			}
			else if (requestCode == CAMERA_REQUEST)
			{
				String strMessage = " ";
				if(!edtMsg.getText().equals(""))
					strMessage = edtMsg.getText().toString();
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append("").append(" ");
				builder.setSpan(new ImageSpan(getActivity(), bm),
						builder.length() - 1, builder.length(), 0);

				builder.append("\r\n" + strMessage);
				edtMsg.setText(builder);

				File f = new File(context.getCacheDir(), "capture.jpg");
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 50, bos);
				byte[] bitmapdata = bos.toByteArray();

				FileOutputStream fos;
				try {
					fos = new FileOutputStream(f);
					try {
						fos.write(bitmapdata);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				imgPath = context.getCacheDir() + "/" + "capture.jpg";
			}
		}
	}

	private BroadcastReceiver mUpdateMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			ConversationEntity chat = (ConversationEntity) intent
					.getSerializableExtra(Constants.INTENT_NEW_MESSAGE_CONTENT);
			if (chat == null) {

				Utilities.playSound(getContext(), R.raw.received);
				getChatHistory();
				return;
			}

			if (chat.getReceiverId() == mContact.getId()) {
				Log.d("State", "Push Send");
				Utilities.playSound(getContext(), R.raw.sent);
			} else {

				Utilities.playSound(getContext(), R.raw.received);
			}
			mAdapter.addChat(chat);
		}
	};

	@Override
	public void onItemClick(BaseEntity model) {
		//addFragment(ContactDetailFragment.getInstance((UserEntity) model), true);
		Bitmap bmSrc1 = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
		imageView.setImageBitmap(bmSrc1);
		imageView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSearch(String query) {

	}

	private BroadcastReceiver mTemplateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String strTemplate = intent
					.getStringExtra(Constants.INTENT_TEMPLATE_CONTENT);
			if (mContact.getFirstName() != null
					&& !mContact.getFirstName().isEmpty()) {

				strTemplate = strTemplate.replace(FIRST_NAME_KEY,
						mContact.getFirstName());
			}

			if (mContact.getLastName() != null
					&& !mContact.getLastName().isEmpty()) {

				strTemplate = strTemplate.replace(LAST_NAME_KEY,
						mContact.getLastName());
			}

			if (mContact.getBusinessName() != null
					&& !mContact.getBusinessName().isEmpty()) {

				strTemplate = strTemplate.replace(BUSINESS_NAME_KEY,
						mContact.getBusinessName());
			}

			edtMsg.setText(strTemplate);
			edtMsg.setSelection(strTemplate.length());
			edtMsg.requestFocus();
		}
	};
}
