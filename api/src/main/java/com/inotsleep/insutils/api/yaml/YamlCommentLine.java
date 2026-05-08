package com.inotsleep.insutils.api.yaml;

import com.inotsleep.insutils.api.config.CommentType;

public record YamlCommentLine(CommentType commentType, String comment) {
}
