package com.inotsleep.insutils.api.config;

/**
 * The type of comment line.
 */
public enum CommentType {
  /**
   * empty line
   */
  BLANK_LINE,
  /**
   * comment which start with #
   */
  BLOCK,
  /**
   * ending the line
   */
  IN_LINE
}
