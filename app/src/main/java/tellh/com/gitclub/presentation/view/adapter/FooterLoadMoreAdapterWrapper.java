package tellh.com.gitclub.presentation.view.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import tellh.com.gitclub.R;
import tellh.com.gitclub.common.utils.Utils;
import tellh.com.gitclub.presentation.contract.bus.RxBusPostman;

/**
 * Created by tlh on 2016/2/18.
 */
public class FooterLoadMoreAdapterWrapper extends HeaderAndFooterAdapterWrapper {
    private int curPage;

    public enum UpdateType {
        REFRESH, LOAD_MORE
    }

    public enum FooterState {
        PULL_TO_LOAD_MORE,
        LOADING,
        NO_MORE,
    }

    private FooterState mFooterStatus = FooterState.PULL_TO_LOAD_MORE;
    private String toLoadText = Utils.getString(R.string.pull_to_load_more);
    private String noMoreText = Utils.getString(R.string.no_more);
    private String loadingText = Utils.getString(R.string.loading);

    public FooterState getFooterStatus() {
        return mFooterStatus;
    }

    public FooterLoadMoreAdapterWrapper(BaseRecyclerAdapter adapter) {
        super(adapter);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void onBindFooter(RecyclerViewHolder holder, int position) {
        ProgressBar progressBar = (ProgressBar) holder.getView(R.id.progressBar);
        if (mItems.size() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            holder.setText(R.id.tv_footer, Utils.getString(R.string.empty));
            return;
        }
        switch (mFooterStatus) {
            case PULL_TO_LOAD_MORE:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer, toLoadText);
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer, loadingText);
                break;
            case NO_MORE:
                holder.setText(R.id.tv_footer, noMoreText);
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setOnReachFooterListener(RecyclerView recyclerView, final OnReachFooterListener listener) {
        if (recyclerView == null || listener == null)
            return;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isReachBottom(recyclerView, newState) || mItems.size() == 0)
                    return;
                RxBusPostman.postQuickReturnEvent(true);
                if (mFooterStatus != FooterState.LOADING
                        && mFooterStatus != FooterState.NO_MORE) {
                    setFooterStatus(FooterState.LOADING);
                    listener.onToLoadMore(curPage);
                }
            }
        });
    }


    public void setFooterStatus(FooterState status) {
        mFooterStatus = status;
        notifyDataSetChanged();
    }

    public boolean isReachBottom(RecyclerView recyclerView, int newState) {
        return recyclerView != null && newState == RecyclerView.SCROLL_STATE_IDLE && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1;
    }

    public interface OnReachFooterListener {
        void onToLoadMore(int curPage);
    }


    public void setToLoadText(String toLoadText) {
        this.toLoadText = toLoadText;
    }

    public void setNoMoreText(String noMoreText) {
        this.noMoreText = noMoreText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public void OnGetData(List data, UpdateType updateType) {
        if (updateType == UpdateType.REFRESH) {
            refresh(data);
            curPage = 1;
        } else {
            addAll(data);
            curPage++;
            if (data.isEmpty())
                setFooterStatus(FooterLoadMoreAdapterWrapper.FooterState.NO_MORE);
            else
                setFooterStatus(FooterLoadMoreAdapterWrapper.FooterState.PULL_TO_LOAD_MORE);
        }
    }

    public int getCurPage() {
        return curPage;
    }
}