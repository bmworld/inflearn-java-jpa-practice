package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import static org.springframework.util.StringUtils.isEmpty;


/**
 * @WARNING => Specification (JPA CRITERIA)는..........그냥.....쓰지마시라....
 */
public class MemberSpecification {

  public static Specification<Member> teamName(final String teamName) {
    return (root, query, builder) -> {
      if (isEmpty(teamName)) {
        return null;
      }

      Join<Member, Team> t = root.join("team", JoinType.INNER); // Member와 Team을 Join
      return builder.equal(t.get("name"), teamName);
    };

  }


  public static Specification<Member> username(final String name) {
    return (root, query, builder) -> isEmpty(name)
        ? null
        : builder.equal(root.get("name"), name);
  }

}
