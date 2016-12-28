package com.zhangruofan.requery.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhangruofan.requery.R;
import com.zhangruofan.requery.model.PersonProxy;
import com.zhangruofan.requery.util.Constants;
import com.zhangruofan.requery.util.CustomizeSubscriber;
import com.zhangruofan.requery.view.PersonEditActivity;
import io.requery.android.QueryRecyclerAdapter;
import io.requery.query.Result;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.ResultSetIterator;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangruofan on 16-12-26.
 */

public class PersonAdapter extends QueryRecyclerAdapter<PersonProxy, PersonAdapter.PersonViewHolder> implements View.OnClickListener {

    private static final String TAG = PersonAdapter.class.getSimpleName();

    private SingleEntityStore mDataStore;
    private Context mContext;

    public PersonAdapter(SingleEntityStore dataStore, Context context) {
        super(PersonProxy.$TYPE);
        mDataStore = dataStore;
        mContext = context;
    }

    @Override
    public Result<PersonProxy> performQuery() {
        TimingLogger timings = new TimingLogger(TAG, "methodA");
        timings.addSplit("performQuery start");

        mDataStore.select(PersonProxy.class);

        Result<PersonProxy> result = (Result<PersonProxy>) mDataStore.select(PersonProxy.class).get();

        timings.addSplit("performQuery end");
        timings.dumpToLog();
        return result;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false);
        v.setOnClickListener(this);

        PersonViewHolder vh = new PersonViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PersonProxy item, PersonViewHolder holder, int position) {
        holder.update(position, item);
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        private ImageView picture;
        private TextView name;
        private View root;

        public PersonViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            picture = (ImageView) itemView.findViewById(R.id.picture);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        public void update(int position, PersonProxy item) {
            int[] colors = Constants.Color.colors;
            picture.setBackgroundColor(colors[position % colors.length]);
            name.setText(item.getName());
            root.setTag(item.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person:
                Intent intent = new Intent(mContext, PersonEditActivity.class);
                intent.putExtra(Constants.Extra.EXTRA_PERSON_ID, (int) v.getTag());
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void queryAsync() {
        Observable.create(new Observable.OnSubscribe<ResultSetIterator<PersonProxy>>() {
            @Override
            public void call(Subscriber<? super ResultSetIterator<PersonProxy>> subscriber) {
                Result<PersonProxy> result = performQuery();
                subscriber.onNext((ResultSetIterator<PersonProxy>) result.iterator());
                subscriber.onCompleted();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomizeSubscriber<ResultSetIterator<PersonProxy>>() {
                    @Override
                    public void onNext(ResultSetIterator<PersonProxy> personProxyResultSetIterator) {
                        setResult(personProxyResultSetIterator);
                    }
                });
    }
}
