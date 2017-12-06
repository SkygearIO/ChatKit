/*******************************************************************************
 * Original work Copyright 2016 stfalcon.com
 * Modified work Copyright 2017 Oursky Ltd.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.stfalcon.chatkit.messages;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

class RecyclerScrollMoreListener
        extends RecyclerView.OnScrollListener {

    private OnLoadMoreListener loadMoreListener;
    private boolean listening = false;

    private RecyclerView.LayoutManager mLayoutManager;

    RecyclerScrollMoreListener(LinearLayoutManager layoutManager, OnLoadMoreListener loadMoreListener) {
        this.mLayoutManager = layoutManager;
        this.loadMoreListener = loadMoreListener;
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    private int getLastVisibleItemPosition() {
        int lastVisibleItemPosition = 0;

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        return lastVisibleItemPosition;
    }

    private boolean shouldTriggerLoadMore() {
        int visibleThreshold = 5;
        int lastVisibleItemPosition = getLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();
        return this.listening && (lastVisibleItemPosition + visibleThreshold) > totalItemCount;
    }

    private void triggerLoadMore() {
        int totalItemCount = mLayoutManager.getItemCount();
        this.loadMoreListener.onLoadMore(totalItemCount);
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (loadMoreListener != null) {
            if (this.shouldTriggerLoadMore()) {
                this.triggerLoadMore();
            }
        }
    }

    public void startListening() {
        this.listening = true;
        if (this.shouldTriggerLoadMore()) {
            this.triggerLoadMore();
        }
    }

    public void stopListening() {
        this.listening = false;
    }

    interface OnLoadMoreListener {
        void onLoadMore(int total);
    }
}
