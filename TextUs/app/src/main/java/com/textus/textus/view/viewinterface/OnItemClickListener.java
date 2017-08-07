package com.textus.textus.view.viewinterface;

import com.textus.textus.entity.BaseEntity;

/**
 * Hai Nguyen - 12/27/15.
 */
public interface OnItemClickListener {

	void onItemClick(BaseEntity model);
	void onSearch(String query);
}
