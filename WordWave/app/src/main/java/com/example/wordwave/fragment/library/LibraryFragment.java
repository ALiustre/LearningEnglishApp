package com.example.wordwave.fragment.library;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wordwave.R;
import com.example.wordwave.databinding.FragmentLibraryBinding;
import com.example.wordwave.fragment.library.folder.FolderFragment;
import com.example.wordwave.fragment.library.topic.TopicFragment;
import com.google.android.material.tabs.TabLayout;

public class LibraryFragment extends Fragment {

    private LibraryViewModel mViewModel;

    private FragmentLibraryBinding binding;

    TopicFragment topicFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    private View mView;
    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        mView = rootView;
        getActivity().setTitle("Library");
        addFragment();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        // TODO: Use the ViewModel
    }

    private void addFragment(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(TopicFragment.newInstance(), "Topic");
        adapter.addFragment(FolderFragment.newInstance(), "Folder");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);

    }

}