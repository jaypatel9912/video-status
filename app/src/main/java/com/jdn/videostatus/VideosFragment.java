package com.jdn.videostatus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nightonke.boommenu.Util;
import com.wang.avi.AVLoadingIndicatorView;
import com.white.progressview.HorizontalProgressView;

import org.json.XML;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideosFragment extends Fragment {

    public static VideosFragment newInstance(boolean latest) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.is_latest, latest);
        fragment.setArguments(args);
        return fragment;
    }

    View view;
    boolean latest;
    AVLoadingIndicatorView progressVIew;
    AppCompatTextView text;
    RecyclerView recycler_view;
    CoordinatorLayout parentLayout;
    VideoAdapter adapter;
    TextView tvNoVideos;
    HorizontalProgressView progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item, container, false);

        latest = getArguments().getBoolean(Constants.is_latest);

        progressBar = view.findViewById(R.id.progressBar);

        tvNoVideos = view.findViewById(R.id.tvNoVideos);
        progressVIew = view.findViewById(R.id.progressVIew);
        text = view.findViewById(R.id.text);
        recycler_view = view.findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        parentLayout = view.findViewById(R.id.content);

        progressVIew.setVisibility(View.VISIBLE);
        tvNoVideos.setVisibility(View.GONE);

        Type type = new TypeToken<ArrayList<VideoItem>>() {
        }.getType();

        ArrayList<VideoItem> listVideos = new Gson().fromJson(Utils.getPreference(getActivity(), latest ? Constants.LATEST : Constants.POPULAR), type);

        if (listVideos != null && listVideos.size() > 0) {
            progressVIew.setVisibility(View.GONE);
        }


        if (latest)
            getData();
        else
            setAdapter(listVideos);

        return view;
    }

    private void getData() {

        if (Utils.isNetworkAvailable(Objects.requireNonNull(getActivity())))
            fillData();
        else {
            progressVIew.setVisibility(View.GONE);
            View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView, R.string.no_internet, Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isAdded() && isVisibleToUser) {
            if (adapter == null || ((latest && Constants.IS_CATEGORY_CHANGED_LATEST) || (!latest && Constants.IS_CATEGORY_CHANGED_POPULAR))) {
                recycler_view.setAdapter(null);
                getData();
                tvNoVideos.setVisibility(View.GONE);
                progressVIew.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fillData() {
        Call<String> call = null;

        tvNoVideos.setVisibility(View.GONE);
        String language = Utils.getPreference(getActivity(), Constants.language);
        if (latest)
            call = Globals.initRetrofit(getActivity()).getLatestVideos("-1", "LIST", language, "100");
        else
            call = Globals.initRetrofit(getActivity()).getPopularVideos("-1", "TOP", "100", language);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    assert response.body() != null;
                    String json = XML.toJSONObject(response.body()).toString();

                    try {
                        VideoStatusResponse videoStatusResponse = new Gson().fromJson(json, VideoStatusResponse.class);
                        setAdapter(videoStatusResponse.videos.listVideos);

                        if (latest) {
                            Utils.setPreference(getActivity(), Constants.LATEST, new Gson().toJson(videoStatusResponse.videos.listVideos));
                            Constants.IS_CATEGORY_CHANGED_LATEST = false;
                        } else {
                            Utils.setPreference(getActivity(), Constants.POPULAR, new Gson().toJson(videoStatusResponse.videos.listVideos));
                            Constants.IS_CATEGORY_CHANGED_POPULAR = false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvNoVideos.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressVIew.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressVIew.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    VideoAdapter.OnDownloadClickListener listener = new VideoAdapter.OnDownloadClickListener() {
        @Override
        public void onDownloadClick(VideoItem item) {

            if (Constants.IS_DOWNLOADING) {
                Toast.makeText(getActivity(), R.string.download_in_progress, Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int writeStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), R.string.sdcard_permission, Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_CLIPS + "/" + item.name);
                if (!file.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    new DownloadFile(item.location + item.name, file, new DownloadFile.DownloadListener() {
                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onProgress(int progress) {
                            progressBar.setProgress(progress);
                        }

                        @Override
                        public void onDownloadComplete() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), getString(R.string.download_done), Toast.LENGTH_SHORT).show();
                        }
                    }).execute();
                } else {
                    Toast.makeText(getActivity(), R.string.video_already_downloaded, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void setAdapter(ArrayList<VideoItem> listVideos) {
        if (listVideos != null && listVideos.size() > 0) {
            adapter = new VideoAdapter(listVideos, listener);
            recycler_view.setAdapter(adapter);
            tvNoVideos.setVisibility(View.GONE);
        } else {
            tvNoVideos.setVisibility(View.VISIBLE);
        }
    }

}
