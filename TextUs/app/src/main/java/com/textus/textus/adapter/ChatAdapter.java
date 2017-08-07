package com.textus.textus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.textus.textus.R;
import com.textus.textus.constant.Constants;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.utility.Utilities;
import com.textus.textus.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.android.gms.analytics.internal.zzy.j;

/**
 * Hai Nguyen - 12/30/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

	private long mContactId;
	private LayoutInflater mInflater;
	private List<ConversationEntity> mChats;
	private OnItemClickListener mListener;
	private List<ImageView> mImgView;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayImageOptions options;

	public ChatAdapter(Context ctx, OnItemClickListener listener, long contactId) {

		this.mContactId = contactId;
		this.mInflater = LayoutInflater.from(ctx);
		this.mListener = listener;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = mInflater.inflate(R.layout.layout_item_chat, parent, false);
		return new ViewHolder(view);
	}

	public void fitImagge(ImageView imgView, Bitmap bitmap)
	{
		int imageWidth = bitmap.getWidth();
		int imageHeight = bitmap.getHeight();

		int newWidth = 500;
		float scaleFactor = (float)newWidth/(float)imageWidth;
		int newHeight = (int)(imageHeight * scaleFactor);

		bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
		imgView.setImageBitmap(bitmap);
	}
	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final ConversationEntity chat = mChats.get(position);
		long nextSenderId = 0;
		long senderId = chat.getSenderId();
		if (position < mChats.size() - 1) {

			ConversationEntity nextItem = mChats.get(position + 1);
			nextSenderId = nextItem.getSenderId();
		}

		String strImg = chat.getImageUrl();
		if(strImg == null)
			holder.imgView.setVisibility(View.GONE);
		else {
			holder.imgView.setVisibility(View.VISIBLE);
			ImageLoader imgLoader =  ImageLoader.getInstance();
			imgLoader.loadImage(strImg, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					//holder.imgView.setImageBitmap(loadedImage);
					fitImagge(holder.imgView, loadedImage);
				}
			});
			//ImageLoader.getInstance().displayImage(strImg, holder.imgView, options, animateFirstListener);
		}
//
//		else

		if (chat.getReceiverId() == mContactId) {

			holder.txtMsgLeft.setVisibility(View.GONE);
			holder.txtNameLeft.setVisibility(View.GONE);
			holder.txtMsgRight.setVisibility(View.VISIBLE);
			holder.txtNameRight.setVisibility(View.VISIBLE);
			if (nextSenderId != senderId) {

				holder.txtNameRight.setVisibility(View.VISIBLE);
			} else {

				holder.txtNameRight.setVisibility(View.GONE);
			}

			// Data
			holder.txtNameRight.setText("You");
			if(!chat.getContent().equals("[Image Attached]")) {
				holder.txtMsgRight.setText(chat.getContent());
				holder.txtMsgRight.setVisibility(View.VISIBLE);
			}
			else
				holder.txtMsgRight.setVisibility(View.GONE);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.gravity= Gravity.RIGHT;
			lp.rightMargin = 50;
			holder.imgView.setLayoutParams(lp);

		} else {
			holder.txtMsgRight.setVisibility(View.GONE);
			holder.txtNameRight.setVisibility(View.GONE);
			holder.txtMsgLeft.setVisibility(View.VISIBLE);
			if (nextSenderId != senderId) {

				holder.txtNameLeft.setVisibility(View.VISIBLE);
			} else {

				holder.txtNameLeft.setVisibility(View.GONE);
			}

			// Data
			if(!chat.getContent().equals("[Image Attached]")) {
				holder.txtMsgLeft.setText(chat.getContent());
				holder.txtMsgLeft.setVisibility(View.VISIBLE);
			}
			else
				holder.txtMsgLeft.setVisibility(View.GONE);
			holder.txtNameLeft.setText(chat.getSenderName());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.gravity= Gravity.LEFT;
			lp.leftMargin = 50;
			holder.imgView.setLayoutParams(lp);
		}

		if ((mChats.size() - 1 - position) % 3 == 0) {

			holder.txtTime.setVisibility(View.VISIBLE);
			holder.txtTime.setText(Utilities.getChatTime(chat.getDeliverAt()));
		} else {

			holder.txtTime.setVisibility(View.GONE);
		}
		holder.imgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Constants.imgView = holder.imgView;
				if(chat.getImageUrl() != null)
					mListener.onItemClick(chat);
			}
		});
	}

	@Override
	public int getItemCount() {

		return mChats == null ? 0 : mChats.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.chat_item_time)
		TextView txtTime;

		@Bind(R.id.chat_item_name_left)
		TextView txtNameLeft;

		@Bind(R.id.chat_item_name_right)
		TextView txtNameRight;

		@Bind(R.id.chat_item_msg_left)
		TextView txtMsgLeft;

		@Bind(R.id.chat_item_msg_right)
		TextView txtMsgRight;

		@Bind(R.id.chat_item_img_view)
		ImageView imgView;
		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	/**
	 * Set chat data
	 * 
	 * @param chats
	 *            chat data
	 */
	public void setChats(List<ConversationEntity> chats) {

		// if (chats != null) {
		//
		// // Collections.reverse(chats);
		// }

		this.mChats = chats;
		notifyDataSetChanged();
	}

	/**
	 * Add chat item
	 * 
	 * @param chat
	 *            chat item
	 */
	public void addChat(ConversationEntity chat) {

		if (mChats == null) {

			mChats = new ArrayList<>();
		}

		this.mChats.add(0, chat);
		notifyDataSetChanged();
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
