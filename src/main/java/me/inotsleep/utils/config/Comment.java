package me.inotsleep.utils.config;

import org.snakeyaml.engine.v2.comments.CommentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Comments.class)
public @interface Comment {
    String value();
    CommentType type() default CommentType.BLOCK;
}