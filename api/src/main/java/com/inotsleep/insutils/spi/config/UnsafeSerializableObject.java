package com.inotsleep.insutils.spi.config;

import com.inotsleep.insutils.api.yaml.YamlMappingNode;
import org.jetbrains.annotations.ApiStatus;

public abstract class UnsafeSerializableObject {

    public UnsafeSerializableObject() {}

    @ApiStatus.Experimental
    public abstract void beforeDeserialization(YamlMappingNode node);

    @ApiStatus.Experimental
    public abstract void afterDeserialization(YamlMappingNode node);

    @ApiStatus.Experimental
    public abstract void beforeSerialization(YamlMappingNode node);

    @ApiStatus.Experimental
    public abstract void afterSerialization(YamlMappingNode node);


}
