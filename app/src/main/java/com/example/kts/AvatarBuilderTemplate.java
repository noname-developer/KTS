package com.example.kts;

import androidx.core.content.res.ResourcesCompat;

import com.example.avatarGenerator.AvatarGenerator;
import com.example.avatarGenerator.AvatarBuilderInitializer;

import org.jetbrains.annotations.NotNull;

public class AvatarBuilderTemplate implements AvatarBuilderInitializer {
    @Override
    public AvatarGenerator.Builder initBuilder(@NotNull AvatarGenerator.Builder builder) {
        return builder
                .font(ResourcesCompat.getFont(builder.getContext(), R.font.futura_medium));
    }
}
