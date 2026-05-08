package com.inotsleep.insutils.api.yaml;

import java.util.Optional;

public enum YamlScalarType {

    DOUBLE_QUOTED,
    /**
     * Single quoted scalar
     */
    SINGLE_QUOTED,
    /**
     * Literal scalar
     */
    LITERAL,
    /**
     * Folded scalar
     */
    FOLDED,
    /**
     * Mixture of scalar styles to dump JSON format. Double-quoted style for !!str, !!binary,
     * !!timestamp. Plain style - for !!bool, !!float, !!int, !!null
     *
     * These are never dumped - !!merge, !!value, !!yaml
     */
    JSON_SCALAR_STYLE,
    /**
     * Plain scalar
     */
    PLAIN
}
