package com.inotsleep.insutils.spi.config;

import org.jetbrains.annotations.ApiStatus;
import org.snakeyaml.engine.v2.nodes.*;

public abstract class UnsafeSerializableObject {

    public UnsafeSerializableObject() {}

    @ApiStatus.Experimental
    public abstract void beforeDeserialization(MappingNode node);

    @ApiStatus.Experimental
    public abstract void afterDeserialization(MappingNode node);

    @ApiStatus.Experimental
    public abstract void beforeSerialization(MappingNode node);

    @ApiStatus.Experimental
    public abstract void afterSerialization(MappingNode node);


}