package info.meizi_retrofit.ui.largepic;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.meizi_retrofit.R;
import info.meizi_retrofit.utils.PicassoHelper;
import info.meizi_retrofit.utils.RxMeizhi;
import info.meizi_retrofit.widget.TouchImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Mr_Wrong on 15/10/6.
 */
public class LargePicFragment extends Fragment {
    private static final String URL = "url";
    private static final String GROUPID = "groupid";
    private static final String POSITION = "position";
    @Bind(R.id.image)
    TouchImageView image;
    private String url;
    private LargePicActivity activity;
    private String groupid;
    private int position;
    protected CompositeSubscription mSubscriptions = new CompositeSubscription();

    @OnClick(R.id.image)
    void toggleToolbar() {
        activity.supportFinishAfterTransition();
    }

    public LargePicFragment() {
    }

    public static Fragment newFragment(String url, String groupid, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        bundle.putString(GROUPID, groupid);
        bundle.putInt(POSITION, position);
        LargePicFragment fragment = new LargePicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (LargePicActivity) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(URL);
        groupid = getArguments().getString(GROUPID);
        position = getArguments().getInt(POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewer, container, false);
        ButterKnife.bind(this, view);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("保存图片");
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mSubscriptions.add(
                                RxMeizhi.saveImageAndGetPathObservable(getContext(), url, groupid + "_" + position)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Uri>() {
                                            @Override
                                            public void call(Uri uri) {
                                                File appDir = new File(Environment.getExternalStorageDirectory(), "Meizi");
                                                String msg = String.format("图片已保存至 %s 文件夹", appDir.getAbsolutePath());
                                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                            }
                                        }));
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PicassoHelper.getInstance(activity)
                .load(url)
                .into(image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public View getSharedElement() {
        return image;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mSubscriptions != null) {
            this.mSubscriptions.unsubscribe();
        }
    }
}
