package com.example.kts.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kts.R;
import com.example.kts.ui.group.subjects.GroupSubjectsFragment;
import com.example.kts.ui.group.users.GroupUsersFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class GroupFragment extends Fragment {

    private GroupViewModel viewModel;
    private TabLayout tabLayout;
    private TabItem tiGroupUsers, tiGroupSubjects;
    private ViewPager2 viewPager;
    private String groupUuid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group, container, false);
        viewPager = root.findViewById(R.id.viewpager_group);
        if (getArguments() != null) {
            groupUuid = getArguments().getString(GROUP_UUID);
        }
        GroupFragmentAdapter adapter = new GroupFragmentAdapter(this, groupUuid);
        viewPager.setAdapter(adapter);
        tabLayout = root.findViewById(R.id.tabLayout_group);
        tiGroupUsers = root.findViewById(R.id.tabItem_group_users);
        tiGroupSubjects = root.findViewById(R.id.tabItem_group_subjects);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            Toast.makeText(getActivity(), "select " + position, Toast.LENGTH_SHORT).show();
        }).attach();
        tabLayout.getTabAt(0).setText(R.string.group_users);
        tabLayout.getTabAt(1).setText(R.string.group_subjects);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        viewModel.onGroupUuidReceived(groupUuid);
        viewModel.toolbarName.observe(getViewLifecycleOwner(), title ->
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title));
    }

    public static class GroupFragmentAdapter extends FragmentStateAdapter {

        private final String groupUuid;

        public GroupFragmentAdapter(Fragment fragment, String groupUuid) {
            super(fragment);
            this.groupUuid = groupUuid;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return GroupUsersFragment.newInstance(groupUuid);
                case 1:
                    return GroupSubjectsFragment.newInstance(groupUuid);
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}