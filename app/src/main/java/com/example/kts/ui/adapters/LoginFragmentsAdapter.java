package com.example.kts.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kts.ui.login.LoginViewModel;
import com.example.kts.ui.login.choiceOfGroup.ChoiceOfGroupFragment;
import com.example.kts.ui.login.choiceOfRole.ChoiceOfRoleFragment;
import com.example.kts.ui.login.signInByPhoneNumber.SignInByPhoneNumberFragment;
import com.example.kts.ui.login.userAccountList.UserAccountListFragment;
import com.example.kts.ui.login.verifyPhoneNumber.VerifyPhoneNumberFragment;

import java.util.ArrayList;
import java.util.List;

public class LoginFragmentsAdapter extends FragmentStateAdapter {

    private List<LoginViewModel.FragmentType> fragmentTypeList = new ArrayList<>();

    public LoginFragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public List<LoginViewModel.FragmentType> getData() {
        return fragmentTypeList;
    }

    public void setData(List<LoginViewModel.FragmentType> fragmentTypeList) {
        this.fragmentTypeList = fragmentTypeList;
    }

    public void addData(LoginViewModel.FragmentType fragmentType) {
        fragmentTypeList.add(fragmentType);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (fragmentTypeList.get(position)) {
            case CHOICE_ROLE:
                return new ChoiceOfRoleFragment();
            case CHOICE_GROUP:
                return new ChoiceOfGroupFragment();
            case CHOICE_USER_ACCOUNT:
                return new UserAccountListFragment();
            case NUM_PHONE:
                return new SignInByPhoneNumberFragment();
            case VERIFY_PHONE_NUM:
                return new VerifyPhoneNumberFragment();
            default:
                return null;
        }
    }


    public void removeFragment(int position) {
        fragmentTypeList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return fragmentTypeList.size();
    }
}
