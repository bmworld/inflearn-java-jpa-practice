package study.datajpa.repository;

import lombok.ToString;

public interface NestedClosedProjections {
  String getName();

  TeamInfo getTeam();

  interface TeamInfo {
    String getName();
  }
}
