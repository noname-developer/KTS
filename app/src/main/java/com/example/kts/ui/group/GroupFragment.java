package com.example.kts.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kts.R;
import com.example.kts.ui.group.choiceOfSubjectTeacher.ChoiceOfSubjectTeacherDialog;
import com.example.kts.ui.group.subjects.GroupSubjectsFragment;
import com.example.kts.ui.group.users.GroupUsersFragment;
import com.example.kts.ui.userEditor.UserEditorActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class GroupFragment extends Fragment {

    private GroupViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String groupUuid;
    private Menu menu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
        viewModel.onPrepareOptions(viewPager.getCurrentItem());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.onOptionSelect(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, new GroupViewModelFactory(getActivity().getApplication(), groupUuid)).get(GroupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group, container, false);
        viewPager = root.findViewById(R.id.viewpager_group);
        if (getArguments() != null) {
            groupUuid = getArguments().getString(GROUP_UUID);
        }
        GroupFragmentAdapter adapter = new GroupFragmentAdapter(this, groupUuid);
        viewPager.setAdapter(adapter);
        tabLayout = root.findViewById(R.id.tabLayout_group);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();
        tabLayout.getTabAt(0).setText(R.string.group_users);
        tabLayout.getTabAt(1).setText(R.string.group_subjects);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.toolbarName.observe(getViewLifecycleOwner(), title ->
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title));
        viewModel.menuItemVisibility.observe(getViewLifecycleOwner(), idAndVisiblePair -> {
            MenuItem menuItem = menu.findItem(idAndVisiblePair.first);
            menuItem.setVisible(idAndVisiblePair.second);
        });
        viewModel.openChoiceSubjectTeacher.observe(getViewLifecycleOwner(), groupUuid -> {
            ChoiceOfSubjectTeacherDialog dialog = ChoiceOfSubjectTeacherDialog.newInstance(
                    groupUuid, "", "");
            dialog.show(getChildFragmentManager(), "CHOICE_OF_SUBJECT_TEACHER_DIALOG");
        });
        viewModel.openUserEditor.observe(getViewLifecycleOwner(), userTypeWithGroupUuidPair -> {
            Intent intent = new Intent(getContext(), UserEditorActivity.class);
            intent.putExtra(UserEditorActivity.USER_ROLE, userTypeWithGroupUuidPair.first);
            intent.putExtra(UserEditorActivity.USER_GROUP_UUID, userTypeWithGroupUuidPair.second);
            startActivity(intent);
        });
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