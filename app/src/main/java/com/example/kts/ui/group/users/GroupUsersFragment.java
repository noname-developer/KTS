package com.example.kts.ui.group.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.ui.adapters.UserAdapter;
import com.example.kts.ui.userEditor.UserEditorActivity;
import com.example.kts.customPopup.PopupBuilder;

import org.jetbrains.annotations.NotNull;

public class GroupUsersFragment extends Fragment {

    private static final String GROUP_UUID = "GROUP_UUID";
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private GroupUsersViewModel viewModel;
    private ListPopupWindow popupWindow;
    private LinearLayoutManager layoutManager;

    @NotNull
    public static GroupUsersFragment newInstance(String groupUuid) {
        GroupUsersFragment fragment = new GroupUsersFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_UUID, groupUuid);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GroupUsersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group_users, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_users);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setUserItemClickListener(position -> viewModel.onUserItemClick(position));
        adapter.setUserItemLongClickListener(position -> viewModel.onUserItemLongClick(position));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.onGroupUuidReceived(getArguments().getString(GROUP_UUID));
        viewModel.userList.observe(getViewLifecycleOwner(), users -> {
            adapter.submitList(users);
            adapter.notifyDataSetChanged();
        });
        viewModel.showUserOptions.observe(getViewLifecycleOwner(), positionWithOptionsPair -> {
            popupWindow = new PopupBuilder().setPopupMenu(
                    popupWindow,
                    layoutManager.findViewByPosition(positionWithOptionsPair.first),
                    positionWithOptionsPair.second, R.layout.item_popup_icon_content,
                    position -> viewModel.onOptionUserClick(position)
            );
            popupWindow.show();
        });
        viewModel.openUserEditor.observe(getViewLifecycleOwner(), user -> {
            Intent intent = new Intent(getActivity(), UserEditorActivity.class);
            intent.putExtra(UserEditorActivity.USER_ROLE, user.getRole());
            intent.putExtra(UserEditorActivity.USER_UUID, user.getUuid());
            intent.putExtra(UserEditorActivity.USER_GROUP_UUID, user.getGroupUuid());
            startActivity(intent);
        });
    }
}