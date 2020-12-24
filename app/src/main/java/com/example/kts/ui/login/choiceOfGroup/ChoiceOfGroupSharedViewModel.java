package com.example.kts.ui.login.choiceOfGroup;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.RxBusChoiceOfGroup;
import com.example.kts.data.model.EntityModel;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Specialty;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.GroupRepository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.kts.utils.PredicateUtil.distinctByKey;

public class ChoiceOfGroupSharedViewModel extends AndroidViewModel {

    private final Map<String, Boolean> expandableSpecialties = new HashMap<>();
    private final GroupRepository groupRepository;
    private final GroupInfoRepository groupInfoRepository;
    public LiveData<List<Specialty>> allSpecialties;
    public LiveData<List<GroupEntity>> groupsBySpecialty;
    public MutableLiveData<String> selectedSpecialtyUuid = new MutableLiveData<>();
    public MediatorLiveData<List<EntityModel>> groupAndSpecialtyList = new MediatorLiveData<>();

    public ChoiceOfGroupSharedViewModel(@NonNull Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        groupsBySpecialty = Transformations.switchMap(selectedSpecialtyUuid, groupRepository::getGroupsBySpecialtyUuid);
        groupInfoRepository = new GroupInfoRepository(application);
        allSpecialties = groupRepository.getAllSpecialties();

        groupAndSpecialtyList.addSource(allSpecialties, specialtyList -> {
            for (Specialty specialty : specialtyList) {
                expandableSpecialties.put(specialty.getName(), false);
            }
            groupAndSpecialtyList.setValue(sortedList(new ArrayList<>(specialtyList)));
        });
        groupAndSpecialtyList.addSource(groupsBySpecialty, groups -> {
            groupAndSpecialtyList.getValue().addAll(groups);
            groupAndSpecialtyList.setValue(sortedList(groupAndSpecialtyList.getValue().stream()
                    .filter(distinctByKey(EntityModel::getUuid))
                    .collect(Collectors.toList())));
        });
    }

    @NotNull
    @Contract("_ -> param1")
    private List<EntityModel> sortedList(@NotNull List<EntityModel> list) {
        list.sort((o1, o2) -> getSpecialtyUuid(o1).compareTo(getSpecialtyUuid(o2)));
        return list;
    }

    @Nullable
    private String getSpecialtyUuid(Object o) {
        if (o instanceof Specialty) {
            return ((Specialty) o).getUuid();
        } else if (o instanceof GroupEntity) {
            return ((GroupEntity) o).getSpecialtyUuid();
        }
        return null;
    }

    public void onSpecialtyItemClick(int position) {
        Specialty specialty = (Specialty) groupAndSpecialtyList.getValue().get(position);
        String specialtyName = specialty.getName();
        String specialityUuid = specialty.getUuid();
        if (expandableSpecialties.get(specialtyName)) {
            groupAndSpecialtyList.getValue().removeAll(getGroupListBySpecialtyUuid(specialty.getUuid()));
            groupAndSpecialtyList.setValue(groupAndSpecialtyList.getValue());
        } else {
            selectedSpecialtyUuid.setValue(specialityUuid);
        }
        Log.d("lol", "onSpecialtyItemClick: " + specialtyName + " " + expandableSpecialties.get(specialtyName));
        expandableSpecialties.replace(specialtyName, !expandableSpecialties.get(specialtyName));
    }

    public void onGroupItemClick(int position) {
        String groupUuid = groupAndSpecialtyList.getValue().get(position).getUuid();
        groupInfoRepository.loadGroupInfo(groupUuid);
        RxBusChoiceOfGroup.getInstance().postSelectGroupEvent(groupUuid);
    }

    @NotNull
    private List<GroupEntity> getGroupListBySpecialtyUuid(String specialtyUuid) {
        List<GroupEntity> groupEntityListBySpecialty = new ArrayList<>();
        for (Object o : groupAndSpecialtyList.getValue()) {
            if (o instanceof GroupEntity) {
                if (((GroupEntity) o).getSpecialtyUuid().equals(specialtyUuid)) {
                    groupEntityListBySpecialty.add((GroupEntity) o);
                }
            }
        }
        return groupEntityListBySpecialty;
    }

    @Override
    public void onCleared() {
        super.onCleared();
        RxBusChoiceOfGroup.getInstance().clearEvent();
    }
}
